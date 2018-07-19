package com.example.administrator.mediaapplication.main;

import android.app.Application;

import com.example.administrator.mediaapplication.database.MediaDataBase;
import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by Administrator on 2018/7/6.
 */

public class MediaApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(FlowConfig.builder(this)
                .addDatabaseConfig(DatabaseConfig.builder(MediaDataBase.class)
                        .databaseName("mediadatabase")
                        .build())
                .build());
        FileScanner.getInstance();
    }
}
