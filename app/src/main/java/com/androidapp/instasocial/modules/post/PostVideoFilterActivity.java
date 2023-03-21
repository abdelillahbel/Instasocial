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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidapp.instasocial.App;
import com.androidapp.instasocial.R;
import com.androidapp.instasocial.ui.ImageFilter.GPUImageFilterTools;
import com.androidapp.instasocial.utils.AspectRatio;
import com.androidapp.instasocial.utils.Preferences;
import com.selfishare.videokit.VideoKit;
import com.selfishare.videokit.presets.Filter;
import com.selfishare.videokit.presets.VidFilterManager;

import java.io.File;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageBoxBlurFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageColorInvertFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageEmbossFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHazeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHueFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSharpenFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.GPUImageVignetteFilter;
import project.android.imageprocessing.FastImageProcessingPipeline;
import project.android.imageprocessing.FastImageProcessingView;
import project.android.imageprocessing.input.VideoResourceInput;
import project.android.imageprocessing.output.ScreenEndpoint;

/**
 * Video filter class
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)

public class PostVideoFilterActivity extends AppCompatActivity implements View.OnClickListener {


    /**
     * Member variables declarations/initializations
     */
    private static final String TAG = "PostVideoFilterActivity";
    Intent a;
    String videoPath = "";
    private static final boolean VERBOSE = false;
    private File vidInFile = null;
    private File vidOutFile, picInFile;
    private FastImageProcessingPipeline pipeline;
    private VideoResourceInput video;
    private ScreenEndpoint screen;
    private VidFilterManager filterManager;
    private long touchTime;
    private FastImageProcessingView glView;
    public static GPUImageView normal_img, panel1, panel2, panel3, panel4, panel5, panel6, panel7, panel8, panel9, panel10,
            panel11, panel12, panel13, panel14, panel15, panel16, panel17, panel18, panel19, panel20;
    private GPUImageFilter mFilter;
    private GPUImageFilterTools.FilterAdjuster mFilterAdjuster;
    Bitmap video1stFrame;
    ImageView video_play_icon, back_img;
    TextView nxt_txt;
    public static LinearLayout manage_filters_layout, linearLayoutPanel_normal, linearLayoutPanel1, linearLayoutPanel2, linearLayoutPanel3, linearLayoutPanel4, linearLayoutPanel5,
            linearLayoutPanel6, linearLayoutPanel7, linearLayoutPanel8, linearLayoutPanel9, linearLayoutPanel10, managelinear;
    ImageView videoCover, img_color_panel, videoCoverPreview;
    SharedPreferences sp;
    SharedPreferences.Editor edsp;
    HorizontalScrollView horizontal_panels;
    LinearLayout videoFrames;
    ImageView vidFrame1, vidFrame2, vidFrame3, vidFrame4, vidFrame5, vidFrame6, vidFrame7, mute_btn, un_mute_btn;
    Bitmap bmp[] = new Bitmap[30];
    private Rect rect1, rect2, rect3, rect4, rect5, rect6, rect7;
    RelativeLayout shade1, shade2, shade3, shade4, shade5, shade6, shade7;
    final float growTo = 1.2f;
    final long duration = 300;
    ScaleAnimation grow;
    ScaleAnimation shrink;
    AnimationSet growAndShrink;
    String from = "", desc = "", postingFrom = "";
    public boolean mutevideo = false;
    private int videoHeight, videoWidth;
    AspectRatio vidAspectRatio;
    FrameLayout framelay_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_post_video_filter_activity);

        /**
         * UI controls initialization
         */
        File dir = getDir("InstaSocial_images", Context.MODE_PRIVATE);
        if (!dir.exists()) {
            dir.mkdir();
        }
        vidOutFile = new File(dir, "demo-out.mp4");
        picInFile = new File(dir, "picOverlay.png");

        grow = new ScaleAnimation(1, growTo, 1, growTo,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        grow.setDuration(duration / 2);
        shrink = new ScaleAnimation(growTo, 1, growTo, 1,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(duration / 2);
        shrink.setStartOffset(duration / 2);
        growAndShrink = new AnimationSet(true);
        growAndShrink.setInterpolator(new LinearInterpolator());
        growAndShrink.addAnimation(grow);
        growAndShrink.addAnimation(shrink);
        framelay_img = findViewById(R.id.framelay_img);
        AspectRatio ratio = new AspectRatio(1, App.isTablet() ? 1 :0.93);
        ViewGroup.LayoutParams params = framelay_img.getLayoutParams();
        params.width = App.getScreenWidth();
        params.height = ratio.getHeightBy(App.getScreenWidth());

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        a = getIntent();
        videoPath = a.getStringExtra("videopath");

        try {
            vidInFile = new File(videoPath);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        from = a.getStringExtra("from");
        desc = a.getStringExtra("desc");
        postingFrom = a.getStringExtra("postingFrom");

        Log.e("videoPath === ", videoPath);

        edsp = sp.edit();
        edsp.putString(Preferences.NORMALVIDEO, videoPath);
        edsp.apply();


        video1stFrame = ThumbnailUtils.createVideoThumbnail(videoPath,
                MediaStore.Images.Thumbnails.MINI_KIND);
        video_play_icon = (ImageView) findViewById(R.id.play_icon);
        nxt_txt = (TextView) findViewById(R.id.nxt_txt);
        back_img = (ImageView) findViewById(R.id.back_img);
        videoCover = (ImageView) findViewById(R.id.videoCover);
        horizontal_panels = (HorizontalScrollView) findViewById(R.id.horizontal_panels);
        videoFrames = (LinearLayout) findViewById(R.id.videoFrames);
        img_color_panel = (ImageView) findViewById(R.id.img_color_panel);
        videoCoverPreview = (ImageView) findViewById(R.id.videoCoverPreview);
        mute_btn = (ImageView) findViewById(R.id.mute_btn);
        un_mute_btn = (ImageView) findViewById(R.id.un_mute_btn);
        shade1 = (RelativeLayout) findViewById(R.id.shade1);
        shade2 = (RelativeLayout) findViewById(R.id.shade2);
        shade3 = (RelativeLayout) findViewById(R.id.shade3);
        shade4 = (RelativeLayout) findViewById(R.id.shade4);
        shade5 = (RelativeLayout) findViewById(R.id.shade5);
        shade6 = (RelativeLayout) findViewById(R.id.shade6);
        shade7 = (RelativeLayout) findViewById(R.id.shade7);

        glView = (FastImageProcessingView) findViewById(R.id.videoView);
        video = new VideoResourceInput(glView, this, Uri.fromFile(vidInFile));
        pipeline = new FastImageProcessingPipeline();
        screen = new ScreenEndpoint(pipeline);
        filterManager = new VidFilterManager(vidInFile, picInFile, vidOutFile);
        video.addTarget(screen);//default filter
        vidFrame1 = (ImageView) findViewById(R.id.videoFrame1);
        vidFrame2 = (ImageView) findViewById(R.id.videoFrame2);
        vidFrame3 = (ImageView) findViewById(R.id.videoFrame3);
        vidFrame4 = (ImageView) findViewById(R.id.videoFrame4);
        vidFrame5 = (ImageView) findViewById(R.id.videoFrame5);
        vidFrame6 = (ImageView) findViewById(R.id.videoFrame6);
        vidFrame7 = (ImageView) findViewById(R.id.videoFrame7);

        Log.e("%%%" + glView.getHeight(), "hjk" + glView.getWidth());
        normal_img = (GPUImageView) findViewById(R.id.normal_img);
        panel1 = (GPUImageView) findViewById(R.id.panel_img1);
        panel2 = (GPUImageView) findViewById(R.id.panel_img2);
        panel3 = (GPUImageView) findViewById(R.id.panel_img3);
        panel4 = (GPUImageView) findViewById(R.id.panel_img4);
        panel5 = (GPUImageView) findViewById(R.id.panel_img5);
        panel6 = (GPUImageView) findViewById(R.id.panel_img6);
        panel7 = (GPUImageView) findViewById(R.id.panel_img7);
        panel8 = (GPUImageView) findViewById(R.id.panel_img8);
        panel9 = (GPUImageView) findViewById(R.id.panel_img9);
        panel10 = (GPUImageView) findViewById(R.id.panel_img10);

        manage_filters_layout = (LinearLayout) findViewById(R.id.manage_filters_layout);
        linearLayoutPanel_normal = (LinearLayout) findViewById(R.id.layoutPanelNoFiler);
        linearLayoutPanel1 = (LinearLayout) findViewById(R.id.layoutPanel1);
        linearLayoutPanel2 = (LinearLayout) findViewById(R.id.layoutPanel2);
        linearLayoutPanel3 = (LinearLayout) findViewById(R.id.layoutPanel3);
        linearLayoutPanel4 = (LinearLayout) findViewById(R.id.layoutPanel4);
        linearLayoutPanel5 = (LinearLayout) findViewById(R.id.layoutPanel5);
        linearLayoutPanel6 = (LinearLayout) findViewById(R.id.layoutPanel6);
        linearLayoutPanel7 = (LinearLayout) findViewById(R.id.layoutPanel7);
        linearLayoutPanel8 = (LinearLayout) findViewById(R.id.layoutPanel8);
        linearLayoutPanel9 = (LinearLayout) findViewById(R.id.layoutPanel9);
        linearLayoutPanel10 = (LinearLayout) findViewById(R.id.layoutPanel10);
        managelinear = (LinearLayout) findViewById(R.id.manage_linear);


        /**
         * Prepare to play video
         */
        final int videoLayoutHeight = App.getScreenWidth();//added code
        final int videoLayoutWidth = App.getScreenWidth();//added code

        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
//                videoWidth = mp.getVideoWidth();
//                videoHeight = mp.getVideoHeight();
//                vidAspectRatio = new AspectRatio(videoWidth, videoHeight);
//                int viewHeight = framelay_img.getLayoutParams().height;
//                int viewWidth = vidAspectRatio.getWidthBy(viewHeight);
//                Bitmap transBitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
//                Canvas canvas = new Canvas(transBitmap);
//                Paint paint = new Paint();
//                paint.setColor(getResources().getColor(android.R.color.transparent));
//                paint.setXfermode((Xfermode) new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
//                canvas.drawPaint(paint);
//                ViewGroup.LayoutParams params = glView.getLayoutParams();
//                params.width = viewWidth;
//                params.height = viewHeight;
//                glView.setLayoutParams(params);
//                Log.e(TAG, "onVideoPrepared: videoSize: " + videoWidth + " * " + videoHeight +
//                        " resized: " + viewWidth + " * " + viewHeight);

                AspectRatio videoRatio=new AspectRatio(mp.getVideoWidth(),mp.getVideoHeight());
                int srcW = mp.getVideoWidth();
                int srcH = mp.getVideoHeight();
                if (srcW>srcH){
                    glView.getLayoutParams().width=videoLayoutWidth;
                    glView.getLayoutParams().height=videoRatio.getHeightBy(videoLayoutWidth);
                }else if (srcW<srcH){
                    glView.getLayoutParams().height=videoLayoutHeight;
                    glView.getLayoutParams().width=videoRatio.getWidthBy(videoLayoutHeight);
                }else {
                    glView.getLayoutParams().width=App.getScreenWidth();
                    glView.getLayoutParams().height=App.getScreenWidth();
                }

            }
        });


        glView.setVisibility(View.VISIBLE);
        videoCoverPreview.setVisibility(View.GONE);
        pipeline.addRootRenderer(video);
        glView.setPipeline(pipeline);
        pipeline.startRendering();
        video.startWhenReady();
        horizontal_panels.setVisibility(View.VISIBLE);
        videoFrames.setVisibility(View.GONE);

        glView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent me) {
                Log.d("toucheddd", "#############");
                if (System.currentTimeMillis() - 100 > touchTime) {
                    touchTime = System.currentTimeMillis();
                    if (video.isPlaying()) {
                        video.stop();

                        video_play_icon.setVisibility(View.VISIBLE);
                    } else {
                        video.startWhenReady();
                        video_play_icon.setVisibility(View.GONE);
                    }
                }
                return true;
            }

        });


        /**
         * Toggle filters' visibility based on user's preferences
         */

        if (sp.getBoolean("1", true))
            linearLayoutPanel1.setVisibility(View.VISIBLE);
        else
            linearLayoutPanel1.setVisibility(View.GONE);

        if (sp.getBoolean("2", true))
            linearLayoutPanel2.setVisibility(View.VISIBLE);
        else
            linearLayoutPanel2.setVisibility(View.GONE);
        if (sp.getBoolean("3", true))
            linearLayoutPanel3.setVisibility(View.VISIBLE);
        else
            linearLayoutPanel3.setVisibility(View.GONE);
        if (sp.getBoolean("4", true))
            linearLayoutPanel4.setVisibility(View.VISIBLE);
        else
            linearLayoutPanel4.setVisibility(View.GONE);
        if (sp.getBoolean("5", true))
            linearLayoutPanel5.setVisibility(View.VISIBLE);
        else
            linearLayoutPanel5.setVisibility(View.GONE);
        if (sp.getBoolean("6", true))
            linearLayoutPanel6.setVisibility(View.VISIBLE);
        else
            linearLayoutPanel6.setVisibility(View.GONE);
        if (sp.getBoolean("7", true))
            linearLayoutPanel7.setVisibility(View.VISIBLE);
        else
            linearLayoutPanel7.setVisibility(View.GONE);
        if (sp.getBoolean("8", true))
            linearLayoutPanel8.setVisibility(View.VISIBLE);
        else
            linearLayoutPanel8.setVisibility(View.GONE);
        if (sp.getBoolean("9", true))
            linearLayoutPanel9.setVisibility(View.VISIBLE);
        else
            linearLayoutPanel9.setVisibility(View.GONE);
        if (sp.getBoolean("10", true))
            linearLayoutPanel10.setVisibility(View.VISIBLE);
        else
            linearLayoutPanel10.setVisibility(View.GONE);

        /**
         * Register click events
         */
        nxt_txt.setOnClickListener(this);
        normal_img.setOnClickListener(this);
        panel1.setOnClickListener(this);
        panel2.setOnClickListener(this);
        panel3.setOnClickListener(this);
        panel4.setOnClickListener(this);
        panel5.setOnClickListener(this);
        panel6.setOnClickListener(this);
        panel7.setOnClickListener(this);
        panel8.setOnClickListener(this);
        panel9.setOnClickListener(this);
        panel10.setOnClickListener(this);
        manage_filters_layout.setOnClickListener(this);
        back_img.setOnClickListener(this);
        videoCover.setOnClickListener(this);
        img_color_panel.setOnClickListener(this);
        vidFrame1.setOnClickListener(this);
        vidFrame2.setOnClickListener(this);
        vidFrame3.setOnClickListener(this);
        vidFrame4.setOnClickListener(this);
        vidFrame5.setOnClickListener(this);
        vidFrame6.setOnClickListener(this);
        vidFrame7.setOnClickListener(this);
        mute_btn.setOnClickListener(this);
        un_mute_btn.setOnClickListener(this);
        save_images();
    }

    /**
     * Set preview image
     * Apply filer
     * Render
     */
    private void save_images() {

        CheckDensity();

        normal_img.setImage(video1stFrame);
        normal_img.requestRender();

        panel1.setImage(video1stFrame);
        selfie1_filter(new GPUImageSepiaFilter());
        panel1.requestRender();

        panel2.setImage(video1stFrame);
        selfie4_filter(new GPUImageGrayscaleFilter());
        panel2.requestRender();


        panel3.setImage(video1stFrame);
        selfie5_filter(new GPUImageContrastFilter(4.0f));
        panel3.requestRender();

        panel4.setImage(video1stFrame);//haze
        selfie7_filter(new GPUImageHazeFilter());
        mFilterAdjuster.adjust(10);
        panel4.requestRender();

        panel5.setImage(video1stFrame);//hue filter
        selfie8_filter(new GPUImageHueFilter(100.0f));
        panel5.requestRender();

        panel6.setImage(video1stFrame);
        selfie9_filter(new GPUImageColorInvertFilter());
        panel6.requestRender();

        panel7.setImage(video1stFrame);//boxblur
        selfie13_filter(new GPUImageBoxBlurFilter());
        panel7.requestRender();

        panel8.setImage(video1stFrame);
        selfie14_filter(new GPUImageEmbossFilter());
        panel8.requestRender();


        panel9.setImage(video1stFrame);//sharpen filter
        selfie15_filter(new GPUImageSharpenFilter());
        panel9.requestRender();


        panel10.setImage(video1stFrame);//vignette filter
        PointF point = new PointF(0.5f, 0.5f);
        selfie16_filter(new GPUImageVignetteFilter(point, new float[]{0.0f, 0.0f, 0.0f}, 0.3f, 0.75f));
        panel10.requestRender();

    }

    /**
     * Density check based on the dimensions
     */
    private void CheckDensity() {

        normal_img.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        panel1.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        panel2.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        panel3.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        panel4.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        panel5.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        panel6.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        panel7.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        panel8.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        panel9.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        panel10.setScaleType(GPUImage.ScaleType.CENTER_CROP);

        double density = getResources().getDisplayMetrics().density;

        if (density >= 4.0) {
            //xxxhdpi
            normal_img.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
            panel1.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
            panel2.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
            panel3.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
            panel4.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
            panel5.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
            panel6.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
            panel7.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
            panel8.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
            panel9.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
            panel10.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
            managelinear.setLayoutParams(new LinearLayout.LayoutParams(330, 330));
        } else if (density >= 3.0 && density < 4.0) {
            //xxhdpi
            normal_img.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            panel1.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            panel2.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            panel3.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            panel4.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            panel5.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            panel6.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            panel7.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            panel8.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            panel9.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            panel10.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            managelinear.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
        } else if (density >= 2.0 && density < 3.0) {
            //xhdpi
            normal_img.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
            panel1.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
            panel2.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
            panel3.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
            panel4.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
            panel5.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
            panel6.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
            panel7.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
            panel8.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
            panel9.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
            panel10.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
            managelinear.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
        } else if (density >= 1.5 && density < 2.0) {
            //hdpi
            normal_img.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            panel1.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            panel2.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            panel3.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            panel4.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            panel5.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            panel6.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            panel7.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            panel8.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            panel9.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            panel10.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            managelinear.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
        } else if (density >= 1.0 && density < 1.5) {
            //mdpi
            normal_img.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            panel1.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            panel2.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            panel3.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            panel4.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            panel5.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            panel6.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            panel7.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            panel8.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            panel9.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            panel10.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            managelinear.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
        }
    }

    /**
     * Methosd to apply filter
     *
     * @param filter
     */
    private void selfie1_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            panel1.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }


    private void selfie4_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            panel2.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie5_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            panel3.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }


    private void selfie7_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            panel4.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie8_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            panel5.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie9_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            panel6.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }


    private void selfie13_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            panel7.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie14_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            panel8.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie15_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            panel9.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    private void selfie16_filter(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            Log.e("The fIlter is ", "" + filter.getClass());
            mFilter = filter;
            panel10.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.mute_btn:

                filterManager.isMute(false);
                mute_btn.setVisibility(View.GONE);
                un_mute_btn.setVisibility(View.VISIBLE);
                video.unMute();
                Log.e("unmute", "unmute");
                break;
            case R.id.un_mute_btn:
                filterManager.isMute(true);
                un_mute_btn.setVisibility(View.GONE);
                mute_btn.setVisibility(View.VISIBLE);
                video.mute();
                Log.e("mute", "mute");
                break;
            case R.id.back_img:
                try {
                    PostVideoRecorderPreL.videoBean.clear();
                } catch (Exception e) {
                }

                video.stop();
                finish();
                break;

            case R.id.nxt_txt:

                video.stop();
                new VidFilterTask().execute(filterManager.getFfmpegArgs());


                break;
            case R.id.videoCover:
                Log.e("here", "here");
                horizontal_panels.setVisibility(View.GONE);
                videoFrames.setVisibility(View.VISIBLE);

                glView.setVisibility(View.GONE);
                videoCoverPreview.setVisibility(View.VISIBLE);
                videoCoverPreview.setImageBitmap(bmp[0]);
                vidFrame1.setImageBitmap(bmp[0]);
                vidFrame2.setImageBitmap(bmp[1]);
                vidFrame3.setImageBitmap(bmp[2]);
                vidFrame4.setImageBitmap(bmp[3]);
                vidFrame5.setImageBitmap(bmp[4]);
                vidFrame6.setImageBitmap(bmp[5]);
                vidFrame7.setImageBitmap(bmp[6]);
                break;

            case R.id.img_color_panel:
                break;


            /**
             * Highlight the selected filter with blue border --- STARTS HERE ----
             */
            case R.id.normal_img:
                linearLayoutPanel_normal.setBackground(ContextCompat.getDrawable(this, R.drawable.filters_bluebackground));
                linearLayoutPanel1.setBackground(null);
                linearLayoutPanel2.setBackground(null);
                linearLayoutPanel3.setBackground(null);
                linearLayoutPanel4.setBackground(null);
                linearLayoutPanel5.setBackground(null);
                linearLayoutPanel6.setBackground(null);
                linearLayoutPanel7.setBackground(null);
                linearLayoutPanel8.setBackground(null);
                linearLayoutPanel9.setBackground(null);
                linearLayoutPanel10.setBackground(null);
                glView.setVisibility(View.VISIBLE);
                filterManager.switchFilter(pipeline, screen, video, Filter.none);
                break;

            //----------filter definition
            case R.id.panel_img1:
                linearLayoutPanel_normal.setBackground(null);
                linearLayoutPanel1.setBackground(ContextCompat.getDrawable(this, R.drawable.filters_bluebackground));
                linearLayoutPanel2.setBackground(null);
                linearLayoutPanel3.setBackground(null);
                linearLayoutPanel4.setBackground(null);
                linearLayoutPanel5.setBackground(null);
                linearLayoutPanel6.setBackground(null);
                linearLayoutPanel7.setBackground(null);
                linearLayoutPanel8.setBackground(null);
                linearLayoutPanel9.setBackground(null);
                linearLayoutPanel10.setBackground(null);
                //normalVideoView.setVisibility(View.GONE);
                glView.setVisibility(View.VISIBLE);
                filterManager.switchFilter(pipeline, screen, video, Filter.sepia);

                break;
            case R.id.panel_img2:
                linearLayoutPanel_normal.setBackground(null);
                linearLayoutPanel1.setBackground(null);
                linearLayoutPanel2.setBackground(ContextCompat.getDrawable(this, R.drawable.filters_bluebackground));
                linearLayoutPanel3.setBackground(null);
                linearLayoutPanel4.setBackground(null);
                linearLayoutPanel5.setBackground(null);
                linearLayoutPanel6.setBackground(null);
                linearLayoutPanel7.setBackground(null);
                linearLayoutPanel8.setBackground(null);
                linearLayoutPanel9.setBackground(null);
                linearLayoutPanel10.setBackground(null);
//                normalVideoView.setVisibility(View.GONE);
                glView.setVisibility(View.VISIBLE);
                filterManager.switchFilter(pipeline, screen, video, Filter.grayscale);

                break;
            case R.id.panel_img3:
                linearLayoutPanel_normal.setBackground(null);
                linearLayoutPanel1.setBackground(null);
                linearLayoutPanel2.setBackground(null);
                linearLayoutPanel3.setBackground(ContextCompat.getDrawable(this, R.drawable.filters_bluebackground));
                linearLayoutPanel4.setBackground(null);
                linearLayoutPanel5.setBackground(null);
                linearLayoutPanel6.setBackground(null);
                linearLayoutPanel7.setBackground(null);
                linearLayoutPanel8.setBackground(null);
                linearLayoutPanel9.setBackground(null);
                linearLayoutPanel10.setBackground(null);
//                normalVideoView.setVisibility(View.GONE);
                glView.setVisibility(View.VISIBLE);
                filterManager.switchFilter(pipeline, screen, video, Filter.contrest);

                break;
            case R.id.panel_img4:
                linearLayoutPanel_normal.setBackground(null);
                linearLayoutPanel1.setBackground(null);
                linearLayoutPanel2.setBackground(null);
                linearLayoutPanel3.setBackground(null);
                linearLayoutPanel4.setBackground(ContextCompat.getDrawable(this, R.drawable.filters_bluebackground));
                linearLayoutPanel5.setBackground(null);
                linearLayoutPanel6.setBackground(null);
                linearLayoutPanel7.setBackground(null);
                linearLayoutPanel8.setBackground(null);
                linearLayoutPanel9.setBackground(null);
                linearLayoutPanel10.setBackground(null);
//                normalVideoView.setVisibility(View.GONE);
                glView.setVisibility(View.VISIBLE);
                filterManager.switchFilter(pipeline, screen, video, Filter.haze);

                break;
            case R.id.panel_img5:
                linearLayoutPanel_normal.setBackground(null);
                linearLayoutPanel1.setBackground(null);
                linearLayoutPanel2.setBackground(null);
                linearLayoutPanel3.setBackground(null);
                linearLayoutPanel4.setBackground(null);
                linearLayoutPanel5.setBackground(ContextCompat.getDrawable(this, R.drawable.filters_bluebackground));
                linearLayoutPanel6.setBackground(null);
                linearLayoutPanel7.setBackground(null);
                linearLayoutPanel8.setBackground(null);
                linearLayoutPanel9.setBackground(null);
                linearLayoutPanel10.setBackground(null);
//                normalVideoView.setVisibility(View.GONE);
                glView.setVisibility(View.VISIBLE);
                filterManager.switchFilter(pipeline, screen, video, Filter.hue);

                break;
            case R.id.panel_img6:
                linearLayoutPanel_normal.setBackground(null);
                linearLayoutPanel1.setBackground(null);
                linearLayoutPanel2.setBackground(null);
                linearLayoutPanel3.setBackground(null);
                linearLayoutPanel4.setBackground(null);
                linearLayoutPanel5.setBackground(null);
                linearLayoutPanel6.setBackground(ContextCompat.getDrawable(this, R.drawable.filters_bluebackground));
                linearLayoutPanel7.setBackground(null);
                linearLayoutPanel8.setBackground(null);
                linearLayoutPanel9.setBackground(null);
                linearLayoutPanel10.setBackground(null);
//                normalVideoView.setVisibility(View.GONE);
                glView.setVisibility(View.VISIBLE);
                filterManager.switchFilter(pipeline, screen, video, Filter.negate);

                break;
            case R.id.panel_img7:
                linearLayoutPanel_normal.setBackground(null);
                linearLayoutPanel1.setBackground(null);
                linearLayoutPanel2.setBackground(null);
                linearLayoutPanel3.setBackground(null);
                linearLayoutPanel4.setBackground(null);
                linearLayoutPanel5.setBackground(null);
                linearLayoutPanel6.setBackground(null);
                linearLayoutPanel7.setBackground(ContextCompat.getDrawable(this, R.drawable.filters_bluebackground));
                linearLayoutPanel8.setBackground(null);
                linearLayoutPanel9.setBackground(null);
                linearLayoutPanel10.setBackground(null);
//                normalVideoView.setVisibility(View.GONE);
                glView.setVisibility(View.VISIBLE);
                filterManager.switchFilter(pipeline, screen, video, Filter.boxblur);

                break;
            case R.id.panel_img8:
                linearLayoutPanel_normal.setBackground(null);
                linearLayoutPanel1.setBackground(null);
                linearLayoutPanel2.setBackground(null);
                linearLayoutPanel3.setBackground(null);
                linearLayoutPanel4.setBackground(null);
                linearLayoutPanel5.setBackground(null);
                linearLayoutPanel6.setBackground(null);
                linearLayoutPanel7.setBackground(null);
                linearLayoutPanel8.setBackground(ContextCompat.getDrawable(this, R.drawable.filters_bluebackground));
                linearLayoutPanel9.setBackground(null);
                linearLayoutPanel10.setBackground(null);
//                normalVideoView.setVisibility(View.GONE);
                glView.setVisibility(View.VISIBLE);
                filterManager.switchFilter(pipeline, screen, video, Filter.emboss);

                break;
            case R.id.panel_img9:
                linearLayoutPanel_normal.setBackground(null);
                linearLayoutPanel1.setBackground(null);
                linearLayoutPanel2.setBackground(null);
                linearLayoutPanel3.setBackground(null);
                linearLayoutPanel4.setBackground(null);
                linearLayoutPanel5.setBackground(null);
                linearLayoutPanel6.setBackground(null);
                linearLayoutPanel7.setBackground(null);
                linearLayoutPanel8.setBackground(null);
                linearLayoutPanel9.setBackground(ContextCompat.getDrawable(this, R.drawable.filters_bluebackground));
                linearLayoutPanel10.setBackground(null);
//                normalVideoView.setVisibility(View.GONE);
                glView.setVisibility(View.VISIBLE);
                filterManager.switchFilter(pipeline, screen, video, Filter.sharpen);

                break;
            case R.id.panel_img10:
                linearLayoutPanel_normal.setBackground(null);
                linearLayoutPanel1.setBackground(null);
                linearLayoutPanel2.setBackground(null);
                linearLayoutPanel3.setBackground(null);
                linearLayoutPanel4.setBackground(null);
                linearLayoutPanel5.setBackground(null);
                linearLayoutPanel6.setBackground(null);
                linearLayoutPanel7.setBackground(null);
                linearLayoutPanel8.setBackground(null);
                linearLayoutPanel9.setBackground(null);
                linearLayoutPanel10.setBackground(ContextCompat.getDrawable(this, R.drawable.filters_bluebackground));
//                normalVideoView.setVisibility(View.GONE);
                glView.setVisibility(View.VISIBLE);
                filterManager.switchFilter(pipeline, screen, video, Filter.vignette);

                break;

            /**
             * Highlight the selected filter with blue border --- ENDS HERE ----
             */

            /**
             * Manage filters onclick
             */
            case R.id.manage_filters_layout:
                Intent manageFilterIntent = new Intent(PostVideoFilterActivity.this, ManageFiltersActivity.class);
                manageFilterIntent.putExtra("type", "video");
                manageFilterIntent.putExtra("imagepath", "");
                manageFilterIntent.putExtra("videopath", videoPath);
                manageFilterIntent.putExtra("from", from);
                manageFilterIntent.putExtra("desc", desc);
                manageFilterIntent.putExtra("postingFrom", postingFrom);
                startActivity(manageFilterIntent);
                if (video.isPlaying()) video.stop();
                overridePendingTransition(R.anim.open_next, R.anim.close_main);

                break;
            case R.id.videoFrame1:
                videoCoverPreview.setImageBitmap(bmp[0]);
                vidFrame1.startAnimation(growAndShrink);
                vidFrame2.setAnimation(null);
                vidFrame3.setAnimation(null);
                vidFrame4.setAnimation(null);
                vidFrame5.setAnimation(null);
                vidFrame6.setAnimation(null);
                vidFrame7.setAnimation(null);

                shade1.setVisibility(View.GONE);
                shade2.setVisibility(View.VISIBLE);
                shade3.setVisibility(View.VISIBLE);
                shade4.setVisibility(View.VISIBLE);
                shade5.setVisibility(View.VISIBLE);
                shade6.setVisibility(View.VISIBLE);
                shade7.setVisibility(View.VISIBLE);


                break;
            case R.id.videoFrame2:
                videoCoverPreview.setImageBitmap(bmp[1]);
                vidFrame2.startAnimation(growAndShrink);
                vidFrame1.setAnimation(null);
                vidFrame3.setAnimation(null);
                vidFrame4.setAnimation(null);
                vidFrame5.setAnimation(null);
                vidFrame6.setAnimation(null);
                vidFrame7.setAnimation(null);

                shade1.setVisibility(View.VISIBLE);
                shade2.setVisibility(View.GONE);
                shade3.setVisibility(View.VISIBLE);
                shade4.setVisibility(View.VISIBLE);
                shade5.setVisibility(View.VISIBLE);
                shade6.setVisibility(View.VISIBLE);
                shade7.setVisibility(View.VISIBLE);
                break;
            case R.id.videoFrame3:
                videoCoverPreview.setImageBitmap(bmp[2]);
                vidFrame3.startAnimation(growAndShrink);
                vidFrame1.setAnimation(null);
                vidFrame2.setAnimation(null);
                vidFrame4.setAnimation(null);
                vidFrame5.setAnimation(null);
                vidFrame6.setAnimation(null);
                vidFrame7.setAnimation(null);

                shade1.setVisibility(View.VISIBLE);
                shade2.setVisibility(View.VISIBLE);
                shade3.setVisibility(View.GONE);
                shade4.setVisibility(View.VISIBLE);
                shade5.setVisibility(View.VISIBLE);
                shade6.setVisibility(View.VISIBLE);
                shade7.setVisibility(View.VISIBLE);
                break;
            case R.id.videoFrame4:
                videoCoverPreview.setImageBitmap(bmp[3]);
                vidFrame4.startAnimation(growAndShrink);
                vidFrame1.setAnimation(null);
                vidFrame3.setAnimation(null);
                vidFrame2.setAnimation(null);
                vidFrame5.setAnimation(null);
                vidFrame6.setAnimation(null);
                vidFrame7.setAnimation(null);

                shade1.setVisibility(View.VISIBLE);
                shade2.setVisibility(View.VISIBLE);
                shade3.setVisibility(View.VISIBLE);
                shade4.setVisibility(View.GONE);
                shade5.setVisibility(View.VISIBLE);
                shade6.setVisibility(View.VISIBLE);
                shade7.setVisibility(View.VISIBLE);
                break;
            case R.id.videoFrame5:
                videoCoverPreview.setImageBitmap(bmp[4]);
                vidFrame1.setAnimation(null);
                vidFrame3.setAnimation(null);
                vidFrame4.setAnimation(null);
                vidFrame2.setAnimation(null);
                vidFrame6.setAnimation(null);
                vidFrame7.setAnimation(null);

                vidFrame5.startAnimation(growAndShrink);
                shade1.setVisibility(View.VISIBLE);
                shade2.setVisibility(View.VISIBLE);
                shade3.setVisibility(View.VISIBLE);
                shade4.setVisibility(View.VISIBLE);
                shade5.setVisibility(View.GONE);
                shade6.setVisibility(View.VISIBLE);
                shade7.setVisibility(View.VISIBLE);
                break;
            case R.id.videoFrame6:
                videoCoverPreview.setImageBitmap(bmp[5]);
                vidFrame6.startAnimation(growAndShrink);
                vidFrame1.setAnimation(null);
                vidFrame3.setAnimation(null);
                vidFrame4.setAnimation(null);
                vidFrame5.setAnimation(null);
                vidFrame2.setAnimation(null);
                vidFrame7.setAnimation(null);

                shade1.setVisibility(View.VISIBLE);
                shade2.setVisibility(View.VISIBLE);
                shade3.setVisibility(View.VISIBLE);
                shade4.setVisibility(View.VISIBLE);
                shade5.setVisibility(View.VISIBLE);
                shade6.setVisibility(View.GONE);
                shade7.setVisibility(View.VISIBLE);
                break;
            case R.id.videoFrame7:
                videoCoverPreview.setImageBitmap(bmp[6]);
                vidFrame7.startAnimation(growAndShrink);
                vidFrame1.setAnimation(null);
                vidFrame3.setAnimation(null);
                vidFrame4.setAnimation(null);
                vidFrame5.setAnimation(null);
                vidFrame6.setAnimation(null);
                vidFrame2.setAnimation(null);

                shade1.setVisibility(View.VISIBLE);
                shade2.setVisibility(View.VISIBLE);
                shade3.setVisibility(View.VISIBLE);
                shade4.setVisibility(View.VISIBLE);
                shade5.setVisibility(View.VISIBLE);
                shade6.setVisibility(View.VISIBLE);
                shade7.setVisibility(View.GONE);
                break;
        }


        vidFrame1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
