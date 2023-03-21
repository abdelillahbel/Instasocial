/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.modules.post;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.androidapp.instasocial.App;
import com.androidapp.instasocial.R;
import com.androidapp.instasocial.ui.ImageFilter.GPUImageDarkShadow;
import com.androidapp.instasocial.ui.ImageFilter.GPUImageFilterTools;
import com.androidapp.instasocial.ui.ImageFilter.GPUImageHiglightFilter;
import com.androidapp.instasocial.ui.ImageFilter.GPUImageLordKelvinFilter;
import com.androidapp.instasocial.ui.ImageFilter.GPUImageNashvilleFilter;
import com.androidapp.instasocial.utils.AspectRatio;
import com.androidapp.instasocial.utils.BitmapUtil;
import com.androidapp.instasocial.utils.BitmapUtils;
import com.androidapp.instasocial.utils.Preferences;
import com.fenchtose.nocropper.CropperImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageAddBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageBilateralFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageBoxBlurFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageBulgeDistortionFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageCGAColorspaceFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageColorInvertFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageColorMatrixFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageEmbossFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHighlightShadowFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHueFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageKuwaharaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSaturationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSharpenFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSketchFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageToneCurveFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.GPUImageVignetteFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageWeakPixelInclusionFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageWhiteBalanceFilter;

/**
 * Image filter class
 */

public class PostImageFilterActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Member variables declarations/initializations
     */

    public static final String TAG = "ImageFilterActivity";
    Intent a;
    String imagepath;
    FrameLayout framelay_img;
    public static GPUImageView filterimg;
    TextView next_txt, seekbar_count, control_seekbar_count;
    public static ImageView adjustfilterimg;
    CropperImageView cropperImageView;
    ImageView panelplate, setting;
    ImageView back, bright, seek_close, seek_tick, manage, control_seek_close, control_seek_tick_img;
    public static LinearLayout linearLayoutPanel_normal, linearLayoutPanel1, linearLayoutPanel2, linearLayoutPanel4, linearLayoutPanel5, linearLayoutPanel6, linearLayoutPanel7,
            linearLayoutPanel8, linearLayoutPanel9, linearLayoutPanel10, linearLayoutPanel11, linearLayoutPanel12, linearLayoutPanel13, linearLayoutPanel14,
            linearLayoutPanel15, linearLayoutPanel16, linearLayoutPanel17, managelinear;
    public static GPUImageView normal_img, panel1, panel2, panel3, panel4, panel5, panel6, panel7, panel8, panel9, panel10,
            panel11, panel12, panel13, panel14, panel15, panel16, panel17, panel18, panel19, panel20;
    GPUImage gp;
    SeekBar seekBar, control_seekbar;
    int progress = 0, progress2 = 0;
    View lineview;
    LinearLayout customizeicon_linear;
    HorizontalScrollView horizontalpanels, horizontalsettings;
    RelativeLayout seekbarrela, set_controls_seekbarrela, adjust_linear, colors_rela, tilt_shift_rela;

    private GPUImageFilter mFilter, tempfilter;
    private GPUImageFilterTools.FilterAdjuster mFilterAdjuster;
    ImageView adjust, brightness, contrast, structure, warmth, saturation, color, fade, highlights, shadow, vignette, sharpen;
    Bitmap bitmap2, effect_applied_bitmap, rotate_bitmap;
    String filter_imgg;
    SharedPreferences sp;
    SharedPreferences.Editor edsp;
    Bitmap mBitmap, filtered_bitmap;
    int rotate;
    File mFileTemp;

    TextView txtCrop, txtBrightness, txtContrast, txtStructure, txtWarmth,
            txtSaturation, txtColor, txtFade, txtHighlights, txtShadow, txtVignette,txtSharpen;
    LinearLayout layoutCrop,layoutBrightness,layoutContrast,layoutStructure,layoutWarmth,layoutSaturation,layoutColor,layoutFade,layoutHighlights,layoutShadow,layoutVignette,layoutSharpen;

    //----------------------------------
    //---------------------for store filters progressbar values-------------------------
    String clickedfilter;
    int brightvalue = 0, contrastvalue = 50, structurevalue = 0, warmthvalue = 50, saturationvalue = 50, fadevalue = 0, highlightvalue = 0,
            shadowvalue = 0, vignettevalue = 0, sharpenvalue = 0;
    String brighttxt = "0", contrasttxt = "0", structuretxt = "0", warmthtxt = "0", saturationtxt = "0", fadetxt = "0", highlighttxt = "0",
            shadowtxt = "0", vignettetxt = "0", sharpentxt = "0";

    //---------------------seekbarsvisible from where----------------------------------

    String brightfrom;

    //--------------------for adjust image--------------------------
    ImageView adjust_close_img, adjust_tick_img;
    ImageView rotate_img;

    //--------------------for colors effects------------------------
    String colorseffect;
    TextView color_shadow_txt, color_highlight_txt;
    HorizontalScrollView shad__color_horizontal, high__color_horizontal;
    //shadow colors
    ImageView white_shad, red_shad, green_shad, blue_shad, yellow_shad, lightblue_shad, purple_shad, gray_shad, orange_shad;
    //highlight colors effects
    ImageView white_high, red_high, green_high, blue_high, yellow_high, lightblue_high, purple_high, gray_high, orange_high;
    ImageView colors_close, colors_tick;


    //-------------------for tiltshift-----------------------------
    public Bitmap baseImage;
    public Boolean isSample = false;
    public Boolean isSaved = false;
