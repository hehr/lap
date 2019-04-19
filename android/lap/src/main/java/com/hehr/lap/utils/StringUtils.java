package com.hehr.lap.utils;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class StringUtils {

    private static class Holder {
        private static final StringUtils INSTANCE = new StringUtils();
    }
    private StringUtils (){}

    public static final StringUtils getInstance() {
        return StringUtils.Holder.INSTANCE;
    }

    /**
     * if chinese str
     * @param str
     * @return
     */
    public boolean isChineseAndEnglish(String  str) {

        str = this.subBrace(str);//先做去括号处理

        for (char c: str.toCharArray()) {
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
            if (!(ub ==
                    Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS// 4E00-9FBF：CJK 统一表意符号
                    || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS //F900-FAFF：CJK 兼容象形文字
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A//CJK 统一表意符号扩展 A
                    || isEnglish(String.valueOf(c)) //包含英文部分解析
            )) {
                return false;
            }
        }
        return true;
    }


    public boolean isEnglish(String charaString) {
        return charaString.matches("^[a-zA-Z]*");
    }

    /**
     * 去掉各种括号内的内容
     * @return
     */
    public String subBrace(String str){

        String patternBrace = "\\([^)]*\\)";//小括号

        String patternBrace0 = "\\[[^\\]]+\\]";//中括号

        String patternBrace1 = "（[^）]+）";//中文小括号

        String patternBrace2 = "【[^】]+】";//【】

        String patternBrace3 = "「[^」]+」";//「」

        String patternBrace4 = "［[^］]+］";//中文中括号

        String patternBrace5 = "《[^》]+》";//《》

        String patternBrace6 = "<[^>]+>";//<>

        String pattern = patternBrace
                + "|" + patternBrace0
                + "|" + patternBrace1
                + "|" + patternBrace2
                + "|" + patternBrace3
                + "|" + patternBrace4
                + "|" + patternBrace5
                + "|" + patternBrace6
                ;


        return str.replaceAll(pattern,"");
    }


    public String md5(String key){

        StringBuffer sb = new StringBuffer();

        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(key.getBytes());
            for (byte b : result) {
                int number = b & 0xff;
                String str = Integer.toHexString(number);
                if (str.length() == 1) {
                    sb.append("0");
                }
                sb.append(str);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    /**
     * 判断是否含有特殊字符,并直接将特殊字符全部替换并分组
     *
     * @param str
     * @return true为包含，false为不包含
     */
    public List<String> separateWord(String str) {

        if(TextUtils.isEmpty(str)) {
            return  null;
        }

        String regEx = "[^\\p{Han}^\\p{Digit}^\\p{Alpha}]";

        str = str.replaceAll(regEx , " ");

        String[] chars = str.split(" ");

        List<String>  l = new ArrayList<>();

        for (String c: chars) {
            if(c.isEmpty() || !this.isChineseAndEnglish(c)){ //去除非中文和空格
                continue;
            }
            l.add(c);
        }

        return  l;

    }


}
