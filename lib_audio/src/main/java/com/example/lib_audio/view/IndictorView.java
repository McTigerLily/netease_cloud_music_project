package com.example.lib_audio.view;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.ViewPager;

import com.example.lib_audio.R;
import com.example.lib_audio.mediaplayer.core.AudioController;
import com.example.lib_audio.mediaplayer.events.AudioLoadEvent;
import com.example.lib_audio.mediaplayer.events.AudioPauseEvent;
import com.example.lib_audio.mediaplayer.events.AudioStartEvent;
import com.example.lib_audio.mediaplayer.model.AudioBean;
import com.example.lib_audio.view.adapter.MusicPagerAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * 音乐播放页面的唱针功能
 */
public class IndictorView extends RelativeLayout implements ViewPager.OnPageChangeListener {

    private Context mContext;
    /*
     * view相关
     */
    private ImageView mImageView;
    private ViewPager mViewPager;
    private MusicPagerAdapter mAdapter;
    /*
     * data
     */
    private AudioBean mAudioBean; //当前播放歌曲
    private ArrayList<AudioBean> mQueue; //播放队列
    public IndictorView(Context context) {
        this(context,null);
    }

    public IndictorView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public IndictorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        EventBus.getDefault().register(this);
        initData();
    }

    private void initData() {
        mQueue = AudioController.getInstance().getmQueue();
        mAudioBean = AudioController.getInstance().getNowPlaying();
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    private void initView() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.indictor_view, this);
        mImageView = rootView.findViewById(R.id.tip_view);
        mViewPager = rootView.findViewById(R.id.view_pager);
        mViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mAdapter=new MusicPagerAdapter(mQueue,mContext);
        mViewPager.setAdapter(mAdapter);
        showLoadView(true);
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        //页面被选中，播放歌曲
        AudioController.getInstance().setmQueueIndex(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

        //判断状态
        switch (state){
            case ViewPager.SCROLL_STATE_IDLE:
                //滑动结束状态/没有滑动
                showPlayView();
                break;
            case ViewPager.SCROLL_STATE_DRAGGING:
                showPauseView();
                //滑动过程中
                break;
            case ViewPager.SCROLL_STATE_SETTLING:
                break;

        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoadEvent(AudioLoadEvent event) {
        //更新viewpager为加载状态
        mAudioBean = event.mAudioBean;
        showLoadView(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioStartEvent(AudioStartEvent event) {
        //更新activity为播放状态
        showPlayView();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPauseEvent(AudioPauseEvent event) {
        //更新activity为暂停状态
        showPauseView();
    }




    private void showLoadView(boolean isSmooth) {
        //加载对应的某一项
        mViewPager.setCurrentItem(mQueue.indexOf(mAudioBean),isSmooth);
    }

    private void showPlayView() {
        Animator anim =mAdapter.getAnim(mViewPager.getCurrentItem());
        if(anim!=null){
            if (anim.isPaused()){
                anim.resume();
            }else {
                anim.start();
            }
        }
    }

    private void showPauseView() {
        Animator anim=mAdapter.getAnim(mViewPager.getCurrentItem());
        if(anim!=null){
            anim.pause();
        }

    }



    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }
}
