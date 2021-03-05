package com.example.lib_audio.view;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.example.lib_audio.R;
import com.example.lib_audio.app.AudioHelper;
import com.example.lib_audio.mediaplayer.core.AudioController;
import com.example.lib_audio.mediaplayer.core.MusicService;
import com.example.lib_audio.mediaplayer.db.GreenDaoHelper;
import com.example.lib_audio.mediaplayer.model.AudioBean;
import com.example.lib_image_loader.app.ImageLoaderManager;

/**
 * Notification帮助类
 * 1.Notification初始化
 * 2.对外（MusicService）提供更新Notification的方法
 */
public class NotificationHelper {
    public static final String CHANNEL_ID = "channel_id_audio";
    public static final String CHANNEL_NAME = "channel_name_audio";
    public static final int NOTIFICATION_ID = 0x111;

    private Notification mNotification;
    private RemoteViews mRemoteViews; // 大布局
    private RemoteViews mSmallRemoteViews; //小布局
    private NotificationManager mNotificationManager;
    //data
    private NotificationHelperListener mListener;
    private String packageName;
    private AudioBean mAudioBean;

    private static class SingletonHolder{
        private static NotificationHelper instance=new NotificationHelper();
    }
    public static NotificationHelper getInstance(){
        return SingletonHolder.instance;
    }

    public void init(NotificationHelperListener listener){
        mNotificationManager= (NotificationManager) AudioHelper.
                getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        packageName=AudioHelper.getContext().getPackageName();
        mAudioBean= AudioController.getInstance().getNowPlaying();
        //初始化initNotification
        initNotification();
        mListener=listener;
        if(mListener!=null){
            mListener.onNotificationInit();
        }

    }

    private void initNotification() {
        //初始化Remote views布局
        initRemoteViews();
        //构建Notification
        //适配安卓8.0的消息渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            CHANNEL_NAME,
                            NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(false);
            channel.enableVibration(false);
            mNotificationManager.createNotificationChannel(channel);
        }
        Intent intent=new Intent(AudioHelper.getContext(),MusicPlayerActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(
                AudioHelper.getContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
                );
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(AudioHelper.getContext(), CHANNEL_ID)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setCustomBigContentView(mRemoteViews) //大布局
                        .setContent(mSmallRemoteViews); //正常布局，两个布局可以切换
        mNotification = builder.build();

        showLoadStatus(mAudioBean);
    }

    private void initRemoteViews() {
        int layoutId=R.layout.notification_big_layout;
        mRemoteViews=new RemoteViews(packageName,layoutId);
        mRemoteViews.setTextViewText(R.id.title_view,mAudioBean.name);
        mRemoteViews.setTextViewText(R.id.tip_view,mAudioBean.album);
        /*if(GreenDaoHelper.selectFavorite(mAudioBean)!=null){
            //被收藏过，置为实心
            mRemoteViews.setImageViewResource(R.id.favourite_view,R.mipmap.note_btn_loved);
        }else{
            //没收藏过，置为空心
            mRemoteViews.setImageViewResource(R.id.favourite_view,R.mipmap.note_btn_love_white);
        }*/
        int smalllayoutId=R.layout.notification_small_layout;
        mSmallRemoteViews=new RemoteViews(packageName,smalllayoutId);
        mRemoteViews.setTextViewText(R.id.title_view,mAudioBean.name);
        mRemoteViews.setTextViewText(R.id.tip_view,mAudioBean.album);

        //点击播放/暂停要发送的广播
        Intent playIntent=new Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR);
        playIntent.putExtra(MusicService.NotificationReceiver.EXTRA
                ,MusicService.NotificationReceiver.EXTRA_PLAY);
        PendingIntent playPendingIntent=PendingIntent.getBroadcast(AudioHelper.getContext()
                ,1
                ,playIntent
                ,PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.play_view,playPendingIntent);
        mSmallRemoteViews.setOnClickPendingIntent(R.id.play_view,playPendingIntent);

