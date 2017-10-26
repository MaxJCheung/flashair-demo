package com.max.flashairdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.beardedhen.androidbootstrap.BootstrapLabel;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by max on 17-10-26.
 */

public class PhotosAdapter extends RecyclerView.Adapter {

    Context context;
    List<PhotoBean> photoBeans;

    public PhotosAdapter(Context context, List<PhotoBean> photoBeans) {
        this.context = context;
        this.photoBeans = photoBeans;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photos, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).tvName.setText(photoBeans.get(position).fileName);
        Glide.with(context).load(photoBeans.get(position).filePath).into(((ViewHolder) holder).ivPhoto);
    }

    @Override
    public int getItemCount() {
        return photoBeans.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @BindView(R.id.iv_photo)
        public BootstrapCircleThumbnail ivPhoto;
        @BindView(R.id.tv_name)
        public BootstrapLabel tvName;
    }

}
