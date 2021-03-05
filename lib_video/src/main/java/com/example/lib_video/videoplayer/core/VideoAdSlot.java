package com.example.lib_video.videoplayer.core;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.lib_video.videoplayer.core.view.CustomVideoView;
import com.example.lib_video.videoplayer.core.view.VideoFullDialog;

/**
 * 小屏播放逻辑：视频的业务逻辑层
 */
public class VideoAdSlot implements CustomVideoView.VideoPlayerListener {

    private Context mContext;

    /*
    UI
     */
    private CustomVideoView mVideoView;
    private ViewGroup mParentView;

    /*
    Data
     */
    private  String mVideoUrl;
    private SDKSlotListener mSlotListener;//上一层实现并回调到上一层context

    public VideoAdSlot(String videlUrl,SDKSlotListener slotListener){
        mVideoUrl=videlUrl;
        mSlotListener=slotListener;
        mParentView=slotListener.getAdParent();
        mContext=mParentView.getContext();
        initVideoView();
    }

    private void initVideoView() {
        mVideoView=new CustomVideoView(mContext);
        if(mVideoUrl!=null){
            mVideoView.setDataSource(mVideoUrl);
            mVideoView.setListener(this);
        }
        RelativeLayout paddingView = new RelativeLayout(mContext);
        paddingView.setBackgroundColor(mContext.getResources().getColor(android.R.color.black));
        paddingView.setLayoutParams(mVideoView.getLayoutParams());
        mParentView.addView(paddingView);
        mParentView.addView(mVideoView);
    }

    /**
     * 功能：调用CustomVideoView实现
     */
    //是否真正停止
    private boolean isRealPause(){
        if(mVideoView!=null){
            return mVideoView.isRealPause();
        }
        return false;
    }
    //是否完成
    private boolean isComplete(){
        if(mVideoView!=null){
            return mVideoView.isComplete();
        }
        return false;
    }
    //暂停Video的播放
    private void pauseVideo(){
        if(mVideoView!=null){
            mVideoView.seekAndPause(0);
        }
    }

    //恢复Video的播放
    private void resumeVideo(){
        if(mVideoView!=null){
            mVideoView.resume();
        }
    }

    //销毁Video
    public void destory(){
        mVideoView.destroy();
        mVideoView=null;
        mContext=null;
        mVideoUrl=null;
    }


    /**
     * 重写：
     */
    //小屏切换到大屏
    @Override
    public void onClickFullScreenBtn() {
        //变成大屏
        mParentView.removeView(mVideoView);//从容器中移出，一个View只能有一个父容器
        VideoFullDialog dialog=new VideoFullDialog(mContext,mVideoView,
                mVideoUrl,
                mVideoView.getCurrentPosition());
        //回到小屏
        dialog.setListener(new VideoFullDialog.FullToSmallListener() {
            @Override
            public void getCurrentPlayPosition(int position) {
                //回到小屏
                backToSmallMode(position);
            }

            @Override
            public void playComplete() {
                //大屏播放完毕后回到小屏如何处理
                bigPlayComplete();
            }
        });
        //大屏也要回调事件
        dialog.setSlotListener(mSlotListener);
        dialog.show();
    }
    //全屏返回小屏：继续播放
    private void backToSmallMode(int position) {
        if(mVideoView.getParent()==null){
            mParentView.addView(mVideoView);
        }
        mVideoView.isShowFullBtn(true);
        mVideoView.mute(true);
        mVideoView.setListener(this);
        mVideoView.seekAndResume(position); //跳到指定位置继续播放
    }
    //全屏播放完成：回到小屏
    private void bigPlayComplete() {
        if(mVideoView.getParent()==null){
            mParentView.addView(mVideoView);
        }
        mVideoView.isShowFullBtn(true);
        mVideoView.mute(true);
        mVideoView.setListener(this);
        mVideoView.seekAndPause(0);
    }





    @Override
    public void onBufferUpdate(int time) {

    }



    @Override
    public void onClickVideo() {

    }

    @Override
    public void onClickBackBtn() {

    }

    @Override
    public void onClickPlay() {

    }

    @Override
    public void onVideoLoadSuccess() {
        if(mSlotListener!=null){
            mSlotListener.onVideoLoadSuccess();
        }
    }

    @Override
    public void onVideoLoadFailed() {
        if (mSlotListener != null) {
            mSlotListener.onVideoFailed();
        }
    }

    @Override
    public void onVideoCompleted() {
        if (mSlotListener != null) {
            mSlotListener.onVideoComplete();
        }
        mVideoView.setIsRealPause(true);
    }

    //传递消息到appcontext层
    public interface SDKSlotListener {

        ViewGroup getAdParent();

        void onVideoLoadSuccess();

        void onVideoFailed();

        void onVideoComplete();
    }
}
