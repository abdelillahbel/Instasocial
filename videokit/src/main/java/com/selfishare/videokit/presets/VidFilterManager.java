package com.selfishare.videokit.presets;

import java.io.File;

import project.android.imageprocessing.FastImageProcessingPipeline;
import project.android.imageprocessing.filter.BasicFilter;
import project.android.imageprocessing.input.VideoResourceInput;
import project.android.imageprocessing.output.GLTextureInputRenderer;
import project.android.imageprocessing.output.ScreenEndpoint;

/**
 * This class gets the filter to be applied and renders it
 */

public class VidFilterManager {
    private BasicFilter selectedFilter = null;
    private Filter selectedPreset = Filter.none;
    private File vidInFile, vidOutFile;
    private File picInFile;
    private static final String tag = "VidFilterManager";
    public boolean is_mute=false;


    public VidFilterManager(File inFile, File outFile) {
        this.vidInFile = inFile;
        this.vidOutFile = outFile;
    }
    public VidFilterManager(File inFile,File picOverlayFile, File outFile) {
        this.vidInFile = inFile;
        this.vidOutFile = outFile;
        this.picInFile=picOverlayFile;
    }

    public void switchFilter(FastImageProcessingPipeline pipe, ScreenEndpoint screen, VideoResourceInput input,Filter filter) {
        BasicFilter fObj = Presets.getInstance(filter);
        pipe.pauseRendering();
        //remove target from prvious filter
        if (selectedFilter != null) {

            selectedFilter.removeTarget(screen);
        }

        //remove all added renderers from input
        for (GLTextureInputRenderer target : input.getTargets()) {
            input.removeTarget(target);
        }

        //destroy previous filter
        if (selectedFilter != null) {
            pipe.addFilterToDestroy(selectedFilter);
        }

        if (fObj == null) {
            input.addTarget(screen);
            selectedFilter=null;
            selectedPreset= Filter.none;
        }else {
            selectedFilter = fObj;
            selectedFilter.addTarget(screen);
            input.addTarget(selectedFilter);
            selectedPreset = filter;
        }
        pipe.startRendering();
        System.gc();
    }

    public void isMute(boolean mute)
    {
        is_mute=mute;
    }

    public String[] getFfmpegArgs() {
        return Presets.getArgs(selectedPreset,picInFile, vidInFile, vidOutFile,is_mute);
    }
}
