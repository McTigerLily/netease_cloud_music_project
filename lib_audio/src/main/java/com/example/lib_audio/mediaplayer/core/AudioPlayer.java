package com.example.lib_audio.mediaplayer.core;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import com.example.lib_audio.app.AudioHelper;
import com.example.lib_audio.mediaplayer.events.AudioCompleteEvent;
import com.example.lib_audio.mediaplayer.events.AudioErrorEvent;
import com.example.lib_audio.mediaplayer.events.AudioLoadEvent;
import com.example.lib_audio.mediaplayer.events.AudioPauseEvent;
import com.example.lib_audio.mediaplayer.events.AudioReleaseEvent;
import com.example.lib_audio.mediaplayer.events.AudioStartEvent;
import com.example.lib_audio.mediaplayer.model.AudioBean;

import org.greenrobot.eventbus.EventBus;

public class AudioPlayer implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener,MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,AudioFocusManager.AudioFocusListener
{
    private static final String TAG = "AudioPlayer";
    private static final int TIME_MSG = 0x01;
    private static final int TIME_INVAL = 100;

    private CustomMediaPlayer mMediaPlayer;
    private WifiManager.WifiLock mWifiLock;//后台保活
    private AudioFocusManager mAudioFocusManager;//音频焦点监听
    private Handler mHandler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case TIME_MSG:
                    break;
            }
        }
    };

    private boolean isPauseByFocusLossTransient;

    public AudioPlayer(){
        init();
    }
    private void init(){
        mMediaPlayer=new CustomMediaPlayer();
        //mMediaPlayer.setWakeMode(AudioHelper.getContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnErrorListener(this);

        mWifiLock=((WifiManager)AudioHelper.getContext().
                getApplicationContext().
                getSystemService(Context.WIFI_SERVICE)).
                createWifiLock(WifiManager.WIFI_MODE_FULL,TAG);
        mAudioFocusManager=new AudioFocusManager(AudioHelper.getContext(),this);
    }


    //对外提供的加载方法
    public void load(AudioBean audioBean){
        try{
            //正常加载逻辑
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(audioBean.mUrl);
            mMediaPlayer.prepareAsync();//会在另外线程中加载，一旦准备好会调用onPrepared方法，里面并调用start方法
            //对外发送load事件
            EventBus.getDefault().post(new AudioLoadEvent(audioBean));
        }catch(Exception e){
            //对外发送error事件
            EventBus.getDefault().post(new AudioErrorEvent());
        }
    }

    //内部开始播放
    private void start(){
        if(!mAudioFocusManager.requestAudioFocus()){
            Log.e(TAG,"获取音频焦点失败");
        }
        mMediaPlayer.start();
        mWifiLock.acquire();
        //对外发送start事件
        EventBus.getDefault().post(new AudioStartEvent());
    }

    //对外提供暂停
    public void pause(){
        if(getStatus()==CustomMediaPlayer.Status.STARTED){
            mMediaPlayer.pause();
            mWifiLock.release();
        }
        //释放音频焦点
        if(mAudioFocusManager!=null){
            mAudioFocusManager.abandonAudioFocus();
        }
        //发送暂停事件
        EventBus.getDefault().post(new AudioPauseEvent());
    }

    //获取状态
    public CustomMediaPlayer.Status getStatus() {
        if(mMediaPlayer!=null){
            return mMediaPlayer.getState();
        }
        return CustomMediaPlayer.Status.STOPPED;
    }

    //对外提供恢复
    public void resume(){
        if(getStatus()==CustomMediaPlayer.Status.PAUSED){
            start();
        }
    }

    //清空播放器占用的资源
    public void release(){
        if(mMediaPlayer==null){
            return;
        }
        mMediaPlayer.release();
        mMediaPlayer=null;
        if(mAudioFocusManager!=null){
            mAudioFocusManager.abandonAudioFocus();
        }
        if(mWifiLock.isHeld()){
            mWifiLock.release();
        }
        mWifiLock=null;
        mAudioFocusManager=null;
        //发送释放事件
        EventBus.getDefault().post(new AudioReleaseEvent());
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //缓存进度回调
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //播放完毕回调
        EventBus.getDefault().post(new AudioCompleteEvent());
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        EventBus.getDefault().post(new AudioErrorEvent());
        return true;//一旦回调就不回调onCompletion方法，自己处理Error，否则会再次回调onCompletion

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //准备完毕，不需要发送事件，直接调用start方法就好，start会发送事件
        start();
    }


    @Override
    public void audioFocusGrant() {
        //再次获取音频焦点
        setVolume(1.0f,1.0f);
        if(isPauseByFocusLossTransient){
            resume();
        }
        isPauseByFocusLossTransient=false;
    }

    private void setVolume(float leftVol, float rightVol) {
        //左声道和右声道
        if(mMediaPlayer!=null){
            mMediaPlayer.setVolume(leftVol,rightVol);
        }

    }

    @Override
    public void audioFocusLoss() {
        //永久失去焦点
        pause();
    }

    @Override
    public void audioFocusLossTransient() {
        //短暂性失去焦点，比如接电话
        pause();
        isPauseByFocusLossTransient=true;
    }

    @Override
    public void audioFocusLossDuck() {
        //瞬间失去焦点， 比如通知
        setVolume(0.5f,0.5f);
    }
}
