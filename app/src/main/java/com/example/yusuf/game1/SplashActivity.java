package com.example.yusuf.game1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.webkit.WebView;

/**
 * The SplashScreen that appears when the app loads.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Create view (Instantiate custom WebView class), and pass it a context and path to our GIF-file:
        GifWebView gifWebViewObj = new GifWebView(this, "file:///android_asset/piggy.gif");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;
        gifWebViewObj.setY(screenHeight/2);
        gifWebViewObj.setX(screenWidth/2);
        setContentView(gifWebViewObj);


        // Delay SplashScreen duration.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class); // Navigate from SplashScreen to MainActivity.
                startActivity(intent);
                finish();
            }
        },5000); // Specify delay in ms.
    }

    // Extend class by a WebView:
    public class GifWebView extends WebView {

        // Create a constructor that takes both a context to call the constructor of the parent-class and a path to the file:
        public GifWebView(Context context, String path) {
            super(context);
            // Use loadUrl() to load that file into our WebView:
            path = "file:///android_asset/scoreanim.gif";
            loadUrl(path);
        }
    }

}
