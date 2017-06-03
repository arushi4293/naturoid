package com.mapank.android.naturoid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/*
  https://www.bignerdranch.com/blog/splash-screens-the-right-way/
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, ThumbnailActivity.class);
        startActivity(intent);
        finish();
    }
}
