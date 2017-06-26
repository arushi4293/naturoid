package com.mapank.android.naturoid.helper;


import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.mapank.android.naturoid.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class Methods {

    public static Intent feedbackIntent(Context context){

        Intent Email = new Intent(Intent.ACTION_SENDTO);
        Email.setData(Uri.parse("mailto:")); // only email apps should handle this
        Email.putExtra(Intent.EXTRA_EMAIL, new String[] { context.getString(R.string.feedback_email_id) });
        Email.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.feedback_subject, context.getString(R.string.app_name)));
        //Email.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.feedback_text));

        //return Intent.createChooser(Email, context.getString(R.string.feedback_chooser_text));
        return Email;

    }

    public static Intent rateIntent(Context context){

        Intent rate_intent = new Intent(Intent.ACTION_VIEW);
        rate_intent.setData(Uri.parse("market://details?id=" + context.getString(R.string.package_name)));

        return rate_intent;
    }

    public static Intent applicationSettingsIntent( Context context){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        return intent;
    }

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private static File getAlbumStorageDir(String albumName) {
        File file = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                ),
                albumName
        );

        if (!file.mkdirs()){
            //nope
        }

        return file;
    }

    public static boolean havePermission(Context context, String permission){

        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                ContextCompat.checkSelfPermission( context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean saveWallpaper( InputStream in, View viewSnack){

        if( ! isExternalStorageWritable() ){
            Snackbar.make( viewSnack, R.string.storage_not_avl, Snackbar.LENGTH_LONG)
                    .show();
            return false;
        }

        File file = new File(getAlbumStorageDir("naturoid"), UUID.randomUUID() + ".jpg");


        //InputStream in = null;
        OutputStream out = null;
        try {
            //in = ImagesLab.getInstance(getContext()).getInputStream(mWallpaperPosition);
            out = new FileOutputStream(file);

            byte[] buffer = new byte[1024]; // Adjust if you want
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1)
            {
                out.write(buffer, 0, bytesRead);
            }

            Snackbar.make( viewSnack, R.string.wallpaper_saved, Snackbar.LENGTH_LONG)
                    .show();
            return true;
        }
        catch (IOException e){
            //nope.....
            Snackbar.make( viewSnack, R.string.problem, Snackbar.LENGTH_LONG)
                    .show();
            return false;
        }
        finally {

            if ( in != null ){
                try {
                    in.close();
                } catch (IOException e) {
                    // nope.....
                }
            }

            if ( out != null ){
                try {
                    out.close();
                } catch (IOException e) {
                    // nope...
                }
            }
        }
    }

    public static boolean setWallpaper( Context context, InputStream in, View viewSnack){

        try {

            //in = ImagesLab.getInstance(getContext()).getInputStream(mWallpaperPosition);
            WallpaperManager.getInstance(context).setStream( in );
            in.close();

            Snackbar.make( viewSnack, R.string.wallpaper_set, Snackbar.LENGTH_LONG)
                    .show();

            return true;

        } catch ( IOException e){
            // nope......
            Snackbar.make( viewSnack, R.string.problem, Snackbar.LENGTH_LONG)
                    .show();
            return false;
        }
        finally {
            if ( in != null ){
                try {
                    in.close();
                } catch (IOException e) {
                    // nope....
                }
            }
        }
    }
}
