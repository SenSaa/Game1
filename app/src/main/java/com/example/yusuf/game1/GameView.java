package com.example.yusuf.game1;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * The main part of the app, that holds almost all of the game logic.
 */

public class GameView extends Activity implements SensorEventListener, GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener {

    // _______________________________________________________ //

    int displayWidth;
    int displayHeight;
    int topMargin;
    int bottomMargin;
    int stripeAnimationSpeed;

    ImageView mainCharImageView;
    ImageView stageImageView;
    ImageView stageImageView2;
    ImageView stageImageView3;
    ImageView stageImageView4;

    SensorManager sensorManager;
    Sensor accelerometer;
    Sensor gyroscope;

    float accelX;
    float accelY;
    float accelZ;

    float gyroX;
    float gyroY;
    float gyroZ;

    GestureDetector gestureDetector;

    boolean accelerometerFlag;
    boolean singleTapFlag;
    boolean swipeFlag;
    boolean longPressFlag;

    CustomView customViewObj;

    TextView scoreTxV;
    int score;
    int negativeScore;

    float customViewRectY;

    float mainCharX;
    float mainCharY;
    float mainCharHeight;
    float mainCharWidth;

    float customViewX;
    float customViewY;

    int parentLayoutColor;
    int stripeShapeColor;
    int customViewRectColor;
    int mainCharColor;

    boolean dialogDismissed;

    Vibrator vibrator;
    boolean allowVibration;

    private final float NOISE = (float) 2.0;
    final float kFilteringFactor = 0.1f;
    float lastAccelX;
    float currentAccelX;

    static final float ALPHA = 0.1f;
    float[] filteredAccelX = new float[3];

    WebView scoreAnimationWebViewGif;

    ImageView scoreBeam;
    int inPewPewMode; // Variable for storing score beam visibility mode.
    ImageView scoreBeam2;


    // _______________________________________________________ //

    // _____Handler - To update the animation method._____
    Handler stageAnimationHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //Log.v("Handler", "handleMessage");
            animation();
        }
    };

    // _______________________________________________________ //

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        // _______________________________________________________ //

        // Display properties.
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        displayWidth = displayMetrics.widthPixels;
        displayHeight = displayMetrics.heightPixels;
        Log.v("width",String.valueOf(displayWidth));
        Log.v("height", String.valueOf(displayHeight));

        // Due to the action bar, the top is not 0, but instead origin (0) + actionbar margin (32).
        topMargin = 32;

        // Due to the bottom system buttons, the bottom is not at displayHeight, but at 1776, so...
        bottomMargin = 144;

        // Speed of Stripes animation.
        stripeAnimationSpeed = 10;

        // _______________________________________________________ //

        // _____Adding Main Character image to ImageView._____
        mainCharImageView = (ImageView) findViewById(R.id.mainCharImageView);
        // ___Initialise Bitmap with drawable image resource.___
        ////Bitmap mainCharBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mainchar);
        Bitmap mainCharBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mainchar_cropped);
        // ___Set Bitmap image as the Image view widget.___
        mainCharImageView.setImageBitmap(mainCharBitmap);

        // _______________________________________________________ //

        // ___Set View Interpolation for the main character when Layout screen appears.___
        //Animation animationInterpolator = new ScaleAnimation(0,1,0,1);
        Animation animationInterpolator = new TranslateAnimation(0,0,-500,50);
        animationInterpolator.setDuration(2000);
        animationInterpolator.setStartOffset(200);
        animationInterpolator.setInterpolator(new BounceInterpolator());
        mainCharImageView.startAnimation(animationInterpolator);

        // _______________________________________________________ //

        // _____Adding stage stripe component images to ImageViews._____
        stageImageView = (ImageView) findViewById(R.id.stageImageView);
        Bitmap stageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.stripe);
        stageImageView.setImageBitmap(stageBitmap);

        stageImageView2 = (ImageView) findViewById(R.id.stageImageView2);
        //Bitmap stageBitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.stripe);
        stageImageView2.setImageBitmap(stageBitmap);

        stageImageView3 = (ImageView) findViewById(R.id.stageImageView3);
        //Bitmap stageBitmap3 = BitmapFactory.decodeResource(getResources(), R.drawable.stripe);
        stageImageView3.setImageBitmap(stageBitmap);

        stageImageView4 = (ImageView) findViewById(R.id.stageImageView4);
        //Bitmap stageBitmap4 = BitmapFactory.decodeResource(getResources(), R.drawable.stripe);
        stageImageView4.setImageBitmap(stageBitmap);

        // _______________________________________________________ //

        // Instantiate sensor manager using "getSystemService", and use it to return default sensors.
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        //// Register Sensors in "onResume" instead of "onCreate".

        // _______________________________________________________ //

        // _____Instantiate Gesture Detector class._____
        // Create an instance of GestureDetector class.
        gestureDetector = new GestureDetector(this,this);
        // Attach listeners that'll be called for double-tap and related gestures
        gestureDetector.setOnDoubleTapListener(this);

        // _______________________________________________________ //

        // The control flag state initialisation was moved to the Dialog selection stage.
