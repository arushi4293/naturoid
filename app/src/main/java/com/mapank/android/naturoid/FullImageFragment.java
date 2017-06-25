package com.mapank.android.naturoid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


public class FullImageFragment extends Fragment {

    private static final String ARG_WALLPAPER_POSITION = "wallpaper.position";

    private int mWallpaperPosition;

    public static FullImageFragment newInstance( int position ) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_WALLPAPER_POSITION, position);

        FullImageFragment fragment = new FullImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWallpaperPosition = (int) getArguments().getSerializable(ARG_WALLPAPER_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_full_image, container, false);

        ImageView img_full_image = ( ImageView ) v.findViewById( R.id.img_full_image);

        Glide.with(getContext())
                .load(ImagesLab.getInstance(getContext()).getFullImageUri( mWallpaperPosition))
                .thumbnail(0.1f)
                .into(img_full_image);

        return v;
    }
}
