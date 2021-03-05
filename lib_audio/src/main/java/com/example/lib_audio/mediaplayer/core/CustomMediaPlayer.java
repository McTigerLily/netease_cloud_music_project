package com.example.lib_audio.mediaplayer.core;

import android.media.MediaPlayer;

import java.io.IOException;

public class CustomMediaPlayer extends MediaPlayer implements MediaPlayer.OnCompletionListener {
    private OnCompletionListener mCompletionListener;
    private Status mState;

    public enum Status{
        IDLE,INITIALIZED,STARTED,PAUSED,STOPPED,COMPLETED;
    }



    public CustomMediaPlayer(){
        super();
        mState=Status.IDLE;
        super.setOnCompletionListener(this);
    }

    @Override
    public void reset() {
        super.reset();
        mState=Status.IDLE;
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, IllegalStateException, SecurityException {
        super.setDataSource(path);
        mState=Status.INITIALIZED;

    }

    @Override
    public void start() throws IllegalStateException {
        super.start();
        mState=Status.STARTED;
    }

    @Override
    public void pause() throws IllegalStateException {
        super.pause();
        mState=Status.PAUSED;
    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        mState=Status.STOPPED;
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        mState=Status.COMPLETED;
    }

    public Status getState(){
        return mState;
    }
    public boolean isComplete(){
        return mState==Status.COMPLETED;
    }


    public void setCompletedListener(OnCompletionListener listener){
        this.mCompletionListener=listener;
    }
}
