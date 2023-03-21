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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * This class is used for display of custom front and back camera for video preview
 */
public class VideoPreview extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = "VidPreview";
	private SurfaceHolder mHolder;
	private static Camera mCamera;
	boolean previewing = false;
	private int viewWidth;
	private int viewHeight;
	private Camera.Size videoSize = null;
//	private Camera.Size previewSize = null;
private Camera.Size pictureSize = null;

	@SuppressWarnings("deprecation")
	public VideoPreview(Context context, Camera camera) {
		super(context);
		mCamera = camera;
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
			}
		} catch (IOException e) {
			Log.d(VIEW_LOG_TAG, "Error setting camera preview: " + e.getMessage());
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
			List<Camera.Size> videoSizes = param.getSupportedVideoSizes();
			List<Camera.Size> picSizes = param.getSupportedPictureSizes();
			Collections.sort(previewSizes, new Comparator<Camera.Size>() {
				//sort previewsize ascending order by height
				@Override
				public int compare(Camera.Size lhs, Camera.Size rhs) {
					Integer w1 = lhs.height;
					Integer w2 = rhs.height;
					return w1.compareTo(w2);
				}
			});
			Collections.sort(videoSizes, new Comparator<Camera.Size>() {
				//sort picture size ascending order by height
				@Override
				public int compare(Camera.Size lhs, Camera.Size rhs) {
					Integer w1 = lhs.height;
					Integer w2 = rhs.height;
					return w1.compareTo(w2);
				}
			});
			Collections.sort(picSizes, new Comparator<Camera.Size>() {
				//sort picturesize ascending order by height
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
			sb.append("\nvideoSize: ");
			for (Camera.Size s : videoSizes) {
				sb.append(s.width + "*" + s.height + " ");
			}

			Log.e(TAG, sb.toString());

			videoSize = chooseVideoSize(videoSizes);
			pictureSize=chooseVideoSize(picSizes);


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

			Camera.Size previewSize = chooseOptimalSize(previewSizes, videoSize);

			Log.e(TAG, "VideoSize: " + videoSize.width + "*" + videoSize.height + " previewSize:" + previewSize.width + "*" + previewSize.height );

			param.setPreviewSize(previewSize.width, previewSize.height);

			mCamera.setParameters(param);
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();
			previewing = true;
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public Camera.Size getPictureSize() {
		return pictureSize;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		if (previewing) {
			mCamera.stopPreview();
			previewing = false;
		}
		Camera.Size previewSize = null;
		Camera.Parameters param = mCamera.getParameters();
		List<Camera.Size> previewSizes = param.getSupportedPreviewSizes();
		List<Camera.Size> videoSizes = param.getSupportedVideoSizes();
		List<Camera.Size> picSizes = param.getSupportedPictureSizes();

		Collections.sort(previewSizes, new Comparator<Camera.Size>() {
			//sort previewsize ascending order by height
			@Override
			public int compare(Camera.Size lhs, Camera.Size rhs) {
				Integer w1 = lhs.height;
				Integer w2 = rhs.height;
				return w1.compareTo(w2);
			}
		});
		Collections.sort(videoSizes, new Comparator<Camera.Size>() {
			//sort picturesize ascending order by height
			@Override
			public int compare(Camera.Size lhs, Camera.Size rhs) {
				Integer w1 = lhs.height;
				Integer w2 = rhs.height;
				return w1.compareTo(w2);
			}
		});

		Collections.sort(picSizes, new Comparator<Camera.Size>() {
			//sort picturesize ascending order by height
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
		sb.append("\nvideoSize: ");
		for (Camera.Size s : previewSizes) {
			sb.append(s.width + "*" + s.height + " ");
		}

		Log.e(TAG, sb.toString());


		videoSize = chooseVideoSize(videoSizes);
		pictureSize=chooseVideoSize(picSizes);

		// Find out if we need to swap dimension to get the preview size relative to sensor
		// coordinate.
		Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int displayRotation = display.getRotation();
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
				Log.e("Test", "Display rotation is invalid: " + displayRotation);
		}
		previewSize = chooseOptimalSize(previewSizes,  videoSize);
		Log.e("test", "VideoSize: " + videoSize.width + "*" + videoSize.height + " previewSize:" + previewSize.width + "*" + previewSize.height);
//        holder.setFixedSize(previewSize.width, previewSize.height);
		param.setPreviewSize(previewSize.width,previewSize.height);

		int orientation = getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setViewSize(previewSize.width,previewSize.height);
		} else {
			setViewSize(previewSize.height,previewSize.width);
		}
		mCamera.setParameters(param);
		mCamera.startPreview();
		previewing = true;

	}




	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		 mCamera.release();
	}

	public Camera.Size getVideoSize() {
		return videoSize;
	}

	public void setViewSize(int width, int height) {
		if (width < 0 || height < 0) {
			throw new IllegalArgumentException("Size cannot be negative.");
		}
		viewWidth = width;
		viewHeight = height;
		requestLayout();
	}

	public void setCamera(Camera camera) {
		//method to set a camera instance
		mCamera = camera;
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
	private static Camera.Size chooseVideoSize(List<Camera.Size> choices) {
		//select lowest resolution with 4:3 aspect ratio
		for (Camera.Size size : choices) {
			if (size.width == (size.height * 4 / 3) &&  size.height <= 1024 && size.width >= 320) {
				return size;
			}
		}
		Log.e(TAG, "choosePictureSize(): Couldn't find any suitable picture size");
		return choices.get(choices.size() - 1);
	}

}