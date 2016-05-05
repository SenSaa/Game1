package com.example.yusuf.game1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

/**
 * Start screen where you navigate to the game.
 */

public class MainActivity extends AppCompatActivity {

    Button startGameButton;

    //InputStream mStream;
    //long mMoviestart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startGameButton = (Button) findViewById(R.id.startGamebutton);
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startGameIntent = new Intent(MainActivity.this, GameView.class);
                //Intent startGameIntent = new Intent(getApplicationContext(), GameView.class);
                startActivity(startGameIntent);
            }
        });

        // ___Set View Interpolation for the main character when Layout screen appears.___
        Animation animationInterpolator = new ScaleAnimation(0,1,0,1);
        //Animation animationInterpolator = new TranslateAnimation(0,0,-500,50);
        animationInterpolator.setDuration(2000);
        animationInterpolator.setStartOffset(200);
        animationInterpolator.setInterpolator(new OvershootInterpolator());
        startGameButton.startAnimation(animationInterpolator);

        WebView webView = (WebView) findViewById(R.id.custom_webview);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Toast toast1 = Toast.makeText(getApplicationContext(),"戦術", Toast.LENGTH_SHORT);
                    toast1.show();
                }
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    // _Gif_
    // Extend class by a WebView:
    // *** Make class "static" to avoid InflateException & NoSuchMethodException.
    public static class GifWebView extends WebView {
        // Create a constructor that takes both a context to call the constructor of the parent-class and a path to the file:
        // *** The constructor overloaded with Context & AttributeSet parameters is the most important constructor.
        public GifWebView(Context context, AttributeSet attrs) {
            super(context, attrs);
            // Use loadUrl() to load that file into our WebView:
            // ** Gif file must be in "assets" directory (create one if it does not exist) under "main".
            loadUrl("file:///android_asset/glassesemote.gif");
        }
    }


}
