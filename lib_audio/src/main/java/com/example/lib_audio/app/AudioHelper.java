package com.example.lib_audio.app;

import android.app.Activity;
import android.content.Context;

import com.example.lib_audio.mediaplayer.core.AudioController;
import com.example.lib_audio.mediaplayer.core.MusicService;
import com.example.lib_audio.mediaplayer.db.GreenDaoHelper;
import com.example.lib_audio.mediaplayer.model.AudioBean;
import com.example.lib_audio.view.MusicPlayerActivity;

import java.util.ArrayList;


public final class AudioHelper {
    private static Context mContext;

    public static void init(Context context){
        mContext=context;
        //GreenDaoHelper.initDatabase();

    }
    public static Context getContext(){return mContext;}

    public static void startMusicService(ArrayList<AudioBean> mLists) {
        MusicService.startMusicService(mLists);
    }

    public static void addAudio(Activity activity,AudioBean bean) {
        AudioController.getInstance().addmQueue(bean);
        MusicPlayerActivity.start(activity);
    }
}
