package com.example.lib_audio.view.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.lib_audio.R;
import com.example.lib_audio.mediaplayer.core.AudioController;
import com.example.lib_audio.mediaplayer.model.AudioBean;
import com.example.lib_image_loader.app.ImageLoaderManager;

import java.util.ArrayList;


public class MusicPagerAdapter extends PagerAdapter {
    private Context mContext;
    private ArrayList<AudioBean> mAudioBeans;
    private SparseArray<ObjectAnimator> mAnims = new SparseArray<>();

    public MusicPagerAdapter(ArrayList<AudioBean> audioBeans,Context context){
        mAudioBeans=audioBeans;
        mContext=context;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View rootView= LayoutInflater.from(mContext).inflate(R.layout.indictor_item_view,null);
        ImageView imageView=rootView.findViewById(R.id.circle_view);
        container.addView(rootView);
        ImageLoaderManager.getInstance().
                displayImageForCircle(imageView,mAudioBeans.get(position).albumPic);
        mAnims.put(position,createAnim(rootView));
        return rootView;
    }



    @Override
    public int getCount() {
        return mAudioBeans == null ? 0 : mAudioBeans.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }




    private ObjectAnimator createAnim(View view) {
        view.setRotation(0);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ROTATION.getName(), 0, 360);
        animator.setDuration(10000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(-1);
        if (AudioController.getInstance().isStartState()) {
            animator.start();
        }
        return animator;
    }
    public ObjectAnimator getAnim(int pos){
        return mAnims.get(pos);
    }
}
