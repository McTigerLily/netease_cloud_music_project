package com.example.netease_cloud_music_module.view.home.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.netease_cloud_music_module.model.CHANNEL;
import com.example.netease_cloud_music_module.view.discovery.DiscoveryFragment;
import com.example.netease_cloud_music_module.view.friend.FriendFragment;
import com.example.netease_cloud_music_module.view.mine.MineFragment;

/*
首页ViewPager的Adapter
 */
public class HomePagerAdapter extends FragmentPagerAdapter {
    private CHANNEL[] mList;
    public HomePagerAdapter(FragmentManager fm,CHANNEL[] datas){
        super(fm);
        mList=datas;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        //只有在进入某一页时才初始化，这样可以加快首页的加载速度，防止卡顿
        int type=mList[position].getValue();
        switch (type){
            case CHANNEL.MINE_ID:
                return MineFragment.newInstance();
            case CHANNEL.DISCOVERY_ID:
                return DiscoveryFragment.newInstance();
            case CHANNEL.FRIEND_ID:
                return FriendFragment.newInstance();

        }
        return null;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.length;
    }
}
