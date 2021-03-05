package com.example.lib_video.videoplayer.core;

import android.view.ViewGroup;

/**
 * 外观类，与调用方进行通信，接收app层的参数
 */
public class VideoAdContext implements VideoAdSlot.SDKSlotListener {
    private ViewGroup mParentView;
    private VideoAdSlot mAdSlot;
    private String mInstance;
    private VideoContextInterface mListener;

    public VideoAdContext(ViewGroup parentView,String instance){
        this.mParentView=parentView;
        this.mInstance=instance;
        init();
    }

    //核心：初始化Slot
    private void init() {
        if(mInstance!=null){
            //传进来的地址不为空
            mAdSlot=new VideoAdSlot(mInstance,this);
        }else{
            //传进来的地址为空
            mAdSlot=new VideoAdSlot(null,this);
            if(mListener!=null){
                mListener.onVideoFailed();
            }
        }
    }
    public void destroy() {
        mAdSlot.destory();
    }

    public void setAdResultListener(VideoContextInterface listener) {
        this.mListener = listener;
    }

    @Override
    public ViewGroup getAdParent() {
        return mParentView;
    }

    @Override
    public void onVideoLoadSuccess() {
        if (mListener != null) {
            mListener.onVideoSuccess();
        }
    }

    @Override
    public void onVideoFailed() {
        if (mListener != null) {
            mListener.onVideoFailed();
        }
    }

    @Override
    public void onVideoComplete() {
        if (mListener != null) {
            mListener.onVideoComplete();
        }
    }

    /**
     * 与外界通信的接口
     */
    private interface VideoContextInterface {

        void onVideoSuccess();

        void onVideoFailed();

        void onVideoComplete();
    }

}
