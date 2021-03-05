package com.example.netease_cloud_music_module.application;

import android.app.Application;
import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.lib_audio.app.AudioHelper;
import com.example.lib_share.ShareManager;


public class CloudMusicApplication extends Application {
    private static CloudMusicApplication mApplication = null;


    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        //音频SDK初始化
        AudioHelper.init(this);
        //ShareManager.init(this);
        ARouter.init(this);

    }

    public static CloudMusicApplication getInstance() {
        return mApplication;
    }
}
