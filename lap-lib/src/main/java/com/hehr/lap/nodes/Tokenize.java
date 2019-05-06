package com.hehr.lap.nodes;

import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.hehr.lap.Bundle;
import com.hehr.lap.Conf;
import com.hehr.lap.bean.Metadata;
import com.hehr.lap.bean.ScannerBean;
import com.hehr.lap.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * 分词节点
 * 分词完毕之后需要通过歌手数据库校验出歌手名和歌曲名
 *
 * @author hehr
 */
public class Tokenize extends BaseNode {

    private static final String TAG = Tokenize.class.getSimpleName();

    private Tokenize() {
    }

    public static class Builder {
        public Tokenize build() {
            return new Tokenize();
        }
    }

    @Override
    public String getName() {
        return Name.TOKENIZE;
    }

    @Override
    public Bundle doWork(Bundle bundle) throws TaskFactory.ExecutorServiceShutdownException, ExecutionException, InterruptedException {
        return bundle
                .getTaskFactory()
                .getExecutor()
                .submit(new TokenizeTask(bundle))
                .get();
    }

    @Override
    public boolean hasNext(Bundle bundle) {
        return true;
    }


    /**
     * @author hehr
     */
    class TokenizeTask extends BaseTask {

        public TokenizeTask(Bundle bundle) {
            super(bundle);
        }

