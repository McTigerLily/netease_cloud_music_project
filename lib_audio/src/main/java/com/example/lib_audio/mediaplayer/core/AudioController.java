package com.example.lib_audio.mediaplayer.core;

import android.util.EventLog;

import com.example.lib_audio.mediaplayer.db.GreenDaoHelper;
import com.example.lib_audio.mediaplayer.events.AudioFavouriteEvent;
import com.example.lib_audio.mediaplayer.exception.AudioQueueEmptyException;
import com.example.lib_audio.mediaplayer.model.AudioBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Random;

//控制逻辑
public class AudioController {


    //播放方式
    public enum PlayMode{
        /**
         * 列表循环
         */
        LOOP,
        /**
         * 随机
         */
        RANDOM,
        /**
         * 单曲循环
         */
        REPEAT
    }

    public static AudioController getInstance(){
        return SingletonHolder.mAudioController;
    }

    private static class SingletonHolder{
        private static final AudioController mAudioController=new AudioController();
    }

    private AudioPlayer mAudioPlayer;//核心播放器
    private ArrayList<AudioBean> mQueue;//歌曲队列
    private int mQueueIndex;//当前播放歌曲索引
    private PlayMode mPlayMode;

    private AudioController(){
        mAudioPlayer=new AudioPlayer();
        mQueue=new ArrayList<>();
        mQueueIndex=0;
        mPlayMode=PlayMode.LOOP;
    }

    public ArrayList<AudioBean> getmQueue(){
        return mQueue==null?new ArrayList<AudioBean>():mQueue;
    }
    //设置播放队列
    public void setmQueue(ArrayList<AudioBean> queue){
        this.setmQueue(queue,0);
    }
    public void setmQueue(ArrayList<AudioBean> queue,int index){
        mQueue.addAll(queue);
        mQueueIndex=index;
    }
    public void addmQueue(AudioBean bean){
        this.addmQueue(0,bean);
    }

    //添加一个歌曲并开始播放
    public void addmQueue(int index,AudioBean bean){
        if(mQueue==null){
            throw new AudioQueueEmptyException("当前播放队列为空");
        }
        int query=quertAudio(bean);
        if(query<=-1){            //没有添加过
            addCustomAudio(index,bean);
            setmQueueIndex(index);
        }else{
            AudioBean currentBean=getNowPlaying();
            if(!currentBean.id.equals(bean.id)){
                setmQueueIndex(query);
            }
        }
    }

    private void addCustomAudio(int index, AudioBean bean) {
        if (mQueue == null) {
            throw new AudioQueueEmptyException("当前播放队列为空,请先设置播放队列.");
        }
        mQueue.add(index, bean);
    }

    private int quertAudio(AudioBean bean) {
        return 0;
    }

    public PlayMode getmPlayMode() {
        return mPlayMode;
    }
    //设置播放模式
    public void setmPlayMode(PlayMode mPlayMode) {
        this.mPlayMode = mPlayMode;
    }
    //设置播放队列索引
    public void setmQueueIndex(int mQueueIndex) {
        if(mQueue==null){
            throw new AudioQueueEmptyException("当前播放队列为空，请先设置一个播放队列");
        }
        this.mQueueIndex = mQueueIndex;
    }

    public int getmQueueIndex() {
        return mQueueIndex;
    }







    //开始
    public void play(){
        AudioBean bean=getNowPlaying();
        mAudioPlayer.load(bean);
    }
    //暂停
    public void pause(){
        mAudioPlayer.pause();

    }

    //恢复
    public void resume(){
        mAudioPlayer.resume();
    }

    //释放
    public void release(){
        mAudioPlayer.release();
        EventBus.getDefault().unregister(this);
    }
    //播放下一首
    public void next(){
        AudioBean bean=getNextPlaying();
        mAudioPlayer.load(bean);
    }

    //播放上一首
    public void previous(){
        AudioBean bean=getPreviousPlaying();
        mAudioPlayer.load(bean);
    }
    //切换播放暂停
    public void playOrPause(){
        if(isStartState()){
        pause();
        }else if(isPauseState()){
            resume();
        }
    }



    private CustomMediaPlayer.Status getStatus() {
        return mAudioPlayer.getStatus();
    }

    public boolean isStartState() {
        return CustomMediaPlayer.Status.STARTED == getStatus();
    }

    public boolean isPauseState() {
        return CustomMediaPlayer.Status.PAUSED == getStatus();
    }

    /*
    public void changeFavourite){
        if(GreenDaoHelper.selectFavorite(getNowPlaying())!=null){
            //收藏过了，点击取消收藏
            GreenDaoHelper.removeFavorite(getNowPlaying());
            EventBus.getDefault().post(new AudioFavouriteEvent(false));
        }else{
            //没收藏过，点击收藏
            GreenDaoHelper.addFavorite(getNowPlaying());
            EventBus.getDefault().post(new AudioFavouriteEvent(true));
        }
    }*/

    public AudioBean getNowPlaying() {
        if(mQueue!=null&&!mQueue.isEmpty()&&mQueueIndex>=0&&mQueueIndex<mQueue.size()){
            return mQueue.get(mQueueIndex);
        }else{
            throw new AudioQueueEmptyException("当前播放队列为空，请先设置一个播放队列");
        }
    }
    private AudioBean getNextPlaying() {
        switch (mPlayMode){
            case LOOP:
                mQueueIndex=(mQueueIndex+1)%mQueue.size();
                break;
            case RANDOM:
                mQueueIndex=new Random().nextInt(mQueue.size())%mQueue.size();
                break;
            case REPEAT:
                break;
        }
        return getNowPlaying();
    }
    private AudioBean getPreviousPlaying() {
        switch (mPlayMode){
            case LOOP:
                mQueueIndex=(mQueueIndex-1)%mQueue.size();
                break;
            case RANDOM:
                mQueueIndex=new Random().nextInt(mQueue.size())%mQueue.size();
                break;
            case REPEAT:
                break;
        }
        return getNowPlaying();
    }

}
