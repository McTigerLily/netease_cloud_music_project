package com.example.lib_audio.view;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.example.lib_audio.R;
import com.example.lib_audio.mediaplayer.core.AudioController;
import com.example.lib_audio.mediaplayer.db.GreenDaoHelper;
import com.example.lib_audio.mediaplayer.events.AudioFavouriteEvent;
import com.example.lib_audio.mediaplayer.events.AudioLoadEvent;
import com.example.lib_audio.mediaplayer.events.AudioPauseEvent;
import com.example.lib_audio.mediaplayer.events.AudioPlayModeEvent;
import com.example.lib_audio.mediaplayer.events.AudioStartEvent;
import com.example.lib_audio.mediaplayer.model.AudioBean;
import com.example.lib_common_ui.base.BaseActivity;
import com.example.lib_image_loader.app.ImageLoaderManager;
import com.example.lib_share.ShareDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MusicPlayerActivity extends BaseActivity {
    private RelativeLayout mBgView;
    private TextView mInfoView;
    private TextView mAuthorView;

    private ImageView mFavouriteView;

    private SeekBar mProgressView;
    private TextView mStartTimeView;
    private TextView mTotalTimeView;

    private ImageView mPlayModeView;
    private ImageView mPlayView;
    private ImageView mNextView;
    private ImageView mPreViousView;

    private Animator animator;
    /**
     * data
     */
    private AudioBean mAudioBean;
    private AudioController.PlayMode mPlayMode;


    public static void start(Activity context){
        Intent intent=new Intent(context,MusicPlayerActivity.class);
        ActivityCompat.startActivity(context,intent,
                ActivityOptionsCompat.makeSceneTransitionAnimation(context).toBundle());
        //context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().setEnterTransition(
                    TransitionInflater.from(this).
                            inflateTransition(
                                    R.transition.transition_bottom2top
                            )
            );
        }
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_music_player);
        initData();
        initView();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void initData(){
        mAudioBean = AudioController.getInstance().getNowPlaying();
        mPlayMode = AudioController.getInstance().getmPlayMode();
    }
    public void initView(){
        mBgView=findViewById(R.id.musicplayer_root_layout);
        //???????????????????????????
        ImageLoaderManager.getInstance().displayImageForViewGroup(mBgView,mAudioBean.albumPic);
        findViewById(R.id.back_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();//????????????finish??????????????????????????????????????????????????????
            }
        });
        findViewById(R.id.title_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        findViewById(R.id.share_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //????????????
                shareMusic(mAudioBean.mUrl,mAudioBean.name);
            }
        });
        findViewById(R.id.show_list_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //??????????????????dialog
            }
        });
        mInfoView=findViewById(R.id.album_view);
        mInfoView.setText(mAudioBean.albumInfo);
        mInfoView.requestFocus();//???????????????????????????????????????

        mAuthorView=findViewById(R.id.author_view);
        mAuthorView.setText(mAudioBean.author);

        mFavouriteView=findViewById(R.id.favourite_view);
        mFavouriteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //????????????
                //AudioController.getInstance().changeFavourite();
            }
        });
        //changeFavouriteStatus(false);

        mStartTimeView=findViewById(R.id.start_time_view);
        mTotalTimeView=findViewById(R.id.total_time_view);
        mProgressView=findViewById(R.id.progress_view);
        mProgressView.setProgress(0);
        mProgressView.setEnabled(false);

        mPlayModeView=findViewById(R.id.play_mode_view);
        mPlayModeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //????????????????????????
                switch (mPlayMode){
                    case LOOP:
                        AudioController.getInstance().setmPlayMode(AudioController.PlayMode.RANDOM);
                        break;
                    case RANDOM:
                        AudioController.getInstance().setmPlayMode(AudioController.PlayMode.REPEAT);
                        break;
                    case REPEAT:
                        AudioController.getInstance().setmPlayMode(AudioController.PlayMode.LOOP);
                        break;
                }
            }
        });
        updatePlayModeView();

        mPreViousView=findViewById(R.id.previous_view);
        mPreViousView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //?????????
                AudioController.getInstance().previous();
            }
        });
        mPlayView=findViewById(R.id.play_view);
        mPlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //??????/??????
                AudioController.getInstance().playOrPause();
            }
        });
        mNextView=findViewById(R.id.next_view);
        mNextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //?????????
                AudioController.getInstance().next();
            }
        });



    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoadEvent(AudioLoadEvent event) {
        //??????notifacation???load??????
        mAudioBean = event.mAudioBean;
        ImageLoaderManager.getInstance().displayImageForViewGroup(mBgView, mAudioBean.albumPic);
        //??????????????????????????????????????????
        mInfoView.setText(mAudioBean.albumInfo);
        mAuthorView.setText(mAudioBean.author);
        changeFavouriteStatus(false);
        mProgressView.setProgress(0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPauseEvent(AudioPauseEvent event) {
        //??????activity???????????????
        showPauseView();
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioStartEvent(AudioStartEvent event) {
        //??????activity???????????????
        showPlayView();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioFavouriteEvent(AudioFavouriteEvent event) {
        //??????activity????????????
        //changeFavouriteStatus(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPlayModeEvent(AudioPlayModeEvent event) {
        mPlayMode = event.mPlayMode;
        //??????????????????
        updatePlayModeView();
    }

    private void showPlayView() {
        mPlayView.setImageResource(R.mipmap.audio_aj6);
    }
    private void showPauseView() {
        mPlayView.setImageResource(R.mipmap.audio_aj7);
    }

    private void updatePlayModeView() {
        switch (mPlayMode){
            case LOOP:
                mPlayModeView.setImageResource(R.mipmap.player_loop);
                break;
            case RANDOM:
                mPlayModeView.setImageResource(R.mipmap.player_random);
                break;
            case REPEAT:
                mPlayModeView.setImageResource(R.mipmap.player_once);
                break;
        }
    }

    private void changeFavouriteStatus(boolean anim) {
        if(GreenDaoHelper.selectFavorite(mAudioBean)!=null){
            mFavouriteView.setImageResource(R.mipmap.audio_aeh);
        }
        else{
            mFavouriteView.setImageResource(R.mipmap.audio_aef);
        }
        //??????????????????
        if(anim){
            if(animator!=null){
                animator.end();
            }
            PropertyValuesHolder animX=PropertyValuesHolder.ofFloat(View.SCALE_X.getName(),
                    1.0f,1.2f,1.0f);
            PropertyValuesHolder animY=PropertyValuesHolder.ofFloat(View.SCALE_Y.getName(),
                    1.0f,1.2f,1.0f);
            animator= ObjectAnimator.ofPropertyValuesHolder(animX,animY);
            animator.setInterpolator(new AccelerateInterpolator());
            animator.setDuration(300);
            animator.start();
        }
    }

    private void shareMusic(String mUrl, String name) {
        ShareDialog dialog=new ShareDialog(this);
        dialog.setShareType(5);//url??????
        dialog.setShareTitle(name);
        dialog.setShareTitleUrl(mUrl);
        dialog.setShareText("?????????");
        dialog.setShareSite("imooc");
        dialog.setShareSiteUrl("http://www.imooc.com");

        dialog.show();


    }
}