package com.example.administrator.mediaapplication.ui;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.administrator.mediaapplication.R;
import com.example.administrator.mediaapplication.database.MediaItem;
import com.example.administrator.mediaapplication.database.MediaItem_Table;
import com.example.administrator.mediaapplication.main.FileScanner;
import com.example.administrator.mediaapplication.ui.adapter.SongAdapter;
import com.example.administrator.mediaapplication.util.Utils;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.IOException;

import static com.example.administrator.mediaapplication.ui.adapter.SongAdapter.TYPE_ALBUM;
import static com.example.administrator.mediaapplication.ui.adapter.SongAdapter.TYPE_ARTIST;
import static com.example.administrator.mediaapplication.ui.adapter.SongAdapter.TYPE_FOLDER;

public class MainActivity extends AppCompatActivity implements FileScanner.OnScanListener {

    LinearLayout mRootView;
    FileScanner mFileScanner;
    ProgressDialog mProgressDialog;
    ListView mLvSong;
    MediaPlayer mediaPlayer;


    SongAdapter mSongAdapter;
    long startScanTime;
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


        mSongAdapter = new SongAdapter(MainActivity.this, null);
        mLvSong.setAdapter(mSongAdapter);
        mLvSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mSongAdapter.getIsInDir()) {
                    switch (mSongAdapter.getCurrentType()) {
                        case TYPE_FOLDER:
                            String parentPath = mSongAdapter.mList.get(position).getParentPath();
                            mSongAdapter.mList = SQLite.select().from(MediaItem.class).where(MediaItem_Table.parentPath.eq(parentPath)).and(MediaItem_Table.enable.eq(true)).queryList();
                            mSongAdapter.setIsInDir(false);
                            mSongAdapter.notifyDataSetChanged();
                            break;
                        case TYPE_ARTIST:
                            String artist = mSongAdapter.mList.get(position).getArtist();
                            mSongAdapter.mList = SQLite.select().from(MediaItem.class).where(artist == null ? MediaItem_Table.artist.isNull() : MediaItem_Table.artist.eq(artist)).and(MediaItem_Table.enable.eq(true)).queryList();
                            mSongAdapter.setIsInDir(false);
                            mSongAdapter.notifyDataSetChanged();
                            break;
                        case TYPE_ALBUM:
                            String album = mSongAdapter.mList.get(position).getAlbum();
                            mSongAdapter.mList = SQLite.select().from(MediaItem.class).where(album == null ? MediaItem_Table.album.isNull() : MediaItem_Table.album.eq(album)).and(MediaItem_Table.enable.eq(true)).queryList();
                            mSongAdapter.setIsInDir(false);
                            mSongAdapter.notifyDataSetChanged();
                            break;
                    }
                } else {
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(mSongAdapter.mList.get(position).getPath());
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_tab_scan:
                    mFileScanner.scanFile("/mnt/sdcard/");
                    mFileScanner.scanFile("/storage/609D-0FED/");
                    mFileScanner.scanFile("/storage/609F-01BE/");
                    mFileScanner.scanFile("/storage/8605-11F3/");
                    mFileScanner.scanFile("/storage/852A-15FC/");
                    break;
                case R.id.btn_tab_artist: {
                    mSongAdapter.mList = SQLite.select(MediaItem_Table.artist).distinct().from(MediaItem.class).where(MediaItem_Table.enable.eq(true)).queryList();
                    Log.d("xjp", "onClick: " + mSongAdapter.mList.size());
                    mSongAdapter.setIsInDir(true);
                    mSongAdapter.setType(SongAdapter.TYPE_ARTIST);
                    mSongAdapter.notifyDataSetChanged();
                }
                break;
                case R.id.btn_tab_album: {
                    mSongAdapter.mList = SQLite.select(MediaItem_Table.album).distinct().from(MediaItem.class).where(MediaItem_Table.enable.eq(true)).queryList();
                    Log.d("xjp", "onClick: " + mSongAdapter.mList.size());
                    mSongAdapter.setIsInDir(true);
                    mSongAdapter.setType(SongAdapter.TYPE_ALBUM);
                    mSongAdapter.notifyDataSetChanged();
                }
                break;
                case R.id.btn_tab_fold:
                    mSongAdapter.mList = SQLite.select(MediaItem_Table.parentPath).distinct().from(MediaItem.class).where(MediaItem_Table.enable.eq(true)).queryList();
                    Log.d("xjp", "onClick: " + mSongAdapter.mList.size());
                    mSongAdapter.setIsInDir(true);
                    mSongAdapter.setType(TYPE_FOLDER);
                    mSongAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };


    @Override
    public void onScanStart() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("xjp", "run: show");
                mProgressDialog.show();
                startScanTime = System.currentTimeMillis();
            }
        });

    }

    @Override
    public void onScanFinish() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                mProgressDialog.cancel();
                Log.d("xjp", "run: cost "+((now -startScanTime)/1000) +"s");
                mSongAdapter.mList = SQLite.select().from(MediaItem.class).where(MediaItem_Table.enable.eq(true)).queryList();
                mSongAdapter.notifyDataSetChanged();
            }
        });
    }


}
