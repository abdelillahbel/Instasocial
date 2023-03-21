/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.modules.post;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.ShutterCallback;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidapp.instasocial.R;
import com.androidapp.instasocial.ui.CustomFrontBackCameraPreview;

import java.io.File;
import java.io.FileOutputStream;
import java.util.logging.Logger;

import static android.media.ThumbnailUtils.OPTIONS_RECYCLE_INPUT;


/**
 * Class used to capture image
 */
public class PostCameraFragment extends Fragment {

    /**
     * Member variables declarations/initializations
     */
    View rootView;
    private LinearLayout cameraPreview;
    RelativeLayout  bottom_rela;
    CustomFrontBackCameraPreview mPreview;
    public static Camera camera;
    int cameraId;
    private static String TAG = "AddContestCamFragment";
    private boolean cameraFront = false;
    ImageButton capture_old;
    Camera.PictureCallback mPicture;
    Camera.PictureCallback pic_callback;
    File mFileTemp;
    private static final int OPTIONS_NONE = 0x0;
    private static final int OPTIONS_SCALE_UP = 0x1;
    Camera.CameraInfo info;
    //    boolean previewing = false;
    String imagePath = "";
    ImageView close_img, switchcamera, switchflash;
    public static int f = 0;
    public static boolean image_selected, image_from_gallery, video_selected, you_tube_selected;
    private boolean isFlashOn = false;
    Camera.Parameters p;
    TextView nxt;
    Bundle data;
    String from = "", desc = "", postingFrom = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.add_post_camera_fragment, null);

        /**
         * UI controls initialization
         */
        data = getArguments();
        from = data.getString("from", "");
        desc = data.getString("desc", "");
        postingFrom = data.getString("postingFrom", "");
        bottom_rela = (RelativeLayout) rootView.findViewById(R.id.bottom_rela);
        capture_old = (ImageButton) rootView.findViewById(R.id.capture_img);
        close_img = (ImageView) rootView.findViewById(R.id.camera_close_img);
        cameraPreview = (LinearLayout) rootView.findViewById(R.id.imagePreview);
        switchflash = (ImageView) rootView.findViewById(R.id.switch_flash);
        switchcamera = (ImageView) rootView.findViewById(R.id.camera_reverse);
        nxt = (TextView) rootView.findViewById(R.id.nxt_txt);

        close_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().finish();
            }
        });

        Context context = getActivity();
        PackageManager pm = context.getPackageManager();

        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Logger message;
            Log.e("err", "Device has no camera!");
            Toast.makeText(getActivity(),
                    "Your device doesn't have camera!", Toast.LENGTH_SHORT).show();
        }

        cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

        if (camera == null) {
            try {
                camera = Camera.open();
                p = camera.getParameters();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else{
            try {
                camera.release();
                camera = null;
                camera = Camera.open();
                p = camera.getParameters();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mPreview = new CustomFrontBackCameraPreview(getActivity(), camera, cameraId);
        cameraPreview.addView(mPreview);

        /**
         * Toggle flash logic
         */
        switchflash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFlashOn) {
                    try {
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        camera.setParameters(p);
                        isFlashOn = false;
                        Log.i("info", "torch is turned off!");
                    } catch (Exception e) {
                    }
                } else {
                    try {
                        Log.i("info", "torch is turned on!");
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(p);
                        isFlashOn = true;
                    } catch (Exception e) {
                    }
                }
            }
        });


        /**
         * Toggle camera
         */
        switchcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int camerasNumber = Camera.getNumberOfCameras();
                if (camerasNumber > 1) {
                    // release the old camera instance
                    // switch camera, from the front and the back and vice versa
                    releaseCamera();
                    chooseCamera();
                } else {
                    Toast toast = Toast.makeText(getActivity(), "Sorry, your phone has only one camera!",
                            Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        /**
         * Code setup to take a picture
         */
        capture_old.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                image_selected = true;
                video_selected = false;
                you_tube_selected = false;
                image_from_gallery = false;
                try {
                    camera.takePicture(shutterCallback, null, pic_callback);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        /**
         * Redirect to filter screen
         */
        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imagePath == null || imagePath.isEmpty()) {
                    Toast.makeText(getActivity(), "Take a Photo", Toast.LENGTH_SHORT).show();
                } else {
                    Intent nxt = new Intent(getActivity(), PostImageFilterActivity.class);
                    nxt.putExtra("imageviewpath", imagePath);
                    nxt.putExtra("from", from);
                    nxt.putExtra("desc", desc);
                    nxt.putExtra("postingFrom", postingFrom);
                    startActivity(nxt);

                }
            }
        });

        info = new Camera.CameraInfo();

        return rootView;
    }


    ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
            Log.d(TAG, "onShutter'd");
        }
    };

    /**
     * get thumbanil from the captured image
     * @param source
     * @param width
     * @param height
     * @param options
     * @return
     */
    public static Bitmap extractThumbnail(Bitmap source, int width, int height, int options) {
        if (source == null) {
            return null;
        }

        float scale;
        if (source.getWidth() < source.getHeight()) {
            scale = width / (float) source.getWidth();
        } else {
            scale = height / (float) source.getHeight();
        }
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        Bitmap thumbnail = transform(matrix, source, width, height,
                OPTIONS_SCALE_UP | options);
        return thumbnail;
    }

    /*
     * Transform source Bitmap to targeted width and height.
     */

    private static Bitmap transform(Matrix scaler,
                                    Bitmap source,
                                    int targetWidth,
                                    int targetHeight,
                                    int options) {
        boolean scaleUp = (options & OPTIONS_SCALE_UP) != 0;
        boolean recycle = (options & OPTIONS_RECYCLE_INPUT) != 0;

        int deltaX = source.getWidth() - targetWidth;
        int deltaY = source.getHeight() - targetHeight;
        if (!scaleUp && (deltaX < 0 || deltaY < 0)) {
            /*
            * In this case the bitmap is smaller, at least in one dimension,
            * than the target.  Transform it by placing as much of the image
            * as possible into the target and leaving the top/bottom or
            * left/right (or both) black.
            */
            Bitmap b2 = Bitmap.createBitmap(targetWidth, targetHeight,
                    Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b2);

            int deltaXHalf = Math.max(0, deltaX / 2);
            int deltaYHalf = Math.max(0, deltaY / 2);
            Rect src = new Rect(
                    deltaXHalf,
                    deltaYHalf,
                    deltaXHalf + Math.min(targetWidth, source.getWidth()),
                    deltaYHalf + Math.min(targetHeight, source.getHeight()));
            int dstX = (targetWidth - src.width()) / 2;
            int dstY = (targetHeight - src.height()) / 2;
            Rect dst = new Rect(
                    dstX,
                    dstY,
                    targetWidth - dstX,
                    targetHeight - dstY);
            c.drawBitmap(source, src, dst, null);
            if (recycle) {
                source.recycle();
            }
            c.setBitmap(null);
            return b2;
        }
        float bitmapWidthF = source.getWidth();
        float bitmapHeightF = source.getHeight();

        float bitmapAspect = bitmapWidthF / bitmapHeightF;
        float viewAspect = (float) targetWidth / targetHeight;

        if (bitmapAspect > viewAspect) {
            float scale = targetHeight / bitmapHeightF;
            if (scale < .9F || scale > 1F) {
                scaler.setScale(scale, scale);
            } else {
                scaler = null;
            }
        } else {
            float scale = targetWidth / bitmapWidthF;
            if (scale < .9F || scale > 1F) {
                scaler.setScale(scale, scale);
            } else {
                scaler = null;
            }
        }

        Bitmap b1;
        if (scaler != null) {
            // this is used for minithumb and crop, so we want to filter here.
            b1 = Bitmap.createBitmap(source, 0, 0,
                    source.getWidth(), source.getHeight(), scaler, true);
        } else {
            b1 = source;
        }

        if (recycle && b1 != source) {
            source.recycle();
        }

        int dx1 = Math.max(0, b1.getWidth() - targetWidth);
        int dy1 = Math.max(0, b1.getHeight() - targetHeight);

        Bitmap b2 = Bitmap.createBitmap(
                b1,
                dx1 / 2,
                dy1 / 2,
                targetWidth,
                targetHeight);

        if (b2 != b1) {
            if (recycle || b1 != source) {
                b1.recycle();
            }
        }

        return b2;
    }

    private Camera.PictureCallback getPictureCallback(final boolean cameraFront2) {
        pic_callback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                // TODO Auto-generated method stub
                Log.d(TAG, "PIC CALLBACK>>");
                camera.stopPreview();
                try {
                    Log.d(TAG, "Data: " + "[" + data + "]");
                    String state = Environment.getExternalStorageState();
                    if (Environment.MEDIA_MOUNTED.equals(state)) {
                        Log.d(TAG, "Media mounted");
                        File dir = getActivity().getDir("Instasocial_images", Context.MODE_PRIVATE);
                        try {
                            if (dir.isDirectory()) {
                                dir.delete();
                            }
                            if (dir.mkdir()) {
                                System.out.println("Directory created");

                            } else {
                                System.out.println("Directory is not created");
                            }
                            mFileTemp = new File(dir, "instasocial_img.png");
                            Log.e("DIRECTORY -----> ", String.valueOf(dir));
                            Log.e("CAPTURED IMAGE -----> ", String.valueOf(mFileTemp));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d(TAG, "MEdia NOT mounted" + getActivity().getFilesDir().getAbsolutePath());

                        mFileTemp = new File(getActivity().getFilesDir(), "instasocial_img.png");

                    }


                    Bitmap bm = BitmapFactory.decodeByteArray(data, 0, (data != null) ? data.length : 0);
//                    Camera.Size picSize=mPreview.getPictureSize();
                    int prevPicWidth = bm.getWidth();
                    int prevPicHeight = bm.getHeight();
                    System.out.println("Previous pic image width " + bm.getWidth() + bm.getHeight());
                    float prevPicAspectRatio = prevPicWidth > prevPicHeight ? (float) prevPicWidth / (float) prevPicHeight :
                            (float) prevPicHeight / (float) prevPicWidth;

                    int picHeight = 480;
                    int picWidth = (int) (picHeight * prevPicAspectRatio);

//                    int picWidth = picSize!=null?picSize.width:480;
//                    int picHeight = picSize!=null?picSize.height:640;

                    Log.e(TAG, "bitmap size from preview: " + bm.getWidth() + "*" + bm.getHeight());

                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        Log.e(TAG, "Portrait");
                        // Notice that width and height are reversed
                        Bitmap scaled = Bitmap.createScaledBitmap(bm, picWidth, picHeight, true);
                        int w = scaled.getWidth();
                        int h = scaled.getHeight();
                        // Setting post rotate to 90
                        Matrix mtx = new Matrix();
                        mtx.postRotate(90);
                        // Rotating Bitmap
                        if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//                            mtx.preRotate(90);
                            float[] mirrorY = {-1, 0, 0, 0, 1, 0, 0, 0, -1};
                            Matrix matrixMirrorY = new Matrix();
                            matrixMirrorY.setValues(mirrorY);
                            mtx.postConcat(matrixMirrorY);
                            mtx.preRotate(0);
                        }
                        bm = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);
                    } else {// LANDSCAPE MODE
                        Log.e(TAG, "landscape");
                        //No need to reverse width and height
                        Bitmap scaled = Bitmap.createScaledBitmap(bm, picWidth, picHeight, true);
                        bm = scaled;
                    }
                    Bitmap bmp = extractThumbnail(bm, 400, 400, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                    FileOutputStream outStream = new FileOutputStream(mFileTemp);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                    outStream.write(data);
                    outStream.close();
                    //new FileOperation(data,mFileTemp).execute(bm);
                    imagePath = mFileTemp.getAbsolutePath();
                    Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);

                    camera.stopPreview();
