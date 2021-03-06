package com.hehr.lap;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hehr.lap.bean.AudioBean;
import com.hehr.lap.listener.InitializeListener;
import com.hehr.lap.listener.QueryListener;
import com.hehr.lap.listener.ScanListener;

import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button scanPath;//扫描指定路径

    private Button scanDefaultPath;//扫描外置存储

    private Button setPath;//设置文件路径

    private TextView resultView;//文本框

    private EditText editText;//输入框


    private static final String TAG = MainActivity.class.getSimpleName();


    private boolean isInit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        scanPath = (Button) findViewById(R.id.button_scan_path);
        scanDefaultPath = (Button) findViewById(R.id.button_scan_default);
        setPath = (Button) findViewById(R.id.button_set_path);
        resultView = (TextView) findViewById(R.id.tv);
        editText = (EditText) findViewById(R.id.editText);


        Lap.getInstance().setEntryNumber(500);//设置单个目录下解析文件个数限制

        Lap.getInstance().initialize(this, new InitializeListener() {
            @Override
            public void onError(Error error) {
                isInit = false;
            }

            @Override
            public void onInit() {
                isInit = true;
            }
        });

        scanDefaultPath.setOnClickListener(this);
        scanPath.setOnClickListener(this);

    }


    private static final String SD_CARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

    @Override
    public void onClick(View v) {
        if (v.getId() == scanDefaultPath.getId()) {
            if (isInit) {
                Lap.getInstance().scan(SD_CARD_PATH, new ScanListener() {

                    @Override
                    public void onResult(List<AudioBean> list) {
                        Log.d(TAG, list.toString());
                    }

                    @Override
                    public void onError(Error error) {
                        Log.e(TAG, error.toString());
                    }
                });
            }
        } else if (v.getId() == scanPath.getId()) {

            Log.e(TAG, "hahahah");
            Lap.getInstance().optDB("蔡健雅", "红色高跟鞋", "若你碰到他", new QueryListener() {
                @Override
                public void onResult(List<AudioBean> list) {

                    Log.e(TAG, "list size :" + list.size());
                    for (AudioBean b : list) {
                        Log.e(TAG, "b:" + b.toString());
                    }
                }

                @Override
                public void onError(Error error) {
                    Log.e(TAG, error.toString());
                }
            });
        }
    }


}