/*
        // __Initialise control flag states.__
        accelerometerFlag = true; // Set accelerometer as the default control method
        singleTapFlag = false;
        swipeFlag = false;
        longPressFlag = false;
*/

        // _______________________________________________________ //

        // Reference CustomView class.
        customViewObj = (CustomView)findViewById(R.id.custom_view);

        // custom view x&y.
        customViewX = customViewObj.rectX;
        customViewY = customViewObj.rectY;

        customViewRectY = 0;

        // _______________________________________________________ //

        // Initialise score variable and reference score TextView.
        score = 0;
        scoreTxV = (TextView) findViewById(R.id.scoreTextView);
        negativeScore = 0;

        // _______________________________________________________ //

        // Change background Color of Views.
        viewsBackgroundColor();

        // _______________________________________________________ //

        // boolean state variable - so that we can start the animation only after a dialog button has been pressed (and dialog dismissed).
        dialogDismissed = false;
        // Call method that creates and shows the dialog.
        openDialog();

        // _______________________________________________________ //

        // Vibration flag - to control vibration behaviour.
        allowVibration = false;

        // _______________________________________________________ //

        ////WebView scoreAnimationWebViewGif = (WebView)findViewById(R.id.custom_webview2);
        ////scoreAnimationWebViewGif.setVisibility(View.GONE);

        // _______________________________________________________ //

        // Score beam image.
        scoreBeam = (ImageView) findViewById(R.id.scoreBeam);
        Bitmap scoreBeamBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.scorebeam2);
        scoreBeam.setImageBitmap(scoreBeamBitmap);
        inPewPewMode = View.GONE;
        scoreBeam.setVisibility(inPewPewMode);

        // Score beam image2.
        scoreBeam2 = (ImageView) findViewById(R.id.scoreBeam2);
        Bitmap scoreBeamBitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.scorebeam3);
        scoreBeam2.setImageBitmap(scoreBeamBitmap2);
        inPewPewMode = View.GONE;
        scoreBeam2.setVisibility(inPewPewMode);

        // _______________________________________________________ //

        // Executing the animation algorithm.
        // *** This must run last, so that we do not encounter NullPointers exceptions etc.
        animation(); // Call stage animation method.
    }

    // Change background Color of Views.
    public void viewsBackgroundColor() {

        parentLayoutColor = Color.rgb(179,42,138);
        stripeShapeColor = Color.BLACK;
        //customViewRectColor = Color.YELLOW;
        mainCharColor = Color.BLACK;

        RelativeLayout layout = (RelativeLayout)findViewById(R.id.parentLayout);
        layout.setBackgroundColor(parentLayoutColor);

        stageImageView.setBackgroundColor(stripeShapeColor);
        stageImageView2.setBackgroundColor(stripeShapeColor);
        stageImageView3.setBackgroundColor(stripeShapeColor);
        stageImageView4.setBackgroundColor(stripeShapeColor);

        //customViewObj.setBackgroundColor(customViewRectColor);

        mainCharImageView.setBackgroundColor(mainCharColor);
    }


    // _______________________________________________________ //

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister Sensors.
        sensorManager.unregisterListener(this);

        // Set Vibration flag to false, when activity is paused and cancel vibration.
        allowVibration = false;
        vibrator.cancel();

        // When device sleeps - animation method should stop updating.
        stageAnimationHandler.removeMessages(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register Sensors.
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);

        // Instantiate Vibrator service, and set Vibration flag to true, when activity is resumed.
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        allowVibration = true;

        // When device wakes from sleep (back to game) - animation method should resume updating.
        stageAnimationHandler.sendEmptyMessageDelayed(0,20);
    }

    // _______________________________________________________ //

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // if option item selected - is accelerometer action button, then...
        if (item.getItemId() == R.id.action_accelerometer) {
            accelerometerFlag = true;
            singleTapFlag = false;
            swipeFlag = false;
            return true;
        }
        // if option item selected - is SingleTap action button, then...
        if (item.getItemId() == R.id.action_singleTap) {
            singleTapFlag = true;
            swipeFlag = false;
            accelerometerFlag = false;
            return true;
        }
        // if option item selected - is swipe action button, then...
        if (item.getItemId() == R.id.action_swipe) {
            swipeFlag = true;
            singleTapFlag = false;
            accelerometerFlag = false;
            return true;
        }
        /*
        if (item.getItemId() == R.id.action_longPress) {
            return true;
        }
        */
        return super.onOptionsItemSelected(item);
    }

    // _______________________________________________________ //

    // _____Animate stage stripe views._____
    private void animation() {

        // Stripe 1 (top left).
        //Log.v("View", String.valueOf(stageImageView.getHeight()));
        Rect myViewRect = new Rect();
        stageImageView.getGlobalVisibleRect(myViewRect);
        float x = myViewRect.left;
        float y = myViewRect.top;
        //Log.v("X", String.valueOf(x));
        //Log.v("Y", String.valueOf(y));

        // if the stripe stage image has not reached the bottom of the screen, then...
        if (y < displayHeight - bottomMargin) {
            y = y + stripeAnimationSpeed; // Increment the stripe ImageView's y-position.
            stageImageView.setY(y); // Set the ImageView's y-position to the updated y-position.
        }
        // otherwise, if the stripe stage image has reached the bottom of the screen, then...
        else if (y >= displayHeight - bottomMargin) {
            y = topMargin; // reposition the y-position of the stripe imageview to the top.
            stageImageView.setY(y); // Set the ImageView's y-position to the updated y-position.
        }

        // _______________________________________________________ //

        // Stripe 2 (top right).
        Rect myViewRect2 = new Rect();
        stageImageView2.getGlobalVisibleRect(myViewRect2);
        float x2 = myViewRect2.left;
        float y2 = myViewRect2.top;
        //Log.v("X2", String.valueOf(x2));
        //Log.v("Y2", String.valueOf(y2));
        if (y2 < displayHeight - bottomMargin) {
            y2 = y2 + stripeAnimationSpeed;
            stageImageView2.setY(y2);
        } else if (y2 >= displayHeight - bottomMargin) {
            ////y2 = 72;
            y2 = topMargin;
            stageImageView2.setY(y2);
        }

        // _______________________________________________________ //

        // Stripe 3 (bottom left).
        Rect myViewRect3 = new Rect();
        stageImageView3.getGlobalVisibleRect(myViewRect3);
        float x3 = myViewRect3.left;
        float y3 = myViewRect3.top;
        //Log.v("X3", String.valueOf(x3));
        //Log.v("Y3", String.valueOf(y3));
        if (y3 < displayHeight - bottomMargin) {
            y3 = y3 + stripeAnimationSpeed;
            stageImageView3.setY(y3);
        } else if (y3 >= displayHeight - bottomMargin) {
            ////y3 = 72;
            y3 = topMargin;
            stageImageView3.setY(y3);
        }

        // _______________________________________________________ //

        // Stripe 4 (bottom right).
        Rect myViewRect4 = new Rect();
        stageImageView4.getGlobalVisibleRect(myViewRect4);
        float x4 = myViewRect4.left;
        float y4 = myViewRect4.top;
        //Log.v("X4", String.valueOf(x4));
        //Log.v("Y4", String.valueOf(y4));
        if (y4 < displayHeight - bottomMargin) {
            y4 = y4 + stripeAnimationSpeed;
            stageImageView4.setY(y4);
        } else if (y4 >= displayHeight - bottomMargin) {
            ////y4 = 72;
            y4 = topMargin;
            stageImageView4.setY(y4);
        }

        // _______________________________________________________ //

        // _Set animation for CustomView class._

        // *Getters access not required - since the CustomView Class does not accept input.*
        //customViewObj.getRectX();
        //customViewObj.getRectY();

        customViewObj.setRectY(customViewRectY);
        //customViewObj.setRectY();
        customViewObj.setRectX();

        // _______________________________________________________ //

        // ___Scoring.___

        // Access mainChar dimension properties.
        mainCharXY();

        // Retrieve the dimensional properties of the Custom View shape.
        //float customViewX = customViewObj.rectX;
        customViewX = customViewObj.rectX;
        //float customViewY = customViewObj.rectY;
        customViewY = customViewObj.rectY;
        float customViewWidth = customViewObj.rectWidth;
        float customViewHeight = customViewObj.rectHeight;
        // Determine the centre x&y positions of the Custom View shape.
        float customViewCentreX = customViewX + customViewWidth / 2;
        float customViewCentreY = customViewY + customViewHeight / 2;

        // _Scoring condition._

        // if the horizontal centre (x-axis) of the Custom View shape is between the left and right of the mainCharacter, and
        // if the vertical centre (y-axis) of the Custom View shape is at the top of the mainChar..., then
        //if (customViewCentreX >= (mainCharX) && customViewCentreX <= (mainCharX + mainCharWidth) && customViewCentreY >= ((mainCharY-mainCharHeight/2)+customViewHeight/2) && customViewCentreY <= ((mainCharY-mainCharHeight/2)+customViewHeight) ) {
        if (customViewCentreX >= (mainCharX) && customViewCentreX <= (mainCharX + mainCharWidth) && customViewCentreY >= ((mainCharY-mainCharHeight/2)+customViewHeight/4) && customViewCentreY <= ((mainCharY-mainCharHeight/2)+customViewHeight*3/4) ) {

            // * Implement vibration only when flag is set to true - otherwise, vibration will be occurring even if activity is destroyed.
            if (allowVibration) {
                vibrator.vibrate(50);
            }

            score++; // increment score
            scoreTxV.setText(String.valueOf(score)); // set TextView to the score updated score.

            /*
            //////// This makes mainChar smaller by customViewHeight/2. ////////
            mainCharImageView.setY(mainCharY - customViewHeight/2);
            */

            // _Score Beam execution._
            // Start thread that - controls score beam 1 visibility.
            ScoreAnimation threadObj = new ScoreAnimation();
            threadObj.start();

            /*
            // Execute AsyncTask that - controls score beam 2 visibility.
            // * Before doing so, insert a delay before the Thread-AsyncTask transition - to implement the functionality of the beam firing twice.
            Handler taskDelayHandler = new Handler();
            taskDelayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //ScoreAnimation2 task = new ScoreAnimation2();
                    //task.execute();
                    new ScoreAnimation2().execute();
                }
            },150);
            */

            customViewObj.setRectY2(mainCharWidth); // Call method that repositions x&y of Custom View - to restart shape animation.

            // Depending on the score, reset the customView's animation speed.
            if (score >= 5) {
                customViewObj.customViewSpeed();
            } else if (score >= 10) {
                customViewObj.customViewSpeed2();
                //customViewObj.setCustomViewRectColor();
            } else if (score >= 15) {
                customViewObj.customViewSpeed3();
            } else if (score >= 20) {
                customViewObj.customViewSpeed4();
                //customViewObj.setCustomViewRectColor2();
            } else if (score >= 25) {
                customViewObj.customViewSpeed5();
            } else if (score >= 30) {
                customViewObj.customViewSpeed6();
            } else if (score >= 35) {
                customViewObj.customViewSpeed7();
            } else if (score >= 40) {
                customViewObj.customViewSpeed8();
            } else if (score >= 45) {
                customViewObj.customViewSpeed9();
            } else if (score >= 50) {
                customViewObj.customViewSpeed10();
            }

        }
        // _______________________________________________________ //

        // ___Negative Scoring.___

        // _Negative Scoring condition._
        // if CustomView shape reaches the bottom of the screen, then..
        else if (customViewY >= displayHeight - (bottomMargin + customViewHeight)) {
            negativeScore++; // increment negative score.
            if (negativeScore == 1) { // 1 missed attempts.
                // Display a warning text on screen.
                Toast fail1 = Toast.makeText(this, "First Missed Attempt!", Toast.LENGTH_SHORT);
                fail1.show();
            } else if (negativeScore == 2) { // 2 missed attempts.
                // Display a warning text on screen.
                Toast fail2 = Toast.makeText(this, "Warning!!", Toast.LENGTH_SHORT);
                fail2.show();
            } else if (negativeScore == 3) { // 3 missed attempts.
                // Display a warning text on screen.
                Toast fail3 = Toast.makeText(this, "Game Over!!!", Toast.LENGTH_SHORT);
                fail3.show();
                // Navigate back to MainActivity.
                Intent backToActivity = new Intent(GameView.this, MainActivity.class);
                startActivity(backToActivity);
                //
                Toast currentScore = Toast.makeText(this, "Score = " + String.valueOf(score), Toast.LENGTH_SHORT);
                currentScore.show();
                //
                this.onDestroy();
            }
        }

        // _______________________________________________________ //

        // _____Schedule Handler to be executed, after a delay (in ms)._____

        if (dialogDismissed) { // Schedule handler only after dialog has been dismissed.
            stageAnimationHandler.sendEmptyMessageDelayed(0, 20); // Since delay is in ms (1000ms = 1s), fps ~ 1000/delay. 40ms delay = 25 frames per second.
        }

    }

    // _______________________________________________________ //

    // Getter method for mainChar dimension properties.
    public void mainCharXY() {
        // Retrieve the left x-position & top y-position, width & height of the main character ImageView.
        Rect myViewRect5 = new Rect();
        mainCharImageView.getGlobalVisibleRect(myViewRect5);
        mainCharX = myViewRect5.left;
        mainCharY = myViewRect5.top;
        mainCharHeight = mainCharImageView.getHeight();
        mainCharWidth = mainCharImageView.getWidth();
    }

    // _______________________________________________________ //

    // _____Sensor Event Handling._____
    @Override
    public void onSensorChanged(SensorEvent event) {
        // __Accelerometer__
        //if (event.sensor.getType() == accelerometer.getType()) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelX = -(event.values[0]); // By setting value to negative: left tilt = left view shift, right tilt = right view shift.
            accelY = event.values[1];
            accelZ = event.values[2];

            // _Moving the main character along the x-axis - using the accelerometer._

            // *-Minimise changes (filter small changes), preventing shakiness of accelerometer-controlled shape, by...*-
            // (i) Rounding up accelerometer value to specified decimal places.
            float accelXRounded2dp = Math.round(accelX * 100) / 100; // Accelerometer value rounded up.
            // (ii) Alternatively, by using smoothing (filtering).
            // * As the smoothing factor (alpha) is reduced, the smoothing level increases.
            //filteredAccelX = exponentialSmoothing(event.values.clone(), filteredAccelX, ALPHA);
            filteredAccelX[0] = filteredAccelX[0] + ALPHA * (event.values[0] - filteredAccelX[0]);

            // Access mainChar dimension properties.
            mainCharXY();
            // Create a variable that holds the centre of mainChar at the horizontal centre of the screen.
            float screenAbsoluteCentreX = (displayWidth-mainCharWidth)/2; // "displayWidth/2" places the left/start of the mainCharacter at the centre of the screen, whereas subtracting by "mainCharWidth" places the centre of the mainCharacter at the centre of the screen.

            // (1) Modify/shift/translate x-position of main character by the accelerometer x-axis event values.
            //mainCharX = accelXRounded2dp*100;
            mainCharX = -filteredAccelX[0]*100; // The negative sign is in place so that - left tilt = left view shift, right tilt = right view shift.


            // (2) Manage the limits/bounds of the x-position variable - so that it does not exceed the right & left side of screen.
            // In case, x-position of main char exceeds display width (view disappears to the right side of screen):
            if (mainCharX >= screenAbsoluteCentreX ) {
                mainCharX = screenAbsoluteCentreX;
            }
            // In case, x-position of main char becomes negative (view disappears to the left side of screen):
            else if (mainCharX <= -screenAbsoluteCentreX ) {
                mainCharX = -screenAbsoluteCentreX;
            }

            // (3) Pass the accelerometer modified variable - to the ImageView's x-position.
            // if Accelerometer flag state is set to true through menu item, then modify mainChar x-position on Accelerometer sensor events.
            if (accelerometerFlag) {
                // Set the x-position of mainChar.
                mainCharImageView.setX(mainCharX + screenAbsoluteCentreX); // Adding "screenAbsoluteCentreX" places mainChar at the horizontal centre of the screen.
            }

        }

        // _______________________________________________________ //
        // __Gyroscope__
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyroX = event.values[0];
            gyroY = event.values[1];
            gyroZ = event.values[2];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    // Filtering Method - Used to smooth the movement of mainChar when controlled by accelerometer (which is very sensitive).
    private float[] exponentialSmoothing( float[] input, float[] output, float alpha ) {
        if ( output == null )
            return input;
        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + alpha * (input[i] - output[i]);
        }
        return output;
    }


    // __Callback Method must be implemented - To respond to touch events.__
    @Override
    public boolean onTouchEvent(MotionEvent event){
        // _Forward TouchEvents to the GestureDetector._
        //this.gestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }

    // _____Touch Tap Event Handling._____
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }
    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    // Single tap touch gesture.
    @Override
    public boolean onDown(MotionEvent e) {
        // if single tap flag state is set to true through menu item, then modify mainChar x-position on single tap touch events.
        if (singleTapFlag) {

            // Access mainChar dimension properties.
            mainCharXY();

            // if the right half of the touchscreen is pressed, then...
            if (e.getX() > displayWidth / 2) {
                if (mainCharX >= displayWidth - mainCharWidth) { // if mainChar is equal to or exceeds displayWidth, then...
                    mainCharX = displayWidth - mainCharWidth; // Limit the mainChar to the right edge of the screen.
                } else {
                    mainCharImageView.setX(mainCharX + displayWidth / 10); // Move mainChar to the right.
                }
            }
            // if the left half of the touchscreen is pressed, then...
            if (e.getX() < displayWidth / 2) {
                if (mainCharX <= 0) { // if mainChar is less than or equal to the left edge of the screen (0), then...
                    mainCharX = 0; // Limit the mainChar to the left edge of the screen.
                } else {
                    mainCharImageView.setX(mainCharX - displayWidth / 10); // Move mainChar to the left.
                }
            }
        }
        return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }
    @Override
    public void onShowPress(MotionEvent e) {
    }
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }
    // Fling/Swipe Gestures.
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // if swipe flag state is set to true through menu item, then modify mainChar x-position on single tap touchfling gesture events.
        if (swipeFlag) {

            // Access mainChar dimension properties.
            mainCharXY();

            // ** e1 is first/initial point of fling/swipe gesture, while e2 is the final point.
            // ** Since (+positive) equates to moving right, while (-negative) to moving left... along the x-axis.
            // ** Therefore, e1 > e2 = The initial point exceeded the final point. [Right-to-Left]
            // ** Therefore, e2 > e1 = The final point exceeded the initial point. [Left-to-Right]

            // In Left-to-Right swipe, and while mainChar x-position is less than display width, then...
            if (e2.getX() > e1.getX() && (mainCharX < (displayWidth - mainCharWidth))) {
                mainCharImageView.setX(mainCharX + displayWidth / 10); // Move mainChar right.
            }
            // In Left-to-Right swipe, and when mainChar x-position exceeds display width, then...
            else if (e2.getX() > e1.getX() && (mainCharX >= (displayWidth - mainCharWidth))) {
                mainCharX = (displayWidth - mainCharWidth); // Stop mainChar from disappearing into the right of the screen.
            }

            // In Right-to-Left swipe, and while mainChar x-position exceeds origin (0), then...
            if (e2.getX() < e1.getX() && (mainCharX > 0)) {
                mainCharImageView.setX(mainCharX - displayWidth / 10); // Move mainChar left.
            }
            // In Right-to-Left swipe, and when mainChar x-position is less than origin (0), then...
            else if (e2.getX() < e1.getX() && (mainCharX <= 0)) {
                mainCharX = -(displayWidth - mainCharWidth); // Stop mainChar from disappearing into the left of the screen.
            }
        }
        return false;
    }

    // _______________________________________________________ //

    public void openDialog() {
        final Dialog dialog = new Dialog(this); // Context, this, etc.
        dialog.setContentView(R.layout.dialog);
        dialog.setTitle(R.string.dialog_title);
        dialog.show();

        dialog.findViewById(R.id.accelerometer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialise game user control flags.
                accelerometerFlag = true;
                singleTapFlag = false;
                // Set the boolean state variable to true.
                dialogDismissed = true;
                // Dismiss dialog.
                dialog.dismiss();
                // Call animation method.
                animation();
            }
        });

        dialog.findViewById(R.id.singleTap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialise game user control flags.
                singleTapFlag = true;
                accelerometerFlag = false;
                // Set the boolean state variable to true.
                dialogDismissed = true;
                // Dismiss dialog.
                dialog.dismiss();
                // Call animation method.
                animation();
            }
        });

        // When the area outside the dialog is clicked - Stop Dialog from being Canceled.
        dialog.setCanceledOnTouchOutside(false);

        // After Back Button Press - Stop Dialog from being dismissed.
        dialog.setCancelable(false);
    }

    // _______________________________________________________ //

    // Thread for managing score beam 1 visibility.
    public class ScoreAnimation extends Thread {

        Handler handler;

        public ScoreAnimation() {
            handler = new Handler();
        }

        @Override
        public void run() {
            super.run();

            // Make the score beam ImageView visible.
            // *** Must be run within main UI thread - because views can only be manipulated by main UI Thread.
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    // Score Beam Interpolation Animation.
                    Animation beamAnimInterpolator = new TranslateAnimation(0,0,500,0);
                    beamAnimInterpolator.setDuration(200);
                    beamAnimInterpolator.setInterpolator(new FastOutSlowInInterpolator());
                    scoreBeam.startAnimation(beamAnimInterpolator);

                    // Set the x-position of the score beam to the position of the main char shape.
                    scoreBeam.setX(customViewX);
                    inPewPewMode = View.VISIBLE;
                    scoreBeam.setVisibility(inPewPewMode);
                }
            });

            // Use a Handler, to make it invisible after a delay (this delay is how long the score beam remains visible).
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Make the score beam ImageView invisible.
                            inPewPewMode = View.GONE;
                            scoreBeam.setVisibility(inPewPewMode);
                        }
                    });
                }
            }, 200);
        }

    }

    // _______________________________________________________ //

    // AsyncTask for managing score beam 2 visibility.
    public class ScoreAnimation2 extends AsyncTask {

        Handler handler;

        public ScoreAnimation2() {
            handler = new Handler();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            // Score Beam Interpolation Animation.
            Animation beamAnimInterpolator2 = new TranslateAnimation(0,0,-500,0);
            beamAnimInterpolator2.setDuration(200);
            beamAnimInterpolator2.setInterpolator(new AccelerateDecelerateInterpolator());
            scoreBeam2.startAnimation(beamAnimInterpolator2);

            // Set the x-position of the score beam to the position of the main char shape.
            scoreBeam2.setX(customViewX);
            // Make the score beam ImageView visible.
            inPewPewMode = View.VISIBLE;
            scoreBeam2.setVisibility(inPewPewMode);

            // Use a Handler, to make score beam invisible after a delay (this delay is how long the score beam remains visible).
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Make the score beam ImageView invisible.
                    inPewPewMode = View.GONE;
                    scoreBeam2.setVisibility(inPewPewMode);
                }
            }, 200);
        }

    }

    // _______________________________________________________ //

    // _Gif_
    // Extend class by a WebView:
    // *** Make class "static" to avoid InflateException & NoSuchMethodException.
    public static class GifWebView2 extends WebView {
        // Create a constructor that takes both a context to call the constructor of the parent-class and a path to the file:
        // *** The constructor overloaded with Context & AttributeSet parameters is the most important constructor.
        public GifWebView2(Context context, AttributeSet attrs) {
            super(context, attrs);
            // Use loadUrl() to load that file into our WebView:
            // ** Gif file must be in "assets" directory (create one if it does not exist) under "main".
            loadUrl("file:///android_asset/scoreanim.gif");
        }
    }

    // _______________________________________________________ //

}
