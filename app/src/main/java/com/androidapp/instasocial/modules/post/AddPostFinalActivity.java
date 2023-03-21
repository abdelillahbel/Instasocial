/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.modules.post;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.androidapp.instasocial.App;
import com.androidapp.instasocial.BuildConfig;
import com.androidapp.instasocial.R;
import com.androidapp.instasocial.activity.HomeActivity;
import com.androidapp.instasocial.utils.Config;
import com.androidapp.instasocial.utils.FileUploader;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;


/**
 * This class is used to display the final media selected and customized by the user
 */

public class AddPostFinalActivity extends AppCompatActivity {

    /**
     * Member variables declarations/initializations
     */
    public static final String TAG = "PostActivity";
    public static final String ARG_MEDIA_PATH = "mediaPath";
    public static final String ARG_TYPE = "type";
    Views views;
    ApiPostFeed api;
    ApiPostFeed.PostType type;
    File mediaFile;
    Dialog prog_dialog;
    ProgressBar progressBarUpload;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        type = i.hasExtra(ARG_TYPE) ? (ApiPostFeed.PostType) i.getSerializableExtra(ARG_TYPE) : ApiPostFeed.PostType.text;
        mediaFile = new File(i.hasExtra(ARG_MEDIA_PATH) ?
                i.getStringExtra(ARG_MEDIA_PATH): "");
        setContentView(R.layout.add_post_final_activity);
        views = new Views();
        views.textVisible(false);
        views.mediaVisible(true);
        views.setPreviewImage(mediaFile.getAbsolutePath());

        views.btnPostMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call api here

