/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class is used for display of custom front and back camera preview
 */
public class CustomFrontBackCameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "CustomCameraPreview";
    private SurfaceHolder mHolder;
    boolean previewing = false;
    private Camera mCamera;
    Context context;
    int mCameraId;
    private int viewWidth;
    private int viewHeight;
    private Camera.Size pictureSize = null;
    private Camera.Size previewSize = null;



    @SuppressWarnings("deprecation")
    public CustomFrontBackCameraPreview(Context context, Camera camera, int cameraId) {
        super(context);
        mCamera = camera;
        this.context = context;
        this.mCameraId = cameraId;
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        Log.e(TAG, "initiated");

    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "surfaceCreated");
        try {
            // create the surface and start camera preview
            if (mCamera != null) {
                mCamera.setDisplayOrientation(90);
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
                setCamera(mCamera);
                previewing = true;
            }
        } catch (Exception e) {
            Log.d(VIEW_LOG_TAG,
                    "Error setting camera preview: " + e.getMessage());
        }
    }

    public void refreshCamera(Camera camera) {
        Log.e(TAG, "refreshCamera");
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        // stop preview before making changes
        try {
            mCamera.stopPreview();
            mCamera.release();//new code
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }
        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        setCamera(camera);


        try {

            Camera.Parameters param = mCamera.getParameters();
            List<Camera.Size> previewSizes = param.getSupportedPreviewSizes();
            List<Camera.Size> pictureSizes = param.getSupportedPictureSizes();
            Collections.sort(previewSizes, new Comparator<Camera.Size>() {
                //sort previewsize ascending order by height
                @Override
                public int compare(Camera.Size lhs, Camera.Size rhs) {
                    Integer w1 = lhs.height;
                    Integer w2 = rhs.height;
                    return w1.compareTo(w2);
                }
            });
            Collections.sort(pictureSizes, new Comparator<Camera.Size>() {
                //sort picture size ascending order by height
                @Override
                public int compare(Camera.Size lhs, Camera.Size rhs) {
                    Integer w1 = lhs.height;
                    Integer w2 = rhs.height;
                    return w1.compareTo(w2);
                }
            });
            StringBuilder sb = new StringBuilder();
            sb.append("previewSize: ");
            for (Camera.Size s : previewSizes) {
                sb.append(s.width + "*" + s.height + " ");
            }
            sb.append("\npictureSize: ");
            for (Camera.Size s : pictureSizes) {
                sb.append(s.width + "*" + s.height + " ");
            }

            Log.e(TAG, sb.toString());

            pictureSize = choosePictureSize(pictureSizes);


            // Find out if we need to swap dimension to get the preview size relative to sensor
            // coordinate.
            Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int displayRotation = display.getRotation();
            Log.e(TAG, "Display rotation: " + displayRotation);
            switch (displayRotation) {
                case Surface.ROTATION_0:
                    mCamera.setDisplayOrientation(90);
                    break;
                case Surface.ROTATION_90:
                    break;
                case Surface.ROTATION_180:
                    break;
                case Surface.ROTATION_270:
                    mCamera.setDisplayOrientation(180);
                    break;
                default:
                    Log.e(TAG, "Display rotation is invalid: " + displayRotation);
            }

            previewSize = chooseOptimalSize(previewSizes, pictureSize);

            Log.e(TAG, "PictureSize: " + pictureSize.width + "*" + pictureSize.height + " previewSize:" + previewSize.width + "*" + previewSize.height );
            param.setJpegQuality(100);


            param.setPictureSize(pictureSize.width, pictureSize.height);
            param.setPreviewSize(previewSize.width, previewSize.height);


            mCamera.setParameters(param);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
            previewing = true;
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        Log.e(TAG, "surfaceChanged");
        try {
            mCamera.stopPreview();
            previewing = false;
        } catch (Exception e) {

        }

        try {

            Camera.Parameters param = mCamera.getParameters();
            List<Camera.Size> previewSizes = param.getSupportedPreviewSizes();
            List<Camera.Size> pictureSizes = param.getSupportedPictureSizes();
            Collections.sort(previewSizes, new Comparator<Camera.Size>() {
                //sort previewsize ascending order by height
                @Override
                public int compare(Camera.Size lhs, Camera.Size rhs) {
                    Integer w1 = lhs.height;
                    Integer w2 = rhs.height;
                    return w1.compareTo(w2);
                }
            });
            Collections.sort(pictureSizes, new Comparator<Camera.Size>() {
                //sort picture size ascending order by height
                @Override
                public int compare(Camera.Size lhs, Camera.Size rhs) {
                    Integer w1 = lhs.height;
                    Integer w2 = rhs.height;
                    return w1.compareTo(w2);
                }
            });
            StringBuilder sb = new StringBuilder();
            sb.append("previewSize: ");
            for (Camera.Size s : previewSizes) {
                sb.append(s.width + "*" + s.height + " ");
            }
            sb.append("\npictureSize: ");
            for (Camera.Size s : pictureSizes) {
                sb.append(s.width + "*" + s.height + " ");
            }

            Log.e(TAG, sb.toString());
            pictureSize = choosePictureSize(pictureSizes);


            // Find out if we need to swap dimension to get the preview size relative to sensor
            // coordinate.
            Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int displayRotation = display.getRotation();
            int rotatedPreviewWidth = w;
            int rotatedPreviewHeight = h;
            switch (displayRotation) {
                case Surface.ROTATION_0:
                    rotatedPreviewWidth = h;
                    rotatedPreviewHeight = w;
                    mCamera.setDisplayOrientation(90);
                    break;
                case Surface.ROTATION_90:
                    rotatedPreviewWidth = w;
                    rotatedPreviewHeight = h;
                    break;
                case Surface.ROTATION_180:
                    rotatedPreviewWidth = h;
                    rotatedPreviewHeight = w;
                    break;
                case Surface.ROTATION_270:
                    rotatedPreviewWidth = w;
                    rotatedPreviewHeight = h;
                    mCamera.setDisplayOrientation(180);
                    break;
                default:
                    Log.e(TAG, "Display rotation is invalid: " + displayRotation);
            }

            previewSize = chooseOptimalSize(previewSizes,  pictureSize);

            Log.e(TAG, "PictureSize: " + pictureSize.width + "*" + pictureSize.height + " previewSize:" + previewSize.width + "*" + previewSize.height + " rotatedSize " + rotatedPreviewWidth + "*" + rotatedPreviewHeight);
            param.setJpegQuality(100);

            param.setPictureSize(pictureSize.width, pictureSize.height);
            param.setPreviewSize(previewSize.width, previewSize.height);


            // We fit the aspect ratio of  to the size of preview we picked.
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setViewSize(previewSize.width, previewSize.height);
            } else {
                setViewSize(previewSize.height, previewSize.width);
            }

            mCamera.setParameters(param);
            mCamera.startPreview();
            previewing = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setCamera(Camera camera) {
        // method to set a camera instance
        mCamera = camera;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, "surfaceDestroyed");
        if (mCamera != null) {
            mCamera.release();
        }

    }

    public void setViewSize(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        viewWidth = width;
        viewHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        Log.e(TAG, "onMeasure(" + width + ", " + height + ")");
        if (0 == viewWidth || 0 == viewHeight) {
            setMeasuredDimension(width, height);
        } else {
            if (width < height * viewWidth / viewHeight) {
                setMeasuredDimension(width, width * viewHeight / viewWidth);
            } else {
                setMeasuredDimension(height * viewWidth / viewHeight, height);
            }
        }
    }

    private static Camera.Size chooseOptimalSize(List<Camera.Size> choices, Camera.Size aspectRatio) {
        List<Camera.Size> bigEnough = new ArrayList<Camera.Size>();
        int w = aspectRatio.width;
        int h = aspectRatio.height;
        for (Camera.Size option : choices) {
            if ((option.width == ((option.height * w) / h))) {
                bigEnough.add(option);
            }
        }
        if (bigEnough.size() > 0) {
            return Collections.max(bigEnough, new Comparator<Camera.Size>() {
                @Override
                public int compare(Camera.Size lhs, Camera.Size rhs) {
                    return Long.signum((long) lhs.width * lhs.height - (long) rhs.width * rhs.height);
                }
            });
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices.get(0);
        }
    }

    private static Camera.Size choosePictureSize(List<Camera.Size> choices) {
        //select lowest resolution with 4:3 aspect ratio
        for (Camera.Size size : choices) {
            if (size.width == (size.height * 4 / 3) && size.height <= 1024 && size.width > 320) {
//            if (size.width == (size.height * 4 / 3)) {
                return size;
            }
        }
        Log.e(TAG, "choosePictureSize(): Couldn't find any suitable picture size");
        return choices.get(choices.size() - 1);
    }


}