//                    mPreview.refreshCamera(camera);

                    if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        camera.setParameters(p);
                    }

                    if (imagePath == null || imagePath.isEmpty()) {
                        Toast.makeText(getActivity(), "Take a Photo", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast toast = Toast.makeText(getActivity(), "Processing...", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                        Intent nxt = new Intent(getActivity(), PostImageFilterActivity.class);
                        nxt.putExtra("imageviewpath", imagePath);
                        nxt.putExtra("from", from);
                        nxt.putExtra("desc", desc);
                        nxt.putExtra("postingFrom", postingFrom);

                        startActivity(nxt);
                        getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_main);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "onPictureTaken - jpeg");
            }
        };
        return pic_callback;
    }


    /**
     * Release camera when not in use
     */
    private void releaseCamera() {
        // stop and release camera
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    // check if the device has camera

    private boolean hasCamera(Context context) {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    public void chooseCamera() {
        // if the camera preview is the front
        if (cameraFront) {
            cameraId = findBackFacingCamera();

            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview
                f = 1;
                camera = Camera.open(cameraId);
                mPicture = getPictureCallback(cameraFront);
                mPreview.refreshCamera(camera);
            }
        } else {
            cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview
                f = 2;
                camera = Camera.open(cameraId);
                System.out.println("Front facing camera : " + cameraFront);
                mPicture = getPictureCallback(cameraFront);
                mPreview.refreshCamera(camera);
            }
        }
    }

    /**
     * open front camera
     * @return
     */
    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    /**
     * open rear camera
     * @return
     */
    private int findBackFacingCamera() {
        int cameraId = -1;
        // Search for the back facing camera
        // get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        // for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    public void onResume() {
        super.onResume();
        releaseCamera();
        if (camera == null)
            Log.d(TAG, "CustomCam - Resume - Camera is NULL");
        if (!hasCamera(getActivity())) {
            Toast toast = Toast.makeText(getActivity(), "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
        }
        if (camera == null) {
            // if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {
                Toast.makeText(getActivity(), "No front facing camera found.",
                        Toast.LENGTH_LONG).show();
            }
            camera = Camera.open(findBackFacingCamera());

            mPicture = getPictureCallback(cameraFront);
            mPreview.refreshCamera(camera);
        } else {
            if (findFrontFacingCamera() < 0) {
                // Toast.makeText(this, "No front facing camera found.",
                // Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity(), "No front facing camera found.",
                        Toast.LENGTH_LONG).show();
//                switchCamera.setVisibility(View.GONE);
            }

            camera.release();
            camera = null;
            camera = Camera.open(findBackFacingCamera());

            mPicture = getPictureCallback(cameraFront);
            mPreview.refreshCamera(camera);
        }
    }


}
