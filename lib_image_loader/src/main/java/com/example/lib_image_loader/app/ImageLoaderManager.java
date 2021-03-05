package com.example.lib_image_loader.app;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.lib_image_loader.R;
import com.example.lib_image_loader.image.CustomRequestListener;
import com.example.lib_image_loader.image.Utils;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class ImageLoaderManager {
    private ImageLoaderManager(){

    }
    private static class SingletonHolder{
        private static ImageLoaderManager instance=new ImageLoaderManager();
    }

    public static ImageLoaderManager getInstance(){
        return SingletonHolder.instance;
    }

    /**
     * 为ImageView加载图片,不需要任何其他的设置，把url加载到imageview上
     */
    public void displayImageforView(ImageView imageView, String url){
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .transition(withCrossFade())//过渡效果
                .into(imageView);
    }

    /**
     * 加载圆形图片到View中
     * @param imageView
     * @param url
     */
    public void displayImageForCircle(final ImageView imageView, String url){
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .into(new BitmapImageViewTarget(imageView){
                    @Override
                    //把imageView包装成imageViewTarget
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);
                        RoundedBitmapDrawable drawable= RoundedBitmapDrawableFactory
                                .create(imageView.getResources(),resource);
                        drawable.setCircular(true);
                        imageView.setImageDrawable(drawable);
                    }
                });
    }

    /**
     * 为viewgroup设置背景并模糊处理
     * @param group
     * @param url
     */
    public void displayImageForViewGroup(final ViewGroup group,String url){
        Glide.with(group.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull final Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        final Bitmap bitmap=resource;
                        Observable.just(resource).map(new Function<Bitmap, Drawable>() {
                            @Override
                            public Drawable apply(Bitmap bitmap){
                                //将bitmap模糊处理并转为drawable
                                Drawable drawable=new BitmapDrawable(Utils.doBlur(
                                        resource,
                                        100,
                                        true));//可以复用
                                return drawable;
                            }
                        })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Drawable>() {
                                    @Override
                                    public void accept(Drawable drawable) throws Exception {
                                        group.setBackground(drawable);
                                    }
                                });
                    }
                });
    }


    //初始化CommonRequestOption
    private RequestOptions initCommonRequestOption(){
        RequestOptions requestOptions=new RequestOptions();
        requestOptions.placeholder(R.mipmap.b4y)
        .error(R.mipmap.b4y)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .skipMemoryCache(false)
        .priority(Priority.NORMAL);
        return requestOptions;
    }



    /**
     * 为非view加载图片
     */
    private void displayImageForTarget(Context context, Target target, String url) {
        this.displayImageForTarget(context, target, url, null);
    }
    
    /**
     * 为非view加载图片
     */
    private void displayImageForTarget(Context context, Target target, String url, CustomRequestListener requestListener){
        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .transition(withCrossFade())
                .fitCenter()
                .listener(requestListener)
                .into(target);
    }

    //为Notification中的控件加载图片
    public void displayImageForNotification(Context context, int id,RemoteViews rv,
                                            Notification notification,int NOTIFICATION_ID,String url){
        this.displayImageForTarget(context,initNotificationTarget(context,id,rv,notification,NOTIFICATION_ID),url);
    };

    private NotificationTarget initNotificationTarget(Context context, int id,RemoteViews rv,
                                                      Notification notification,int NOTIFICATION_ID){
        NotificationTarget target=new NotificationTarget(context,id,rv,notification,NOTIFICATION_ID);
        return target;
    }

}
