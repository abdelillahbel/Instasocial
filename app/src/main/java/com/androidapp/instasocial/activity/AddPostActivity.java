/**
 * Company : Bsetec
 * Product: Instasocial
 * Email : support@bsetec.com
 * Copyright © 2018 BSEtec. All rights reserved.
 **/

package com.androidapp.instasocial.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidapp.instasocial.App;
import com.androidapp.instasocial.R;
import com.androidapp.instasocial.modules.post.PostCameraFragment;
import com.androidapp.instasocial.modules.post.PostLibraryFragment;
import com.androidapp.instasocial.modules.post.PostVideoRecorderPreL;
import com.androidapp.instasocial.utils.MarshMallowPermission;

/**
 * This class is used to display gallery of images, image capture and video recorder
 */

public class AddPostActivity extends AppCompatActivity {

    /**
     * Member variables declarations/initializations
     */
    public static final String TAG = "AddPost";
    Fragment fragment;
    TextView library, photo, video;
    Bundle bundle;
    MarshMallowPermission marshMallowPermission;
    LinearLayout tab_linear;
    String IamFrom = "", ToOpen = "", desc = "", postingFrom = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_post_activity);

        //instance creation to invoke permissions
        marshMallowPermission = new MarshMallowPermission(this);

        library = (TextView) findViewById(R.id.lib_tab);
        photo = (TextView) findViewById(R.id.photo_tab);
        video = (TextView) findViewById(R.id.video_tab);
        tab_linear = (LinearLayout) findViewById(R.id.tab_linear);

        IamFrom = (getIntent() != null && getIntent().hasExtra("IamFrom")) ? getIntent().getStringExtra("IamFrom") : "Home";
        ToOpen = (getIntent() != null && getIntent().hasExtra("ToOpen")) ? getIntent().getStringExtra("ToOpen") : "";
        desc = (getIntent() != null && getIntent().hasExtra("desc")) ? getIntent().getStringExtra("desc") : "";
        postingFrom = (getIntent() != null && getIntent().hasExtra("postingFrom")) ? getIntent().getStringExtra("postingFrom") : "";

        if (IamFrom.equalsIgnoreCase("NewsFeed")) {
            tab_linear.setVisibility(View.GONE);
            if (ToOpen.equalsIgnoreCase("Gallery")) {
                libraryFrag();//function call to view the gallery images
            }
            if (ToOpen.equalsIgnoreCase("Camera")) {
                capturePhoto();//function call to capture an image
            }
            if (ToOpen.equalsIgnoreCase("Video")) {
                captureVideo();//function call to record video
            }
        } else if (IamFrom.equalsIgnoreCase("Home")) {
            tab_linear.setVisibility(View.VISIBLE);
            libraryFrag();
        }


        library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                library.setEnabled(false);
                photo.setEnabled(true);
                video.setEnabled(true);
                libraryFrag();//function call to view the gallery images
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                library.setEnabled(true);
                photo.setEnabled(false);
                video.setEnabled(true);
                capturePhoto();//function call to capture an image
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                library.setEnabled(true);
                photo.setEnabled(true);
                video.setEnabled(false);
                captureVideo();//function call to record video
            }
        });
    }


    /**
     * Method definitions to initiate image gallery, photo capture and video recorder  -----  STARTS HERE  -----
     */

    private void libraryFrag() {
        /**
         * Check whether permission is granted to read the images from gallery
         */

        if (!marshMallowPermission.checkPermissionForExternalStorage())
            marshMallowPermission.requestPermissionForExternalStorage();
        else {
            setUiLibrarySelected();
            fragment = new PostLibraryFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment).commit();
            bundle = new Bundle();
            bundle.putString("from", IamFrom);
            bundle.putString("desc", desc);
            bundle.putString("postingFrom", postingFrom);
            fragment.setArguments(bundle);
        }
    }

    public void capturePhoto() {

        /**
         * Check whether permission is granted to open default camera and store images
         */

        if (!marshMallowPermission.checkPermissionForExternalStorage())
            marshMallowPermission.requestPermissionForExternalStorage();
        else if (!marshMallowPermission.checkPermissionForCamera())
            marshMallowPermission.requestPermissionForCamera();
        else {
            setUiPhotoSelected();
            fragment = new PostCameraFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment).commit();

            bundle = new Bundle();
            bundle.putString("from", IamFrom);
            bundle.putString("desc", desc);
            bundle.putString("postingFrom", postingFrom);
            fragment.setArguments(bundle);
        }
    }

    public void captureVideo() {
        /**
         * Check whether permission is granted to open default camera and record video
         */

        if (!marshMallowPermission.checkPermissionForExternalStorage())
            marshMallowPermission.requestPermissionForExternalStorage();
        else if (!marshMallowPermission.checkPermissionForCamera()) {
            marshMallowPermission.requestPermissionForCamera();
        } else if (!marshMallowPermission.checkPermissionForRecord()) {
            marshMallowPermission.requestPermissionForRecord();
        } else {
            setUiVideoSelected();
            fragment = new PostVideoRecorderPreL();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment).commit();

            bundle = new Bundle();
            bundle.putString("from", IamFrom);
            bundle.putString("desc", desc);
            bundle.putString("postingFrom", postingFrom);


            fragment.setArguments(bundle);
        }
    }


    /**
     * Method definitions to initiate image gallery, photo capture and video recorder  -----  ENDS HERE  -----
     */




    /**
     * Methods used to update the UI to based on user's clicks  -----  STARTS HERE  -----
     */
  public void setUiVideoSelected() {
        library.setTextColor(ContextCompat.getColor(this, R.color.iron));
        photo.setTextColor(ContextCompat.getColor(this, R.color.iron));
        video.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        library.setBackgroundResource(R.color.colorWhite);
        photo.setBackgroundResource(R.color.colorWhite);
        video.setBackgroundResource(R.drawable.button_bottom_border);
    }

    public void setUiPhotoSelected() {
        library.setTextColor(ContextCompat.getColor(this, R.color.iron));
        photo.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        video.setTextColor(ContextCompat.getColor(this, R.color.iron));
        library.setBackgroundResource(R.color.colorWhite);
        photo.setBackgroundResource(R.drawable.button_bottom_border);
        video.setBackgroundResource(R.color.colorWhite);
    }

    public void setUiLibrarySelected() {
        library.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        photo.setTextColor(ContextCompat.getColor(this, R.color.iron));
        video.setTextColor(ContextCompat.getColor(this, R.color.iron));
        library.setBackgroundResource(R.drawable.button_bottom_border);
        photo.setBackgroundResource(R.color.colorWhite);
        video.setBackgroundResource(R.color.colorWhite);
    }

    /**
     * Methods used to update the UI to based on user's clicks  -----  ENDS HERE  -----
     */


    /**
     *
     * @param requestCode 3 --> image capture, 2 --> image gallery, 1 --> video capture
     * @param permissions contains the array of permissions
     * @param grantResults implies whether permission is granted or denied
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 3) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                capturePhoto();
                return;
            } else {
                App.showToast("Permission denied");
            }
        }else if (requestCode==2){
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               libraryFrag();
                return;
            } else {
                libraryFrag();
            }
        }
        else if (requestCode==1){
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureVideo();
                return;
            } else {
                App.showToast("Permission denied");
            }
        }
    }
}
