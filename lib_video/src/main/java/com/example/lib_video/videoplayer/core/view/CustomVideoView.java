package com.example.lib_video.videoplayer.core.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.example.lib_video.R;

public class CustomVideoView extends RelativeLayout implements View.OnClickListener, TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PLAYING = 1;
    private static final int STATE_PAUSING = 2;
    private static final int LOAD_TOTAL_COUNT = 3;
    /**
     * UI
     */
    private RelativeLayout mPlayerView;
    private TextureView mVideoView;
    private Button mMiniPlayBtn;
    private ImageView mFullBtn;
    private ImageView mLoadingBar;
    private AudioManager audioManager;
    private Surface videoSurface;

    /**
     * Data
     */
    private String mUrl;
    private boolean isMute;
    private int mScreenWidth, mDestationHeight;

    /**
     * Status状态保护
     */
    private boolean canPlay = true;
    private boolean mIsRealPause;
    private boolean mIsComplete;
    private int mCurrentCount;
    private int playerState = STATE_IDLE;

    private ScreenEventReceiver mScreenReceiver;
    private MediaPlayer mediaPlayer;
    private VideoPlayerListener mListener;


    public CustomVideoView(Context context) {
        super(context);
        audioManager=(AudioManager)getContext().getSystemService(Context.AUDIO_SERVICE);
        initData();
        initView();
        registerBroadcastReceiver();
    }
    public void initView(){
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        mPlayerView = (RelativeLayout) inflater.inflate(R.layout.video_player_layout, this);
        mVideoView = mPlayerView.findViewById(R.id.xadsdk_player_video_textureView);
        mVideoView.setOnClickListener(this);
        mVideoView.setKeepScreenOn(true);
        mVideoView.setSurfaceTextureListener(this);
        initSmallLayoutMode(); //小屏状态下显示的view
    }

    //小屏状态下显示的view
    private void initSmallLayoutMode() {
        LayoutParams params = new LayoutParams(mScreenWidth, mDestationHeight);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mPlayerView.setLayoutParams(params);

        mMiniPlayBtn = mPlayerView.findViewById(R.id.xadsdk_small_play_btn);
        mFullBtn = mPlayerView.findViewById(R.id.xadsdk_to_full_view);
        mLoadingBar = mPlayerView.findViewById(R.id.loading_bar);
        mMiniPlayBtn.setOnClickListener(this);
        mFullBtn.setOnClickListener(this);
    }

    private void initData(){
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mDestationHeight = (int) (mScreenWidth * (9 / 16.0f));
    }

    private void registerBroadcastReceiver(){
        if (mScreenReceiver == null) {
            mScreenReceiver = new ScreenEventReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            getContext().registerReceiver(mScreenReceiver, filter);
        }
    }

    private void unRegisterBroadcastReceiver(){
        if (mScreenReceiver != null) {
            getContext().unregisterReceiver(mScreenReceiver);
        }
    }

    private synchronized void checkMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }


    /**
     * 布局：
     */
    //显示暂加载页面布局
    private void showLoadingView() {
        mFullBtn.setVisibility(View.GONE);
        mLoadingBar.setVisibility(View.VISIBLE);
        AnimationDrawable anim = (AnimationDrawable) mLoadingBar.getBackground();
        anim.start();
        mMiniPlayBtn.setVisibility(View.GONE);
    }
    //显示播放页面布局
    private void showPlayView() {
        mLoadingBar.clearAnimation();
        mLoadingBar.setVisibility(View.GONE);
        mMiniPlayBtn.setVisibility(View.GONE);
    }
    //显示暂停页面布局
    private void showPlayOrPauseView(boolean show) {
        mFullBtn.setVisibility(show ? View.VISIBLE : View.GONE);
        mMiniPlayBtn.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoadingBar.clearAnimation();
        mLoadingBar.setVisibility(View.GONE);
    }


    /**
     * 状态：进入播放状态时的状态更新
     */
    private void entryResumeState() {
        canPlay = true;
        setCurrentPlayState(STATE_PLAYING);
        setIsRealPause(false);
        setIsComplete(false);
    }


    public  void setCurrentPlayState(int state) {
        playerState=state;
    }

    public  void setIsRealPause(boolean isComplete) {
        mIsComplete = isComplete;
    }

    public  void setIsComplete(boolean isRealPause) {
        mIsRealPause = isRealPause;

    }

    public void setListener(VideoPlayerListener listener){
        mListener=listener;
    }


    public void setDataSource(String url) {
        this.mUrl = url;
    }

    public int getCurrentPosition() {
        if (this.mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public boolean isRealPause() {
        return mIsRealPause;
    }

    public boolean isComplete() {
        return mIsComplete;
    }
    public void isShowFullBtn(boolean isShow) {
        mFullBtn.setImageResource(isShow ? R.drawable.xadsdk_ad_mini : R.drawable.xadsdk_ad_mini_null);
        mFullBtn.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
    //静音，true代表静音
    public void mute(boolean mute) {
        isMute = mute;
        if (mediaPlayer != null && this.audioManager != null) {
            float volume = isMute ? 0.0f : 1.0f;
            mediaPlayer.setVolume(volume, volume);
        }
    }


    public boolean isPlaying() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return true;
        }
        return false;
    }


    /**
     * 功能：
     */
    //加载功能
    public void load() {
        if (this.playerState != STATE_IDLE) {
            return;
        }
        showLoadingView();
        try {
            setCurrentPlayState(STATE_IDLE);
            checkMediaPlayer();//检查有没有MediaPlayer，如果没有就新建一个
            mute(true);
            mediaPlayer.setDataSource(this.mUrl);
            mediaPlayer.prepareAsync(); //开始异步加载
        } catch (Exception e) {
            stop(); //error以后重新调用stop加载
        }
    }

    //恢复功能，播放视频
    public void resume() {
        if (this.playerState != STATE_PAUSING) {
            return;
        }
        if (!isPlaying()) {
            entryResumeState();
            mediaPlayer.setOnSeekCompleteListener(null);
            mediaPlayer.start();
            showPlayOrPauseView(true);
        } else {
            showPlayOrPauseView(false);
        }
    }

    //定位并恢复功能,跳到指定点播放视频，从小屏到大屏会调用这个方法
    public void seekAndResume(int position) {
        if (mediaPlayer != null) {
            showPlayOrPauseView(true);
            entryResumeState();
            mediaPlayer.seekTo(position);
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
        }
    }


    //暂停功能
    public void pause() {
        if (this.playerState != STATE_PLAYING) {
            return;
        }
        setCurrentPlayState(STATE_PAUSING);
        if (isPlaying()) {
            mediaPlayer.pause();
            if (!this.canPlay) {
                this.mediaPlayer.seekTo(0);
            }
        }
        showPlayOrPauseView(false);
    }

    //跳转到指定位置并暂停功能
    public void seekAndPause(int position) {
        if (this.playerState != STATE_PLAYING) {
            return;
        }
        showPlayOrPauseView(false);
        setCurrentPlayState(STATE_PAUSING);
        if (isPlaying()) {
            mediaPlayer.seekTo(position);
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    mediaPlayer.pause();
                }
            });
        }
    }

    //播放完毕回到初始状态的功能
    public void playBack() {
        setCurrentPlayState(STATE_PAUSING);
        if (mediaPlayer != null) {
            mediaPlayer.setOnSeekCompleteListener(null);
            mediaPlayer.seekTo(0);
            mediaPlayer.pause();
        }
        this.showPlayOrPauseView(false);
    }

    //停止功能,释放MediaPlayer
    public void stop() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.reset();
            this.mediaPlayer.setOnSeekCompleteListener(null);
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
        setCurrentPlayState(STATE_IDLE);
        if (mCurrentCount < LOAD_TOTAL_COUNT) { //满足重新加载的条件
            mCurrentCount += 1;
            load();
        } else {
            showPlayOrPauseView(false); //显示暂停状态
        }
    }


    //销毁功能，销毁MediaPlayer
    public void destroy() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.setOnSeekCompleteListener(null);
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
        setCurrentPlayState(STATE_IDLE);
        mCurrentCount = 0;
        setIsComplete(false);
        setIsRealPause(false);
        unRegisterBroadcastReceiver();
        showPlayOrPauseView(false); //除了播放和loading外其余任何状态都显示pause
    }

    //功能：全屏模式下暂停，全屏不显示暂停状态,后续可以整合，不必单独出一个方法
    public void pauseForFullScreen() {
        if (playerState != STATE_PLAYING) {
            return;
        }
        setCurrentPlayState(STATE_PAUSING);
        if (isPlaying()) {
            mediaPlayer.pause();
            if (!this.canPlay) {
                mediaPlayer.seekTo(0);
            }
        }
    }
    /**
     * 重载方法
     */
    @Override
    public void onClick(View v) {
        if(v==mMiniPlayBtn){
            if(playerState==STATE_PAUSING){
                resume();
            }else{
                load();
            }
        }
        else if(v==mFullBtn){
            if(mListener!=null){
                mListener.onClickFullScreenBtn();
            }
        }
        else if(v==mVideoView){
            if(mListener!=null){
                mListener.onClickVideo();
            }
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        if(visibility==VISIBLE){
            //可见时
            if(isRealPause()||isComplete()){
                //主动暂停
                pause();
            }else{
                //被动暂停
                resume();
            }
        }else{
            //不可见时
            pause();
        }
    }

    //Surface可用状态的操作
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        videoSurface=new Surface(surface);
        checkMediaPlayer();
        mediaPlayer.setSurface(videoSurface);
        //--可以加载视频了--
        load();
    }

    //MediaPlayer加载成功的操作：开始播放，并告诉上一层我成功了
    @Override
    public void onPrepared(MediaPlayer mp) {
        showPlayView();
        if(mediaPlayer!=null){
            mCurrentCount=0;
            setCurrentPlayState(STATE_PLAYING);
            resume();
            if(mListener!=null){
                mListener.onVideoLoadSuccess();
            }
        }
    }

    //播放完毕的操作：置变量为初始状态
    @Override
    public void onCompletion(MediaPlayer mp) {
        playBack();
        setIsComplete(true);
        setIsRealPause(true);
        if(mListener!=null){
            mListener.onVideoCompleted();
        }
    }


    //播放出错的操作：
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        setCurrentPlayState(STATE_ERROR);
        if(mCurrentCount>=LOAD_TOTAL_COUNT){
            //真正出错
            showPlayOrPauseView(false);
            if(mListener!=null){
                mListener.onVideoLoadFailed();
            }
        }
        else{
            //出错重试
            stop();
        }
        return false;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }




    /**
     * 监听锁屏事件的广播接收器
     */
    private class ScreenEventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //主动锁屏时 pause, 主动解锁屏幕时，resume
            switch (intent.getAction()) {
                //action为用户可见，处于前台，亮屏
                case Intent.ACTION_USER_PRESENT:
                    if (playerState == STATE_PAUSING) {
                        if (isRealPause()) {
                            //手动点的暂停，回来后还暂停
                            pause();
                        } else {
                            resume();
                        }
                    }
                    break;
                    //灭屏
                case Intent.ACTION_SCREEN_OFF:
                    if (playerState == STATE_PLAYING) {
                        pause();
                    }
                    break;
            }
        }
    }



    public interface VideoPlayerListener {

        void onBufferUpdate(int time);

        void onClickFullScreenBtn();

        void onClickVideo();

        void onClickBackBtn();

        void onClickPlay();

        void onVideoLoadSuccess();

        void onVideoLoadFailed();

        void onVideoCompleted();
    }
}
