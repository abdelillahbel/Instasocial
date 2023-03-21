/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.modules.post;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidapp.instasocial.R;
import com.androidapp.instasocial.ui.VideoPreview;
import com.androidapp.instasocial.utils.MarshMallowPermission;
import com.googlecode.mp4parser.BasicContainer;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.WritableByteChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Video recording activity ====> for versions lesser that Lollipop
 */
public class PostVideoRecorderPreL extends Fragment implements View.OnClickListener {

    /**
     * Member variables declarations/initializations
     */
    final String FileLocation = (Environment.getExternalStorageDirectory() + "/instasocial/");
    int TIME_MAX = 15000;
    private ImageView capture_video, stop_stop, btn_camera_reverse, closeAcitvity;
    private TextView timer_text, delete_videoPart, use_video, paused;
    private ProgressBar progressBars;
    private RelativeLayout videoLayout, relVideoRec, otherControlLauout;
    private static Camera mCamera;
    private VideoPreview mPreview;
    private CountDownTimer myCountDown;
    private static long countDown;
    private boolean cameraFront = false, longpress, reversed, videoInPart = false;
    private MediaRecorder mediaRecorder;
    public static ArrayList<VideoBean> videoBean;
    String videoPath;
    private boolean isRecording = false;
    String galleryText = "";
    MarshMallowPermission marshMallowPermission;
    TextView press_and_hold;
    ImageView arrow;
    TextureView textureSurface;
    Bundle data;
    String from="",desc="",postingFrom="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_post_video_fragment, container, false);

        data=getArguments();
        from=data.getString("from","");
        desc=data.getString("desc","");
        postingFrom = data.getString("postingFrom", "");

        /**
         * UI initializations
         */
        capture_video = (ImageView) v.findViewById(R.id.capture_video);
        stop_stop = (ImageView) v.findViewById(R.id.stop_stop);
        btn_camera_reverse = (ImageView) v.findViewById(R.id.btn_camera_reverse);
        closeAcitvity = (ImageView) v.findViewById(R.id.camera_cross_arrow);
        videoLayout = (RelativeLayout) v.findViewById(R.id.new_layout);

        progressBars = (ProgressBar) v.findViewById(R.id.progressBars);
        videoLayout = (RelativeLayout) v.findViewById(R.id.new_layout);
        relVideoRec = (RelativeLayout) v.findViewById(R.id.imagePreview);

        timer_text = (TextView) v.findViewById(R.id.timer_text);
        delete_videoPart = (TextView) v.findViewById(R.id.delete_btn);
        use_video = (TextView) v.findViewById(R.id.use_photo_text);
        otherControlLauout = (RelativeLayout) v.findViewById(R.id.special_layout);
        paused = (TextView) v.findViewById(R.id.paused);
        press_and_hold = (TextView) v.findViewById(R.id.press_and_hold);
        arrow = (ImageView) v.findViewById(R.id.popup_arrow);
        textureSurface = (TextureView) v.findViewById(R.id.textureSurface);
        textureSurface.setVisibility(View.GONE);
        press_and_hold.setVisibility(View.VISIBLE);
        arrow.setVisibility(View.VISIBLE);

