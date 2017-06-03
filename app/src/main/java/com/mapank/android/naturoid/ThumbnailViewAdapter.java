package com.mapank.android.naturoid;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.List;

/*
 * https://github.com/googleads/googleads-mobile-android-examples/tree/master/advanced/NativeExpressRecyclerViewExample
 */

class ThumbnailViewAdapter extends RecyclerView.Adapter< RecyclerView.ViewHolder>{

    private static final int THUMB_VIEW_TYPE = 0;
    private static final int NATIVE_EXPRESS_AD_VIEW_TYPE = 1;

    private final Context mContext;
    private ImagesLab mImagesLab;

    private final List<Object> mRecyclerViewItems;

    ThumbnailViewAdapter( Context context, List<Object> recyclerViewItems){
        this.mContext = context;
        this.mImagesLab = ImagesLab.getInstance( context );
        this.mRecyclerViewItems = recyclerViewItems;
    }

    private class ThumbnailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView img_thumbnail;

        ThumbnailViewHolder(View view) {
            super(view);
            itemView.setOnClickListener(this);

            img_thumbnail = ( ImageView) view.findViewById(R.id.img_thumbnail);
            img_thumbnail.getLayoutParams().height = mImagesLab.getThumbHeight();
            img_thumbnail.getLayoutParams().width = mImagesLab.getThumbWidth();
        }

        @Override
        public void onClick(View v) {

            Intent intent = FullImageActivity.newWallpaperUriIntent( mContext, (int) mRecyclerViewItems.get( getAdapterPosition()));
            mContext.startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        }
    }

    private class NativeExpressAdViewHolder extends RecyclerView.ViewHolder{
        NativeExpressAdViewHolder(View view){
            super(view);
        }
    }

    @Override
    public int getItemCount() {
        return mRecyclerViewItems.size();
    }

    @Override
    public int getItemViewType(int position){
        return ( position + 1 ) % ThumbnailActivity.ITEMS_PER_AD == 0 ? NATIVE_EXPRESS_AD_VIEW_TYPE
                : THUMB_VIEW_TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        switch (viewType) {
            case THUMB_VIEW_TYPE:
                View thumbLayoutView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.row_thumbnail_layout, viewGroup, false);
                return new ThumbnailViewHolder(thumbLayoutView);

            case NATIVE_EXPRESS_AD_VIEW_TYPE:
            default:
                View nativeExpressLayoutView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.native_express_ad_container, viewGroup, false);
                return new NativeExpressAdViewHolder(nativeExpressLayoutView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);

        switch ( viewType){
            case THUMB_VIEW_TYPE:
                ThumbnailViewHolder thumbnailViewHolder = (ThumbnailViewHolder) holder;
                Glide.with(mContext)
                        .load(mImagesLab.getThumbUri( (int) mRecyclerViewItems.get(position) ))
                        .thumbnail( 0.1f )
                        .into(thumbnailViewHolder.img_thumbnail);
                break;

            case NATIVE_EXPRESS_AD_VIEW_TYPE:
            default:

                NativeExpressAdViewHolder nativeExpressHolder =
                        (NativeExpressAdViewHolder) holder;
                NativeExpressAdView adView =
                        (NativeExpressAdView) mRecyclerViewItems.get(position);
                ViewGroup adCardView = (ViewGroup) nativeExpressHolder.itemView;

                if (adCardView.getChildCount() > 0) {
                    adCardView.removeAllViews();
                }
                if (adView.getParent() != null) {
                    ((ViewGroup) adView.getParent()).removeView(adView);
                }

                adCardView.addView(adView);
        }
    }
}
