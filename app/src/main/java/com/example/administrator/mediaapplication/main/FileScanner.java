package com.example.administrator.mediaapplication.main;

import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.example.administrator.mediaapplication.database.MediaItem;
import com.example.administrator.mediaapplication.database.MediaItem_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by Administrator on 2018/7/6.
 */

public class FileScanner {
    final String TAG = getClass().getSimpleName();
    private static FileScanner INSTANCE = new FileScanner();
    Stack<String> mPendingScanDir;
    Map<String, List<String>> mAllSong;
    OnScanListener mOnScanListener;
    boolean isScaning;
    HandlerThread mWorkThread;
    Handler mWorkHandler;

    final int MSG_SCAN_START=0;
    final int MSG_SCAN_NEXT=1;
    private FileScanner() {
        mPendingScanDir = new Stack<>();
        mAllSong = new HashMap<>();
        mWorkThread = new HandlerThread("work");
        mWorkThread.start();
        mWorkHandler = new Handler(mWorkThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case MSG_SCAN_START:
                        String dirPath = (String) msg.obj;
                        scanStart(dirPath);
                        break;
                    case MSG_SCAN_NEXT:
                        scan();
                        break;
                }
            }
        };
    }

    public static FileScanner getInstance() {
        return INSTANCE;
    }

    public void registerScanListener(OnScanListener onScanListener) {
        mOnScanListener = onScanListener;
    }

    public void scanFile(String dirPath) {
        mWorkHandler.obtainMessage(MSG_SCAN_START,dirPath).sendToTarget();
    }

    private void scanStart(String dirPath){
        mPendingScanDir.push(dirPath);
        if (!isScaning) {
            isScaning = true;
            if (mOnScanListener != null) {
                mOnScanListener.onScanStart();
            }
            SQLite.update(MediaItem.class).set(MediaItem_Table.enable.eq(false)).execute();
            mWorkHandler.obtainMessage(MSG_SCAN_NEXT).sendToTarget();
        }
    }

    public void scanFile(File dir) {
        scanFile(dir.getAbsolutePath());
    }

    public Map<String, List<String>> getAllSong() {
        return mAllSong;
    }

    private void scan() {
        if (mPendingScanDir.size() > 0) {
            String dirPath = mPendingScanDir.pop();
            Log.d(TAG, "scan: " + dirPath);
            scanDir(dirPath);
        }
        if (!mPendingScanDir.isEmpty())
            mWorkHandler.obtainMessage(MSG_SCAN_NEXT).sendToTarget();
        else {
            isScaning = false;
            if (mOnScanListener != null)
                mOnScanListener.onScanFinish();
        }
    }

    private void scanDir(String dirPath) {
        File dir = new File(dirPath);
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                List<String> mTempDir = new ArrayList<>();
                List<String> mTempList = new ArrayList<>();
                for (File f : files) {
                    if (isNoMedia(f))
                        return;

                    if (f.isDirectory()) {

                        if (isAcceptDir(dir))
                            mTempDir.add(f.getAbsolutePath());

                    } else {

                        if (isAudio(f)) {
                            mTempList.add(f.toString());
                            Log.d(TAG, "scan: get " + f.toString());
                        } else {
                            Log.e(TAG, "scan: ignore " + f.toString());
                        }
                    }
                }

                mPendingScanDir.addAll(mTempDir);

                if (mTempList.size() > 0) {
                    //search weather in db
                    for (String path : mTempList) {
                        addInDatabase(path);
                    }
                }
                mAllSong.put(dirPath, mTempList);
            }
        }
    }

    private boolean addInDatabase(String path) {
        MediaItem mediaItem = SQLite.select().from(MediaItem.class).where(MediaItem_Table.path.eq(path)).querySingle();
        if (mediaItem == null) {
            Log.d(TAG, "scanDir: add in db " + path);
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            try {
                mmr.setDataSource(path);
                // api level 10, 即从GB2.3.3开始有此功能
                String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                // 专辑名
                String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                // 媒体格式
                String mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
                // 艺术家
                String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                // 播放时长单位为毫秒
                String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                // 从api level 14才有，即从ICS4.0才有此功能
                String bitrate = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
                // 路径
                String date = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);

                Log.d(TAG, "scanDir: " + "path：" + path + "\t" + "title: " + title + "\t" + "artist: " + artist + "\t" + "album:" + album
                        + "\t" + "duration: " + duration + "\t" + "mine: " + mime + "\t" + "bitrate: " + bitrate + "\t" + "date: " + date +"\t"+"enable: true"
                );
                MediaItem m = new MediaItem(path, new File(path).getParent(), title, artist, album, Long.parseLong(duration),true);
                m.save();
                return true;
            } catch (RuntimeException e) {
                return false;
            }
        } else {
            mediaItem.setEnable(true);
            mediaItem.update();
            return false;
        }
    }

    private boolean isAcceptDir(File dir) {
        if (dir.getName().startsWith(".")) {
            return false;
        }
        return true;
    }

    private boolean isNoMedia(File f) {
        if (TextUtils.equals(f.getName(), ".nomedia")) {
            return true;
        }
        return false;
    }


    private boolean isAudio(String path) {
        String[] audioFormat = {".mp3", ".wma", "wmv", ".ape"};
        for (String s : audioFormat) {
            if (path.endsWith(s))
                return true;
        }
        return false;
    }

    private boolean isAudio(File file) {
        String[] audioFormat = {".mp3", ".wma", "wmv", ".ape", ".aac"};
        String path = file.getPath();
        for (String s : audioFormat) {
            if (path.endsWith(s))
                return true;
        }
        return false;
    }

    public interface OnScanListener {
        void onScanStart();

        void onScanFinish();
    }
}
