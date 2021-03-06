package com.example.netease_cloud_music_module.view.friend;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lib_common_ui.recyclerview.wrapper.LoadMoreWrapper;
import com.example.lib_network.okhttp.listener.DisposeDataListener;
import com.example.netease_cloud_music_module.R;
import com.example.netease_cloud_music_module.api.RequestCenter;
import com.example.netease_cloud_music_module.model.friend.BaseFriendModel;
import com.example.netease_cloud_music_module.model.friend.FriendBodyValue;
import com.example.netease_cloud_music_module.view.friend.adapter.FriendRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LoadMoreWrapper.OnLoadMoreListener {
    /**
     * UI
     */
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private FriendRecyclerAdapter mAdapter;
    private LoadMoreWrapper mLoadMoreWrapper;

    /**
     * data
     */
    private BaseFriendModel mRecommandData;
    private List<FriendBodyValue> mDatas=new ArrayList<>();
    private Context mContext;


    public static FriendFragment newInstance() {
        FriendFragment fragment = new FriendFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_friend, container, false);
        mSwipeRefreshLayout=rootView.findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().
                getColor(android.R.color.holo_red_light));
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView=rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //???????????????UI
        requestData();
    }

    @Override
    public void onRefresh() {
        requestData();
    }

    //??????????????????
    private void requestData() {
        //????????????
        RequestCenter.requestFriendData(new DisposeDataListener() {
            @Override
            public void onSuccess(Object object) {
                //????????????
                mRecommandData=(BaseFriendModel) object;
                updateUI();
            }

            @Override
            public void onFailure(Object object) {
                //??????????????????
            }
        });
    }

    private void updateUI() {
        //????????????
        mSwipeRefreshLayout.setRefreshing(false);
        mDatas=mRecommandData.data.list;
        mAdapter=new FriendRecyclerAdapter(mContext,mDatas);
        //???adapter??????????????????????????????????????????loadingmore?????????
        mLoadMoreWrapper=new LoadMoreWrapper(mAdapter);
        mLoadMoreWrapper.setLoadMoreView(R.layout.default_loading);
        mLoadMoreWrapper.setOnLoadMoreListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onLoadMoreRequested() {
        loadMore();
    }

    private void loadMore() {
        //????????????
        RequestCenter.requestFriendData(new DisposeDataListener() {
            @Override
            public void onSuccess(Object object) {
                //????????????
                BaseFriendModel moreData=(BaseFriendModel) object;
                //???????????????adapter???
                mDatas.addAll(moreData.data.list);
                mLoadMoreWrapper.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Object object) {
                //??????????????????
            }
        });
    }
}