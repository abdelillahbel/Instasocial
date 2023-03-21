/*
 *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.selfishare.videokit.presets;

import android.graphics.PointF;

import java.io.File;
import java.util.ArrayList;

import project.android.imageprocessing.filter.BasicFilter;
import project.android.imageprocessing.filter.colour.ColourInvertFilter;
import project.android.imageprocessing.filter.colour.ContrastFilter;
import project.android.imageprocessing.filter.colour.ExposureFilter;
import project.android.imageprocessing.filter.colour.GreyScaleFilter;
import project.android.imageprocessing.filter.colour.HazeFilter;
import project.android.imageprocessing.filter.colour.HueFilter;
import project.android.imageprocessing.filter.colour.SepiaFilter;
import project.android.imageprocessing.filter.effect.EmbossFilter;
import project.android.imageprocessing.filter.effect.SketchFilter;
import project.android.imageprocessing.filter.effect.VignetteFilter;
import project.android.imageprocessing.filter.processing.BoxBlurFilter;
import project.android.imageprocessing.filter.processing.SharpenFilter;

/**
 * This class is used to execute the command for filtering video
 */

public class Presets {
    /**
     * returns the filter object for selected filter
     *
     * @param filter
     * @return
     */
    public static BasicFilter getInstance(Filter filter) {
        BasicFilter obj = null;
        switch (filter) {
            case sepia:
                obj = new SepiaFilter();
                break;
            case grayscale:
                obj = new GreyScaleFilter();
                break;
            case contrest:
                obj = new ContrastFilter(2.0f);// [0.0 to 4.0]    ffmpeg [-2.0 to 2.0]
                break;
            case hue:
                obj = new HueFilter((float) Math.PI / 2.0f); // change the color  [0 to 6.28] angle in radiance      ffmpeg [hue=H=radiance value]
                break;
            case haze:
                obj = new HazeFilter(0.0f, 0.1f);   //distance [-0.3 to 0.3]   sloap [-0.3 to 0.3]   ffmpeg
                break;
            case negate:
                obj = new ColourInvertFilter();
                break;
            case boxblur:
                obj = new BoxBlurFilter();
                break;
            case emboss:
                obj = new EmbossFilter(2.0f); //emboss intensity 0.0 to 4.0, with 1.0 as the normal level
                break;
            case sharpen:
                obj = new SharpenFilter(2.0f);
                break;
            case vignette:
                PointF point = new PointF();
                float[] color = {
                        0.0f, 0.0f, 0.0f//RGB Value
                };
                point.set(0.5f, 0.5f);
                obj = new VignetteFilter(point, color, 0.3f, 0.75f); //point, colorarray[R,G,B],start,end
                break;
            case exposure:
                obj = new ExposureFilter(0.3f);
                break;
            case none:
                break;
            case sketch:
                obj = new SketchFilter();
                break;
        }
        return obj;
    }

    public static String[] getArgs(Filter filter, File picInFile, File vidInFile, File vidOutFile, boolean is_mute) {
        //ffmpeg commandline
        String value = null;
        switch (filter) {
            case sepia:
//                value = "colorchannelmixer=.393:.769:.189:0:.349:.686:.168:0:.272:.534:.131,scale=w=-1:h=360";
                value = "colorchannelmixer=.393:.769:.189:0:.349:.686:.168:0:.272:.534:.131";
                break;
            case grayscale:
                value = "colorchannelmixer=.3:.4:.3:0:.3:.4:.3:0:.3:.4:.3";
                break;
            case contrest:
                value = "eq=contrast=2.0";//between -2.0 to 2.0
                break;
            case hue:
                value = "hue=H=PI/2";
                break;
            case haze:
                value = "curves=blue='0.45/.56'";
                break;
            case negate:
                value = "negate=1";
                break;
            case boxblur:
                value = "boxblur";
                break;
            case emboss:
                value = "convolution=\"-2 -1 0 -1 1 1 0 1 2:-2 -1 0 -1 1 1 0 1 2:-2 -1 0 -1 1 1 0 1 2:-2 -1 0 -1 1 1 0 1 2\"";
                break;
            case sharpen:
                value = "convolution=\"0 -1 0 -1 5 -1 0 -1 0:0 -1 0 -1 5 -1 0 -1 0:0 -1 0 -1 5 -1 0 -1 0:0 -1 0 -1 5 -1 0 -1 0\"";
                break;
            case vignette:
                value = "vignette=PI/3";
                break;
            case exposure:
                value = "colorbalance=rs=-0.1:gs=-0.1:bs=-0.1:rm=0.02:gm=0.02:bm=0.02:rh=0.2:gh=0.2:bh=0.2";
                break;
            case sketch:
                value = "convolution=\"0 0 0 0 0 0 0 -1 0 0 0 -1 5 -1 0 0 0 -1 0 0 0 0 0 0 0\",edgedetect,negate";//strong sketch
                break;
            case none:
                value = "none";
                break;
            default:
                value = "";
                break;
        }

        //default ffmpeg filter arguments
        ArrayList<String> cmdList = new ArrayList<String>();
        if (!value.equals("")) {
            cmdList.add("-y");
            cmdList.add("-i");
            cmdList.add(vidInFile.toString());
            cmdList.add("-codec:v");
            cmdList.add("libx264"); //use high cpu
            cmdList.add("-preset");
            cmdList.add("faster"); //increase speed
            cmdList.add("-pix_fmt");
            cmdList.add("yuv420p");
            boolean isOverlay = picInFile != null && picInFile.exists();
            boolean isFilter = !value.equals("none");
            if (isOverlay || isFilter) {
                cmdList.add("-vf");
                //"movie="+picInFile.toString()+"[overlay];[in]"+"convolution=\"0 0 0 0 0 0 0 -1 0 0 0 -1 5 -1 0 0 0 -1 0 0 0 0 0 0 0\",edgedetect,negate[fil];[fil][overlay]overlay"
                if (isOverlay && isFilter) {
                    cmdList.add("movie=" + picInFile.toString() + "[overlay];[in]" + value + "[fil];[fil][overlay]overlay");
                } else if (isOverlay) {
                    //overlay only
                    cmdList.add("movie=" + picInFile.toString() + "[overlay];[in][overlay]overlay");
                } else {
                    //filter only
                    cmdList.add(value);
                }
            }

            if (is_mute)
                cmdList.add("-an");//to mute video
            cmdList.add(vidOutFile.toString());


        }
        String[] arg = new String[cmdList.size()];
        arg = cmdList.toArray(arg);
        return arg;
    }
}
