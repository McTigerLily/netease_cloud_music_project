package com.example.netease_cloud_music_module.view.home.page;

import androidx.annotation.RequiresApi;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;


import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.lib_audio.app.AudioHelper;
import com.example.lib_audio.mediaplayer.core.AudioController;
import com.example.lib_audio.mediaplayer.model.AudioBean;
import com.example.lib_common_ui.base.BaseActivity;
import com.example.lib_image_loader.app.ImageLoaderManager;
import com.example.lib_pullalive.app.AliveJobService;
import com.example.netease_cloud_music_module.R;
import com.example.netease_cloud_music_module.model.CHANNEL;
import com.example.netease_cloud_music_module.model.login.LoginEvent;
import com.example.netease_cloud_music_module.utils.UserManager;
import com.example.netease_cloud_music_module.view.home.adapter.HomePagerAdapter;
import com.example.netease_cloud_music_module.view.login.LoginActivity;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class HomeActivity extends BaseActivity implements View.OnClickListener{
    private static final CHANNEL[] CHANNELS=new CHANNEL[]{
            CHANNEL.MY,CHANNEL.DISCOVERY,CHANNEL.FRIEND
    };
    /*
     * View
     */
    private View mToggleView;
    private View mSearchView;
    private DrawerLayout mDrawerLayout;
    private ViewPager mViewPager;
    private HomePagerAdapter mAdapter;
    private ImageView mPhotoView;
    private ViewGroup unLoginLayout;
    /*
     * data
     */
    private ArrayList<AudioBean> mLists = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pullAliveService();
        setContentView(R.layout.activity_home);
        EventBus.getDefault().register(this);
        initView();
        initData();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void pullAliveService() {
        AliveJobService.start(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void initData(){
        mLists.add(new AudioBean("100001", "http://sp-sycdn.kuwo.cn/resource/n2/85/58/433900159.mp3",
                "以你的名字喊我", "周杰伦", "七里香", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1559698076304&di=e6e99aa943b72ef57b97f0be3e0d2446&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fblog%2F201401%2F04%2F20140104170315_XdG38.jpeg",
                "4:30"));
        mLists.add(
                new AudioBean("100002", "http://sq-sycdn.kuwo.cn/resource/n1/98/51/3777061809.mp3", "勇气",
                        "梁静茹", "勇气", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
                        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1559698193627&di=711751f16fefddbf4cbf71da7d8e6d66&imgtype=jpg&src=http%3A%2F%2Fimg0.imgtn.bdimg.com%2Fit%2Fu%3D213168965%2C1040740194%26fm%3D214%26gp%3D0.jpg",
                        "4:40"));
        mLists.add(
                new AudioBean("100003", "http://sp-sycdn.kuwo.cn/resource/n2/52/80/2933081485.mp3", "灿烂如你",
                        "汪峰", "春天里", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
                        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1559698239736&di=3433a1d95c589e31a36dd7b4c176d13a&imgtype=0&src=http%3A%2F%2Fpic.zdface.com%2Fupload%2F201051814737725.jpg",
                        "3:20"));
        mLists.add(
                new AudioBean("100004", "http://sr-sycdn.kuwo.cn/resource/n2/33/25/2629654819.mp3", "小情歌",
                        "五月天", "小幸运", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
                        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1559698289780&di=5146d48002250bf38acfb4c9b4bb6e4e&imgtype=0&src=http%3A%2F%2Fpic.baike.soso.com%2Fp%2F20131220%2Fbki-20131220170401-1254350944.jpg",
                        "2:45"));


        AudioHelper.startMusicService(mLists);
    }
    private void initView(){
        mDrawerLayout=findViewById(R.id.drawer_layout);
        mToggleView=findViewById(R.id.toggle_view);
        mToggleView.setOnClickListener(this);
        mSearchView=findViewById(R.id.search_view);

        mViewPager=findViewById(R.id.view_pager);
        mAdapter=new HomePagerAdapter(getSupportFragmentManager(),CHANNELS);
        mViewPager.setAdapter(mAdapter);
        initMagicIndicator();

        unLoginLayout=findViewById(R.id.unloggin_layout);
        unLoginLayout.setOnClickListener(this);
        mPhotoView=findViewById(R.id.avatr_view);
    }

    //初始化指示器
    private void initMagicIndicator() {
        MagicIndicator magicIndicator=findViewById(R.id.magic_indicator);
        magicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator=new CommonNavigator(this);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                //长度
                return CHANNELS==null?0:CHANNELS.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context,final int index) {
                //初始化效果
                SimplePagerTitleView simplePagerTitleView=new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setText(CHANNELS[index].getKey());
                simplePagerTitleView.setTextSize(19);
                simplePagerTitleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                simplePagerTitleView.setNormalColor(Color.parseColor("#999999"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#333333"));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return null;
            }
            @Override
            public float getTitleWeight(Context context, int index) {
                return 1.0f;
            }
        });
        //设置完了commonNavigator
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator,mViewPager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.unloggin_layout:
                if(!UserManager.getInstance().hasLogined()){
                    LoginActivity.start(this);
                }else{
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginEvent(LoginEvent loginEvent){
        unLoginLayout.setVisibility(View.GONE);
        mPhotoView.setVisibility(View.VISIBLE);
        ImageLoaderManager.getInstance().displayImageForCircle(mPhotoView,UserManager.getInstance().getUser().data.photoUrl);

    }
}