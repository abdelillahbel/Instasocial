/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.utils;

import android.util.Log;

/**
 * Aspect ratio management
 */

public class AspectRatio {
    private static final String TAG="AspectRatioCalc";
    private boolean isLandscape=false;
    private double aspectRatio;

    public AspectRatio(int width, int height){
        this.isLandscape=width>height;
        this.aspectRatio=isLandscape?(width/(double)height): (height/(double)width);
        Log.d(TAG,"size: "+width+" * "+height +" aspectRatio: "+aspectRatio+" isLandscape : "+isLandscape);
    }
    public AspectRatio(double width, double height){
        this.isLandscape=width>height;
        this.aspectRatio=isLandscape?(width/height): (height/width);
        Log.d(TAG,"size: "+width+" * "+height +" aspectRatio: "+aspectRatio+" isLandscape : "+isLandscape);
    }
    public int getHeightBy(int width){
        return isLandscape?(int) Math.floor(width/aspectRatio):(int) Math.floor(width*aspectRatio);
    }
    public int getWidthBy(int height){
        return isLandscape?(int) Math.floor(height*aspectRatio):(int) Math.floor(height/aspectRatio);
    }
}
