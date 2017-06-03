package com.mapank.android.naturoid;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;


class ImagesLab {
    private static ImagesLab sImagesLab;
    private final Context mContext;
    private String[] mImagesNames;

    private final int mThumbHeight;
    private final int mThumbWidth;
    private final int mScreenWidth;

    private static final String THUMBNAIL_FOLDER = "thumbnail_images";
    private static final String TAG = "ImagesLab";

    private static final int IMG_HEIGHT = 1920;
    private static final int IMG_WIDTH = 1080;

    static final int NO_THUMB_COL = 3;

    static ImagesLab getInstance(@NonNull Context context) {

        if ( sImagesLab == null ){
            sImagesLab = new ImagesLab(context);
        }

        return sImagesLab;
    }

    private ImagesLab( Context context ) {
        mContext = context.getApplicationContext();

        try {
            mImagesNames = mContext.getAssets().list(THUMBNAIL_FOLDER);
        }
        catch(IOException ioe){
            Log.d( TAG, ioe.toString());
            mImagesNames = new String[0];
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);

        //int THUMB_MARGIN_px = (int) Math.ceil(THUMB_MARGIN * displayMetrics.density); // dp to pixel
        int THUMB_MARGIN_px = (int) context.getResources().getDimension(R.dimen.thumbnail_offset) * 2;

        mThumbWidth = Math.round ((displayMetrics.widthPixels - THUMB_MARGIN_px * (NO_THUMB_COL + 1 )) / NO_THUMB_COL);
        mThumbHeight = Math.round( (float) mThumbWidth / IMG_WIDTH * IMG_HEIGHT);

        mScreenWidth = displayMetrics.widthPixels;
    }

    String getThumbUri( int position ){
        try {
            return "file:///android_asset/" + THUMBNAIL_FOLDER + "/" + mImagesNames[position];
        }
        catch(ArrayIndexOutOfBoundsException e) {
            Log.d( TAG, e.toString());
            return "file:///android_asset/" + THUMBNAIL_FOLDER + "/" + mImagesNames[0];
        }
    }

    String getFullImageUri( int position ){
        try {
            return "file:///android_asset/" + THUMBNAIL_FOLDER + "/" + mImagesNames[position];
        }
        catch(ArrayIndexOutOfBoundsException e) {
            Log.d( TAG, e.toString());
            return "file:///android_asset/" + THUMBNAIL_FOLDER + "/" + mImagesNames[0];
        }
    }

    int getImagesCount(){
        return mImagesNames.length;
    }

    int getThumbWidth(){
        return mThumbWidth;
    }

    int getThumbHeight(){
        return mThumbHeight;
    }

    int getScreenWidth(){
        return mScreenWidth;
    }

    InputStream getInputStream( int position ){
        try {
            return mContext.getAssets().open(THUMBNAIL_FOLDER + "/" + mImagesNames[position]);
        }
        catch(IOException e) {
            Log.d( TAG, e.toString());
            return null;
        }
    }
}
