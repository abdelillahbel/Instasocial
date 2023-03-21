/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ImageView;

import com.androidapp.instasocial.R;
import com.androidapp.instasocial.modules.post.PostImageFilterActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class BlurTask extends AsyncTask<Void, Integer, Void> {
	private PostImageFilterActivity activity;
	private ImageView imageView;
	private int blurAmount;
	private Bitmap source;
	private Bitmap mask;
	private int maskPosX;
	private int maskPosY;

	private Bitmap result;

	public BlurTask(PostImageFilterActivity activity, ImageView imageView, int blurAmount, Bitmap source, int maskPosX, int maskPosY) {
		this.activity = activity;
		this.imageView = imageView;
		this.blurAmount = blurAmount;
		this.source = source;
		this.mask = BitmapUtil.getDrawableAsBitmap(activity, R.drawable.mask);
		this.maskPosX = maskPosX;
		this.maskPosY = maskPosY;
	}

	@Override
	protected void onPreExecute() {
		activity.blurFinished = false;
	}

	@Override
	protected Void doInBackground(Void... params) {
		// Get the sharp part of the picture
		Bitmap sharp = BitmapUtil.applyMask(source, mask, maskPosX, maskPosY);

		// Blur it twice
//		source = BitmapUtil.getDrawableAsBitmap(activity,R.drawable.splash_page);
		Bitmap blurryBackground = BitmapUtil.renderScriptBlur(source, activity, blurAmount);
		blurryBackground = BitmapUtil.renderScriptBlur(blurryBackground, activity, blurAmount);

		// Put all those together
		Canvas canvas = new Canvas(blurryBackground);
		Paint paint = new Paint();
		canvas.drawBitmap(sharp, maskPosX, maskPosY, paint);

		result = blurryBackground;

		return null;
	}

	@Override
	protected void onPostExecute(Void res) {
		PostImageFilterActivity.filterimg.setImage(result);
		activity.blurFinished = true;

		//SaveImage(result);
	}

	private void SaveImage(Bitmap finalBitmap) {

		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/Bitmap");
		myDir.mkdirs();
		Random generator = new Random();
		int n = 10000;
		n = generator.nextInt(n);
		String fname = "Image-"+ n +".jpg";
		File file = new File(myDir, fname);
		if (file.exists ()) file.delete ();
		try {
			FileOutputStream out = new FileOutputStream(file);
			finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
