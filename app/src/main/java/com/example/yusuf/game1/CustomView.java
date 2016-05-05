package com.example.yusuf.game1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import java.util.Random;

/**
 * The methods controlling the custom view that acts as the goal of the game.
 */

public class CustomView extends View {

    Paint paint;
    float rectX, rectY;
    float rectWidth;
    float rectHeight;

    int displayWidth;
    int displayHeight;
    int topMargin;
    int bottomMargin;

    int shapeAnimationSpeed;

    Random randomObj;

    int rectColor;


    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Rectangle shape paint object.
        paint = new Paint();
        rectColor = Color.YELLOW;
        paint.setColor(rectColor);

        // Rectangle shape properties.
        rectX = 0;
        rectY = 0;
        rectWidth = 100;
        rectHeight = 100;

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        displayWidth = displayMetrics.widthPixels;
        displayHeight = displayMetrics.heightPixels;

        // Due to the action bar, the top is not 0, but instead origin (0) + actionbar margin (32).
        topMargin = 0;

        // Due to the bottom system buttons, the bottom is not at displayHeight, but at 1776, so...
        bottomMargin = 144;

        // Rectangle shape animation speed.
        shapeAnimationSpeed = 15;

        randomObj = new Random();

    }

    // _____Drawing + Animating stage stripe views._____
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw Rectangle shape.
        canvas.drawRect(rectX,rectY,rectX+rectWidth,rectY+rectHeight,paint);
    }

    // _____Get Methods - To retrieve attributes (which you want to manipulate)._____
    // * Not being used - Since this CustomView class does not accept input, which needs to be accessed by GameView class.*
    public float getRectX() {
        return rectX;
    }
    public float getRectY() {
        return rectY;
    }

    // _____Set Methods - To retrieve attributes (which you want to manipulate)._____
    public void setRectY(float newRectY) {
        newRectY = rectY;
        // Update shape y-axis position.
        if (rectY < displayHeight - (bottomMargin + rectHeight)) { // if shape y-position is less than the display height, then...
            rectY = rectY + shapeAnimationSpeed; // Increment y-position.
        }

        else if (rectY >= displayHeight - (bottomMargin + rectHeight)) { // if shape y-position exceeds display height, then...
            rectY = topMargin; // Set shape y-position to the top (in this case, 0 (origin) + action bar height).
        }

        invalidate();
    }
    public void setRectY2(float newRectY2) {
        newRectY2 = rectY;
        // Update shape y-axis position.
        rectY = topMargin; // Set shape y-position to the top (in this case, 0 (origin) + action bar height).
        // Randomise x-position.
        int randInt2 = randomObj.nextInt( (int) (displayWidth-rectWidth) );
        rectX = randInt2;
        // Redraw
        invalidate();
    }

    public void setRectX(/*float newRectX*/) {
        //newRectX = rectX;
        // Randomise shape x-axis position.
        int randInt = randomObj.nextInt( (int) (displayWidth-rectWidth) );
        if (rectY >= displayHeight - (bottomMargin + rectHeight)) {
            rectX = randInt;
        }

        invalidate();
    }

    // Set Methods for increasing Custom View Rectangle Animation Speed - Used for when scores increase!
    public void customViewSpeed() {
        shapeAnimationSpeed += 2;
    }
    public void customViewSpeed2() {
        shapeAnimationSpeed += 4;
    }
    public void customViewSpeed3() {
        shapeAnimationSpeed += 6;
    }
    public void customViewSpeed4() {
        shapeAnimationSpeed += 8;
    }
    public void customViewSpeed5() {
        shapeAnimationSpeed += 10;
    }
    public void customViewSpeed6() {
        shapeAnimationSpeed += 12;
    }
    public void customViewSpeed7() {
        shapeAnimationSpeed += 14;
    }
    public void customViewSpeed8() {
        shapeAnimationSpeed += 16;
    }
    public void customViewSpeed9() {
        shapeAnimationSpeed += 18;
    }
    public void customViewSpeed10() {
        shapeAnimationSpeed += 20;
    }

    // Change CustomView Rectangle Shape color.
    public void setCustomViewRectColor() {
        rectColor = Color.GREEN;
        paint.setColor(rectColor);
    }
    public void setCustomViewRectColor2() {
        rectColor = Color.RED;
        paint.setColor(rectColor);
    }

}