        @Override
        public Bundle call() throws Exception {

            //1 对文件对文件名依据特殊符号进行分词,并组装出来一个TokenizeBean的集合(tokenizeList),以及一个所有分词结果的集合(strList)
            //2 通过一个超长的SQL对singer表进行一次SELECT IN 查询，查询出来所有的其中的歌手名
            //3 遍历查询出来的结果，通过中间数据集合(tokenizeList)找出来其文件名进而找出来其在bundle.list中的位置

            //所有的分词结果集合
            List<String> strList = new ArrayList<>();
            //中间数据集合
            List<TokenizeBean> tokenizeList = new ArrayList<>();

            Set<String> artists = new HashSet<>();

            for (ScannerBean todo : bundle.getToDoList()) {

                if (TextUtils.isEmpty(todo.getFileNameWithOutSuffix()) || todo.isEffect()) {
                    continue;
                }


                String fileNameWithOutSuffix = todo.getFileNameWithOutSuffix();

                String toSubBraceStr = StringUtils.getInstance().subBrace(fileNameWithOutSuffix);

                List<String> participlesList = StringUtils.getInstance().separateWord(toSubBraceStr);
                //字符串没有分出来的也可以不要了

                if (participlesList == null || participlesList.size() == 0) {
                    continue;
                }
                TokenizeBean t = new TokenizeBean();
                Set<String> s = new HashSet<>();
                //分词结果最多只取两个
                for (int i = 0; i < participlesList.size(); i++) {
                    String str = participlesList.get(i);
                    //分词之后不是纯中文的也可以丢了
                    if (!StringUtils.getInstance().isChineseAndEnglish(str)) {
                        continue;
                    }
                    s.add(str);
                    strList.add(str);
                }
                t.setTokenSet(s);
                t.setFileNameWithSuffix(fileNameWithOutSuffix);
                tokenizeList.add(t);
            }
            String sql = "select "
                    + Conf.ScannerDB.SINGER_COLUMN_NAME
                    + " from "
                    + Conf.ScannerDB.TABLE_SINGER_NAME
                    + " where "
                    + Conf.ScannerDB.SINGER_COLUMN_NAME
                    + " in ";
            //SQL查询的占位符
            StringBuffer selectInBuffer = new StringBuffer();
            selectInBuffer.append(" ( ");
            String[] selectionArgs = new String[strList.size()];
            for (int i = 0; i < strList.size(); i++) {
                selectionArgs[i] = strList.get(i);
                if (i == 0) {
                    selectInBuffer.append(" ?");
                } else {
                    selectInBuffer.append(",?");
                }
            }
            selectInBuffer.append(" ) ; ");
            sql = sql + selectInBuffer;
            Cursor cursor = bundle.getDbManager().queryData2Cursor(sql, selectionArgs);


            while (cursor != null
                    && cursor.getCount() > 0
                    && cursor.moveToNext()
            ) {
                artists.add(cursor.getString(cursor.getColumnIndex(Conf.ScannerDB.SINGER_COLUMN_NAME)));
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            return differentiateArtistAndTitle(artists, tokenizeList, bundle);
        }


        /**
         * @param artists
         * @param tokenizeBeansList
         * @param bundle
         * @return
         */
        private Bundle differentiateArtistAndTitle(Set<String> artists, List<TokenizeBean> tokenizeBeansList, Bundle bundle) {

            // 设置一个临时变量，避免 Iterator IllegalStateException
            boolean removed = false;

            //处理只有歌曲名，没有歌手名的情况，如：男声欢唱加重鼓.mp3
            for (Iterator<TokenizeBean> it = tokenizeBeansList.iterator(); it.hasNext(); ) {
                TokenizeBean tokenizeBean = it.next();
                if (tokenizeBean.getTokenSet() != null
                        && tokenizeBean.getTokenSet().size() == 1) {
                    String str = tokenizeBean.getTokenSet().iterator().next();
                    removed = true;
                    //非歌手名
                    if (!TextUtils.isEmpty(str) && !artists.contains(str)) {
                        Log.i(TAG, "differentiateArtistAndTitle add only title  " + str);
                        String fileNameWithOutSuffix = tokenizeBean.getFileNameWithSuffix();
                        for (ScannerBean scannerBean : bundle.getList()) {
                            if (!scannerBean.isEffect()
                                    && scannerBean.getFileNameWithOutSuffix().equals(fileNameWithOutSuffix)) {
                                scannerBean.getMetadata().setTitle(str);
                                if (removed) {
                                    it.remove();//已经解析过就从集合内移除
                                    removed = false;
                                }

                            }
                        }
                    }
                }
            }

            //有歌手名的情况
            for (String artist : artists) {
                for (TokenizeBean tokenizeBean : tokenizeBeansList) {
                    if (tokenizeBean.getTokenSet().contains(artist)) {
                        String fileNameWithOutSuffix = tokenizeBean.getFileNameWithSuffix();
                        for (ScannerBean scannerBean : bundle.getList()) {
                            if (!scannerBean.isEffect() && scannerBean.getFileNameWithOutSuffix().equals(fileNameWithOutSuffix)) {
                                scannerBean.getMetadata().setArtist(artist);
                                if (tokenizeBean.getTokenSet().size() == 2) {//分词结果只有两个,剩余结果直接作为歌曲名称
                                    for (String name : tokenizeBean.getTokenSet()) {
                                        if (!name.equals(artist)) {
                                            scannerBean.getMetadata().setTitle(name);
                                        }
                                    }
                                } else { //分词结果剩余多个
                                    tokenizeBean.getTokenSet().remove(artist);//先移除歌手名
                                    List<Metadata> extraList = new ArrayList<>();
                                    Metadata extraMetaData;
                                    for (Iterator<String> it = tokenizeBean.getTokenSet().iterator(); it.hasNext(); ) {
                                        String str = it.next();
                                        if (!artists.contains(str)) {
                                            extraMetaData = new Metadata();
                                            extraMetaData.setArtist(artist);
                                            extraMetaData.setTitle(str);
                                            extraList.add(extraMetaData);
                                        } else {
                                            extraMetaData = new Metadata();
                                            extraMetaData.setArtist(str);
                                            extraList.add(extraMetaData);
                                        }
                                    }
                                    scannerBean.getMetadata().setExtra(extraList);
                                }
                            }
                        }
                    }
                }
            }
            return bundle;
        }
    }


    /**
     * 分词使用的特殊集合
     */
    private class TokenizeBean {

        private String fileNameWithSuffix;
        private Set<String> tokenSet;

        public String getFileNameWithSuffix() {
            return fileNameWithSuffix;
        }

        public void setFileNameWithSuffix(String fileNameWithSuffix) {
            this.fileNameWithSuffix = fileNameWithSuffix;
        }

        public Set<String> getTokenSet() {
            return tokenSet;
        }

        public void setTokenSet(Set<String> tokenSet) {
            this.tokenSet = tokenSet;
        }

        @Override
        public String toString() {
            return "TokenizeBean{" +
                    "fileNameWithSuffix='" + fileNameWithSuffix + '\'' +
                    ", tokenList=" + tokenSet +
                    '}';
        }
    }

}
