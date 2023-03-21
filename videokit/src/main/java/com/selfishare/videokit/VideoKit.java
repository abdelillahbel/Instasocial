


/*
 *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.selfishare.videokit;

import android.util.Log;

import java.util.ArrayList;

/**
 * This class is used to load the .so library files
 */
public class VideoKit {
    private static final String tag="videokit";
    private static boolean isLibLoaded=false;
    static {

        try {
            System.loadLibrary("videokit");
            isLibLoaded=true;
        }catch (UnsatisfiedLinkError e){
            isLibLoaded=false;
        }
    }
    private static native int run(String[] arguments);
    public static int execute(String arguments){
        return execute(arguments.split("\\s+"));
    }

    /**
     * This function is used to check whether the library has been loaded or not
     */
    public static int execute(String[] args){
        int retVal=1;
        try {
            if (!isLibLoaded) {
                throw new UnsatisfiedLinkError("lib****.so not loaded");
            }
            String[] params = new String[args.length + 1];
            params[0] = "ffmpeg";
            System.arraycopy(args, 0, params, 1, args.length);
            retVal= run(params);
        }catch (Exception e){
            Log.e(tag,e.toString());
        }finally {
            return retVal;
        }
    }

}
