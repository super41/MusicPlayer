package com.example.administrator.mediaapplication;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.administrator.mediaapplication.main.FileScanner;
import com.example.administrator.mediaapplication.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity  implements FileScanner.OnScanListener{

    LinearLayout mRootView;
    FileScanner mFileScanner;
    ProgressDialog mProgressDialog;
    ListView mLvSong;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mRootView = findViewById(R.id.rootView);
        Utils.registerClickListner(mRootView, onClickListener);
        mLvSong = findViewById(R.id.lv_song);
        mProgressDialog = new ProgressDialog(this);
        mFileScanner = FileScanner.getInstance();
        mFileScanner.registerScanListener(this);
        mediaPlayer = new MediaPlayer();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_tab_scan:
                    mFileScanner.scanFile("/mnt/sdcard/");
                    break;
                case R.id.btn_tab_artist:
                    break;
                case R.id.btn_tab_album:
                    break;
                case R.id.btn_tab_fold:
                    break;

            }
        }
    };

    @Override
    public void onScanStart() {
        mProgressDialog.show();
    }

    @Override
    public void onScanFinish() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.hide();
                Map<String,List<String>> map =mFileScanner.getAllSong();
                final List<String> allList = new ArrayList<>();
                for(List<String> list:map.values()){
                    allList.addAll(list);
                }
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,allList);
                mLvSong.setAdapter(arrayAdapter);
                mLvSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        try {
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource(allList.get(position));
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


    }
}
