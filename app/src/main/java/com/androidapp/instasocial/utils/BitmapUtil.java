/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;


public class BitmapUtil {
	/**
	 * Applies mask to source bitmap. Returns a bitmap with the size
	 * of the mask (or lower)
	 * @param source Source bitmap
	 * @param mask Bitmap mask
	 * @param maskPosX X pos of the mask
	 * @param maskPosY Y pos of the mask
	 * @return Bitmap
	 */
	public static Bitmap applyMask(Bitmap source, Bitmap mask, int maskPosX, int maskPosY) {
		int maskWidth = mask.getWidth();
		int maskHeight = mask.getHeight();

		// Copy the original bitmap
		Bitmap bitmap = copy(source);
		bitmap.setHasAlpha(true);

		// If the mask is larger than the source, resize the mask
		if (mask.getWidth() > source.getWidth() || mask.getHeight() > source.getHeight())
			mask = resizeBitmap(mask, source.getWidth(), source.getHeight());

		// Crop bitmap to fit mask
		bitmap = Bitmap.createBitmap(bitmap, maskPosX, maskPosY, maskWidth, maskHeight);

		// Apply mask
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		canvas.drawBitmap(mask, 0, 0, paint);

		mask.recycle();
		return bitmap;
	}

	public static Bitmap resizeBitmap(Bitmap source, int maxWidth, int maxHeight) {
		int outWidth;
		int outHeight;
		int inWidth = source.getWidth();
		int inHeight = source.getHeight();
		if (inWidth > inHeight) {
			outWidth = maxWidth;
			outHeight = (inHeight * maxWidth) / inWidth;
		} else {
			outHeight = maxHeight;
			outWidth = (inWidth * maxHeight) / inHeight;
		}

		return Bitmap.createScaledBitmap(source, outWidth, outHeight, false);
	}

	public static Bitmap getDrawableAsBitmap(Context context, int drawable) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			options.inMutable = true;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;

		Resources res = context.getResources();
		return BitmapFactory.decodeResource(res, drawable, options);
	}

	public static Bitmap renderScriptBlur(Bitmap originalBitmap, Context context, float radius) {
		Bitmap outBitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(),
				Bitmap.Config.ARGB_8888);

		RenderScript rs = RenderScript.create(context);
		ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
		Allocation allIn = Allocation.createFromBitmap(rs, originalBitmap);
		Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);
		blurScript.setRadius(radius);
		blurScript.setInput(allIn);
		blurScript.forEach(allOut);
		allOut.copyTo(outBitmap);

		rs.destroy();

		return outBitmap;
	}

	public static Bitmap copy(Bitmap src) {
		Bitmap bitmap;
		if (src.isMutable()) {
			bitmap = src;
		} else {
			bitmap = src.copy(Bitmap.Config.ARGB_8888, true);
			src.recycle();
		}

		return bitmap;
	}
}