        File dir1 = new File(Environment.getExternalStorageDirectory() + "/instasocial/");
        try {
            if (dir1.mkdir()) {
                System.out.println("Directory created");
            } else {
                System.out.println("Directory is not created");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        videoBean = new ArrayList<VideoBean>();
        marshMallowPermission = new MarshMallowPermission(getActivity());
        mPreview = new VideoPreview(getActivity(), mCamera);
        relVideoRec.setVisibility(View.VISIBLE);
        relVideoRec.addView(mPreview);

        /**
         * Register click events
         */

        delete_videoPart.setOnClickListener(this);
        use_video.setOnClickListener(this);
        closeAcitvity.setOnClickListener(this);
        btn_camera_reverse.setOnClickListener(this);

        /**
         * Hold to record - logic
         */
        capture_video.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        // paused.setVisibility(View.INVISIBLE);

                        capture();
                        capture_video.setBackgroundResource(R.drawable.rec_press);
                        press_and_hold.setVisibility(View.GONE);
                        arrow.setVisibility(View.GONE);

                        //Toast.makeText(getApplicationContext(),"Pressed",Toast.LENGTH_LONG).show();
                        break;
                    case MotionEvent.ACTION_UP:
                        myCountDown.cancel();
                        stopRecording();
                        capture_video.setBackgroundResource(R.drawable.video_red_btn);

                        // paused.setVisibility(View.VISIBLE);
                        if (progressBars.getProgress() < 15000) {
                            videoInPart = true;
                        }
                        //Toast.makeText(getApplicationContext(),"HOLD",Toast.LENGTH_LONG).show();
                        break;
                }
                return true;
            }
        });

        /**
         * new Counter that counts 3000 ms with a tick each 1000 ms
         */

        myCountDown = new CountDownTimer(TIME_MAX * 10, 1000) {
            public void onTick(long millisUntilFinished) {
                //update the UI with the new count
                if (videoInPart) {
                    progressBars.setProgress(progressBars.getProgress() + 950 - 1000);
                    videoInPart = false;
                }
                if (progressBars.getProgress() <= TIME_MAX) {
                    progressBars.setProgress(progressBars.getProgress() + 950);
                    long progressTime = progressBars.getProgress();
                    String aa = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(progressTime), TimeUnit.MILLISECONDS.toSeconds(progressTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(progressTime)));
                    timer_text.setText(aa);
                    if (progressBars.getProgress() == TIME_MAX) {
                        stopRecording();
                        // Toast.makeText(getApplicationContext(),"Video reached to 60 sec",Toast.LENGTH_LONG).show();
                    }
                }
                if (progressBars.getProgress() > 1000) {
                    videoLayout.setVisibility(View.VISIBLE);
                    use_video.setVisibility(View.VISIBLE);

                }

            }

            public void onFinish() {
                //start the activity
                countDown = 0;
            }
        };

        return v;
    }

    /**
     * Stop video rec - logic
     */
    private void stopRecording() {
        try {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                myCountDown.cancel();
                videoBean.add(new VideoBean(progressBars.getProgress(), videoPath));
                Log.d("info", "is" + progressBars.getProgress() + "dfdsf" + videoPath);
            }
            releaseMediaRecorder();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public void onResume() {
        super.onResume();
        textureSurface.setVisibility(View.GONE);
        relVideoRec.setVisibility(View.VISIBLE);

        /**
         * Camera handler on resume
         */
        releaseCamera();
        if (!hasCamera(getActivity())) {
            Toast toast = Toast.makeText(getActivity(), "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            getActivity().finish();
        }
        if (mCamera == null) {
            // if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {
                Toast.makeText(getActivity(), "No front facing camera found.", Toast.LENGTH_LONG).show();
                if (btn_camera_reverse != null) {
                    btn_camera_reverse.setVisibility(View.GONE);
                }

            }
            try {
                mCamera = Camera.open(findBackFacingCamera());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mPreview.refreshCamera(mCamera);
        }
    }

    /**
     * Detect and open Front camera
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
     * Detect and open Rear camera
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

    /**
     * Whether device has a camera?
     * @param context
     * @return
     */
    private boolean hasCamera(Context context) {
        // check if the device has camera
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera_cross_arrow:
                getActivity().finish();
                break;
            case R.id.btn_camera_reverse:
                Log.d("rev in k", "rev");

                if (isRecording == false) {
                    changeCamera();
                }
                break;
            case R.id.delete_btn:
                deleteVideoPart();
                break;
            case R.id.use_photo_text:
                new MergeVideo().execute();
                break;

        }
    }

    /**
     *Delete part of the video
     */
    private void deleteVideoPart() {
        if (videoBean != null && videoBean.size() != 0) {
            videoBean.remove(videoBean.size() - 1);
            if (videoBean.size() == 0) {
                initialStatus();
            } else {
                int updatedTime = (int) videoBean.get(videoBean.size() - 1).getVideoTime();
                progressBars.setProgress(updatedTime);
                long progressTime = progressBars.getProgress();
                String aa = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(progressTime), TimeUnit.MILLISECONDS.toSeconds(progressTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(progressTime)));
                timer_text.setText(aa);
            }
        }
    }

    /**
     * Reset data
     */
    private void initialStatus() {
        int updatedTime = 0;
        isRecording = false;
        progressBars.setProgress(updatedTime);
        long progressTime = progressBars.getProgress();
        String aa = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(progressTime), TimeUnit.MILLISECONDS.toSeconds(progressTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(progressTime)));
        timer_text.setText(aa);
        videoLayout.setVisibility(View.GONE);
        use_video.setVisibility(View.GONE);

        // otherControlLauout.setVisibility(View.VISIBLE);
    }

    /**
     * Toggle cameras
     */
    private void changeCamera() {

        if (reversed) {
            System.out.println("false");
            reversed = false;
        } else {
            System.out.println("true");
            reversed = true;
        }
        // get the number of cameras
        int camerasNumber = Camera.getNumberOfCameras();
        if (camerasNumber > 1) {
            releaseCamera();
            chooseCamera();
        } else {
            Toast toast = Toast.makeText(getActivity(), "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    /**
     * Choose front or back cam
     */
    public void chooseCamera() {
        // if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                mCamera = Camera.open(cameraId);
                // mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                mCamera = Camera.open(cameraId);
                // mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // when on Pause, release camera in order to be used from other
        // applications
        releaseCamera();
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * Prepare media recorder
     */
    void capture() {
        if (!prepareMediaRecorder()) {
            Toast.makeText(getActivity(), "Failed to create directory MyCameraVideo.", Toast.LENGTH_LONG).show();
            getActivity().finish();
        }
        try {
            isRecording = true;
            mediaRecorder.start();
            myCountDown.start();
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Media recorder settings
     * @return
     */
    private boolean prepareMediaRecorder() {
        System.out.println("inside prepare media recorder ");
        mediaRecorder = new MediaRecorder();
        Date date = new Date();
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(date.getTime());
        try {
            mCamera.unlock();
        } catch (RuntimeException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        mediaRecorder.setCamera(mCamera);

        videoPath = Environment.getExternalStorageDirectory() + "/instasocial/" + timeStamp + ".mp4";
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
       // mediaRecorder.setVideoSize(320,240);
        int vidWidth=mPreview.getVideoSize()!=null?mPreview.getVideoSize().width:320;
        int vidHeight=mPreview.getVideoSize()!=null?mPreview.getVideoSize().height:240;
        mediaRecorder.setVideoSize(vidWidth,vidHeight);

        mediaRecorder.setVideoFrameRate(30);
        mediaRecorder.setOutputFile(Environment.getExternalStorageDirectory() + "/instasocial/" + timeStamp + ".mp4");
        mediaRecorder.setVideoEncodingBitRate(16000000);
        mediaRecorder.setMaxDuration(15000); // Set max duration 60 sec.

        if (!reversed) {
            mediaRecorder.setOrientationHint(90);
        } else {
            mediaRecorder.setOrientationHint(270);
        }

        try {
            mediaRecorder.prepare();
        } catch (Exception e) {
        }
        return true;

    }


    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            mCamera.lock(); // lock camera for later use
        }
    }


    /**
     * Class to merge all 'part' videos
     */

    class MergeVideo extends AsyncTask<String, Integer, String> {
        ProgressDialog dialog;
        String finalVideoPath = "";
        boolean single_video = false;

        @Override
        protected String doInBackground(String... params) {
            int count = videoBean.size();
            if (count > 1) {
                Log.d("count>1", "is" + count);
                try {
                    Movie[] inMovies = new Movie[count];
                    for (int i = 0; i < videoBean.size(); i++) {
                        File file = new File(videoBean.get(i).getVideopath());
                        System.out.println("file path " + i + file);
                        inMovies[i] = MovieCreator.build(file.getAbsolutePath());
                    }

                    List<Track> videoTracks = new LinkedList<Track>();
                    List<Track> audioTracks = new LinkedList<Track>();
                    Log.d("Movies length", "isss  " + inMovies.length);
                    if (inMovies.length != 0) {

                        for (Movie m : inMovies) {

                            for (Track t : m.getTracks()) {
                                if (t.getHandler().equals("soun")) {
                                    audioTracks.add(t);
                                }
                                if (t.getHandler().equals("vide")) {
                                    videoTracks.add(t);
                                }
                                if (t.getHandler().equals("")) {

                                }
                            }

                        }
                    }
                    Movie result = new Movie();

                    System.out.println("audio and videoo tracks : " + audioTracks.size() + " , " + videoTracks.size());
                    if (audioTracks.size() > 0) {
                        result.addTrack(new AppendTrack(audioTracks
                                .toArray(new Track[audioTracks.size()])));
                    }
                    if (videoTracks.size() > 0) {
                        result.addTrack(new AppendTrack(videoTracks
                                .toArray(new Track[videoTracks.size()])));
                    }

                    final String finlaVideoFile = "/result23.mp4";
                    Date date = new Date();
                    String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(date.getTime());

                    finalVideoPath = new File(Environment.getExternalStorageDirectory() + "/instasocial") + "/" + finlaVideoFile;
                    BasicContainer out = (BasicContainer) new DefaultMp4Builder().build(result);
                    WritableByteChannel fc = new RandomAccessFile(String.format(FileLocation + finlaVideoFile), "rw").getChannel();
                    out.writeContainer(fc);
                    fc.close();
                    single_video = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (count == 1) {
                Log.d("count=1", "is" + count);
                single_video = true;
                finalVideoPath = videoBean.get(0).getVideopath();
            }

            return finalVideoPath;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            finalVideoPath = "";
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Processing...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);
            dialog.dismiss();
            initialStatus();
            releaseCamera();
            System.out.println("file path final" + path);
            //Redirect the output video to filter screen
            Intent i = new Intent(getActivity(), PostVideoFilterActivity.class);
            i.putExtra("videopath", path);
            i.putExtra("from", from);
            i.putExtra("desc", desc);
            i.putExtra("postingFrom", postingFrom);

            startActivity(i);
        }
    }
}
