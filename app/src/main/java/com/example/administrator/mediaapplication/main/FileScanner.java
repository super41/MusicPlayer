package com.example.administrator.mediaapplication.main;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

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

    private FileScanner() {
        mPendingScanDir = new Stack<>();
        mAllSong = new HashMap<>();
        mWorkThread = new HandlerThread("work");
        mWorkThread.start();
        mWorkHandler = new Handler(mWorkThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                scan();
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
        mPendingScanDir.push(dirPath);
        if (!isScaning) {
            isScaning = true;
            mWorkHandler.obtainMessage().sendToTarget();
            if (mOnScanListener != null)
                mOnScanListener.onScanStart();
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
            mWorkHandler.obtainMessage().sendToTarget();
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
                    //if user in db
                    if(!exitInDb){
                        Metrre mt = mt(filePath);
                        mediaFile name = mt.name;
                        mediaFile source = filePath;
                        mediaFile airtrist = mt.art;
                        ...
                        save in db;
                    }
                    mAllSong.put(dirPath, mTempList);
                }
            }
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