//    public TiltShift tilt;

    public static Integer width;
    private static Integer height;

    private static Integer blurTop;
    private static Integer blurBottom;
    private static Integer blurStrengthTop;
    private static Integer blurStrengthBottom;

    public boolean blurFinished;
    private int lastTouchedPositionX;
    private int lastTouchedPositionY;
    private Bitmap mask;

    String from = "", desc = "", postingFrom = "";
    boolean is_filtered = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_post_image_filter_activity);

        /**
         * UI controls initialization
         */
        sp = PreferenceManager.getDefaultSharedPreferences(PostImageFilterActivity.this);
        a = getIntent();
        imagepath = a.getStringExtra("imageviewpath");
        from = a.getStringExtra("from");
        desc = a.getStringExtra("desc");
        postingFrom = a.getStringExtra("postingFrom");

        edsp = sp.edit();
        edsp.putString(Preferences.NORMALIMG, imagepath);
        edsp.apply();
        findandclick();

        txtCrop = findViewById(R.id.txtCrop);
        txtBrightness = findViewById(R.id.txtBrightness);
        txtContrast = findViewById(R.id.txtContrast);
        txtStructure = findViewById(R.id.txtStructure);
        txtWarmth = findViewById(R.id.txtWarmth);
        txtSaturation = findViewById(R.id.txtSaturation);
        txtColor = findViewById(R.id.txtColor);
        txtFade = findViewById(R.id.txtFade);
        txtHighlights = findViewById(R.id.txtHighlights);
        txtShadow = findViewById(R.id.txtShadow);
        txtVignette = findViewById(R.id.txtVignette);
        txtSharpen = findViewById(R.id.txtSharpen);


        layoutCrop = findViewById(R.id.layoutCrop);
        layoutBrightness = findViewById(R.id.layoutBrightness);
        layoutContrast = findViewById(R.id.layoutContrast);
        layoutStructure = findViewById(R.id.layoutStructure);
        layoutWarmth = findViewById(R.id.layoutWarmth);
        layoutSaturation = findViewById(R.id.layoutSaturation);
        layoutColor = findViewById(R.id.layoutColor);
        layoutFade = findViewById(R.id.layoutFade);
        layoutHighlights = findViewById(R.id.layoutHighlight);
        layoutShadow = findViewById(R.id.layoutShadow);
        layoutVignette = findViewById(R.id.layoutVignette);
        layoutSharpen = findViewById(R.id.layoutSharpen);

        seekBar = (SeekBar) findViewById(R.id.seekbar);
        seekbar_count = (TextView) findViewById(R.id.seekbar_count);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressvalue, boolean b) {
                progress = progressvalue;
                if (mFilterAdjuster != null) {
                    mFilterAdjuster.adjust(progressvalue);
                }
                filterimg.requestRender();
                seekbar_count.setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        control_seekbar = (SeekBar) findViewById(R.id.control_seekbar);
        control_seekbar_count = (TextView) findViewById(R.id.control_seekbar_count);
        control_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressvalue, boolean b) {
//                progress2 = progressvalue;
                progress2 = control_seekbar.getProgress();
                progress2 -= 50;

                if (mFilterAdjuster != null) {
                    mFilterAdjuster.adjust(progressvalue);
                }
                filterimg.requestRender();

                control_seekbar_count.setText("" + progress2);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        blurFinished = true;
        lastTouchedPositionX = -1;
        lastTouchedPositionY = -1;
        mask = BitmapUtil.getDrawableAsBitmap(this, R.drawable.mask);
    }

    /**
     * Map ids and register click events
     */
    private void findandclick() {

        framelay_img = (FrameLayout) findViewById(R.id.framelay_img);

        AspectRatio ratio = new AspectRatio(1, App.isTablet() ? 1 : 0.93);
        ViewGroup.LayoutParams params = framelay_img.getLayoutParams();
        params.width = App.getScreenWidth();
        params.height = ratio.getHeightBy(App.getScreenWidth());
        filterimg = (GPUImageView) findViewById(R.id.filter_img);
        filterimg.setScaleType(GPUImage.ScaleType.CENTER_INSIDE);


        mBitmap = BitmapFactory.decodeFile(imagepath);

        float scale1280 = 0;
        try {
            if (mBitmap != null) {


                int maxP = Math.max(mBitmap.getWidth(), mBitmap.getHeight());
                scale1280 = (float) maxP / 1280;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        mBitmap = Bitmap.createScaledBitmap(mBitmap, (int) (mBitmap.getWidth() / scale1280),
                (int) (mBitmap.getHeight() / scale1280), true);

        //filterimg.setImage(mBitmap);
        filterimg.setImage(new File(imagepath));
        filter_imgg = null;
        gp = filterimg.getGPUImage();
        is_filtered = true;

        // filterimg.setImage(new File(imagepath));


        adjustfilterimg = (ImageView) findViewById(R.id.adjust_imgview);
        adjustfilterimg.setImageBitmap(decodeSampledBitmapFromResource(imagepath, 220, 200));

        cropperImageView = (CropperImageView) findViewById(R.id.adjust_cropper_view);
        cropperImageView.setMakeSquare(false);

        next_txt = (TextView) findViewById(R.id.nxt_txt);
        back = (ImageView) findViewById(R.id.back_img);

        lineview = (View) findViewById(R.id.newview);
        customizeicon_linear = (LinearLayout) findViewById(R.id.customize_icons_linear);
        panelplate = (ImageView) findViewById(R.id.img_color_panel);
        bright = (ImageView) findViewById(R.id.img_brightness);
        setting = (ImageView) findViewById(R.id.img_setting);


        horizontalpanels = (HorizontalScrollView) findViewById(R.id.horizontal_panels);
        seekbarrela = (RelativeLayout) findViewById(R.id.seekbar_rela);
        seek_close = (ImageView) findViewById(R.id.seek_close_img);
        seek_tick = (ImageView) findViewById(R.id.seek_tick_img);

        horizontalsettings = (HorizontalScrollView) findViewById(R.id.horizontal_setting_panels);
        set_controls_seekbarrela = (RelativeLayout) findViewById(R.id.control_seekbar_rela);
        control_seek_close = (ImageView) findViewById(R.id.control_seek_close_img);
        control_seek_tick_img = (ImageView) findViewById(R.id.control_seek_tick_img);


        linearLayoutPanel_normal = (LinearLayout) findViewById(R.id.layoutPanel_normal);
        linearLayoutPanel1 = (LinearLayout) findViewById(R.id.layoutPanel1);
        linearLayoutPanel2 = (LinearLayout) findViewById(R.id.layoutPanel2);
        linearLayoutPanel4 = (LinearLayout) findViewById(R.id.layoutPanel4);
        linearLayoutPanel5 = (LinearLayout) findViewById(R.id.layoutPanel5);
        linearLayoutPanel6 = (LinearLayout) findViewById(R.id.layoutPanel6);
        linearLayoutPanel7 = (LinearLayout) findViewById(R.id.layoutPanel7);
        linearLayoutPanel8 = (LinearLayout) findViewById(R.id.layoutPanel8);
        linearLayoutPanel9 = (LinearLayout) findViewById(R.id.layoutPanel9);
        linearLayoutPanel10 = (LinearLayout) findViewById(R.id.layoutPanel10);
        linearLayoutPanel11 = (LinearLayout) findViewById(R.id.layoutPanel11);
        linearLayoutPanel12 = (LinearLayout) findViewById(R.id.layoutPanel12);
        linearLayoutPanel13 = (LinearLayout) findViewById(R.id.layoutPanel13);
        linearLayoutPanel14 = (LinearLayout) findViewById(R.id.layoutPanel14);
        linearLayoutPanel15 = (LinearLayout) findViewById(R.id.layoutPanel15);
        linearLayoutPanel16 = (LinearLayout) findViewById(R.id.layoutPanel16);
        linearLayoutPanel17 = (LinearLayout) findViewById(R.id.layoutPanel17);
        managelinear = (LinearLayout) findViewById(R.id.manage_linear);

        normal_img = (GPUImageView) findViewById(R.id.normal_img);
        panel1 = (GPUImageView) findViewById(R.id.panel_img1);
        panel2 = (GPUImageView) findViewById(R.id.panel_img2);
        panel4 = (GPUImageView) findViewById(R.id.panel_img4);
        panel5 = (GPUImageView) findViewById(R.id.panel_img5);
        panel6 = (GPUImageView) findViewById(R.id.panel_img6);
        panel7 = (GPUImageView) findViewById(R.id.panel_img7);
        panel8 = (GPUImageView) findViewById(R.id.panel_img8);
        panel9 = (GPUImageView) findViewById(R.id.panel_img9);
        panel10 = (GPUImageView) findViewById(R.id.panel_img10);
        panel11 = (GPUImageView) findViewById(R.id.panel_img11);
        panel12 = (GPUImageView) findViewById(R.id.panel_img12);
        panel13 = (GPUImageView) findViewById(R.id.panel_img13);
        panel14 = (GPUImageView) findViewById(R.id.panel_img14);
        panel15 = (GPUImageView) findViewById(R.id.panel_img15);
        panel16 = (GPUImageView) findViewById(R.id.panel_img16);
        panel17 = (GPUImageView) findViewById(R.id.panel_img17);
        manage = (ImageView) findViewById(R.id.manage_filters);

        adjust = (ImageView) findViewById(R.id.adjust_img);
        brightness = (ImageView) findViewById(R.id.brightness_img);
        contrast = (ImageView) findViewById(R.id.contrast_img);
        structure = (ImageView) findViewById(R.id.structure_img);
        warmth = (ImageView) findViewById(R.id.warmth_img);
        saturation = (ImageView) findViewById(R.id.saturation_img);
        color = (ImageView) findViewById(R.id.color_img);
        fade = (ImageView) findViewById(R.id.fade_img);
        highlights = (ImageView) findViewById(R.id.highlight_img);
        shadow = (ImageView) findViewById(R.id.shadow_img);
        vignette = (ImageView) findViewById(R.id.vignette_img);
        sharpen = (ImageView) findViewById(R.id.sharpen_img);


        adjust_linear = (RelativeLayout) findViewById(R.id.adjust_rela);
        rotate_img = (ImageView) findViewById(R.id.rotate_button);
        adjust_close_img = (ImageView) findViewById(R.id.adjust_close_img);
        adjust_tick_img = (ImageView) findViewById(R.id.adjust_tick_img);


        colors_rela = (RelativeLayout) findViewById(R.id.colors_rela);
        colors_tick = (ImageView) findViewById(R.id.color_tick_img);
        colors_close = (ImageView) findViewById(R.id.color_close_img);

        color_shadow_txt = (TextView) findViewById(R.id.col_shadow_txt);
        color_highlight_txt = (TextView) findViewById(R.id.col_highlight_txt);

        shad__color_horizontal = (HorizontalScrollView) findViewById(R.id.colors_horizontal_shad);
        high__color_horizontal = (HorizontalScrollView) findViewById(R.id.colors_horizontal_high);


        white_shad = (ImageView) findViewById(R.id.white_shad);
        red_shad = (ImageView) findViewById(R.id.red_shad);
        green_shad = (ImageView) findViewById(R.id.green_shad);
        blue_shad = (ImageView) findViewById(R.id.blue_shad);
        yellow_shad = (ImageView) findViewById(R.id.yellow_shad);
        lightblue_shad = (ImageView) findViewById(R.id.lightblue_shad);
        purple_shad = (ImageView) findViewById(R.id.purple_shad);
        gray_shad = (ImageView) findViewById(R.id.gray_shad);
        orange_shad = (ImageView) findViewById(R.id.orange_shad);

        white_high = (ImageView) findViewById(R.id.white_high);
        red_high = (ImageView) findViewById(R.id.red_high);
        green_high = (ImageView) findViewById(R.id.green_high);
        blue_high = (ImageView) findViewById(R.id.blue_high);
        yellow_high = (ImageView) findViewById(R.id.yellow_high);
        lightblue_high = (ImageView) findViewById(R.id.lightblue_high);
        purple_high = (ImageView) findViewById(R.id.purple_high);
        gray_high = (ImageView) findViewById(R.id.gray_high);
        orange_high = (ImageView) findViewById(R.id.orange_high);


        next_txt.setOnClickListener(this);
        back.setOnClickListener(this);

        panelplate.setOnClickListener(this);
        bright.setOnClickListener(this);
        setting.setOnClickListener(this);


        seek_close.setOnClickListener(this);
        seek_tick.setOnClickListener(this);

        normal_img.setOnClickListener(this);
        normal_img.callOnClick();
        panel1.setOnClickListener(this);
        panel2.setOnClickListener(this);
        panel4.setOnClickListener(this);
        panel5.setOnClickListener(this);
        panel6.setOnClickListener(this);
        panel7.setOnClickListener(this);
        panel8.setOnClickListener(this);
        panel9.setOnClickListener(this);
        panel10.setOnClickListener(this);
        panel11.setOnClickListener(this);
        panel12.setOnClickListener(this);
        panel13.setOnClickListener(this);
        panel14.setOnClickListener(this);
        panel15.setOnClickListener(this);
        panel16.setOnClickListener(this);
        panel17.setOnClickListener(this);
        manage.setOnClickListener(this);

        control_seek_close.setOnClickListener(this);
        control_seek_tick_img.setOnClickListener(this);

        adjust.setOnClickListener(this);
        brightness.setOnClickListener(this);
        contrast.setOnClickListener(this);
        structure.setOnClickListener(this);
        warmth.setOnClickListener(this);
        saturation.setOnClickListener(this);
        color.setOnClickListener(this);
        fade.setOnClickListener(this);
        highlights.setOnClickListener(this);
        shadow.setOnClickListener(this);
        vignette.setOnClickListener(this);
        sharpen.setOnClickListener(this);

        rotate_img.setOnClickListener(this);
        adjust_close_img.setOnClickListener(this);
        adjust_tick_img.setOnClickListener(this);

        color_shadow_txt.setOnClickListener(this);
        color_highlight_txt.setOnClickListener(this);

        white_shad.setOnClickListener(this);
        red_shad.setOnClickListener(this);
        green_shad.setOnClickListener(this);
        blue_shad.setOnClickListener(this);
        yellow_shad.setOnClickListener(this);
        lightblue_shad.setOnClickListener(this);
        purple_shad.setOnClickListener(this);
        gray_shad.setOnClickListener(this);
        orange_shad.setOnClickListener(this);

        white_high.setOnClickListener(this);
        red_high.setOnClickListener(this);
        green_high.setOnClickListener(this);
        blue_high.setOnClickListener(this);
        yellow_high.setOnClickListener(this);
        lightblue_high.setOnClickListener(this);
        purple_high.setOnClickListener(this);
        gray_high.setOnClickListener(this);
        orange_high.setOnClickListener(this);

        colors_close.setOnClickListener(this);
        colors_tick.setOnClickListener(this);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                save_images();
            }
        });

    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View view) {
        Saving sav;
        switch (view.getId()) {
            case R.id.nxt_txt:

                if (from.equalsIgnoreCase("Home")) {
                     edsp = sp.edit();
                    if (filter_imgg == null) {
                        if (is_filtered) {
                            Log.e("Inside Filtered", "Insde if Filtered"+ imagepath);
                            Saving_next sa = new Saving_next();
                            sa.execute();
                        } else {
                            Log.e("Inside else Filtered", "Insde else Filtered" +imagepath);
                            Intent nxt = new Intent(PostImageFilterActivity.this, AddPostFinalActivity.class);
                            nxt.putExtra(AddPostFinalActivity.ARG_TYPE, ApiPostFeed.PostType.photo);
                            nxt.putExtra(AddPostFinalActivity.ARG_MEDIA_PATH,
                                    filter_imgg != null ? filter_imgg : imagepath
                            );
                            edsp.putString("mediaPath", imagepath);
                            edsp.apply();
                            startActivity(nxt);
//                            finish();

                        }
                    } else {
                        Log.e(TAG, "Insde Whole else Filtered");
                        Intent nxt = new Intent(PostImageFilterActivity.this, AddPostFinalActivity.class);
                        nxt.putExtra(AddPostFinalActivity.ARG_TYPE, ApiPostFeed.PostType.photo);
                        nxt.putExtra(AddPostFinalActivity.ARG_MEDIA_PATH,
                                filter_imgg != null ? filter_imgg : imagepath
                        );
                        edsp = sp.edit();
                        edsp.putString("selectedimgpath", filter_imgg);
                        edsp.putString("fromfilter", "imagecategory");
                        edsp.apply();
                        startActivity(nxt);
                        finish();
                    }

                }


                break;

            case R.id.back_img:

                finish();
                break;

            //------------------------ FilterPage Menus--------------------------------------------------------------------

            case R.id.img_color_panel:
                panelplate.setColorFilter(App.getColorRes(R.color.colorPrimary));
                bright.setColorFilter(App.getColorRes(R.color.blackGray));
                setting.setColorFilter(App.getColorRes(R.color.blackGray));

                horizontalpanels.setVisibility(View.VISIBLE);
                seekbarrela.setVisibility(View.GONE);
                horizontalsettings.setVisibility(View.GONE);

                break;

            case R.id.img_brightness:
                panelplate.setColorFilter(App.getColorRes(R.color.colorPrimary));
                bright.setColorFilter(App.getColorRes(R.color.blackGray));
                setting.setColorFilter(App.getColorRes(R.color.blackGray));
                lineview.setVisibility(View.VISIBLE);
                customizeicon_linear.setVisibility(View.GONE);
                horizontalpanels.setVisibility(View.GONE);
                seekbarrela.setVisibility(View.VISIBLE);
                horizontalsettings.setVisibility(View.GONE);
                brightfrom = "main";
                clickedfilter = "brightness_img";

                if (brightvalue == 0) {
                    Saving savi = new Saving();
                    savi.execute(new GPUImageBrightnessFilter());
                } else {
                    if (filter_imgg != null) {
                        filterimg.setImage(new File(filter_imgg));
                    }
                    switchFilterTo(new GPUImageBrightnessFilter(0f));
                    filterimg.requestRender();
                }
                seekBar.setProgress(brightvalue);
                seekbar_count.setText(brighttxt);
                break;

            case R.id.img_setting:
                panelplate.setColorFilter(App.getColorRes(R.color.blackGray));
                bright.setColorFilter(App.getColorRes(R.color.blackGray));
                setting.setColorFilter(App.getColorRes(R.color.colorPrimary));
                horizontalpanels.setVisibility(View.GONE);
                seekbarrela.setVisibility(View.GONE);
                horizontalsettings.setVisibility(View.VISIBLE);

                break;


            case R.id.seek_close_img:
                lineview.setVisibility(View.GONE);
                customizeicon_linear.setVisibility(View.VISIBLE);
                seekbarrela.setVisibility(View.GONE);
                if (brightfrom.equals("main")) {
                    panelplate.setImageResource(R.drawable.blue_color_panel);
                    bright.setImageResource(R.drawable.grey_brightness);
                    setting.setImageResource(R.drawable.grey_settings);
                    horizontalpanels.setVisibility(View.VISIBLE);
                    horizontalsettings.setVisibility(View.GONE);
                } else {
                    panelplate.setImageResource(R.drawable.grey_color_panel);
                    bright.setImageResource(R.drawable.grey_brightness);
                    setting.setImageResource(R.drawable.blue_settings);
                    horizontalpanels.setVisibility(View.GONE);
                    horizontalsettings.setVisibility(View.VISIBLE);
                }

                filterimg.setVisibility(View.VISIBLE);
                adjustfilterimg.setVisibility(View.GONE);
                switchFilterTo(tempfilter);
                break;

            case R.id.seek_tick_img:
                lineview.setVisibility(View.GONE);
                customizeicon_linear.setVisibility(View.VISIBLE);
                seekbarrela.setVisibility(View.GONE);
                if (brightfrom.equals("main")) {

                    horizontalpanels.setVisibility(View.VISIBLE);
                    horizontalsettings.setVisibility(View.GONE);
                } else {
                    horizontalpanels.setVisibility(View.GONE);
                    horizontalsettings.setVisibility(View.VISIBLE);
                }

                filterimg.setVisibility(View.VISIBLE);
                adjustfilterimg.setVisibility(View.GONE);
                tempfilter = filterimg.getFilter();

                if (clickedfilter.equals("brightness_img")) {
                    brightvalue = seekBar.getProgress();
                    brighttxt = seekbar_count.getText().toString();
                    txtBrightness.setBackgroundResource(R.drawable.filters_applied_bg);
                } else if (clickedfilter.equals("structure_img")) {
                    structurevalue = seekBar.getProgress();
                    structuretxt = seekbar_count.getText().toString();
                    txtStructure.setBackgroundResource(R.drawable.filters_applied_bg);
                } else if (clickedfilter.equals("fade_img")) {
                    fadevalue = seekBar.getProgress();
                    fadetxt = seekbar_count.getText().toString();
                    txtFade.setBackgroundResource(R.drawable.filters_applied_bg);
                } else if (clickedfilter.equals("highlight_img")) {
                    highlightvalue = seekBar.getProgress();
                    highlighttxt = seekbar_count.getText().toString();
                    txtHighlights.setBackgroundResource(R.drawable.filters_applied_bg);
                } else if (clickedfilter.equals("shadow_img")) {
                    shadowvalue = seekBar.getProgress();
                    shadowtxt = seekbar_count.getText().toString();
                    txtShadow.setBackgroundResource(R.drawable.filters_applied_bg);
                } else if (clickedfilter.equals("vignette_img")) {
                    vignettevalue = seekBar.getProgress();
                    vignettetxt = seekbar_count.getText().toString();
                    txtVignette.setBackgroundResource(R.drawable.filters_applied_bg);
                } else if (clickedfilter.equals("sharpen_img")) {
                    sharpenvalue = seekBar.getProgress();
                    sharpentxt = seekbar_count.getText().toString();
                    txtSharpen.setBackgroundResource(R.drawable.filters_applied_bg);
                }

                filtered_bitmap = filterimg.getGPUImage().getBitmapWithFilterApplied();
                saveImage("InstaSocial_images", "Filterimg", filtered_bitmap);
                break;

            //----------------------Filter Panel Effects--------------------------------------------------------------------

            case R.id.normal_img:
                Log.e("Normal image", "normal image ");

                filterimg.setImage(mBitmap);
                switchFilterTo(new GPUImageFilter(GPUImageFilter.NO_FILTER_VERTEX_SHADER, GPUImageFilter.NO_FILTER_FRAGMENT_SHADER));
                filterimg.requestRender();
                tempfilter = filterimg.getFilter();

//                filtered_bitmap = filterimg.getGPUImage().getBitmapWithFilterApplied();
                gp = filterimg.getGPUImage();
                is_filtered = true;
                filter_imgg = null;
                ReverseAppliedValuesForFilters();
//                saveImage("InstaSocial_images", "Filterimg", filtered_bitmap);

                linearLayoutPanel_normal.setBackground(ContextCompat.getDrawable(this, R.drawable.filters_bluebackground));
                linearLayoutPanel1.setBackground(null);
                linearLayoutPanel2.setBackground(null);
                linearLayoutPanel4.setBackground(null);
                linearLayoutPanel5.setBackground(null);
                linearLayoutPanel6.setBackground(null);
                linearLayoutPanel7.setBackground(null);
                linearLayoutPanel8.setBackground(null);
                linearLayoutPanel9.setBackground(null);
                linearLayoutPanel10.setBackground(null);
                linearLayoutPanel11.setBackground(null);
                linearLayoutPanel12.setBackground(null);
                linearLayoutPanel13.setBackground(null);
                linearLayoutPanel14.setBackground(null);
                linearLayoutPanel15.setBackground(null);
                linearLayoutPanel16.setBackground(null);
                linearLayoutPanel17.setBackground(null);
                break;

            case R.id.panel_img1:
                /*if (filterimg != null) {
                    filterimg.setImage(mBitmap);
                }*/
                filterimg.setImage(mBitmap);
                switchFilterTo(new GPUImageSepiaFilter());
                filterimg.requestRender();
                tempfilter = filterimg.getFilter();

                gp = filterimg.getGPUImage();
                is_filtered = true;
                filter_imgg = null;
                ReverseAppliedValuesForFilters();

                linearLayoutPanel_normal.setBackground(null);
                linearLayoutPanel1.setBackground(ContextCompat.getDrawable(this, R.drawable.filters_bluebackground));
                linearLayoutPanel2.setBackground(null);
                linearLayoutPanel4.setBackground(null);
                linearLayoutPanel5.setBackground(null);
                linearLayoutPanel6.setBackground(null);
                linearLayoutPanel7.setBackground(null);
                linearLayoutPanel8.setBackground(null);
                linearLayoutPanel9.setBackground(null);
                linearLayoutPanel10.setBackground(null);
                linearLayoutPanel11.setBackground(null);
                linearLayoutPanel12.setBackground(null);
                linearLayoutPanel13.setBackground(null);
                linearLayoutPanel14.setBackground(null);
                linearLayoutPanel15.setBackground(null);
                linearLayoutPanel16.setBackground(null);
                linearLayoutPanel17.setBackground(null);
                break;

            case R.id.panel_img2:
                /*if (filterimg != null) {
                    filterimg.setImage(mBitmap);
                }*/
                filterimg.setImage(mBitmap);
                switchFilterTo(new GPUImageBoxBlurFilter(2.0f));
                filterimg.requestRender();
                tempfilter = filterimg.getFilter();

                filter_imgg = null;
                gp = filterimg.getGPUImage();
                is_filtered = true;
                ReverseAppliedValuesForFilters();

                linearLayoutPanel_normal.setBackground(null);
                linearLayoutPanel1.setBackground(null);
                linearLayoutPanel2.setBackground(ContextCompat.getDrawable(this, R.drawable.filters_bluebackground));
                linearLayoutPanel4.setBackground(null);
                linearLayoutPanel5.setBackground(null);
                linearLayoutPanel6.setBackground(null);
                linearLayoutPanel7.setBackground(null);
                linearLayoutPanel8.setBackground(null);
                linearLayoutPanel9.setBackground(null);
                linearLayoutPanel10.setBackground(null);
                linearLayoutPanel11.setBackground(null);
                linearLayoutPanel12.setBackground(null);
                linearLayoutPanel13.setBackground(null);
                linearLayoutPanel14.setBackground(null);
                linearLayoutPanel15.setBackground(null);
                linearLayoutPanel16.setBackground(null);
                linearLayoutPanel17.setBackground(null);
                break;


            case R.id.panel_img4:
                /*if (filter_imgg != null) {
                    filterimg.setImage(mBitmap);
                }*/
                filterimg.setImage(mBitmap);
                switchFilterTo(new GPUImageEmbossFilter());
                filterimg.requestRender();
                tempfilter = filterimg.getFilter();

                filter_imgg = null;
                gp = filterimg.getGPUImage();
                is_filtered = true;
                ReverseAppliedValuesForFilters();

                linearLayoutPanel_normal.setBackground(null);
                linearLayoutPanel1.setBackground(null);
                linearLayoutPanel2.setBackground(null);
                linearLayoutPanel4.setBackground(ContextCompat.getDrawable(this, R.drawable.filters_bluebackground));
                linearLayoutPanel5.setBackground(null);
                linearLayoutPanel6.setBackground(null);
                linearLayoutPanel7.setBackground(null);
                linearLayoutPanel8.setBackground(null);
                linearLayoutPanel9.setBackground(null);
                linearLayoutPanel10.setBackground(null);
                linearLayoutPanel11.setBackground(null);
                linearLayoutPanel12.setBackground(null);
                linearLayoutPanel13.setBackground(null);
                linearLayoutPanel14.setBackground(null);
                linearLayoutPanel15.setBackground(null);
                linearLayoutPanel16.setBackground(null);
                linearLayoutPanel17.setBackground(null);
                break;

            case R.id.panel_img5:
                /*if (filterimg != null) {
                    filterimg.setImage(mBitmap);
                }*/
                filterimg.setImage(mBitmap);
                switchFilterTo(new GPUImageHueFilter(10.0f));
                filterimg.requestRender();
                tempfilter = filterimg.getFilter();

                filter_imgg = null;
                gp = filterimg.getGPUImage();
                is_filtered = true;
                ReverseAppliedValuesForFilters();

                linearLayoutPanel_normal.setBackground(null);
                linearLayoutPanel1.setBackground(null);
                linearLayoutPanel2.setBackground(null);
                linearLayoutPanel4.setBackground(null);
                linearLayoutPanel5.setBackground(ContextCompat.getDrawable(this, R.drawable.filters_bluebackground));
                linearLayoutPanel6.setBackground(null);
                linearLayoutPanel7.setBackground(null);
                linearLayoutPanel8.setBackground(null);
                linearLayoutPanel9.setBackground(null);
                linearLayoutPanel10.setBackground(null);
                linearLayoutPanel11.setBackground(null);
                linearLayoutPanel12.setBackground(null);
                linearLayoutPanel13.setBackground(null);
                linearLayoutPanel14.setBackground(null);
                linearLayoutPanel15.setBackground(null);
                linearLayoutPanel16.setBackground(null);
                linearLayoutPanel17.setBackground(null);
                break;

            case R.id.panel_img6:
                /*if (filterimg != null) {
                    filterimg.setImage(mBitmap);
                }*/
                filterimg.setImage(mBitmap);
                switchFilterTo(new GPUImageNashvilleFilter(PostImageFilterActivity.this));
                filterimg.requestRender();
                tempfilter = filterimg.getFilter();

                filter_imgg = null;
                gp = filterimg.getGPUImage();
                is_filtered = true;
                ReverseAppliedValuesForFilters();

                linearLayoutPanel_normal.setBackground(null);
                linearLayoutPanel1.setBackground(null);
                linearLayoutPanel2.setBackground(null);
                linearLayoutPanel4.setBackground(null);
                linearLayoutPanel5.setBackground(null);
                linearLayoutPanel6.setBackground(ContextCompat.getDrawable(this, R.drawable.filters_bluebackground));
                linearLayoutPanel7.setBackground(null);
                linearLayoutPanel8.setBackground(null);
                linearLayoutPanel9.setBackground(null);
                linearLayoutPanel10.setBackground(null);
                linearLayoutPanel11.setBackground(null);
                linearLayoutPanel12.setBackground(null);
                linearLayoutPanel13.setBackground(null);
                linearLayoutPanel14.setBackground(null);
                linearLayoutPanel15.setBackground(null);
                linearLayoutPanel16.setBackground(null);
                linearLayoutPanel17.setBackground(null);
                break;

            case R.id.panel_img7:
                /*if (filterimg != null) {
                    filterimg.setImage(mBitmap);
                }*/
                filterimg.setImage(mBitmap);
                switchFilterTo(new GPUImageSketchFilter());
                filterimg.requestRender();
                tempfilter = filterimg.getFilter();

                filter_imgg = null;
                gp = filterimg.getGPUImage();
                is_filtered = true;
                ReverseAppliedValuesForFilters();

                linearLayoutPanel_normal.setBackground(null);
                linearLayoutPanel1.setBackground(null);
                linearLayoutPanel2.setBackground(null);
                linearLayoutPanel4.setBackground(null);
                linearLayoutPanel5.setBackground(null);
                linearLayoutPanel6.setBackground(null);
                linearLayoutPanel7.setBackground(ContextCompat.getDrawable(this, R.drawable.filters_bluebackground));
                linearLayoutPanel8.setBackground(null);
                linearLayoutPanel9.setBackground(null);
                linearLayoutPanel10.setBackground(null);
                linearLayoutPanel11.setBackground(null);
                linearLayoutPanel12.setBackground(null);
                linearLayoutPanel13.setBackground(null);
                linearLayoutPanel14.setBackground(null);
                linearLayoutPanel15.setBackground(null);
                linearLayoutPanel16.setBackground(null);
                linearLayoutPanel17.setBackground(null);
                break;


            case R.id.panel_img8:
                /*if (filterimg != null) {
                    filterimg.setImage(mBitmap);
                }*/
                filterimg.setImage(mBitmap);
                switchFilterTo(new GPUImageGrayscaleFilter());
                filterimg.requestRender();
                tempfilter = filterimg.getFilter();

                filter_imgg = null;
                gp = filterimg.getGPUImage();
                is_filtered = true;
                ReverseAppliedValuesForFilters();

                linearLayoutPanel_normal.setBackground(null);
                linearLayoutPanel1.setBackground(null);
                linearLayoutPanel2.setBackground(null);
                linearLayoutPanel4.setBackground(null);
                linearLayoutPanel5.setBackground(null);
                linearLayoutPanel6.setBackground(null);
                linearLayoutPanel7.setBackground(null);
                linearLayoutPanel8.setBackground(ContextCompat.getDrawable(this, R.drawable.filters_bluebackground));
                linearLayoutPanel9.setBackground(null);
                linearLayoutPanel10.setBackground(null);
                linearLayoutPanel11.setBackground(null);
                linearLayoutPanel12.setBackground(null);
                linearLayoutPanel13.setBackground(null);
                linearLayoutPanel14.setBackground(null);
                linearLayoutPanel15.setBackground(null);
                linearLayoutPanel16.setBackground(null);
                linearLayoutPanel17.setBackground(null);
                break;

            case R.id.panel_img9:
                /*if (filterimg != null) {
                    filterimg.setImage(mBitmap);
                }*/
                filterimg.setImage(mBitmap);
                switchFilterTo(new GPUImageColorInvertFilter());
                filterimg.requestRender();
                tempfilter = filterimg.getFilter();

                filter_imgg = null;
                gp = filterimg.getGPUImage();
                is_filtered = true;
                ReverseAppliedValuesForFilters();

                linearLayoutPanel_normal.setBackground(null);
                linearLayoutPanel1.setBackground(null);
                linearLayoutPanel2.setBackground(null);
                linearLayoutPanel4.setBackground(null);
                linearLayoutPanel5.setBackground(null);
                linearLayoutPanel6.setBackground(null);
                linearLayoutPanel7.setBackground(null);
                linearLayoutPanel8.setBackground(null);
                linearLayoutPanel9.setBackground(ContextCompat.getDrawable(this, R.drawable.filters_bluebackground));
                linearLayoutPanel10.setBackground(null);
                linearLayoutPanel11.setBackground(null);
                linearLayoutPanel12.setBackground(null);
                linearLayoutPanel13.setBackground(null);
                linearLayoutPanel14.setBackground(null);
                linearLayoutPanel15.setBackground(null);
                linearLayoutPanel16.setBackground(null);
                linearLayoutPanel17.setBackground(null);
                break;

            case R.id.panel_img10:
                /*if (filterimg != null) {
                    filterimg.setImage(mBitmap);
                }*/
                filterimg.setImage(mBitmap);
                switchFilterTo(new GPUImageToneCurveFilter());
                filterimg.requestRender();
                tempfilter = filterimg.getFilter();

                filter_imgg = null;
                gp = filterimg.getGPUImage();
                is_filtered = true;
                ReverseAppliedValuesForFilters();

                linearLayoutPanel_normal.setBackground(null);
                linearLayoutPanel1.setBackground(null);
                linearLayoutPanel2.setBackground(null);
                linearLayoutPanel4.setBackground(null);
                linearLayoutPanel5.setBackground(null);
                linearLayoutPanel6.setBackground(null);
                linearLayoutPanel7.setBackground(null);
                linearLayoutPanel8.setBackground(null);
                linearLayoutPanel9.setBackground(null);
                linearLayoutPanel10.setBackground(ContextCompat.getDrawable(this, R.drawable.filters_bluebackground));
                linearLayoutPanel11.setBackground(null);
                linearLayoutPanel12.setBackground(null);
                linearLayoutPanel13.setBackground(null);
                linearLayoutPanel14.setBackground(null);
                linearLayoutPanel15.setBackground(null);
                linearLayoutPanel16.setBackground(null);
                linearLayoutPanel17.setBackground(null);
                break;

            case R.id.panel_img11:
                /*if (filterimg != null) {
                    filterimg.setImage(mBitmap);
                }*/
                filterimg.setImage(mBitmap);
                switchFilterTo(new GPUImageLordKelvinFilter(PostImageFilterActivity.this));
                filterimg.requestRender();
                tempfilter = filterimg.getFilter();

                filter_imgg = null;
                gp = filterimg.getGPUImage();
                is_filtered = true;
                ReverseAppliedValuesForFilters();

                linearLayoutPanel_normal.setBackground(null);
                linearLayoutPanel1.setBackground(null);
                linearLayoutPanel2.setBackground(null);
                linearLayoutPanel4.setBackground(null);
                linearLayoutPanel5.setBackground(null);
                linearLayoutPanel6.setBackground(null);
                linearLayoutPanel7.setBackground(null);
                linearLayoutPanel8.setBackground(null);
                linearLayoutPanel9.setBackground(null);
                linearLayoutPanel10.setBackground(null);
                linearLayoutPanel11.setBackground(ContextCompat.getDrawable(this, R.drawable.filters_bluebackground));
                linearLayoutPanel12.setBackground(null);
                linearLayoutPanel13.setBackground(null);
                linearLayoutPanel14.setBackground(null);
                linearLayoutPanel15.setBackground(null);
                linearLayoutPanel16.setBackground(null);
                linearLayoutPanel17.setBackground(null);
                break;

            case R.id.panel_img12:
                /*if (filterimg != null) {
                    filterimg.setImage(mBitmap);
                }*/
                filterimg.setImage(mBitmap);
                switchFilterTo(new GPUImageKuwaharaFilter(5));
                filterimg.requestRender();
                tempfilter = filterimg.getFilter();

                gp = filterimg.getGPUImage();
                is_filtered = true;
                filter_imgg = null;
                ReverseAppliedValuesForFilters();

                linearLayoutPanel_normal.setBackground(null);
                linearLayoutPanel1.setBackground(null);
                linearLayoutPanel2.setBackground(null);
                linearLayoutPanel4.setBackground(null);
                linearLayoutPanel5.setBackground(null);
                linearLayoutPanel6.setBackground(null);
                linearLayoutPanel7.setBackground(null);
                linearLayoutPanel8.setBackground(null);
                linearLayoutPanel9.setBackground(null);
                linearLayoutPanel10.setBackground(null);
                linearLayoutPanel11.setBackground(null);
                linearLayoutPanel12.setBackground(ContextCompat.getDrawable(this, R.drawable.filters_bluebackground));
                linearLayoutPanel13.setBackground(null);
                linearLayoutPanel14.setBackground(null);
                linearLayoutPanel15.setBackground(null);
                linearLayoutPanel16.setBackground(null);
                linearLayoutPanel17.setBackground(null);
                break;

            case R.id.panel_img13:
                /*if (filterimg != null) {
                    filterimg.setImage(mBitmap);
                }*/
                filterimg.setImage(mBitmap);
                switchFilterTo(new GPUImageAddBlendFilter());
                filterimg.requestRender();
                tempfilter = filterimg.getFilter();

                gp = filterimg.getGPUImage();
                is_filtered = true;
                filter_imgg = null;
                ReverseAppliedValuesForFilters();

                linearLayoutPanel_normal.setBackground(null);
                linearLayoutPanel1.setBackground(null);
                linearLayoutPanel2.setBackground(null);
                linearLayoutPanel4.setBackground(null);
                linearLayoutPanel5.setBackground(null);
                linearLayoutPanel6.setBackground(null);
                linearLayoutPanel7.setBackground(null);
                linearLayoutPanel8.setBackground(null);
                linearLayoutPanel9.setBackground(null);
                linearLayoutPanel10.setBackground(null);
                linearLayoutPanel11.setBackground(null);
                linearLayoutPanel12.setBackground(null);
                linearLayoutPanel13.setBackground(ContextCompat.getDrawable(this, R.drawable.filters_bluebackground));
                linearLayoutPanel14.setBackground(null);
                linearLayoutPanel15.setBackground(null);
                linearLayoutPanel16.setBackground(null);
                linearLayoutPanel17.setBackground(null);
                break;

            case R.id.panel_img14:
                /*if (filterimg != null) {
                    filterimg.setImage(mBitmap);
                }*/
                filterimg.setImage(mBitmap);
                switchFilterTo(new GPUImageBulgeDistortionFilter());
                filterimg.requestRender();
                tempfilter = filterimg.getFilter();

                gp = filterimg.getGPUImage();
                is_filtered = true;
                filter_imgg = null;
                ReverseAppliedValuesForFilters();

                linearLayoutPanel_normal.setBackground(null);
                linearLayoutPanel1.setBackground(null);
                linearLayoutPanel2.setBackground(null);
                linearLayoutPanel4.setBackground(null);
                linearLayoutPanel5.setBackground(null);
                linearLayoutPanel6.setBackground(null);
                linearLayoutPanel7.setBackground(null);
                linearLayoutPanel8.setBackground(null);
                linearLayoutPanel9.setBackground(null);
                linearLayoutPanel10.setBackground(null);
                linearLayoutPanel11.setBackground(null);
                linearLayoutPanel12.setBackground(null);
                linearLayoutPanel13.setBackground(null);
                linearLayoutPanel14.setBackground(ContextCompat.getDrawable(this, R.drawable.filters_bluebackground));
                linearLayoutPanel15.setBackground(null);
                linearLayoutPanel16.setBackground(null);
                linearLayoutPanel17.setBackground(null);
                break;

            case R.id.panel_img15:
                /*if (filterimg != null) {
                    filterimg.setImage(mBitmap);
                }*/
                filterimg.setImage(mBitmap);
                switchFilterTo(new GPUImageCGAColorspaceFilter());
                filterimg.requestRender();
                tempfilter = filterimg.getFilter();

                gp = filterimg.getGPUImage();
                is_filtered = true;
                filter_imgg = null;
                ReverseAppliedValuesForFilters();

                linearLayoutPanel_normal.setBackground(null);
                linearLayoutPanel1.setBackground(null);
                linearLayoutPanel2.setBackground(null);
                linearLayoutPanel4.setBackground(null);
                linearLayoutPanel5.setBackground(null);
                linearLayoutPanel6.setBackground(null);
                linearLayoutPanel7.setBackground(null);
                linearLayoutPanel8.setBackground(null);
                linearLayoutPanel9.setBackground(null);
                linearLayoutPanel10.setBackground(null);
                linearLayoutPanel11.setBackground(null);
                linearLayoutPanel12.setBackground(null);
                linearLayoutPanel13.setBackground(null);
                linearLayoutPanel14.setBackground(null);
                linearLayoutPanel15.setBackground(ContextCompat.getDrawable(this, R.drawable.filters_bluebackground));
                linearLayoutPanel16.setBackground(null);
                linearLayoutPanel17.setBackground(null);
                break;

            case R.id.panel_img16:
                /*if (filterimg != null) {
                    filterimg.setImage(mBitmap);
                }*/
                filterimg.setImage(mBitmap);
                switchFilterTo(new GPUImageWeakPixelInclusionFilter());
                filterimg.requestRender();
                tempfilter = filterimg.getFilter();

                gp = filterimg.getGPUImage();
                is_filtered = true;
                filter_imgg = null;
                ReverseAppliedValuesForFilters();

                linearLayoutPanel_normal.setBackground(null);
                linearLayoutPanel1.setBackground(null);
                linearLayoutPanel2.setBackground(null);
                linearLayoutPanel4.setBackground(null);
                linearLayoutPanel5.setBackground(null);
                linearLayoutPanel6.setBackground(null);
                linearLayoutPanel7.setBackground(null);
                linearLayoutPanel8.setBackground(null);
                linearLayoutPanel9.setBackground(null);
                linearLayoutPanel10.setBackground(null);
                linearLayoutPanel11.setBackground(null);
                linearLayoutPanel12.setBackground(null);
                linearLayoutPanel13.setBackground(null);
                linearLayoutPanel14.setBackground(null);
                linearLayoutPanel15.setBackground(null);
                linearLayoutPanel16.setBackground(ContextCompat.getDrawable(this, R.drawable.filters_bluebackground));
                linearLayoutPanel17.setBackground(null);
                break;

            case R.id.panel_img17:
                /*if (filterimg != null) {
                    filterimg.setImage(mBitmap);
                }*/
                filterimg.setImage(mBitmap);
                switchFilterTo(new GPUImageBilateralFilter(2.0f));
                filterimg.requestRender();
                tempfilter = filterimg.getFilter();

                gp = filterimg.getGPUImage();
                is_filtered = true;
                filter_imgg = null;
                ReverseAppliedValuesForFilters();

                linearLayoutPanel_normal.setBackground(null);
                linearLayoutPanel1.setBackground(null);
                linearLayoutPanel2.setBackground(null);
                linearLayoutPanel4.setBackground(null);
                linearLayoutPanel5.setBackground(null);
                linearLayoutPanel6.setBackground(null);
                linearLayoutPanel7.setBackground(null);
                linearLayoutPanel8.setBackground(null);
                linearLayoutPanel9.setBackground(null);
                linearLayoutPanel10.setBackground(null);
                linearLayoutPanel11.setBackground(null);
                linearLayoutPanel12.setBackground(null);
                linearLayoutPanel13.setBackground(null);
                linearLayoutPanel14.setBackground(null);
                linearLayoutPanel15.setBackground(null);
                linearLayoutPanel16.setBackground(null);
                linearLayoutPanel17.setBackground(ContextCompat.getDrawable(this, R.drawable.filters_bluebackground));
                break;

            case R.id.manage_filters:
                Intent m = new Intent(PostImageFilterActivity.this, ManageFiltersActivity.class);
                m.putExtra("type", "img");
                m.putExtra("videopath", "");
                m.putExtra("imageuri", imagepath);
                m.putExtra("from", from);
                m.putExtra("desc", desc);
                m.putExtra("postingFrom", postingFrom);
                startActivity(m);

                overridePendingTransition(R.anim.open_next, R.anim.close_main);
                break;

            //------------------------Filter Setting Effects-------------------------------------------------------------------------

            case R.id.adjust_img:
                customizeicon_linear.setVisibility(View.GONE);
                horizontalsettings.setVisibility(View.GONE);
                lineview.setVisibility(View.VISIBLE);
                adjust_linear.setVisibility(View.VISIBLE);
                filterAdjustSelection(view);
                rotate_bitmap = filterimg.getGPUImage().getBitmapWithFilterApplied();

                cropperImageView.setImageBitmap(rotate_bitmap);
                filterimg.setVisibility(View.GONE);
                cropperImageView.setVisibility(View.VISIBLE);
                break;

            case R.id.brightness_img:
                brightfrom = "settings";
                clickedfilter = "brightness_img";
                filterAdjustSelection(view);
                if (brightvalue == 0) {
                    Saving savi = new Saving();
                    savi.execute(new GPUImageBrightnessFilter());
                } else {
                    if (filter_imgg != null) {
                        filterimg.setImage(new File(filter_imgg));
                    }
                    switchFilterTo(new GPUImageBrightnessFilter(0f));
                    filterimg.requestRender();
                }
                // sav.execute();
                customizeicon_linear.setVisibility(View.GONE);
                horizontalsettings.setVisibility(View.GONE);
                lineview.setVisibility(View.VISIBLE);
                seekbarrela.setVisibility(View.VISIBLE);

                seekBar.setProgress(brightvalue);
                seekbar_count.setText(brighttxt);
                break;

            case R.id.contrast_img:
                clickedfilter = "contrast_img";
                filterAdjustSelection(view);
                if (contrastvalue == 50) {
                    Saving savi1 = new Saving();
                    savi1.execute(new GPUImageContrastFilter());
                } else {
                    if (filter_imgg != null) {
                        filterimg.setImage(new File(filter_imgg));
                    }
                    switchFilterTo(new GPUImageContrastFilter());
                    filterimg.requestRender();
                }

                customizeicon_linear.setVisibility(View.GONE);
                horizontalsettings.setVisibility(View.GONE);
                lineview.setVisibility(View.VISIBLE);
                set_controls_seekbarrela.setVisibility(View.VISIBLE);

                control_seekbar.setProgress(contrastvalue);
                control_seekbar_count.setText(contrasttxt);
                break;

            case R.id.structure_img:
                brightfrom = "settings";
                clickedfilter = "structure_img";
                filterAdjustSelection(view);
                if (structurevalue == 0) {
                    sav = new Saving();
                    sav.execute(new GPUImageDarkShadow());
                } else {
                    if (filter_imgg != null) {
                        filterimg.setImage(new File(filter_imgg));
                    }
                    switchFilterTo(new GPUImageDarkShadow());
                    filterimg.requestRender();
                }


                customizeicon_linear.setVisibility(View.GONE);
                horizontalsettings.setVisibility(View.GONE);
                lineview.setVisibility(View.VISIBLE);
                seekbarrela.setVisibility(View.VISIBLE);

                seekBar.setProgress(structurevalue);
                seekbar_count.setText(structuretxt);
                break;

            case R.id.warmth_img:
                clickedfilter = "warmth_img";
                filterAdjustSelection(view);
                if (warmthvalue == 50) {
                    Saving sav1 = new Saving();
                    sav1.execute(new GPUImageWhiteBalanceFilter());
                } else {
                    if (filter_imgg != null) {
                        filterimg.setImage(new File(filter_imgg));
                    }
                    switchFilterTo(new GPUImageWhiteBalanceFilter());
                    filterimg.requestRender();
                }

                customizeicon_linear.setVisibility(View.GONE);
                horizontalsettings.setVisibility(View.GONE);
                lineview.setVisibility(View.VISIBLE);
                set_controls_seekbarrela.setVisibility(View.VISIBLE);

                control_seekbar.setProgress(warmthvalue);
                control_seekbar_count.setText(warmthtxt);
                break;

            case R.id.saturation_img:
                clickedfilter = "saturation_img";
                filterAdjustSelection(view);
                if (saturationvalue == 50) {
                    Saving sav2 = new Saving();
                    sav2.execute(new GPUImageSaturationFilter());
                } else {
                    if (filter_imgg != null) {
                        filterimg.setImage(new File(filter_imgg));
                    }
                    switchFilterTo(new GPUImageSaturationFilter());
                    filterimg.requestRender();
                }

                customizeicon_linear.setVisibility(View.GONE);
                horizontalsettings.setVisibility(View.GONE);
                lineview.setVisibility(View.VISIBLE);
                set_controls_seekbarrela.setVisibility(View.VISIBLE);

                control_seekbar.setProgress(saturationvalue);
                control_seekbar_count.setText(saturationtxt);
                break;

            case R.id.color_img:
                if (filter_imgg != null) {
                    Log.e("filter_imgg ----> ", filter_imgg);
                    filterimg.setImage(new File(filter_imgg));
                } else {
                    switchFilterTo(new GPUImageFilter(GPUImageFilter.NO_FILTER_VERTEX_SHADER, GPUImageFilter.NO_FILTER_FRAGMENT_SHADER));
                    filterimg.requestRender();
                    tempfilter = filterimg.getFilter();

                    filtered_bitmap = filterimg.getGPUImage().getBitmapWithFilterApplied();
                    saveImage("InstaSocial_images", "Filterimg", filtered_bitmap);
                }
                filterAdjustSelection(view);
                customizeicon_linear.setVisibility(View.GONE);
                horizontalsettings.setVisibility(View.GONE);
                lineview.setVisibility(View.VISIBLE);
                colors_rela.setVisibility(View.VISIBLE);
                break;

            case R.id.fade_img:
                brightfrom = "settings";
                clickedfilter = "fade_img";
                filterAdjustSelection(view);

                if (fadevalue == 0) {
                    Saving sav2 = new Saving();
                    sav2.execute(new GPUImageColorMatrixFilter(1.0f, new float[]{
                            0.529f, 0.0f, 0.0f, 0.0f,
                            0.0f, 0.529f, 0.0f, 0.0f,
                            0.0f, 0.0f, 0.529f, 0.0f,
                            0.0f, 0.0f, 0.0f, 1.0f
                    }));
                } else {
                    if (filter_imgg != null) {
                        Log.e("filter_imgg ----> ", filter_imgg);
                        filterimg.setImage(new File(filter_imgg));
                    }
                    switchFilterTo(new GPUImageColorMatrixFilter(1.0f, new float[]{
                            0.529f, 0.0f, 0.0f, 0.0f,
                            0.0f, 0.529f, 0.0f, 0.0f,
                            0.0f, 0.0f, 0.529f, 0.0f,
                            0.0f, 0.0f, 0.0f, 1.0f
                    }));
                    filterimg.requestRender();
                }
                customizeicon_linear.setVisibility(View.GONE);
                horizontalsettings.setVisibility(View.GONE);
                lineview.setVisibility(View.VISIBLE);
                seekbarrela.setVisibility(View.VISIBLE);

                seekBar.setProgress(fadevalue);
                seekbar_count.setText(fadetxt);
                break;

            case R.id.highlight_img:
                brightfrom = "settings";
                clickedfilter = "highlight_img";
                filterAdjustSelection(view);
                if (highlightvalue == 0) {
                    Saving sav2 = new Saving();
                    sav2.execute(new GPUImageHiglightFilter());
                } else {
                    if (filter_imgg != null) {
                        Log.e("filter_imgg ----> ", filter_imgg);
                        filterimg.setImage(new File(filter_imgg));
                    }
                    switchFilterTo(new GPUImageHiglightFilter());
                    filterimg.requestRender();
                }
                customizeicon_linear.setVisibility(View.GONE);
                horizontalsettings.setVisibility(View.GONE);
                lineview.setVisibility(View.VISIBLE);
                seekbarrela.setVisibility(View.VISIBLE);

                seekBar.setProgress(highlightvalue);
                seekbar_count.setText(highlighttxt);
                break;

            case R.id.shadow_img:
                brightfrom = "settings";
                clickedfilter = "shadow_img";
                filterAdjustSelection(view);

                if (shadowvalue == 0) {
                    Saving sav2 = new Saving();
                    sav2.execute(new GPUImageHighlightShadowFilter());
                } else {
                    if (filter_imgg != null) {
                        Log.e("filter_imgg ----> ", filter_imgg);
                        filterimg.setImage(new File(filter_imgg));
                    }
                    switchFilterTo(new GPUImageHighlightShadowFilter());
                    filterimg.requestRender();
                }
                customizeicon_linear.setVisibility(View.GONE);
                horizontalsettings.setVisibility(View.GONE);
                lineview.setVisibility(View.VISIBLE);
                seekbarrela.setVisibility(View.VISIBLE);

                seekBar.setProgress(shadowvalue);
                seekbar_count.setText(shadowtxt);
                break;

            case R.id.vignette_img:
                brightfrom = "settings";
                clickedfilter = "vignette_img";
                filterAdjustSelection(view);
                PointF centrePoint = new PointF();
                centrePoint.x = 0.5f;
                centrePoint.y = 0.5f;

                if (vignettevalue == 0) {
                    Saving sav2 = new Saving();
                    sav2.execute(new GPUImageVignetteFilter(centrePoint, new float[]{0.0f, 0.0f, 0.0f}, 0.3f, 0.75f));
                } else {
                    if (filter_imgg != null) {
                        Log.e("filter_imgg ----> ", filter_imgg);
                        filterimg.setImage(new File(filter_imgg));
                    }

                    switchFilterTo(new GPUImageVignetteFilter(centrePoint, new float[]{0.0f, 0.0f, 0.0f}, 0.3f, 0.75f));
                    filterimg.requestRender();
                }
                customizeicon_linear.setVisibility(View.GONE);
                horizontalsettings.setVisibility(View.GONE);
                lineview.setVisibility(View.VISIBLE);
                seekbarrela.setVisibility(View.VISIBLE);

                seekBar.setProgress(vignettevalue);
                seekbar_count.setText(vignettetxt);
                break;


            case R.id.sharpen_img:
                brightfrom = "settings";
                clickedfilter = "sharpen_img";
                filterAdjustSelection(view);
                if (sharpenvalue == 0) {
                    Saving sav2 = new Saving();
                    sav2.execute(new GPUImageSharpenFilter());
                } else {
                    if (filter_imgg != null) {
                        Log.e("filter_imgg ----> ", filter_imgg);
                        filterimg.setImage(new File(filter_imgg));
                    }
                    switchFilterTo(new GPUImageSharpenFilter());
                    filterimg.requestRender();
                }
                customizeicon_linear.setVisibility(View.GONE);
                horizontalsettings.setVisibility(View.GONE);
                lineview.setVisibility(View.VISIBLE);
                seekbarrela.setVisibility(View.VISIBLE);

                seekBar.setProgress(sharpenvalue);
                seekbar_count.setText(sharpentxt);
                break;

            case R.id.control_seek_close_img:
                customizeicon_linear.setVisibility(View.VISIBLE);
                horizontalsettings.setVisibility(View.VISIBLE);
                lineview.setVisibility(View.GONE);
                set_controls_seekbarrela.setVisibility(View.GONE);
                switchFilterTo(tempfilter);
                break;

            case R.id.control_seek_tick_img:
                customizeicon_linear.setVisibility(View.VISIBLE);
                horizontalsettings.setVisibility(View.VISIBLE);
                lineview.setVisibility(View.GONE);
                set_controls_seekbarrela.setVisibility(View.GONE);
                tempfilter = filterimg.getFilter();

                if (clickedfilter.equals("contrast_img")) {
                    contrastvalue = control_seekbar.getProgress();
                    contrasttxt = control_seekbar_count.getText().toString();
                    txtContrast.setBackgroundResource(R.drawable.filters_applied_bg);
                } else if (clickedfilter.equals("warmth_img")) {
                    warmthvalue = control_seekbar.getProgress();
                    warmthtxt = control_seekbar_count.getText().toString();
                    txtWarmth.setBackgroundResource(R.drawable.filters_applied_bg);
                } else if (clickedfilter.equals("saturation_img")) {
                    saturationvalue = control_seekbar.getProgress();
                    saturationtxt = control_seekbar_count.getText().toString();
                    txtSaturation.setBackgroundResource(R.drawable.filters_applied_bg);
                }
                filtered_bitmap = filterimg.getGPUImage().getBitmapWithFilterApplied();
                saveImage("InstaSocial_images", "Filterimg", filtered_bitmap);
                break;

            //--------------------Filter Adjust----------------------------------------------
            case R.id.rotate_button:
                rotate_bitmap = BitmapUtils.rotateBitmap(rotate_bitmap, 90);
                cropperImageView.setImageBitmap(rotate_bitmap);
                break;

            case R.id.adjust_close_img:
                lineview.setVisibility(View.GONE);
                adjust_linear.setVisibility(View.GONE);
                customizeicon_linear.setVisibility(View.VISIBLE);
                horizontalsettings.setVisibility(View.VISIBLE);

                filterimg.setVisibility(View.VISIBLE);
                cropperImageView.setVisibility(View.GONE);
                break;

            case R.id.adjust_tick_img:
                lineview.setVisibility(View.GONE);
                adjust_linear.setVisibility(View.GONE);
                customizeicon_linear.setVisibility(View.VISIBLE);
                horizontalsettings.setVisibility(View.VISIBLE);

                filtered_bitmap = cropperImageView.getCroppedBitmap();
                saveImage("InstaSocial_images", "Filterimg", filtered_bitmap);
                txtCrop.setBackgroundResource(R.drawable.filters_applied_bg);
                filterimg.setVisibility(View.VISIBLE);
                cropperImageView.setVisibility(View.GONE);
                filterimg.setImage(new File(filter_imgg));
                break;

            //----------------------Filter Color effect method-------------------------------------------------------------

            case R.id.col_shadow_txt:
                color_shadow_txt.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                color_highlight_txt.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryInvert));
                shad__color_horizontal.setVisibility(View.VISIBLE);
                high__color_horizontal.setVisibility(View.GONE);
                break;

            case R.id.col_highlight_txt:
                color_highlight_txt.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                color_shadow_txt.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryInvert));
                shad__color_horizontal.setVisibility(View.GONE);
                high__color_horizontal.setVisibility(View.VISIBLE);
                break;

            //---------------------Filter Color shadow Effects------------------------------------------------------------------

            case R.id.white_shad:
                colorseffect = "#ffffff";
                if (tempfilter != null) {
                    switchFilterTo(tempfilter);
                    filterimg.requestRender();
                } else {
                    filterimg.setImage(effect_applied_bitmap);
                }

                switchFilterTo(new GPUImageColorMatrixFilter(0.5f, new float[]{
                        0.008f, 0.91f, 0.71f, 0.0f,
                        0.008f, 0.91f, 0.71f, 0.0f,
                        0.008f, 0.91f, 0.71f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f
                }));
                filterimg.requestRender();
                break;

            case R.id.red_shad:
                colorseffect = "#e63838";
                if (tempfilter != null) {
                    switchFilterTo(tempfilter);
                    filterimg.requestRender();
                } else {
                    filterimg.setImage(effect_applied_bitmap);
                }
                switchFilterTo(new GPUImageColorMatrixFilter(0.5f, new float[]{
                        1f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.416f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.416f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f
                }));
                filterimg.requestRender();
                break;

            case R.id.green_shad:
                colorseffect = "#56dc75";
                if (tempfilter != null) {
                    switchFilterTo(tempfilter);
                    filterimg.requestRender();
                } else {
                    Log.e("green_shad", "green_shad");
                    filterimg = (GPUImageView) findViewById(R.id.filter_img);
                    filterimg.setImage(effect_applied_bitmap);
                }
                switchFilterTo(new GPUImageColorMatrixFilter(0.5f, new float[]{
                        0.0f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.788f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.341f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f
                }));
                filterimg.requestRender();
                break;

            case R.id.blue_shad:
                colorseffect = "#5a86ec";
                if (tempfilter != null) {
                    switchFilterTo(tempfilter);
                    filterimg.requestRender();
                } else {
                    filterimg.setImage(effect_applied_bitmap);
                }
                switchFilterTo(new GPUImageColorMatrixFilter(0.5f, new float[]{
                        0.282f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.463f, 0.0f, 0.0f,
                        0.0f, 0.0f, 1f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f
                }));
                filterimg.requestRender();
                break;

            case R.id.yellow_shad:
                colorseffect = "#efef67";

                if (tempfilter != null) {
                    switchFilterTo(tempfilter);
                    filterimg.requestRender();
                } else {
                    filterimg.setImage(effect_applied_bitmap);
                }
                switchFilterTo(new GPUImageColorMatrixFilter(0.5f, new float[]{
                        1f, 0.0f, 0.0f, 0.0f,
                        0.0f, 1f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f
                }));
                filterimg.requestRender();
                break;

            case R.id.lightblue_shad:
                colorseffect = "#86eced";
                if (tempfilter != null) {
                    switchFilterTo(tempfilter);
                    filterimg.requestRender();
                } else {
                    filterimg.setImage(effect_applied_bitmap);
                }
                switchFilterTo(new GPUImageColorMatrixFilter(0.5f, new float[]{
                        0.529f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.808f, 0.0f, 0.0f,
                        0.0f, 0.0f, 1f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f
                }));
                filterimg.requestRender();
                break;

            case R.id.purple_shad:
                colorseffect = "#c152dc";
                if (tempfilter != null) {
                    switchFilterTo(tempfilter);
                    filterimg.requestRender();
                } else {
                    filterimg.setImage(effect_applied_bitmap);
                }
                switchFilterTo(new GPUImageColorMatrixFilter(0.5f, new float[]{
                        0.82f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.373f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.933f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f
                }));
                filterimg.requestRender();
                break;

            case R.id.gray_shad:
                colorseffect = "#999b9f";
                if (tempfilter != null) {
                    switchFilterTo(tempfilter);
                    filterimg.requestRender();
                } else {
                    filterimg.setImage(effect_applied_bitmap);
                }
                switchFilterTo(new GPUImageColorMatrixFilter(0.5f, new float[]{
                        0.62f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.62f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.62f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f
                }));
                filterimg.requestRender();
                break;

            case R.id.orange_shad:
                colorseffect = "#e98e43";
                if (tempfilter != null) {
                    switchFilterTo(tempfilter);
                    filterimg.requestRender();
                } else {
                    filterimg.setImage(effect_applied_bitmap);
                }
                switchFilterTo(new GPUImageColorMatrixFilter(0.5f, new float[]{
                        1f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.49f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.251f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f
                }));
                filterimg.requestRender();
                break;

            //--------------------filter color highlight efects-----------------------------------------------

            case R.id.white_high:
                if (tempfilter != null) {
                    switchFilterTo(tempfilter);
                    filterimg.requestRender();
                } else {
                    filterimg.setImage(effect_applied_bitmap);
                }
                switchFilterTo(new GPUImageColorMatrixFilter(0.5f, new float[]{
                        0.008f, 0.91f, 0.71f, 0.0f,
                        0.008f, 0.91f, 0.71f, 0.0f,
                        0.008f, 0.91f, 0.71f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f
                }));
                filterimg.requestRender();
                break;

            case R.id.red_high:
                if (tempfilter != null) {
                    switchFilterTo(tempfilter);
                    filterimg.requestRender();
                } else {
                    filterimg.setImage(effect_applied_bitmap);
                }
                switchFilterTo(new GPUImageColorMatrixFilter(0.5f, new float[]{
                        1f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f
                }));
                filterimg.requestRender();
                break;

            case R.id.green_high:
                if (tempfilter != null) {
                    switchFilterTo(tempfilter);
                    filterimg.requestRender();
                } else {
                    filterimg.setImage(effect_applied_bitmap);
                }
                switchFilterTo(new GPUImageColorMatrixFilter(0.5f, new float[]{
                        0.0f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.545f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f
                }));
                filterimg.requestRender();
                break;

            case R.id.blue_high:
                if (tempfilter != null) {
                    switchFilterTo(tempfilter);
                    filterimg.requestRender();
                } else {
                    filterimg.setImage(effect_applied_bitmap);
                }
                switchFilterTo(new GPUImageColorMatrixFilter(0.5f, new float[]{
                        0.0f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.804f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f
                }));
                filterimg.requestRender();
                break;

            case R.id.yellow_high:
                if (tempfilter != null) {
                    switchFilterTo(tempfilter);
                    filterimg.requestRender();
                } else {
                    filterimg.setImage(effect_applied_bitmap);
                }
                switchFilterTo(new GPUImageColorMatrixFilter(0.5f, new float[]{
                        0.804f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.678f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f
                }));
                filterimg.requestRender();
                break;

            case R.id.lightblue_high:
                if (tempfilter != null) {
                    switchFilterTo(tempfilter);
                    filterimg.requestRender();
                } else {
                    filterimg.setImage(effect_applied_bitmap);
                }
                switchFilterTo(new GPUImageColorMatrixFilter(0.5f, new float[]{
                        0.125f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.698f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.667f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f
                }));
                filterimg.requestRender();
                break;

            case R.id.purple_high:
                if (tempfilter != null) {
                    switchFilterTo(tempfilter);
                    filterimg.requestRender();
                } else {
                    filterimg.setImage(effect_applied_bitmap);
                }
                switchFilterTo(new GPUImageColorMatrixFilter(0.5f, new float[]{
                        0.49f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.149f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.804f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f
                }));
                filterimg.requestRender();
                break;

            case R.id.gray_high:
                if (tempfilter != null) {
                    switchFilterTo(tempfilter);
                    filterimg.requestRender();
                } else {
                    filterimg.setImage(effect_applied_bitmap);
                }
                switchFilterTo(new GPUImageColorMatrixFilter(0.5f, new float[]{
                        0.369f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.369f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.369f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f
                }));
                filterimg.requestRender();
                break;

            case R.id.orange_high:
                if (tempfilter != null) {
                    switchFilterTo(tempfilter);
                    filterimg.requestRender();
                } else {
                    filterimg.setImage(effect_applied_bitmap);
                }
                switchFilterTo(new GPUImageColorMatrixFilter(0.5f, new float[]{
                        1f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.271f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f
                }));
                filterimg.requestRender();
                break;

            case R.id.color_close_img:
                colors_rela.setVisibility(View.GONE);
                lineview.setVisibility(View.GONE);
                customizeicon_linear.setVisibility(View.VISIBLE);
                horizontalsettings.setVisibility(View.VISIBLE);
                switchFilterTo(tempfilter);
                break;

            case R.id.color_tick_img:
                customizeicon_linear.setVisibility(View.VISIBLE);
                horizontalsettings.setVisibility(View.VISIBLE);
                lineview.setVisibility(View.GONE);
                colors_rela.setVisibility(View.GONE);

                tempfilter = filterimg.getFilter();

                filtered_bitmap = filterimg.getGPUImage().getBitmapWithFilterApplied();
                saveImage("InstaSocial_images", "Filterimg", filtered_bitmap);

                break;


        }
    }


    public void ReverseAppliedValuesForFilters() {
        brightvalue = 0;
        contrastvalue = 50;
        structurevalue = 0;
        warmthvalue = 50;
        saturationvalue = 50;
        fadevalue = 0;
        highlightvalue = 0;
        shadowvalue = 0;
        vignettevalue = 0;
        sharpenvalue = 0;

        brighttxt = "0";
        contrasttxt = "0";
        structuretxt = "0";
        warmthtxt = "0";
        saturationtxt = "0";
        fadetxt = "0";
        highlighttxt = "0";
        shadowtxt = "0";
        vignettetxt = "0";
        sharpentxt = "0";
    }


    /**
     * Methods to apply the selected the filters in the image and display a preview -- STARTS HERE --
     * @param filter
     */
    private void switchFilterTo(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            filterimg.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie1_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            panel1.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie2_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is box ", "" + filter.getClass());
            mFilter = filter;
            panel2.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie3_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            panel3.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie4_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            panel4.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie5_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            panel5.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie6_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            panel6.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie7_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            panel7.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie8_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            panel8.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie9_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            panel9.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie10_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            panel10.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie11_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            panel11.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie12_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            panel12.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie13_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            panel13.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie14_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            panel14.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie15_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            panel15.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie16_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            panel16.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie17_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            panel17.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie18_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            panel18.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    /**
     * Methods to apply the selected the filters in the image and display a preview -- ENDS HERE --
     */


    /**
     * Save the image after applying filter
     */
    private void save_images() {

        CheckDensity();
        int reqWidth = 200, reqHeight = 200;
        int newWidth = 120, newHeight = 120;
//        int reqWidth=200,reqHeight=200;


        normal_img.setImage(getResizedBitmap(decodeSampledBitmapFromResource(imagepath, reqWidth, reqHeight), newWidth, newHeight));
        normal_img.requestRender();
        // if (linearLayoutPanel1.getVisibility() == View.VISIBLE) {
        panel1.setImage(getResizedBitmap(decodeSampledBitmapFromResource(imagepath, reqWidth, reqHeight), newWidth, newHeight));
        selfie1_filter(new GPUImageSepiaFilter());
        panel1.requestRender();
        //     }
        panel2.setImage(getResizedBitmap(decodeSampledBitmapFromResource(imagepath, reqWidth, reqHeight), newWidth, newHeight));
        selfie2_filter(new GPUImageBoxBlurFilter(2.0f));
        // panel2.setFilter(new GPUImageBoxBlurFilter(2.0f));
        panel2.requestRender();



        panel4.setImage(getResizedBitmap(decodeSampledBitmapFromResource(imagepath, reqWidth, reqHeight), newWidth, newHeight));
        selfie4_filter(new GPUImageEmbossFilter());
        panel4.requestRender();

        panel5.setImage(getResizedBitmap(decodeSampledBitmapFromResource(imagepath, reqWidth, reqHeight), newWidth, newHeight));
        selfie5_filter(new GPUImageHueFilter(5.0F));
        panel5.requestRender();

        panel6.setImage(getResizedBitmap(decodeSampledBitmapFromResource(imagepath, reqWidth, reqHeight), newWidth, newHeight));
        selfie6_filter(new GPUImageNashvilleFilter(PostImageFilterActivity.this));
        panel6.requestRender();

        panel7.setImage(getResizedBitmap(decodeSampledBitmapFromResource(imagepath, reqWidth, reqHeight), newWidth, newHeight));
        selfie7_filter(new GPUImageSketchFilter());
        panel7.requestRender();

        panel8.setImage(getResizedBitmap(decodeSampledBitmapFromResource(imagepath, reqWidth, reqHeight), newWidth, newHeight));
        selfie8_filter(new GPUImageGrayscaleFilter());
        panel8.requestRender();

        panel9.setImage(getResizedBitmap(decodeSampledBitmapFromResource(imagepath, reqWidth, reqHeight), newWidth, newHeight));
        selfie9_filter(new GPUImageColorInvertFilter());
        panel9.requestRender();

        panel10.setImage(getResizedBitmap(decodeSampledBitmapFromResource(imagepath, reqWidth, reqHeight), newWidth, newHeight));
        selfie10_filter(new GPUImageToneCurveFilter());
        panel10.requestRender();

        panel11.setImage(new File(imagepath));
        selfie11_filter(new GPUImageLordKelvinFilter(PostImageFilterActivity.this));
        panel11.requestRender();

        panel12.setImage(getResizedBitmap(decodeSampledBitmapFromResource(imagepath, reqWidth, reqHeight), newWidth, newHeight));
        selfie12_filter(new GPUImageKuwaharaFilter());
        panel12.requestRender();

        panel13.setImage(getResizedBitmap(decodeSampledBitmapFromResource(imagepath, reqWidth, reqHeight), newWidth, newHeight));
        selfie13_filter(new GPUImageAddBlendFilter());
        panel13.requestRender();

        panel14.setImage(getResizedBitmap(decodeSampledBitmapFromResource(imagepath, reqWidth, reqHeight), newWidth, newHeight));
        selfie14_filter(new GPUImageBulgeDistortionFilter());
        panel14.requestRender();

        panel15.setImage(getResizedBitmap(decodeSampledBitmapFromResource(imagepath, reqWidth, reqHeight), newWidth, newHeight));
        selfie15_filter(new GPUImageCGAColorspaceFilter());
        panel14.requestRender();

        panel16.setImage(getResizedBitmap(decodeSampledBitmapFromResource(imagepath, reqWidth, reqHeight), newWidth, newHeight));
        selfie16_filter(new GPUImageWeakPixelInclusionFilter());
        panel16.requestRender();

        panel17.setImage(getResizedBitmap(decodeSampledBitmapFromResource(imagepath, reqWidth, reqHeight), newWidth, newHeight));
        selfie17_filter(new GPUImageBilateralFilter(2.0f));
        panel17.requestRender();


        /**
         * Toggle filters' visibility based on user's preferences
         */


        if (sp.getBoolean("one", true)) {
            Log.e("Onside if", "OnSide if");
            linearLayoutPanel1.setVisibility(View.VISIBLE);
        } else {
            Log.e("Onside else", "OnSide else");
            linearLayoutPanel1.setVisibility(View.GONE);
        }

        if (sp.getBoolean("two", true)) {
            linearLayoutPanel2.setVisibility(View.VISIBLE);
        } else {
            linearLayoutPanel2.setVisibility(View.GONE);
        }

        if (sp.getBoolean("four", true)) {
            linearLayoutPanel4.setVisibility(View.VISIBLE);
        } else {
            linearLayoutPanel4.setVisibility(View.GONE);
        }

        if (sp.getBoolean("five", true)) {
            linearLayoutPanel5.setVisibility(View.VISIBLE);
        } else {
            linearLayoutPanel5.setVisibility(View.GONE);
        }

        if (sp.getBoolean("six", true)) {
            linearLayoutPanel6.setVisibility(View.VISIBLE);
        } else {
            linearLayoutPanel6.setVisibility(View.GONE);
        }

        if (sp.getBoolean("seven", true)) {
            linearLayoutPanel7.setVisibility(View.VISIBLE);
        } else {
            linearLayoutPanel7.setVisibility(View.GONE);
        }

        if (sp.getBoolean("eight", true)) {
            linearLayoutPanel8.setVisibility(View.VISIBLE);
        } else {
            linearLayoutPanel8.setVisibility(View.GONE);
        }

        if (sp.getBoolean("nine", true)) {
            linearLayoutPanel9.setVisibility(View.VISIBLE);
        } else {
            linearLayoutPanel9.setVisibility(View.GONE);
        }

        if (sp.getBoolean("ten", true)) {
            linearLayoutPanel10.setVisibility(View.VISIBLE);
        } else {
            linearLayoutPanel10.setVisibility(View.GONE);
        }

        if (sp.getBoolean("eleven", true)) {
            linearLayoutPanel11.setVisibility(View.VISIBLE);
        } else {
            linearLayoutPanel11.setVisibility(View.GONE);
        }

        if (sp.getBoolean("twelve", true)) {
            linearLayoutPanel12.setVisibility(View.VISIBLE);
        } else {
            linearLayoutPanel12.setVisibility(View.GONE);
        }

        if (sp.getBoolean("thirteen", true)) {
            linearLayoutPanel13.setVisibility(View.VISIBLE);
        } else {
            linearLayoutPanel13.setVisibility(View.GONE);
        }

        if (sp.getBoolean("fourteen", true)) {
            linearLayoutPanel14.setVisibility(View.VISIBLE);
        } else {
            linearLayoutPanel14.setVisibility(View.GONE);
        }

        if (sp.getBoolean("fifteen", true)) {
            linearLayoutPanel15.setVisibility(View.VISIBLE);
        } else {
            linearLayoutPanel15.setVisibility(View.GONE);
        }

        if (sp.getBoolean("sixteen", true)) {
            linearLayoutPanel16.setVisibility(View.VISIBLE);
        } else {
            linearLayoutPanel16.setVisibility(View.GONE);
        }

        if (sp.getBoolean("seventeen", true)) {
            linearLayoutPanel17.setVisibility(View.VISIBLE);
        } else {
            linearLayoutPanel17.setVisibility(View.GONE);
        }

    }


    /**
     *
     * Density check based on the dimensions
     */

    private void CheckDensity() {

        normal_img.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        panel1.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        panel2.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        panel4.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        panel5.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        panel6.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        panel7.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        panel8.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        panel9.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        panel10.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        panel11.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        panel12.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        panel13.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        panel14.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        panel15.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        panel16.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        panel17.setScaleType(GPUImage.ScaleType.CENTER_CROP);

        double density = getResources().getDisplayMetrics().density;

        Log.e("This Mobile Density", "====>>>>>>" + density);

        if (density >= 4.0) {
            //xxxhdpi
            normal_img.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
            panel1.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
            panel2.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
            panel4.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
            panel5.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
            panel6.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
            panel7.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
            panel8.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
            panel9.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
            panel10.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
            panel11.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
            panel12.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
            panel13.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
            panel14.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
            panel15.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
            panel16.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
            panel17.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
            managelinear.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
        } else if (density >= 3.0 && density < 4.0) {
            //xxhdpi
            normal_img.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            panel1.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            panel2.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            panel4.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            panel5.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            panel6.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            panel7.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            panel8.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            panel9.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            panel10.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            panel11.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            panel12.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            panel13.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            panel14.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            panel15.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            panel16.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            panel17.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            managelinear.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
        } else if (density >= 2.5 && density < 3.0) {

            normal_img.setLayoutParams(new LinearLayout.LayoutParams(320, 320));
            panel1.setLayoutParams(new LinearLayout.LayoutParams(320, 320));
            panel2.setLayoutParams(new LinearLayout.LayoutParams(320, 320));
            panel4.setLayoutParams(new LinearLayout.LayoutParams(320, 320));
            panel5.setLayoutParams(new LinearLayout.LayoutParams(320, 320));
            panel6.setLayoutParams(new LinearLayout.LayoutParams(320, 320));
            panel7.setLayoutParams(new LinearLayout.LayoutParams(320, 320));
            panel8.setLayoutParams(new LinearLayout.LayoutParams(320, 320));
            panel9.setLayoutParams(new LinearLayout.LayoutParams(320, 320));
            panel10.setLayoutParams(new LinearLayout.LayoutParams(320, 320));
            panel11.setLayoutParams(new LinearLayout.LayoutParams(320, 320));
            panel12.setLayoutParams(new LinearLayout.LayoutParams(320, 320));
            panel13.setLayoutParams(new LinearLayout.LayoutParams(320, 320));
            panel14.setLayoutParams(new LinearLayout.LayoutParams(320, 320));
            panel15.setLayoutParams(new LinearLayout.LayoutParams(320, 320));
            panel16.setLayoutParams(new LinearLayout.LayoutParams(320, 320));
            panel17.setLayoutParams(new LinearLayout.LayoutParams(320, 320));
            managelinear.setLayoutParams(new LinearLayout.LayoutParams(320, 320));

            //framelay_img.setLayoutParams(new RelativeLayout.LayoutParams(1000,1000));

        } else if (density >= 2.0 && density < 2.5) {
            //xhdpi
            normal_img.setLayoutParams(new LinearLayout.LayoutParams(220, 220));
            panel1.setLayoutParams(new LinearLayout.LayoutParams(220, 220));
            panel2.setLayoutParams(new LinearLayout.LayoutParams(220, 220));
            panel4.setLayoutParams(new LinearLayout.LayoutParams(220, 220));
            panel5.setLayoutParams(new LinearLayout.LayoutParams(220, 220));
            panel6.setLayoutParams(new LinearLayout.LayoutParams(220, 220));
            panel7.setLayoutParams(new LinearLayout.LayoutParams(220, 220));
            panel8.setLayoutParams(new LinearLayout.LayoutParams(220, 220));
            panel9.setLayoutParams(new LinearLayout.LayoutParams(220, 220));
            panel10.setLayoutParams(new LinearLayout.LayoutParams(220, 220));
            panel11.setLayoutParams(new LinearLayout.LayoutParams(220, 220));
            panel12.setLayoutParams(new LinearLayout.LayoutParams(220, 220));
            panel13.setLayoutParams(new LinearLayout.LayoutParams(220, 220));
            panel14.setLayoutParams(new LinearLayout.LayoutParams(220, 220));
            panel15.setLayoutParams(new LinearLayout.LayoutParams(220, 220));
            panel16.setLayoutParams(new LinearLayout.LayoutParams(220, 220));
            panel17.setLayoutParams(new LinearLayout.LayoutParams(220, 220));
            managelinear.setLayoutParams(new LinearLayout.LayoutParams(220, 220));

        } else if (density >= 1.5 && density < 2.0) {
            //hdpi
            normal_img.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            panel1.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            panel2.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            panel4.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            panel5.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            panel6.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            panel7.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            panel8.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            panel9.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            panel10.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            panel11.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            panel12.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            panel13.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            panel14.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            panel15.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            panel16.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            panel17.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            managelinear.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
        } else if (density >= 1.0 && density < 1.5) {
            //mdpi
            normal_img.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            panel1.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            panel2.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            panel4.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            panel5.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            panel6.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            panel7.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            panel8.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            panel9.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            panel10.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            panel11.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            panel12.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            panel13.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            panel14.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            panel15.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            panel16.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            panel17.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            managelinear.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);

        // If there is saved data reload it

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
        }
    }

    /**
     * Image size calculation
     */
    private void saveImage(final String folderName, final String fileName, final Bitmap image) {

        try {
            File dir = getDir(folderName, Context.MODE_PRIVATE);
            try {
                if (dir.mkdir()) {
                    System.out.println("Directory created");

                } else {
                    System.out.println("Directory is not created");
                }
                mFileTemp = new File(dir, fileName + ".jpg");

                Log.e("DIRECTORY -----> ", String.valueOf(dir));
                Log.e("CAPTURED IMAGE -----> ", String.valueOf(mFileTemp));
            } catch (Exception e) {
                e.printStackTrace();
            }

            FileOutputStream outStream = new FileOutputStream(mFileTemp);
            image.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.close();

            filter_imgg = mFileTemp.getAbsolutePath();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

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

    /**
     * Asynchronous saving
     */

    public class Saving extends AsyncTask<GPUImageFilter, Void, Void> {
        GPUImageFilter gpuImageFilter;

        @Override
        protected void onPreExecute() {
            //   filtered_bitmap =gp.getBitmapWithFilterApplied();

        }

        @Override
        protected Void doInBackground(GPUImageFilter... params) {

            saveImage("InstaSocial_images", "Filterimg", gp.getBitmapWithFilterApplied());
            gpuImageFilter = params[0];
            return null;
        }

        @Override
        protected void onPostExecute(Void res) {
            if (filter_imgg != null) {
                Log.e("filter_imgg ----> ", filter_imgg);
                filterimg.setImage(new File(filter_imgg));
            }
            switchFilterTo(gpuImageFilter);
            filterimg.requestRender();
            is_filtered = false;

        }
    }

    /*
    Redirect the final saved image to Add as a post
     */

    public class Saving_next extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {

            saveImage("InstaSocial_images", "Filterimg", gp.getBitmapWithFilterApplied());
            return null;
        }

        @Override
        protected void onPostExecute(Void res) {
            Intent nxt = new Intent(PostImageFilterActivity.this, AddPostFinalActivity.class);
            nxt.putExtra(AddPostFinalActivity.ARG_TYPE, ApiPostFeed.PostType.photo);
            nxt.putExtra(AddPostFinalActivity.ARG_MEDIA_PATH,
                    filter_imgg != null ? filter_imgg : imagepath
            );
            edsp = sp.edit();
            if (filter_imgg != null) {

                edsp.putString("mediaPath", filter_imgg);
            } else {
                edsp.putString("mediaPath", imagepath);
            }
            edsp.putString("fromfilter", "imagecategory");
            edsp.apply();

            startActivity(nxt);

        }
    }

    private void filterAdjustSelection(View view){
//        layoutCrop.setBackgroundResource(view.getId() == R.id.adjust_img ? R.drawable.filters_bluebackground : android.R.color.transparent);
//        layoutBrightness.setBackgroundResource(view.getId() == R.id.brightness_img ? R.drawable.filters_bluebackground : android.R.color.transparent);
//        layoutContrast.setBackgroundResource(view.getId() == R.id.contrast_img ? R.drawable.filters_bluebackground : android.R.color.transparent);
//        layoutStructure.setBackgroundResource(view.getId() == R.id.structure_img ? R.drawable.filters_bluebackground : android.R.color.transparent);
//        layoutWarmth.setBackgroundResource(view.getId() == R.id.warmth_img ? R.drawable.filters_bluebackground : android.R.color.transparent);
//        layoutSaturation.setBackgroundResource(view.getId() == R.id.saturation_img ? R.drawable.filters_bluebackground : android.R.color.transparent);
//        layoutColor.setBackgroundResource(view.getId() == R.id.color_img ? R.drawable.filters_bluebackground : android.R.color.transparent);
//        layoutFade.setBackgroundResource(view.getId() == R.id.fade_img ? R.drawable.filters_bluebackground : android.R.color.transparent);
//        layoutHighlights.setBackgroundResource(view.getId() == R.id.highlight_img ? R.drawable.filters_bluebackground : android.R.color.transparent);
//        layoutShadow.setBackgroundResource(view.getId() == R.id.shadow_img ? R.drawable.filters_bluebackground : android.R.color.transparent);
//        layoutVignette.setBackgroundResource(view.getId() == R.id.vignette_img ? R.drawable.filters_bluebackground : android.R.color.transparent);
//        layoutSharpen.setBackgroundResource(view.getId() == R.id.sharpen_img ? R.drawable.filters_bluebackground : android.R.color.transparent);

    }


}