                if (type== ApiPostFeed.PostType.video){
                    api=ApiPostFeed.videoInstance(mediaFile.toString());
                    api.setMediaFile(mediaFile);
                    apiCallPostFeed(api, "video");
                }else if (type== ApiPostFeed.PostType.photo){
                    api=ApiPostFeed.photoInstance(mediaFile.toString());
                    api.setMediaFile(mediaFile);
                    apiCallPostFeed(api, "photo");
                }
                api.setTitle(views.editDescription.getText().toString());
            }
        });

        views.back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * UI controls initializations
     */
    private class Views {
        final View layoutMedia, layoutText;
        final EditText editTextPost;
        final ImageView back_img, imgPreview;
        final TextView btnPostMedia, btnPostText;
        final EditText editDescription;
        InterstitialAd mInterstitialAd=null;
        public Views() {
            layoutMedia = findViewById(R.id.layoutMedia);
            layoutText = findViewById(R.id.layoutText);
            editTextPost = findViewById(R.id.editTextPost);
            back_img = findViewById(R.id.back_img);
            imgPreview = findViewById(R.id.imgPreview);
            btnPostMedia = findViewById(R.id.btnPostMedia);
            btnPostText = findViewById(R.id.btnPostText);
            editDescription=findViewById(R.id.editDescription);
            if (Config.AD_SUPPORTED) {
                mInterstitialAd = new InterstitialAd(AddPostFinalActivity.this);
                mInterstitialAd.setAdUnitId(BuildConfig.DEBUG ? "ca-app-pub-3940256099942544/1033173712" : App.getStringRes(R.string.admob_interstitial_ad_id));
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        }

        public void mediaVisible(boolean isVisible) {
            layoutMedia.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }

        public void textVisible(boolean isVisible) {
            layoutText.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }

        public void setPreviewImage(String path) {
            if (type == ApiPostFeed.PostType.photo) {
                imgPreview.setImageBitmap(decodeSampledBitmapFromResource(path, 300, 200));
            } else if (type == ApiPostFeed.PostType.video) {
                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(mediaFile.getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND);
                imgPreview.setImageBitmap(thumb);
            }
        }
    }

    /** Image size calculation **/

    public Bitmap decodeSampledBitmapFromResource(String resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(resId, options);
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        System.out.println("Originalk image height" + height);
        System.out.println("Originall iammge width" + width);
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


    /**
     * API call to post a feed to server
     * @param apiPostFeed
     * @param media_type
     */
    private void apiCallPostFeed(final ApiPostFeed apiPostFeed, final String media_type) {
        if (!App.isOnline()) {
            App.showToast(R.string.noNet);
            return;
        }
        if (apiPostFeed == null) {
            Log.e(TAG, "apiPostFeed object null");
            return;
        }
        final StringRequest request = new StringRequest(Request.Method.POST, apiPostFeed.getUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (prog_dialog != null) {
                            prog_dialog.dismiss();
                        }
                        Log.e(TAG, "apiCallPostFeed response ===" + response);
                        try {
                            JSONObject responseobj = new JSONObject(response);
                            String stat = responseobj.getString("status");
                            String msg = responseobj.getString("status_message");
                            if (stat.equalsIgnoreCase("true")) {
                                App.showToast("Posted");
                                Intent home = new Intent(AddPostFinalActivity.this, HomeActivity.class);
                                home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                HomeActivity.reloadFeed = true;
                                startActivity(home);
                                if (views.mInterstitialAd!=null && views.mInterstitialAd.isLoaded()) {
                                    views.mInterstitialAd.show();
                                } else {
                                }

                            } else {
                                App.showToast(msg);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        loader.stopLoading();
                        if (prog_dialog != null) {
                            prog_dialog.dismiss();
                        }

                        Log.e(TAG, "apiCallPostFeed ERROR");
                        App.showToast("Try Again");
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //  return apiPostFeed.getDefaultHeaders();
                HashMap<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return apiPostFeed.asPostParam();
            }
        };

        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 30000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 1;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "apiCallPostFeed TimeOut");
//                        loader.stopLoading();
                    }
                }).start();
            }
        });

        if (apiPostFeed.isMediaPost()) {
            FileUploader.instance(new FileUploader.UploadListener() {
                @Override
                public void onStart() {
                    Log.e(TAG, "file uploading started");
                    getupload(mediaFile.getAbsolutePath(), media_type);
                }

                @Override
                public void onDone(String fileUrl) {
                    Log.e(TAG, "apiCallPostFeed file upload ur l  " + fileUrl);
                    apiPostFeed.setPostMediaUrl(fileUrl);
                    App.instance().addToRequestQueue(request);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String response, Throwable error) {
                    Log.e(TAG, "error in file upload: " + response);
                }

                @Override
                public void onProgress(float percentage) {
                    if (progressBarUpload != null) {
                        progressBarUpload.setProgress((int) percentage);
                    }
                }
            }).upload(apiPostFeed.getMediaFile(),apiPostFeed.getPostType().getValue());

        } else {
            App.instance().addToRequestQueue(request);
            Log.e(TAG, "apiCallPostFeed " + apiPostFeed);
        }

    }


    /**
     * Upload progressbar
     * @param media_path
     * @param media_type
     */
    public void getupload(String media_path, String media_type) {
        prog_dialog = new Dialog(AddPostFinalActivity.this);
        prog_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        prog_dialog.setContentView(R.layout.progressdialog);
        prog_dialog.setCancelable(false);
        prog_dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        prog_dialog.getWindow().setGravity(Gravity.TOP);
        prog_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        prog_dialog.show();

        ImageView prof_img =prog_dialog.findViewById(R.id.prog_prof);
        progressBarUpload = prog_dialog.findViewById(R.id.progressBarUpload);

        if (media_type.equalsIgnoreCase("photo")) {
            prof_img.setImageBitmap(decodeSampledBitmapFromResource(mediaFile.getAbsolutePath(), 50, 50));
        } else if (media_type.equalsIgnoreCase("video")) {
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(media_path, MediaStore.Images.Thumbnails.MINI_KIND);
            prof_img.setImageBitmap(thumb);
        }

        progressBarUpload.setMax(100);

        progressBarUpload.setProgress(0);
    }


}