        //点击上一首 要发送的广播
        Intent previousIntent=new Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR);
        previousIntent.putExtra(MusicService.NotificationReceiver.EXTRA
                ,MusicService.NotificationReceiver.EXTRA_PRE);
        PendingIntent previousPendingIntent=PendingIntent.getBroadcast(AudioHelper.getContext()
                ,2
                ,previousIntent
                ,PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.previous_view,previousPendingIntent);

        //点击下一首 要发送的广播
        Intent nextIntent=new Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR);
        nextIntent.putExtra(MusicService.NotificationReceiver.EXTRA
                ,MusicService.NotificationReceiver.EXTRA_NEXT);
        PendingIntent nextPendingIntent=PendingIntent.getBroadcast(AudioHelper.getContext()
                ,3
                ,nextIntent
                ,PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.next_view,nextPendingIntent);
        mSmallRemoteViews.setOnClickPendingIntent(R.id.next_view,nextPendingIntent);

        //点击收藏 要发送的广播
        /*Intent favoriteIntent=new Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR);
        favoriteIntent.putExtra(MusicService.NotificationReceiver.EXTRA
                ,MusicService.NotificationReceiver.EXTRA_FAV);
        PendingIntent favoritePendingIntent=PendingIntent.getBroadcast(AudioHelper.getContext()
                ,1
                ,favoriteIntent
                ,PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.favourite_view,favoritePendingIntent);*/

    }

    //更新为加载状态
    public void showLoadStatus(AudioBean bean){
        mAudioBean=bean;
        //大布局
        mRemoteViews.setImageViewResource(R.id.play_view,R.mipmap.note_btn_pause_white);
        mRemoteViews.setImageViewResource(R.id.next_view,R.mipmap.note_btn_next_white);
        mRemoteViews.setImageViewResource(R.id.previous_view,R.mipmap.note_btn_pre_white);
        mRemoteViews.setTextViewText(R.id.title_view,mAudioBean.name);
        mRemoteViews.setTextViewText(R.id.tip_view,mAudioBean.album);
        //Notification加载图片
        ImageLoaderManager.getInstance().displayImageForNotification(
                AudioHelper.getContext(),
                R.id.image_view,
                mRemoteViews,
                mNotification,
                NOTIFICATION_ID,
                mAudioBean.albumPic);
        //更新收藏状态
        /*
        if(GreenDaoHelper.selectFavorite(mAudioBean)!=null){
            //被收藏过，置为实心
            mRemoteViews.setImageViewResource(R.id.favourite_view,R.mipmap.note_btn_loved);
        }else{
            //没收藏过，置为空心
            mRemoteViews.setImageViewResource(R.id.favourite_view,R.mipmap.note_btn_love_white);
        }*/
        //小布局
        mSmallRemoteViews.setImageViewResource(R.id.play_view,R.mipmap.note_btn_pause_white);
        mSmallRemoteViews.setImageViewResource(R.id.next_view,R.mipmap.note_btn_next_white);
        mSmallRemoteViews.setTextViewText(R.id.title_view,mAudioBean.name);
        mSmallRemoteViews.setTextViewText(R.id.tip_view,mAudioBean.album);
        //Notification加载图片
        ImageLoaderManager.getInstance().displayImageForNotification(
                AudioHelper.getContext(),
                R.id.image_view,
                mSmallRemoteViews,
                mNotification,
                NOTIFICATION_ID,
                mAudioBean.albumPic);

        //通知
        mNotificationManager.notify(NOTIFICATION_ID,mNotification);
    }

    //更新为播放状态
    public void showPlayStatus(){
        if(mRemoteViews!=null){
            mRemoteViews.setImageViewResource(R.id.play_view,R.mipmap.note_btn_pause_white);
            mSmallRemoteViews.setImageViewResource(R.id.play_view,R.mipmap.note_btn_pause_white);
            mNotificationManager.notify(NOTIFICATION_ID,mNotification);
        }
    }

    //更新为暂停状态
    public void showPauseStatus(){
        if(mRemoteViews!=null){
            mRemoteViews.setImageViewResource(R.id.play_view,R.mipmap.note_btn_play_white);
            mSmallRemoteViews.setImageViewResource(R.id.play_view,R.mipmap.note_btn_play_white);
            mNotificationManager.notify(NOTIFICATION_ID,mNotification);
        }
    }

    //更新为收藏状态,要连接数据库的
    /*public void changeFavouriteStatus(boolean isFavourite){
        if(mRemoteViews!=null){
            mRemoteViews.setImageViewResource(R.id.favourite_view,
                    isFavourite?R.mipmap.note_btn_loved:R.mipmap.note_btn_love_white);
            mNotificationManager.notify(NOTIFICATION_ID, mNotification);
        }
    }*/
    public Notification getNotification() {
        return mNotification;
    }


    /**
     * 与音乐service的回调通信
     */
    public interface NotificationHelperListener {
        void onNotificationInit();
    }
}