// Construct a rect of the view's bounds
                        rect1 = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (!rect1.contains((int) event.getX(), (int) event.getY())) {
                            // User moved outside bounds
                        } else {
                            Log.e("v1", "move");
                        }
                        break;
                }
                return false;
            }
        });

        vidFrame2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
// Construct a rect of the view's bounds
                        rect2 = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (!rect2.contains((int) event.getX(), (int) event.getY())) {
                            // User moved outside bounds
                        } else {
                            Log.e("v2", "move");
                        }
                        break;

                }
                return false;
            }
        });

        vidFrame3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
// Construct a rect of the view's bounds
                        rect3 = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (!rect3.contains((int) event.getX(), (int) event.getY())) {
                            // User moved outside bounds
                        } else {
                            Log.e("v3", "move");
                        }
                        break;

                }
                return false;
            }
        });
        vidFrame4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
// Construct a rect of the view's bounds
                        rect4 = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (!rect4.contains((int) event.getX(), (int) event.getY())) {
                            // User moved outside bounds
                        } else {
                            Log.e("v4", "move");
                        }
                        break;

                }
                return false;
            }
        });
        vidFrame5.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
// Construct a rect of the view's bounds
                        rect5 = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (!rect5.contains((int) event.getX(), (int) event.getY())) {
                            // User moved outside bounds
                        } else {
                            Log.e("v5", "move");
                        }
                        break;

                }
                return false;
            }
        });
        vidFrame6.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
