package com.example.netease_cloud_music_module.view.discovery;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lib_common_ui.recyclerview.CommonAdapter;
import com.example.lib_common_ui.recyclerview.base.ViewHolder;
import com.example.lib_image_loader.app.ImageLoaderManager;
import com.example.netease_cloud_music_module.R;
import com.example.netease_cloud_music_module.model.discory.RecommandHeadValue;
import com.example.netease_cloud_music_module.model.discory.RecommandMiddleValue;

public class DiscoryRecommandView extends RelativeLayout {
    private Context mContext;

    /*
     * UI
     */
    private RecyclerView mRecyclerView;
    /*
     * Data
     */
    private RecommandHeadValue mHeaderValue;

    public DiscoryRecommandView(Context context, RecommandHeadValue recommandHeadValue) {
        this(context, null, recommandHeadValue);
    }

    public DiscoryRecommandView(Context context, AttributeSet attrs,
                                RecommandHeadValue recommandHeadValue) {
        super(context, attrs);
        mContext = context;
        mHeaderValue = recommandHeadValue;
        initView();
    }

    private void initView() {
        View rootView =
                LayoutInflater.from(mContext).inflate(R.layout.item_discory_head_recommand_layout, this);
        mRecyclerView = rootView.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        mRecyclerView.setAdapter(new CommonAdapter<RecommandMiddleValue>(mContext,
                R.layout.item_discory_head_recommand_recycler_layout, mHeaderValue.middle) {
            @Override
            protected void convert(ViewHolder holder, RecommandMiddleValue value, int position) {
                holder.setText(R.id.text_view, value.info);
                ImageView imageView = holder.getView(R.id.image_view);
                ImageLoaderManager.getInstance().displayImageforView(imageView, value.imageUrl);
            }
        });
    }
}
