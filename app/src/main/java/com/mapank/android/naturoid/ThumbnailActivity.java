package com.mapank.android.naturoid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.ArrayList;
import java.util.List;

import com.mapank.android.naturoid.helper.ItemOffsetDecoration;

/*
 * https://github.com/googleads/googleads-mobile-android-examples/tree/master/advanced/NativeExpressRecyclerViewExample
 */

public class ThumbnailActivity extends AppCompatActivity {

    public static final int ITEMS_PER_AD = 19; // 6*3 + 1
    private static final int NATIVE_EXPRESS_AD_HEIGHT = 150;

    private AdView mAdViewBannerBottom;
    private RecyclerView mRecyclerView;

    private List<Object> mRecyclerViewItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thumbnail);

        //initialize mobile ads
        MobileAds.initialize(this, getString(R.string.ad_app_id));

        mAdViewBannerBottom = (AdView) findViewById(R.id.adViewBanner);
        mRecyclerView = (RecyclerView) findViewById(R.id.thumbnailsRecyclerView);

        initAds();
        initViews();

//         if play store exist show app rater
//        if( Methods.rateIntent(this).resolveActivity(getPackageManager()) != null){
//            AppRater.app_launched(this);
//        }
    }

    private void initAds(){

        AdRequest adRequest = new AdRequest.Builder().addTestDevice("E1AA3F8C8F2FC985939301764B643CAD").build();
        mAdViewBannerBottom.loadAd(adRequest);

        mAdViewBannerBottom.setAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);

                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );

                p.addRule(RelativeLayout.ABOVE, 0);

                mRecyclerView.setLayoutParams(p);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();

                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );

                p.addRule(RelativeLayout.ABOVE, R.id.adViewBanner);

                mRecyclerView.setLayoutParams(p);
            }
        });
    }

    private void initViews(){

        mRecyclerView.setHasFixedSize(true);

        GridLayoutManager layoutManager = new GridLayoutManager(this, ImagesLab.NO_THUMB_COL);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                return  position % ITEMS_PER_AD == 0
                        ? ImagesLab.NO_THUMB_COL
                        : 1;
            }
        });
        mRecyclerView.setLayoutManager(layoutManager);

        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.thumbnail_offset);
        mRecyclerView.addItemDecoration(itemDecoration);

        addThumbPosition();
        addNativeExpressAds();
        setUpAndLoadNativeExpressAds();

        RecyclerView.Adapter adapter = new ThumbnailViewAdapter( this, mRecyclerViewItems);
        mRecyclerView.setAdapter(adapter);
    }

    private void addThumbPosition(){
        for (int i = 0; i < ImagesLab.getInstance(this).getImagesCount(); i++) {
            mRecyclerViewItems.add(i);
        }
    }

    private void addNativeExpressAds() {

        for (int i = 0; i <= mRecyclerViewItems.size(); i += ITEMS_PER_AD) {
            final NativeExpressAdView adView = new NativeExpressAdView( ThumbnailActivity.this);
            mRecyclerViewItems.add(i, adView);
        }
    }

    private void setUpAndLoadNativeExpressAds() {

        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                final float scale = ThumbnailActivity
                        .this.getResources().getDisplayMetrics().density;

                for (int i = 0; i <= mRecyclerViewItems.size(); i += ITEMS_PER_AD) {
                    final NativeExpressAdView adView = (NativeExpressAdView) mRecyclerViewItems.get(i);
                    int adWidth = (int)(ImagesLab.getInstance(getApplicationContext()).getScreenWidth() / scale ) - 2* (int)getResources().getDimension(R.dimen.thumbnail_offset);
                    AdSize adSize = new AdSize( adWidth, NATIVE_EXPRESS_AD_HEIGHT);
                    adView.setAdSize(adSize);
                    adView.setAdUnitId(getString(R.string.ad_native_express));
                    adView.setVisibility(View.GONE);
                }

                loadNativeExpressAd( 0 );
            }
        });
    }

    private void loadNativeExpressAd(final int index) {

        if (index >= mRecyclerViewItems.size()) {
            return;
        }

        Object item = mRecyclerViewItems.get(index);
        if (!(item instanceof NativeExpressAdView)) {
            throw new ClassCastException("Expected item at index " + index + " to be a Native"
                    + " Express ad.");
        }

        final NativeExpressAdView adView = (NativeExpressAdView) item;

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adView.setVisibility(View.VISIBLE);
                loadNativeExpressAd(index + ITEMS_PER_AD);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                adView.setVisibility(View.GONE);
                loadNativeExpressAd(index + ITEMS_PER_AD);
            }
        });

        adView.loadAd(new AdRequest.Builder().addTestDevice("E1AA3F8C8F2FC985939301764B643CAD").build());
    }

}
