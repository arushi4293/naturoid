package com.mapank.android.naturoid.helper;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.mapank.android.naturoid.R;

/**
 * https://medium.com/@BashaChris/the-android-viewpager-has-become-a-fairly-popular-component-among-android-apps-its-simple-6bca403b16d4
 */

public class ParallaxPageTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage(View view, float position) {

        int pageWidth = view.getWidth();
        ImageView img_full_image = (ImageView) view.findViewById( R.id.img_full_image);


        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(1);

        } else if (position <= 1) { // [-1,1]

            img_full_image.setTranslationX(-position * (pageWidth / 2)); //Half the normal speed

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(1);
        }


    }
}
