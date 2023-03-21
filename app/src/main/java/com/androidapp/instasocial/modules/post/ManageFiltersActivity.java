/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.modules.post;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidapp.instasocial.R;
import com.androidapp.instasocial.ui.ImageFilter.GPUImageFilterTools;
import com.androidapp.instasocial.ui.ImageFilter.GPUImageLordKelvinFilter;
import com.androidapp.instasocial.ui.ImageFilter.GPUImageNashvilleFilter;

import jp.co.cyberagent.android.gpuimage.GPUImageAddBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageBilateralFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageBoxBlurFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageBulgeDistortionFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageCGAColorspaceFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageColorInvertFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageEmbossFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHazeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHueFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageKuwaharaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSharpenFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSketchFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageToneCurveFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.GPUImageVignetteFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageWeakPixelInclusionFilter;

/**
 * Class used to Manage the filters needed by the user
 */

public class ManageFiltersActivity extends AppCompatActivity {

    /**
     * Member variables declarations/initializations
     */
    GPUImageView img1, img2, img3, img4, img5, img6, img7, img8, img9, img10,
            img11, img12, img13, img14, img15, img16, img17, img18, img19, img20;
    Intent a;
    String imagepath, videoPath = "";
    private GPUImageFilter mFilter;
    private GPUImageFilterTools.FilterAdjuster mFilterAdjuster;
    Bitmap video1stFrame;
    RelativeLayout panel1_layout, panel2_layout, panel3_layout, panel4_layout, panel5_layout, panel6_layout, panel7_layout, panel8_layout, panel9_layout, panel10_layout, panel11_layout, panel12_layout, panel13_layout, panel14_layout, panel15_layout, panel16_layout, panel17_layout, panel18_layout, panel19_layout, panel20_layout;
    View v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19;
    TextView txtPanel1, txtPanel2, txtPanel3, txtPanel4, txtPanel5, txtPanel6, txtPanel7, txtPanel8, txtPanel9, txtPanel10, txtPanel11, txtPanel12, txtPanel13, txtPanel14, txtPanel15, txtPanel16, txtPanel17, txtPanel18, txtPanel19, txtPanel20;
    CheckBox chk1, chk2, chk3, chk4, chk5, chk6, chk7, chk8, chk9, chk10, chk11, chk12, chk13, chk14, chk15, chk16, chk17, chk18, chk19, chk20;
    ImageView doneBtn;
    public SharedPreferences sharedfilterPanel;
    public SharedPreferences.Editor sharedChecked;
    String desc,postingFrom,from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_filters);

        /**
         * UI controls initialization
         */
        sharedfilterPanel = PreferenceManager.getDefaultSharedPreferences(ManageFiltersActivity.this);
        sharedChecked = sharedfilterPanel.edit();

        a = getIntent();
        desc=a.getStringExtra("desc");
        postingFrom=a.getStringExtra("postingFrom");
        from=a.getStringExtra("from");
        if (a.getStringExtra("type").equalsIgnoreCase("video")) {
            videoPath = a.getStringExtra("videopath");
            video1stFrame = ThumbnailUtils.createVideoThumbnail(videoPath,
                    MediaStore.Images.Thumbnails.MINI_KIND);
        } else
            imagepath = a.getStringExtra("imageuri");

        img1 = (GPUImageView) findViewById(R.id.img1);
        img2 = (GPUImageView) findViewById(R.id.img2);
        img3 = (GPUImageView) findViewById(R.id.img3);
        img4 = (GPUImageView) findViewById(R.id.img4);
        img5 = (GPUImageView) findViewById(R.id.img5);
        img6 = (GPUImageView) findViewById(R.id.img6);
        img7 = (GPUImageView) findViewById(R.id.img7);
        img8 = (GPUImageView) findViewById(R.id.img8);
        img9 = (GPUImageView) findViewById(R.id.img9);
        img10 = (GPUImageView) findViewById(R.id.img10);
        img11 = (GPUImageView) findViewById(R.id.img11);
        img12 = (GPUImageView) findViewById(R.id.img12);
        img13 = (GPUImageView) findViewById(R.id.img13);
        img14 = (GPUImageView) findViewById(R.id.img14);
        img15 = (GPUImageView) findViewById(R.id.img15);
        img16 = (GPUImageView) findViewById(R.id.img16);
        img17 = (GPUImageView) findViewById(R.id.img17);
        img18 = (GPUImageView) findViewById(R.id.img18);
        img19 = (GPUImageView) findViewById(R.id.img19);
        img20 = (GPUImageView) findViewById(R.id.img20);

        panel1_layout = (RelativeLayout) findViewById(R.id.panel1_layout);
        panel2_layout = (RelativeLayout) findViewById(R.id.panel2_layout);
        panel3_layout = (RelativeLayout) findViewById(R.id.panel3_layout);
        panel4_layout = (RelativeLayout) findViewById(R.id.panel4_layout);
        panel5_layout = (RelativeLayout) findViewById(R.id.panel5_layout);
        panel6_layout = (RelativeLayout) findViewById(R.id.panel6_layout);
        panel7_layout = (RelativeLayout) findViewById(R.id.panel7_layout);
        panel8_layout = (RelativeLayout) findViewById(R.id.panel8_layout);
        panel9_layout = (RelativeLayout) findViewById(R.id.panel9_layout);
        panel10_layout = (RelativeLayout) findViewById(R.id.panel10_layout);
        panel11_layout = (RelativeLayout) findViewById(R.id.panel11_layout);
        panel12_layout = (RelativeLayout) findViewById(R.id.panel12_layout);
        panel13_layout = (RelativeLayout) findViewById(R.id.panel13_layout);
        panel14_layout = (RelativeLayout) findViewById(R.id.panel14_layout);
        panel15_layout = (RelativeLayout) findViewById(R.id.panel15_layout);
        panel16_layout = (RelativeLayout) findViewById(R.id.panel16_layout);
        panel17_layout = (RelativeLayout) findViewById(R.id.panel17_layout);
        panel18_layout = (RelativeLayout) findViewById(R.id.panel18_layout);
        panel19_layout = (RelativeLayout) findViewById(R.id.panel19_layout);
        panel20_layout = (RelativeLayout) findViewById(R.id.panel20_layout);

        v1 = (View) findViewById(R.id.v1);
        v2 = (View) findViewById(R.id.v2);
        v3 = (View) findViewById(R.id.v3);
        v4 = (View) findViewById(R.id.v4);
        v5 = (View) findViewById(R.id.v5);
        v6 = (View) findViewById(R.id.v6);
        v7 = (View) findViewById(R.id.v7);
        v8 = (View) findViewById(R.id.v8);
        v9 = (View) findViewById(R.id.v9);
        v10 = (View) findViewById(R.id.v10);
        v11 = (View) findViewById(R.id.v11);
        v12 = (View) findViewById(R.id.v12);
        v13 = (View) findViewById(R.id.v13);
        v14 = (View) findViewById(R.id.v14);
        v15 = (View) findViewById(R.id.v15);
        v16 = (View) findViewById(R.id.v16);
        v17 = (View) findViewById(R.id.v17);
        v18 = (View) findViewById(R.id.v18);
        v19 = (View) findViewById(R.id.v19);

        txtPanel1 = (TextView) findViewById(R.id.txtPanel1);
        txtPanel2 = (TextView) findViewById(R.id.txtPanel2);
        txtPanel3 = (TextView) findViewById(R.id.txtPanel3);
        txtPanel4 = (TextView) findViewById(R.id.txtPanel4);
        txtPanel5 = (TextView) findViewById(R.id.txtPanel5);
        txtPanel6 = (TextView) findViewById(R.id.txtPanel6);
        txtPanel7 = (TextView) findViewById(R.id.txtPanel7);
        txtPanel8 = (TextView) findViewById(R.id.txtPanel8);
        txtPanel9 = (TextView) findViewById(R.id.txtPanel9);
        txtPanel10 = (TextView) findViewById(R.id.txtPanel10);
        txtPanel11 = (TextView) findViewById(R.id.txtPanel11);
        txtPanel12 = (TextView) findViewById(R.id.txtPanel12);
        txtPanel13 = (TextView) findViewById(R.id.txtPanel13);
        txtPanel14 = (TextView) findViewById(R.id.txtPanel14);
        txtPanel15 = (TextView) findViewById(R.id.txtPanel15);
        txtPanel16 = (TextView) findViewById(R.id.txtPanel16);
        txtPanel17 = (TextView) findViewById(R.id.txtPanel17);
        txtPanel18 = (TextView) findViewById(R.id.txtPanel18);
        txtPanel19 = (TextView) findViewById(R.id.txtPanel19);
        txtPanel20 = (TextView) findViewById(R.id.txtPanel20);

        chk1 = (CheckBox) findViewById(R.id.chk1);
        chk2 = (CheckBox) findViewById(R.id.chk2);
        chk3 = (CheckBox) findViewById(R.id.chk3);
        chk4 = (CheckBox) findViewById(R.id.chk4);
        chk5 = (CheckBox) findViewById(R.id.chk5);
        chk6 = (CheckBox) findViewById(R.id.chk6);
        chk7 = (CheckBox) findViewById(R.id.chk7);
        chk8 = (CheckBox) findViewById(R.id.chk8);
        chk9 = (CheckBox) findViewById(R.id.chk9);
        chk10 = (CheckBox) findViewById(R.id.chk10);
        chk11 = (CheckBox) findViewById(R.id.chk11);
        chk12 = (CheckBox) findViewById(R.id.chk12);
        chk13 = (CheckBox) findViewById(R.id.chk13);
        chk14 = (CheckBox) findViewById(R.id.chk14);
        chk15 = (CheckBox) findViewById(R.id.chk15);
        chk16 = (CheckBox) findViewById(R.id.chk16);
        chk17 = (CheckBox) findViewById(R.id.chk17);
        chk18 = (CheckBox) findViewById(R.id.chk18);
        chk19 = (CheckBox) findViewById(R.id.chk19);
        chk20 = (CheckBox) findViewById(R.id.chk20);

        doneBtn = (ImageView) findViewById(R.id.doneBtn);


        /**
         * Seggregate video filters
         */
        if (a.getStringExtra("type").equalsIgnoreCase("video")) {


            panel2_layout.setVisibility(View.GONE);
            panel3_layout.setVisibility(View.GONE);
            panel6_layout.setVisibility(View.GONE);
            panel10_layout.setVisibility(View.GONE);
            panel11_layout.setVisibility(View.GONE);
            panel12_layout.setVisibility(View.GONE);
            panel17_layout.setVisibility(View.GONE);
            panel18_layout.setVisibility(View.GONE);
            panel19_layout.setVisibility(View.GONE);
            panel20_layout.setVisibility(View.GONE);

            v2.setVisibility(View.GONE);
            v3.setVisibility(View.GONE);
            v6.setVisibility(View.GONE);
            v10.setVisibility(View.GONE);
            v11.setVisibility(View.GONE);
            v12.setVisibility(View.GONE);
            v17.setVisibility(View.GONE);
            v18.setVisibility(View.GONE);
            v19.setVisibility(View.GONE);

            txtPanel1.setText("Sepia");
            txtPanel4.setText("GrayScale");
            txtPanel5.setText("Contrast");
            txtPanel7.setText("Haze");
            txtPanel8.setText("Hue");
            txtPanel9.setText("ColorInvert");
            txtPanel13.setText("BoxBlur");
            txtPanel14.setText("Emboss");
            txtPanel15.setText("Sharpen");
            txtPanel16.setText("Vignette");


            img1.setImage(video1stFrame);
            selfie1_filter(new GPUImageSepiaFilter());
            img1.requestRender();

            img4.setImage(video1stFrame);
            selfie4_filter(new GPUImageGrayscaleFilter());
            img4.requestRender();

            img5.setImage(video1stFrame);
            selfie5_filter(new GPUImageContrastFilter(4.0F));
            img5.requestRender();

            img7.setImage(video1stFrame);
            selfie7_filter(new GPUImageHazeFilter());
            img7.requestRender();

            img8.setImage(video1stFrame);
            selfie8_filter(new GPUImageHueFilter(100.0f));
            img8.requestRender();

            img9.setImage(video1stFrame);
            selfie9_filter(new GPUImageColorInvertFilter());
            img9.requestRender();

            img13.setImage(video1stFrame);
            selfie13_filter(new GPUImageBoxBlurFilter());
            img13.requestRender();

            img14.setImage(video1stFrame);
            selfie14_filter(new GPUImageEmbossFilter());
            img14.requestRender();

            img15.setImage(video1stFrame);
            selfie15_filter(new GPUImageSharpenFilter());
            img15.requestRender();

            img16.setImage(video1stFrame);
            PointF point = new PointF(0.5f, 0.5f);
            selfie16_filter(new GPUImageVignetteFilter(point, new float[]{0.0f, 0.0f, 0.0f}, 0.3f, 0.75f));
            img16.requestRender();


            chk1.setChecked(sharedfilterPanel.getBoolean("1", true));
            chk4.setChecked(sharedfilterPanel.getBoolean("2", true));
            chk5.setChecked(sharedfilterPanel.getBoolean("3", true));
            chk7.setChecked(sharedfilterPanel.getBoolean("4", true));
            chk8.setChecked(sharedfilterPanel.getBoolean("5", true));
            chk9.setChecked(sharedfilterPanel.getBoolean("6", true));
            chk13.setChecked(sharedfilterPanel.getBoolean("7", true));
            chk14.setChecked(sharedfilterPanel.getBoolean("8", true));
            chk15.setChecked(sharedfilterPanel.getBoolean("9", true));
            chk16.setChecked(sharedfilterPanel.getBoolean("10", true));


        } else {
            /**
             * Seggregate image filters
             */
            panel3_layout.setVisibility(View.GONE);
            panel18_layout.setVisibility(View.GONE);
            panel19_layout.setVisibility(View.GONE);
            panel20_layout.setVisibility(View.GONE);

            v3.setVisibility(View.GONE);
            v18.setVisibility(View.GONE);
            v19.setVisibility(View.GONE);

            txtPanel1.setText("Sepia");
            txtPanel2.setText("BoxBlur");
            txtPanel4.setText("Emboss");
            txtPanel5.setText("Hue");
            txtPanel6.setText("Nashville");
            txtPanel7.setText("Sketch");
            txtPanel8.setText("Grayscale");
            txtPanel9.setText("Colorinvert");
            txtPanel10.setText("ToneCurve");
            txtPanel11.setText("LordKelvin");
            txtPanel12.setText("Kuwahara");
            txtPanel13.setText("AddBlend");
            txtPanel14.setText("Bulge");
            txtPanel15.setText("CGAColor");
            txtPanel16.setText("WeakPixel");
            txtPanel17.setText("Bilateral");

            img1.setImage(decodeSampledBitmapFromResource(imagepath, 200, 200));
            selfie1_filter(new GPUImageSepiaFilter());
            img1.requestRender();

            img2.setImage(decodeSampledBitmapFromResource(imagepath, 200, 200));
            selfie2_filter(new GPUImageBoxBlurFilter(2.0f));
            img2.requestRender();

            img4.setImage(decodeSampledBitmapFromResource(imagepath, 200, 200));
            selfie4_filter(new GPUImageEmbossFilter());
            img4.requestRender();

            img5.setImage(decodeSampledBitmapFromResource(imagepath, 200, 200));
            selfie5_filter(new GPUImageHueFilter(5.0F));
            img5.requestRender();

            img6.setImage(decodeSampledBitmapFromResource(imagepath, 200, 200));
            selfie6_filter(new GPUImageNashvilleFilter(ManageFiltersActivity.this));
            img6.requestRender();

            img7.setImage(decodeSampledBitmapFromResource(imagepath, 200, 200));
            selfie7_filter(new GPUImageSketchFilter());
            img7.requestRender();

            img8.setImage(decodeSampledBitmapFromResource(imagepath, 200, 200));
            selfie8_filter(new GPUImageGrayscaleFilter());
            img8.requestRender();

            img9.setImage(decodeSampledBitmapFromResource(imagepath, 200, 200));
            selfie9_filter(new GPUImageColorInvertFilter());
            img9.requestRender();

            img10.setImage(decodeSampledBitmapFromResource(imagepath, 200, 200));
            selfie10_filter(new GPUImageToneCurveFilter());
            img10.requestRender();

            img11.setImage(decodeSampledBitmapFromResource(imagepath, 200, 200));
            selfie11_filter(new GPUImageLordKelvinFilter(ManageFiltersActivity.this));
            img11.requestRender();

            img12.setImage(decodeSampledBitmapFromResource(imagepath, 200, 200));
            selfie12_filter(new GPUImageKuwaharaFilter());
            img12.requestRender();

            img13.setImage(decodeSampledBitmapFromResource(imagepath, 200, 200));
            selfie13_filter(new GPUImageAddBlendFilter());
            img13.requestRender();

            img14.setImage(decodeSampledBitmapFromResource(imagepath, 200, 200));
            selfie14_filter(new GPUImageBulgeDistortionFilter());
            img14.requestRender();

            img15.setImage(decodeSampledBitmapFromResource(imagepath, 200, 200));
            selfie15_filter(new GPUImageCGAColorspaceFilter());
            img15.requestRender();

            img16.setImage(decodeSampledBitmapFromResource(imagepath, 200, 200));
            selfie16_filter(new GPUImageWeakPixelInclusionFilter());
            img16.requestRender();

            img17.setImage(decodeSampledBitmapFromResource(imagepath, 200, 200));
            selfie17_filter(new GPUImageBilateralFilter(2.0f));
            img17.requestRender();

            chk1.setChecked(sharedfilterPanel.getBoolean("one", true));
            chk2.setChecked(sharedfilterPanel.getBoolean("two", true));
            chk4.setChecked(sharedfilterPanel.getBoolean("four", true));
            chk5.setChecked(sharedfilterPanel.getBoolean("five", true));
            chk6.setChecked(sharedfilterPanel.getBoolean("six", true));
            chk7.setChecked(sharedfilterPanel.getBoolean("seven", true));
            chk8.setChecked(sharedfilterPanel.getBoolean("eight", true));
            chk9.setChecked(sharedfilterPanel.getBoolean("nine", true));
            chk10.setChecked(sharedfilterPanel.getBoolean("ten", true));
            chk11.setChecked(sharedfilterPanel.getBoolean("eleven", true));
            chk12.setChecked(sharedfilterPanel.getBoolean("twelve", true));
            chk13.setChecked(sharedfilterPanel.getBoolean("thirteen", true));
            chk14.setChecked(sharedfilterPanel.getBoolean("fourteen", true));
            chk15.setChecked(sharedfilterPanel.getBoolean("fifteen", true));
            chk16.setChecked(sharedfilterPanel.getBoolean("sixteen", true));
            chk17.setChecked(sharedfilterPanel.getBoolean("seventeen", true));
        }

        /**
         * Store the user's preference
         */
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (a.getStringExtra("type").equalsIgnoreCase("video")) {

                    if (!chk1.isChecked()) {
                        sharedChecked.putBoolean("1", false);
                        sharedChecked.commit();
                        Log.e("if", "sepia");
                        PostVideoFilterActivity.linearLayoutPanel1.setVisibility(View.GONE);
                        PostVideoFilterActivity.panel1.setFilter(new GPUImageFilter());

                    } else {
                        sharedChecked.putBoolean("1", true);
                        sharedChecked.commit();
                        Log.e("else", "sepia");
                        PostVideoFilterActivity.linearLayoutPanel1.setVisibility(View.VISIBLE);
                    }
                    if (!chk4.isChecked()) {
                        sharedChecked.putBoolean("2", false);
                        sharedChecked.commit();
                        Log.e("if", "blur");
                        PostVideoFilterActivity.panel2.setFilter(new GPUImageFilter());
                        PostVideoFilterActivity.linearLayoutPanel2.setVisibility(View.GONE);
                    } else {
                        Log.e("else", "blur");
                        sharedChecked.putBoolean(
                                "2", true);
                        sharedChecked.commit();
                        PostVideoFilterActivity.linearLayoutPanel2.setVisibility(View.VISIBLE);
                    }
                    if (!chk5.isChecked()) {
                        sharedChecked.putBoolean(
                                "3", false);
                        sharedChecked.commit();
                        PostVideoFilterActivity.panel3.setFilter(new GPUImageFilter());

                        PostVideoFilterActivity.linearLayoutPanel3.setVisibility(View.GONE);
                    } else {
                        sharedChecked.putBoolean(
                                "3", true);
                        sharedChecked.commit();
                        PostVideoFilterActivity.linearLayoutPanel3.setVisibility(View.VISIBLE);
                    }
                    if (!chk7.isChecked()) {
                        sharedChecked.putBoolean(
                                "4", false);
                        sharedChecked.commit();
                        PostVideoFilterActivity.panel4.setFilter(new GPUImageFilter());

                        PostVideoFilterActivity.linearLayoutPanel4.setVisibility(View.GONE);
                    } else {
                        sharedChecked.putBoolean(
                                "4", true);
                        sharedChecked.commit();
                        PostVideoFilterActivity.linearLayoutPanel4.setVisibility(View.VISIBLE);
                    }
                    if (!chk8.isChecked()) {
                        sharedChecked.putBoolean(
                                "5", false);
                        sharedChecked.commit();
                        PostVideoFilterActivity.panel5.setFilter(new GPUImageFilter());

                        PostVideoFilterActivity.linearLayoutPanel5.setVisibility(View.GONE);
                    } else {
                        sharedChecked.putBoolean(
                                "5", true);
                        sharedChecked.commit();
                        PostVideoFilterActivity.linearLayoutPanel5.setVisibility(View.VISIBLE);
                    }
                    if (!chk9.isChecked()) {
                        sharedChecked.putBoolean(
                                "6", false);
                        sharedChecked.commit();
                        PostVideoFilterActivity.panel6.setFilter(new GPUImageFilter());

                        PostVideoFilterActivity.linearLayoutPanel6.setVisibility(View.GONE);
                    } else {
                        sharedChecked.putBoolean(
                                "6", true);
                        sharedChecked.commit();
                        PostVideoFilterActivity.linearLayoutPanel6.setVisibility(View.VISIBLE);
                    }
                    if (!chk13.isChecked()) {
                        sharedChecked.putBoolean(
                                "7", false);
                        sharedChecked.commit();
                        PostVideoFilterActivity.panel7.setFilter(new GPUImageFilter());

                        PostVideoFilterActivity.linearLayoutPanel7.setVisibility(View.GONE);
                    } else {
                        sharedChecked.putBoolean(
                                "7", true);
                        sharedChecked.commit();
                        PostVideoFilterActivity.linearLayoutPanel7.setVisibility(View.VISIBLE);
                    }
                    if (!chk14.isChecked()) {
                        sharedChecked.putBoolean(
                                "8", false);
                        sharedChecked.commit();
                        PostVideoFilterActivity.panel8.setFilter(new GPUImageFilter());

                        PostVideoFilterActivity.linearLayoutPanel8.setVisibility(View.GONE);
                    } else {
                        sharedChecked.putBoolean(
                                "8", true);
                        sharedChecked.commit();
                        PostVideoFilterActivity.linearLayoutPanel8.setVisibility(View.VISIBLE);
                    }
                    if (!chk15.isChecked()) {
                        sharedChecked.putBoolean(
                                "9", false);
                        sharedChecked.commit();
                        PostVideoFilterActivity.panel9.setFilter(new GPUImageFilter());

                        PostVideoFilterActivity.linearLayoutPanel9.setVisibility(View.GONE);
                    } else {
                        sharedChecked.putBoolean(
                                "9", true);
                        sharedChecked.commit();
                        PostVideoFilterActivity.linearLayoutPanel9.setVisibility(View.VISIBLE);
                    }
                    if (!chk16.isChecked()) {
                        sharedChecked.putBoolean(
                                "10", false);
                        sharedChecked.commit();
                        PostVideoFilterActivity.panel10.setFilter(new GPUImageFilter());

                        PostVideoFilterActivity.linearLayoutPanel10.setVisibility(View.GONE);
                    } else {
                        sharedChecked.putBoolean(
                                "10", true);
                        sharedChecked.commit();
                        PostVideoFilterActivity.linearLayoutPanel10.setVisibility(View.VISIBLE);
                    }
                    Intent newIntent =new Intent(ManageFiltersActivity.this,PostVideoFilterActivity.class);
                    newIntent.putExtra("from","manage");
                    newIntent.putExtra("imageviewpath",imagepath);
                    newIntent.putExtra("desc",desc);
                    newIntent.putExtra("from",from);
                    newIntent.putExtra("videopath",videoPath);
                    newIntent.putExtra("postingFrom",postingFrom);
                    startActivity(newIntent);

                    finish();
                    overridePendingTransition(R.anim.open_next, R.anim.close_main);
                } else {

                    if (!chk1.isChecked()) {
                        sharedChecked.putBoolean("one", false);
                        sharedChecked.commit();

                        Log.e("if", "sepia");
                      PostImageFilterActivity.panel1.setFilter(new GPUImageFilter());
                         PostImageFilterActivity.linearLayoutPanel1.setVisibility(View.INVISIBLE);
                        PostImageFilterActivity.panel1.setVisibility(View.GONE);

                    } else {
                        sharedChecked.putBoolean("one", true);
                        sharedChecked.commit();
                        Log.e("else", "sepia");
                      PostImageFilterActivity.linearLayoutPanel1.setVisibility(View.VISIBLE);
                        PostImageFilterActivity.panel1.setVisibility(View.VISIBLE);

                    }

                    if (!chk2.isChecked()) {
                        sharedChecked.putBoolean("two", false);
                        sharedChecked.commit();
                        Log.e("if", "blur");
                    } else {
                        Log.e("else", "blur");
                        sharedChecked.putBoolean("two", true);
                        sharedChecked.commit();
                    }

                    if (!chk4.isChecked()) {
                        sharedChecked.putBoolean("four", false);
                        sharedChecked.commit();
                        PostImageFilterActivity.linearLayoutPanel4.setVisibility(View.INVISIBLE);
                        PostImageFilterActivity.panel4.setFilter(new GPUImageFilter());
                    } else {
                        sharedChecked.putBoolean("four", true);
                        sharedChecked.commit();
                        PostImageFilterActivity.linearLayoutPanel4.setVisibility(View.VISIBLE);
                    }

                    if (!chk5.isChecked()) {
                        sharedChecked.putBoolean("five", false);
                        sharedChecked.commit();
                        PostImageFilterActivity.linearLayoutPanel5.setVisibility(View.GONE);
                        PostImageFilterActivity.panel5.setFilter(new GPUImageFilter());
                    } else {
                        sharedChecked.putBoolean("five", true);
                        sharedChecked.commit();
                        PostImageFilterActivity.linearLayoutPanel5.setVisibility(View.VISIBLE);
                    }

                    if (!chk6.isChecked()) {
                        sharedChecked.putBoolean("six", false);
                        sharedChecked.commit();
                        PostImageFilterActivity.linearLayoutPanel6.setVisibility(View.GONE);
                        PostImageFilterActivity.panel6.setFilter(new GPUImageFilter());

                    } else {
                        sharedChecked.putBoolean("six", true);
                        sharedChecked.commit();
                        PostImageFilterActivity.linearLayoutPanel6.setVisibility(View.VISIBLE);
                    }

                    if (!chk7.isChecked()) {
                        sharedChecked.putBoolean("seven", false);
                        sharedChecked.commit();
                        PostImageFilterActivity.linearLayoutPanel7.setVisibility(View.GONE);
                        PostImageFilterActivity.panel7.setFilter(new GPUImageFilter());

                    } else {
                        sharedChecked.putBoolean("seven", true);
                        sharedChecked.commit();
                        PostImageFilterActivity.linearLayoutPanel7.setVisibility(View.VISIBLE);

                    }

                    if (!chk8.isChecked()) {
                        sharedChecked.putBoolean("eight", false);
                        sharedChecked.commit();
                        PostImageFilterActivity.linearLayoutPanel8.setVisibility(View.GONE);
                        PostImageFilterActivity.panel8.setFilter(new GPUImageFilter());

                    } else {
                        sharedChecked.putBoolean("eight", true);
                        sharedChecked.commit();
                        PostImageFilterActivity.linearLayoutPanel8.setVisibility(View.VISIBLE);
                    }

                    if (!chk9.isChecked()) {
                        sharedChecked.putBoolean("nine", false);
                        sharedChecked.commit();
                        PostImageFilterActivity.linearLayoutPanel9.setVisibility(View.GONE);
                        PostImageFilterActivity.panel9.setFilter(new GPUImageFilter());

                    } else {
                        sharedChecked.putBoolean("nine", true);
                        sharedChecked.commit();
                        PostImageFilterActivity.linearLayoutPanel9.setVisibility(View.VISIBLE);
                    }

                    if (!chk10.isChecked()) {
                        sharedChecked.putBoolean("ten", false);
                        sharedChecked.commit();
                        PostImageFilterActivity.panel10.setFilter(new GPUImageFilter());
                        PostImageFilterActivity.linearLayoutPanel10.setVisibility(View.GONE);
                    } else {
                        sharedChecked.putBoolean("ten", true);
                        sharedChecked.commit();
                        PostImageFilterActivity.linearLayoutPanel10.setVisibility(View.VISIBLE);
                    }

                    if (!chk11.isChecked()) {
                        sharedChecked.putBoolean("eleven", false);
                        sharedChecked.commit();
                        PostImageFilterActivity.linearLayoutPanel11.setVisibility(View.GONE);
                        PostImageFilterActivity.panel11.setFilter(new GPUImageFilter());

                    } else {
                        sharedChecked.putBoolean("eleven", true);
                        sharedChecked.commit();
                        PostImageFilterActivity.linearLayoutPanel11.setVisibility(View.VISIBLE);
                    }

                    if (!chk12.isChecked()) {
                        sharedChecked.putBoolean("twelve", false);
                        sharedChecked.commit();
                        PostImageFilterActivity.linearLayoutPanel12.setVisibility(View.GONE);
                        PostImageFilterActivity.panel12.setFilter(new GPUImageFilter());

                    } else {
                        sharedChecked.putBoolean("twelve", true);
                        sharedChecked.commit();
                        PostImageFilterActivity.linearLayoutPanel12.setVisibility(View.VISIBLE);
                    }

                    if (!chk13.isChecked()) {
                        sharedChecked.putBoolean("thirteen", false);
                        sharedChecked.commit();
                        PostImageFilterActivity.linearLayoutPanel13.setVisibility(View.GONE);
                        PostImageFilterActivity.panel13.setFilter(new GPUImageFilter());

                    } else {
                        sharedChecked.putBoolean("thirteen", true);
                        sharedChecked.commit();
                        PostImageFilterActivity.linearLayoutPanel13.setVisibility(View.VISIBLE);
                    }

                    if (!chk14.isChecked()) {
                        sharedChecked.putBoolean("fourteen", false);
                        sharedChecked.commit();
                        PostImageFilterActivity.linearLayoutPanel14.setVisibility(View.GONE);
                        PostImageFilterActivity.panel14.setFilter(new GPUImageFilter());

                    } else {
                        sharedChecked.putBoolean("fourteen", true);
                        sharedChecked.commit();
                        PostImageFilterActivity.linearLayoutPanel14.setVisibility(View.VISIBLE);
                    }

                    if (!chk15.isChecked()) {
                        sharedChecked.putBoolean("fifteen", false);
                        sharedChecked.commit();
                        PostImageFilterActivity.linearLayoutPanel15.setVisibility(View.GONE);
                        PostImageFilterActivity.panel15.setFilter(new GPUImageFilter());

                    } else {
                        sharedChecked.putBoolean("fifteen", true);
                        sharedChecked.commit();
                        PostImageFilterActivity.linearLayoutPanel15.setVisibility(View.VISIBLE);
                    }

                    if (!chk16.isChecked()) {
                        sharedChecked.putBoolean("sixteen", false);
                        sharedChecked.commit();
                        PostImageFilterActivity.linearLayoutPanel16.setVisibility(View.GONE);
                        PostImageFilterActivity.panel16.setFilter(new GPUImageFilter());

                    } else {
                        sharedChecked.putBoolean("sixteen", true);
                        sharedChecked.commit();
                        PostImageFilterActivity.linearLayoutPanel16.setVisibility(View.VISIBLE);
                    }

                    if (!chk17.isChecked()) {
                        sharedChecked.putBoolean("seventeen", false);
                        sharedChecked.commit();
                        PostImageFilterActivity.linearLayoutPanel17.setVisibility(View.GONE);
                        PostImageFilterActivity.panel17.setFilter(new GPUImageFilter());

                    } else {
                        sharedChecked.putBoolean("seventeen", true);
                        sharedChecked.commit();
                        PostImageFilterActivity.linearLayoutPanel17.setVisibility(View.VISIBLE);
                    }
                    Intent newIntent =new Intent(ManageFiltersActivity.this,PostImageFilterActivity.class);
                    newIntent.putExtra("from","manage");
                    newIntent.putExtra("imageviewpath",imagepath);
                    newIntent.putExtra("desc",desc);
                    newIntent.putExtra("from",from);
                    newIntent.putExtra("videopath",videoPath);
                    newIntent.putExtra("postingFrom",postingFrom);
                    startActivity(newIntent);
                    finish();
                    overridePendingTransition(R.anim.open_next, R.anim.close_main);
                }

            }

        });
    }


    /**
     * Methods used to apply filters -- starts here --
     * @param filter
     */
    private void selfie1_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            img1.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie2_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            img2.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie3_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            img3.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie4_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            img4.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie5_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            img5.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie6_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            img6.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie7_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            img7.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie8_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            img8.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie9_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            img9.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie10_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            img10.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie11_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            img11.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie12_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            img12.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie13_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            img13.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie14_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            img14.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie15_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            img15.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }


    private void selfie16_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            img16.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }


    private void selfie17_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            img17.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie18_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            img18.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie19_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            img19.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }


    private void selfie20_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            img20.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    /**
     * Methods used to apply filters -- ends here --
     */


    /**
     * Image size calculation
    */
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 2;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }

        return inSampleSize;
    }

    public Bitmap decodeSampledBitmapFromResource(String resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(resId, options);
    }
}
