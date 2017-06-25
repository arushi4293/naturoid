package com.mapank.android.naturoid;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import com.mapank.android.naturoid.helper.ParallaxPageTransformer;
import com.mapank.android.naturoid.helper.Methods;

public class FullImageActivity extends AppCompatActivity {

    private static final String EXTRA_WALLPAPER_POSITION = "com.mapank.android.wallpaper.position";

    private static final String PERMISSION_WRITE_EXTERNAL_STORAGE = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int REQUEST_CODE_APPLICATION_SETTING = 1; // from application settings
    private static final int REQUEST_CODE_DIALOG_APPLICATION_SETTING = 2; // from dialog for application settings
    private static final int REQUEST_CODE_DIALOG_PERMISSION_REQUEST = 3; // from dialog for permission request
    private static final int REQUEST_CODE_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 4; // from permission request

    private static final String DIALOG_PERMISSION = "DIALOG_PERMISSION";

    private ViewPager mViewPager;
    private AdView mAdViewBanner;
    private InterstitialAd mInterstitialAd;

    public static Intent newWallpaperUriIntent(Context packageContext, int position) {
        Intent intent = new Intent(packageContext, FullImageActivity.class);
        intent.putExtra(EXTRA_WALLPAPER_POSITION, position);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        mViewPager = (ViewPager) findViewById(R.id.full_image_view_pager);
        mAdViewBanner = (AdView) findViewById(R.id.adViewBanner);

        initialize_buttons();
        initAds();

        int position = (int) getIntent().getSerializableExtra(EXTRA_WALLPAPER_POSITION);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {
                return FullImageFragment.newInstance(position);
            }

            @Override
            public int getCount() {
                return ImagesLab.getInstance(FullImageActivity.this).getImagesCount();
            }
        });

        mViewPager.setPageTransformer(true, new ParallaxPageTransformer());

        mViewPager.setCurrentItem(position);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CODE_APPLICATION_SETTING:
                if (Methods.havePermission(this, PERMISSION_WRITE_EXTERNAL_STORAGE)) {
                    if (
                            Methods.saveWallpaper(
                                    ImagesLab.getInstance(this).getFullImageInputStream(mViewPager.getCurrentItem()),
                                    findViewById(R.id.viewSnack)
                            )
                            ) {
                        showInterstitialAd();
                    }
                } else {
                    Snackbar.make(findViewById(R.id.viewSnack), R.string.permission_request_denied, Snackbar.LENGTH_LONG)
                            .show();
                }
                break;
            case REQUEST_CODE_DIALOG_PERMISSION_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    requestPermission(PERMISSION_WRITE_EXTERNAL_STORAGE, REQUEST_CODE_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
                } else if (resultCode == Activity.RESULT_CANCELED) {

                    Snackbar.make(findViewById(R.id.viewSnack), R.string.permission_request_cancelled, Snackbar.LENGTH_LONG)
                            .setAction(R.string.permission_request_grant, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    requestPermission(PERMISSION_WRITE_EXTERNAL_STORAGE, REQUEST_CODE_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
                                }
                            })
                            .show();
                }
                break;
            case REQUEST_CODE_DIALOG_APPLICATION_SETTING:
                if (resultCode == Activity.RESULT_OK) {
                    startActivityForResult(Methods.applicationSettingsIntent(this), REQUEST_CODE_APPLICATION_SETTING);
                } else if (resultCode == Activity.RESULT_CANCELED) {

                    Snackbar.make(findViewById(R.id.viewSnack), R.string.permission_request_cancelled, Snackbar.LENGTH_LONG)
                            .setAction(R.string.permission_request_open_settings, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivityForResult(Methods.applicationSettingsIntent(FullImageActivity.this), REQUEST_CODE_APPLICATION_SETTING);
                                }
                            })
                            .show();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

        if (sharedPref.getBoolean(getString(R.string.SHARED_PREF_IS_FIRST_TIME_ASK_PERMISSION), true)) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(getString(R.string.SHARED_PREF_IS_FIRST_TIME_ASK_PERMISSION), false);
            editor.apply();
        }


        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (
                            Methods.saveWallpaper(
                                    ImagesLab.getInstance(this).getFullImageInputStream(mViewPager.getCurrentItem()),
                                    findViewById(R.id.viewSnack)
                            )
                            ) {
                        showInterstitialAd();
                    }
                } else {
                    Snackbar.make(findViewById(R.id.viewSnack), R.string.permission_request_denied, Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private void initAds(){

        AdRequest adRequest = new AdRequest.Builder().addTestDevice("E1AA3F8C8F2FC985939301764B643CAD").build();
        mAdViewBanner.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.ad_fullImage_interstitial));
        mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("E1AA3F8C8F2FC985939301764B643CAD").build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("E1AA3F8C8F2FC985939301764B643CAD").build());
            }
        });
    }

    private void showInterstitialAd(){
        if ( mViewPager.getCurrentItem() % 4 == 0 && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    private void initialize_buttons() {

        Button button_rate_it, button_feedback, button_set_wallpaper, button_save_wallpaper;

        button_rate_it = (Button) findViewById(R.id.button_rate_it);
        button_feedback = (Button) findViewById(R.id.button_feedback);
        button_set_wallpaper = (Button) findViewById(R.id.button_set_wallpaper);
        button_save_wallpaper = (Button) findViewById(R.id.button_save_wallpaper);

        //rate
        final Intent rate_intent = Methods.rateIntent(FullImageActivity.this);
        if (rate_intent.resolveActivity(getPackageManager()) != null) {

            button_rate_it.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(rate_intent);
                }
            });
        } else {
            button_rate_it.setVisibility(View.GONE);
        }


        //feedback
        final Intent feedback_intent = Methods.feedbackIntent(FullImageActivity.this);
        if (feedback_intent.resolveActivity(getPackageManager()) != null) {

            button_feedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(feedback_intent);
                }
            });
        } else {
            button_feedback.setVisibility(View.GONE);
        }


        //set wallpaper
        button_set_wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (
                        Methods.setWallpaper(
                                FullImageActivity.this,
                                ImagesLab.getInstance(FullImageActivity.this).getFullImageInputStream(mViewPager.getCurrentItem()),
                                findViewById(R.id.viewSnack)
                        )
                        ) {
                    showInterstitialAd();
                }
            }
        });

        //save wallpaper
        button_save_wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Methods.havePermission(FullImageActivity.this, PERMISSION_WRITE_EXTERNAL_STORAGE)) {
                    if (
                            Methods.saveWallpaper(
                                    ImagesLab.getInstance(FullImageActivity.this).getFullImageInputStream(mViewPager.getCurrentItem()),
                                    findViewById(R.id.viewSnack)
                            )
                            ) {
                        showInterstitialAd();
                    }
                } else {
                    askPermission(PERMISSION_WRITE_EXTERNAL_STORAGE, REQUEST_CODE_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
                }
            }
        });
    }

    private void askPermission(String permission, int requestCode) {

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

        boolean isFirstTime = sharedPref.getBoolean(getString(R.string.SHARED_PREF_IS_FIRST_TIME_ASK_PERMISSION), true);

        if (ActivityCompat.shouldShowRequestPermissionRationale(FullImageActivity.this, permission)) {

            showPermissionDialog(REQUEST_CODE_DIALOG_PERMISSION_REQUEST, R.string.permission_request_grant, R.string.permission_request_desc);

        } else {
            if (isFirstTime) {
                requestPermission(permission, requestCode);
            } else {
                showPermissionDialog(REQUEST_CODE_DIALOG_APPLICATION_SETTING, R.string.permission_request_open_settings, R.string.permission_request_settings_desc);
            }
        }
    }

    @TargetApi(23)
    private void requestPermission(String permission, int requestCode) {
        requestPermissions(
                new String[]{permission},
                requestCode);
    }

    private void showPermissionDialog(int requestCode, int title, int desc) {
        PermissionDialogFragment dialog = PermissionDialogFragment.newInstance(requestCode, title, desc);
        dialog.show(getSupportFragmentManager(), DIALOG_PERMISSION);
    }
}
