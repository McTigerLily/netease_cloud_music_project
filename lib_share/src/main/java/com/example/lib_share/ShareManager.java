package com.example.lib_share;

import android.content.Context;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class ShareManager implements PlatformActionListener{
    private static ShareManager mShareManager=new ShareManager();
    private PlatformShareListener mListener;
    public static ShareManager getInstance(){
        return mShareManager;
    }
    private Platform mCurrentPlatform;
    //分享完成回调
    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        if(mListener!=null) mListener.onComplete(i,hashMap);
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        if(mListener!=null) mListener.onError(i,throwable);
    }
    //分享取消回调
    @Override
    public void onCancel(Platform platform, int i) {
        if(mListener!=null) mListener.onCancel(i);
    }

    //平台类型
    public enum PlatformType{
        QQ,QZone,WeChat,WeChatMoments;
    }
    public static void init(Context context){
        ShareSDK.initSDK(context);
    }

    //分享数据
    public void shareData(ShareData shareData, PlatformShareListener listener){
        mListener=listener;
        switch (shareData.mPlatformType){
            case QQ:
                mCurrentPlatform=ShareSDK.getPlatform(QQ.NAME);
                break;
            case QZone:
                mCurrentPlatform=ShareSDK.getPlatform(QZone.NAME);
                break;
            case WeChat:
                mCurrentPlatform=ShareSDK.getPlatform(Wechat.NAME);
                break;
            case WeChatMoments:
                mCurrentPlatform=ShareSDK.getPlatform(WechatMoments.NAME);
                break;
        }
        mCurrentPlatform.setPlatformActionListener(this);
        mCurrentPlatform.share(shareData.mShareParams);
    }

    //与业务层的接口回调
    public interface PlatformShareListener{
        void onComplete(int var2, HashMap<String, Object> var3);

        void onError(int var2, Throwable var3);

        void onCancel(int var2);
    }


}
