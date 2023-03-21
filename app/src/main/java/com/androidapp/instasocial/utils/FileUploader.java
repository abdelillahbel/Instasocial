/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.utils;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;

public class FileUploader {
    UploadListener uploadListener = null;
    int postType=-1;

    private FileUploader() {
    }

    public static FileUploader instance(UploadListener uploadListener) {
        return new FileUploader().setUploadListener(uploadListener);
    }

    public FileUploader setUploadListener(UploadListener uploadListener) {
        this.uploadListener = uploadListener;
        return this;
    }

    public FileUploader upload(File file,int postType) {
        if (file == null || (file != null && !file.exists())) {
            Log.e("FileUploader", "problem in file: " + file);
            return this;
        }


        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        try {
            if(postType==3)
            params.put("files[]", file,"image/jpg");
            else
                params.put("files[]", file,"video/mp4");

            //  params.put("type","post");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        client.setTimeout(120000);
        if (uploadListener != null) uploadListener.onStart();
        client.post(Config.ApiUrls.MULTIPLEFILEUPLOAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                Double written=Double.valueOf(bytesWritten);
                Double total=Double.valueOf(totalSize);
                float percent = (float) (written / total) * 100;
               // Log.e("FileUploader", "bytesWritten: " + bytesWritten + " Total bytes: " + totalSize + " percent: " + percent);

                if (uploadListener != null) uploadListener.onProgress(percent);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                String fileUrl = getFileUrlFromResponse(response);
                if (uploadListener != null) uploadListener.onDone(fileUrl);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (uploadListener != null)
                    uploadListener.onFailure(statusCode, headers, new String(responseBody), error);
            }

        });
        return this;
    }

    public String getFileUrlFromResponse(String response) {
        String fileUrl = "url-not-found";
        Log.e("Response " , response);
        try {
            JSONObject jsonResponse = new JSONObject(response);
            if (!jsonResponse.getString("status").equalsIgnoreCase("false")) {
                fileUrl = jsonResponse.getJSONArray("fileurl").getString(0);
                Log.e("File url " , fileUrl);
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileUrl;
    }

    public static interface UploadListener {
        public void onStart();

        //        public void onProgress(int progress);
        public void onDone(String fileUrl);

        public void onFailure(int statusCode, Header[] headers, String response, Throwable error);

        public void onProgress(float percentage);
    }
}