// Construct a rect of the view's bounds
                        rect6 = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (!rect6.contains((int) event.getX(), (int) event.getY())) {
                            // User moved outside bounds
                        } else {
                            Log.e("v6", "move");
                        }
                        break;

                }
                return false;
            }
        });
        vidFrame7.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
// Construct a rect of the view's bounds
                        rect7 = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (!rect7.contains((int) event.getX(), (int) event.getY())) {
                            // User moved outside bounds
                        } else {
                            Log.e("v7", "move");
                        }
                        break;

                }
                return false;
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            PostVideoRecorderPreL.videoBean.clear();
        } catch (Exception e) {
        }

    }

    /**
     * Video filter - background task to apply filter and generate the output video
     */
    public class VidFilterTask extends AsyncTask<String, String, String> {
        String TTAG = "FilterTask";
        ProgressDialog progress = new ProgressDialog(PostVideoFilterActivity.this);

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progress.setMessage(values[0]);
        }

        @Override
        protected String doInBackground(String... args) {
            String msg = "";
            try {
                publishProgress("Applying filter");
                VideoKit.execute(args);
                publishProgress("filter applied");
                publishProgress(msg);
            } catch (Exception e) {
                Log.e(TTAG, e.getMessage());
                msg = e.getMessage();
            }
            return msg;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (picInFile.exists()) {
                picInFile.delete();
            }
            if (vidOutFile.exists()) {
                vidOutFile.delete();
            }

            progress.setCancelable(false);
            progress.setTitle("Processing...");
            progress.show();
            if (vidOutFile.exists()) {
                vidOutFile.delete();
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();
            Log.e("SaVideoFile=>>", vidOutFile.toString());
            if (from.equalsIgnoreCase("Home")) {
                Intent nxt = new Intent(PostVideoFilterActivity.this, AddPostFinalActivity.class);
                nxt.putExtra(AddPostFinalActivity.ARG_TYPE, ApiPostFeed.PostType.video);
                nxt.putExtra(AddPostFinalActivity.ARG_MEDIA_PATH, vidOutFile.toString());
                edsp = sp.edit();
                edsp.putString(AddPostFinalActivity.ARG_MEDIA_PATH, vidOutFile.toString());
                edsp.putString("fromfilter", "videocategory");
                edsp.apply();
                startActivity(nxt);
            } else if (from.equalsIgnoreCase("NewsFeed")) {

            }
            overridePendingTransition(R.anim.open_next, R.anim.close_main);


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        screen = null;
        video = null;
        pipeline = null;
        System.gc();
    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            video.startWhenReady();
        } catch (Exception e) {

        }

    }
}
