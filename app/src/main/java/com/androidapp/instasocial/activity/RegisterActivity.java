/**
 * Company : Bsetec
 * Product: Instasocial
 * Email : support@bsetec.com
 * Copyright © 2018 BSEtec. All rights reserved.
 **/

package com.androidapp.instasocial.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidapp.instasocial.App;
import com.androidapp.instasocial.R;
import com.androidapp.instasocial.ui.CompatEditText;
import com.androidapp.instasocial.ui.CompatTextView;
import com.androidapp.instasocial.ui.CustomLoader;
import com.androidapp.instasocial.ui.country.Country;
import com.androidapp.instasocial.ui.country.CountryPicker;
import com.androidapp.instasocial.ui.country.CountryPickerListener;
import com.androidapp.instasocial.utils.BitmapUtils;
import com.androidapp.instasocial.utils.Config;
import com.androidapp.instasocial.utils.MarshMallowPermission;
import com.androidapp.instasocial.utils.Preferences;
import com.fenchtose.nocropper.CropperView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Class used to register account
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {


    /**
     * Member variables declarations/initializations
     */

    MarshMallowPermission permission;
    public static final String TAG = RegisterActivity.class.getSimpleName();
    CountryPicker mCountryPicker;
    String socialValues = "";
    String socFirstName = "", socLastName = "", socUserName = "",
            socImageUrl = "", soc_id = "", socEmailId = "";
    TextInputLayout txtInputLayPass, txtInputLayPassConfirm;
    String[] items = new String[]{"Take from camera",
            "Select from gallery"};
    ArrayAdapter<String> adapter;
    AlertDialog.Builder builder;
    String gender_selected;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    private static final int REQUEST_CODE_READ_PERMISSION = 4;
    String imageUrlUpload;
    private Uri mImageCaptureUri;
    Bitmap photo;
    CircleImageView register_profile_image;
    CropperView mImageView;
    ImageView snap_button, rotate_button;
    boolean profile_image_set;
    TextView register_upload_text;
    EditText dateofbirth;
    Dialog popup;
    RequestQueue queue;
    EditText firstname, lastname, emailid, password, register_confirmpassword_edit, username, description;
    ImageView register_profile_add_image;
    Button crop_button;
    TextView create_account_button;
    LinearLayout image_layout;
    TextView link_login;
    File IMAGE_FILE;
    private boolean isSnappedToCenter = false;
    private Bitmap mBitmap;
    int rotate;
    ScrollView register_master_scroll;
    File mFileTemp;
    SharedPreferences sp;
    CustomLoader customLoader;
    RadioGroup rdoGrpGender;
    CompatTextView txtCountry;
    CompatEditText txtState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        hideKeyboard();
        customLoader = new CustomLoader(this);
        permission = new MarshMallowPermission(this);
        Initialization();
        onClicks();
        try {
            socialValues = getIntent().getStringExtra("socialLoginValue");
            loadSocialData();
        } catch (Exception e) {
            e.printStackTrace();
            socialValues="";
        }
    }

    //Prefill values for new fb user
    public void loadSocialData() {
        try {
            if (socialValues==null)socialValues="";
            String[] split = socialValues.split(",");
            soc_id = split[0];
            socEmailId = split[1];
            socUserName = split[2];
            socFirstName = split[3];
            socLastName = split[4];
            socImageUrl = split[5];
            Log.e("SocialImageUrl", socImageUrl);
            firstname.setText(socFirstName);
            lastname.setText(socLastName);
            username.setText(socUserName);
            emailid.setText(socEmailId);
            register_confirmpassword_edit.setVisibility(View.GONE);
            password.setVisibility(View.GONE);
            txtInputLayPass.setVisibility(View.GONE);
            txtInputLayPassConfirm.setVisibility(View.GONE);
            register_profile_add_image.setVisibility(View.GONE);
            register_upload_text.setVisibility(View.GONE);
            register_profile_image.setVisibility(View.VISIBLE);
            new DownloadImage().execute(socImageUrl);
            Picasso.with(this).load(socImageUrl).into(register_profile_image);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Go back to login page
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void hideKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }


    /**
     * Register click events
     */
    void onClicks() {
        image_layout.setOnClickListener(this);
        create_account_button.setOnClickListener(this);
        layoutLogin.setOnClickListener(this);
    }



    /**
     * Method definitions to select/capture and upload profile picture   -----  STARTS HERE  -----
     */

    //choose from gallery or capture image
    void CaptureImage() {
        builder.setAdapter(adapter,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            startCameraIntent();

                        } else {

                            startGalleryIntent();
                        }
                    }
                });

        final AlertDialog dialog = builder.create();

        dialog.show();

    }

    //Logics to capture image
    @TargetApi(Build.VERSION_CODES.M)
    private void startCameraIntent() {

        if (permission.hasPhotoCamPermissions()) {
            Intent intents = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intents.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            mImageCaptureUri = App.getUriForFile(
                    new File(getFilesDir(),
                            "/SELFIEIMAGES" + String.valueOf(1) + "Selfie_IMG.jpg"));

            intents.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                intents.setClipData(ClipData.newRawUri("", mImageCaptureUri));
                intents.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            try {
                intents.putExtra("return-data", true);
                startActivityForResult(intents, PICK_FROM_CAMERA);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        } else {

            permission.requestPendingPhotoCamPermission();
        }
    }

    //Check for permissions to fetch the images from gallery
    private void startGalleryIntent() {
        if (!hasGalleryPermission()) {
            askForGalleryPermission();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(
                Intent.createChooser(intent, "Select File"),
                PICK_FROM_FILE);
    }

    //Permission is granted or not
    private boolean hasGalleryPermission() {
        return ActivityCompat.checkSelfPermission(RegisterActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    //Request permission
    private void askForGalleryPermission() {
        ActivityCompat.requestPermissions(RegisterActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CODE_READ_PERMISSION);
    }

    //Logics after the permission is granted or denied
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_READ_PERMISSION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startGalleryIntent();
                return;
            } else {
                Toast.makeText(RegisterActivity.this, "Gallery permission not granted", Toast.LENGTH_SHORT).show();
            }
        } else if (permission.hasPhotoCamPermissions()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            mImageCaptureUri = App.getUriForFile(new File(Environment
                    .getExternalStorageDirectory(), "/SELFIEIMAGES" + String.valueOf(1) + "Selfie_IMG"
                    + ".jpg"));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                intent.setClipData(ClipData.newRawUri("", mImageCaptureUri));
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            try {
                intent.putExtra("return-data", true);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
            return;
        } else {
            if (permission.shouldShowPhotoCamPermissionsExplanation()) {
                Toast.makeText(RegisterActivity.this, "Storage and camera permission required", Toast.LENGTH_SHORT).show();
            } else {
                permission.requestPendingPhotoCamPermission();
            }
        }

   }


    /**
     * send the image path and prepare the image for cropping
     * @param absPath
     */
    private void createCropDialog(final String absPath) {
        popup = new Dialog(RegisterActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        popup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popup.setContentView(R.layout.new_crop_layout);
        mImageView = (CropperView) popup.findViewById(R.id.imageview);
        snap_button = (ImageView) popup.findViewById(R.id.snap_button);
        crop_button = (Button) popup.findViewById(R.id.crop_button);
        rotate_button = (ImageView) popup.findViewById(R.id.rotate_button);

        crop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropImage(absPath);
            }
        });

        snap_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snapImage(mImageView);
            }
        });

        rotate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotateImage(mImageView);
            }
        });
        loadNewImage(absPath, mImageView);
        Window window = popup.getWindow();
        window.setGravity(Gravity.CENTER);
        popup.show();

    }

    //crop the selected image
    private void cropImage(String absPath) {

        Bitmap bitmap = mImageView.getCroppedBitmap();
        popup.dismiss();
        saveImage("app", "cropped_img", bitmap);
        register_profile_image.setImageBitmap(bitmap);
        register_profile_image.setVisibility(View.VISIBLE);
        register_profile_add_image.setVisibility(View.GONE);
        register_upload_text.setVisibility(View.GONE);
        if (bitmap != null) {

            try {
                BitmapUtils.writeBitmapToFile(bitmap, new File(Environment.getExternalStorageDirectory() + "/crop_test.jpg"), 90);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //save the cropped image
    private void saveImage(final String folderName, final String fileName, final Bitmap image) {

        try {
            File dir = getDir(folderName, Context.MODE_PRIVATE);
            try {
                if (dir.mkdir()) {
                    System.out.println("Directory created");

                } else {
                    System.out.println("Directory is not created");
                }
                mFileTemp = new File(dir, fileName + ".png");

                Log.e("DIRECTORY -----> ", String.valueOf(dir));
                Log.e("CAPTURED IMAGE -----> ", String.valueOf(mFileTemp));
            } catch (Exception e) {
                e.printStackTrace();
            }

            FileOutputStream outStream = new FileOutputStream(mFileTemp);
            image.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.close();

            String cropped_img = mFileTemp.getAbsolutePath();

            IMAGE_FILE = new File(cropped_img);
            uploadPicture(IMAGE_FILE);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

    private void rotateImage(CropperView mImageView) {
        if (mBitmap == null) {
            Log.e("IMage", "bitmap is not loaded yet");
            return;
        }

        mBitmap = BitmapUtils.rotateBitmap(mBitmap, 90);
        mImageView.setImageBitmap(mBitmap);
    }

    private void snapImage(CropperView mImageView) {
        if (isSnappedToCenter) {
            mImageView.cropToCenter();
        } else {
            mImageView.fitToCenter();
        }

        isSnappedToCenter = !isSnappedToCenter;
    }

    /**
     *
     * @param filePath contains path of the selected image
     * @param mImageView container that holds the image to be cropped
     */
    private void loadNewImage(String filePath, final CropperView mImageView) {

        Log.i("Image", "load image: " + filePath);
        mBitmap = BitmapFactory.decodeFile(filePath);
        Log.i("Image", "bitmap: " + mBitmap.getWidth() + " " + mBitmap.getHeight());

        int maxP = Math.max(mBitmap.getWidth(), mBitmap.getHeight());
        float scale1280 = (float) maxP / 1280;

        if (mImageView.getWidth() != 0) {
            mImageView.setMaxZoom(mImageView.getWidth() * 2 / 1280f);
        } else {

            ViewTreeObserver vto = mImageView.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                    mImageView.setMaxZoom(mImageView.getWidth() * 2 / 1280f);
                    return true;
                }
            });

        }

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                Matrix matrix = new Matrix();
                matrix.postRotate(90.0f);
                Bitmap rotated = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
                mImageView.setImageBitmap(rotated);
                rotate = 90;
                break;
            default:
                mBitmap = Bitmap.createScaledBitmap(mBitmap, (int) (mBitmap.getWidth() / scale1280),
                        (int) (mBitmap.getHeight() / scale1280), true);
                mImageView.setImageBitmap(mBitmap);

        }

        Log.i("RotateImage", "Exif orientation: " + orientation);
        Log.i("RotateImage", "Rotate value: " + rotate);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Log.e("RESuLT OK", "REsult OK");
            return;
        }

        switch (requestCode) {
            case PICK_FROM_CAMERA:
                File imgFile = new File(getFilesDir(), "/SELFIEIMAGES" + String.valueOf(1) + "Selfie_IMG.jpg");
                String absPaths = BitmapUtils.getFilePathFromUri(RegisterActivity.this, Uri.fromFile(imgFile));
                createCropDialog(absPaths);
                break;

            case PICK_FROM_FILE:
                String absPath = BitmapUtils.getFilePathFromUri(RegisterActivity.this, data.getData());
                createCropDialog(absPath);
                break;

            case CROP_FROM_CAMERA:
                Bundle extras = data.getExtras();

                if (extras != null) {
                    photo = extras.getParcelable("data");
                    register_profile_image.setImageBitmap(photo);
                    register_profile_image.setVisibility(View.VISIBLE);
                    register_profile_add_image.setVisibility(View.GONE);
                    register_upload_text.setVisibility(View.GONE);
                    IMAGE_FILE = new File(getPath(mImageCaptureUri));
                    uploadPicture(IMAGE_FILE);
                }
        }

    }


    /**
     * API call to upload image to server
     * @param file image file
     * @return
     */
    public String uploadPicture(File file) {
        customLoader.getCommanLoading();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        try {
            params.put("file", file);
            params.put("type", "avatar");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        client.setTimeout(120000);
        Log.e(TAG, "fileUpload url  " + Config.ApiUrls.FILE_UPLOAD);
        client.post(Config.ApiUrls.FILE_UPLOAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                customLoader.stopLoading();
                String response = new String(responseBody);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Log.e(TAG, "Image response : " + response);
                    if (!jsonResponse.getString("status").equalsIgnoreCase("false")) {
                        imageUrlUpload = jsonResponse.getString("fileurl");
                        profile_image_set = true;
                    } else {
                        // Toast.makeText(RegisterActivity.this, "", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }

        });
        return "";
    }

    /**
     * Method definitions to select/capture and upload profile picture   -----  ENDS HERE  -----
     */

    View layoutLogin;

    /**
     * UI view initializations
     */
    void Initialization() {
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.select_dialog_item, items);
        builder = new AlertDialog.Builder(this);

        description = findViewById(R.id.description);

        rdoGrpGender = findViewById(R.id.radioGroupGender);
        rdoGrpGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                gender_selected = ((RadioButton) rdoGrpGender.findViewById(rdoGrpGender.getCheckedRadioButtonId())).getText().toString().toLowerCase();
            }
        });
        rdoGrpGender.check(R.id.rdoUnspecified);
        register_master_scroll = (ScrollView) findViewById(R.id.register_master_scroll);

        firstname = (EditText) findViewById(R.id.register_first_edit);
        lastname = (EditText) findViewById(R.id.register_lastname_edit);
        emailid = (EditText) findViewById(R.id.register_email_edit);
        password = (EditText) findViewById(R.id.register_password_edit);
        register_confirmpassword_edit = (EditText) findViewById(R.id.register_confirmpassword_edit);
        dateofbirth = findViewById(R.id.register_dataofbirth_edit);
        txtInputLayPass = findViewById(R.id.txtInputLayPass);
        txtInputLayPassConfirm = findViewById(R.id.txtInputLayPassConfirm);
        username = findViewById(R.id.userName);
        create_account_button = findViewById(R.id.register_create_button);
        image_layout = (LinearLayout) findViewById(R.id.top_linear);
        register_profile_image = (CircleImageView) findViewById(R.id.register_profile_image);
        register_upload_text = (TextView) findViewById(R.id.register_upload_text);
        register_profile_add_image = (ImageView) findViewById(R.id.register_profile_add_image);
        txtCountry = findViewById(R.id.txtCountry);
        txtState = findViewById(R.id.txtState);
        link_login = (TextView) findViewById(R.id.link_login);
        layoutLogin = findViewById(R.id.layoutLogin);
        firstname.setFilters(new InputFilter[]{EMOJI_FILTER});
        lastname.setFilters(new InputFilter[]{EMOJI_FILTER});
        emailid.setFilters(new InputFilter[]{EMOJI_FILTER, SPACE_FILTER});
        username.setFilters(new InputFilter[]{EMOJI_FILTER, SPACE_FILTER});
        password.setFilters(new InputFilter[]{EMOJI_FILTER});
        register_confirmpassword_edit.setFilters(new InputFilter[]{EMOJI_FILTER});
        txtCountry.setOnClickListener(this);
        sp = PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this);
        float textSize = register_confirmpassword_edit.getTextSize() / getResources().getDisplayMetrics().density;
        dateofbirth.setTextSize(textSize);
        firstname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (s.length() > 15) {
                        s.delete(s.length() - 1, s.length());
                        firstname.setText("" + s);
                        firstname.setSelection(firstname.getText().toString().length());
                    }
                }
                firstname.setError(null
                );

            }
        });
        lastname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.length() > 0) {
                    if (editable.length() > 15) {
                        editable.delete(editable.length() - 1, editable.length());
                        lastname.setText("" + editable);
                        lastname.setSelection(lastname.getText().toString().length());
                    }
                }
                lastname.setError(null);
            }
        });


        emailid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                emailid.setError(null);
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                password.setError(null);
            }
        });
        register_confirmpassword_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                register_confirmpassword_edit.setError(null);
            }
        });

        dateofbirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.YEAR, -18);
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = String.format("%04d", year) + "-" +
                                String.format("%02d", monthOfYear + 1) + "-" +
                                String.format("%02d", dayOfMonth);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                        Date sel_date = null;

                        try {
                            sel_date = dateFormat.parse(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Age age = calculateAge(sel_date);

                        Log.e("age is", "====>>>" + age);

                        if (age.getYears() >= 18) {
                            dateofbirth.setError(null);
                            dateofbirth.setText(date);
                        } else {
                            Toast.makeText(RegisterActivity.this, "You have to be more than 18 of age to register", Toast.LENGTH_LONG).show();
                            dateofbirth.setText("");
                        }
                    }
                }, yy, mm, dd);
                datePicker.show();

            }
        });
        mCountryPicker = CountryPicker.newInstance(""); //You can limit the displayed countries
        ArrayList<Country> nc = new ArrayList<>();
        for (Country c : Country.getAllCountries()) {
            nc.add(c);
        }
        mCountryPicker.setCountriesList(nc);
        mCountryPicker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode) {
                txtCountry.setText(name);
                txtCountry.setTag(code);
                //  description.setText(dialCode.replace("+", ""));
                hideKeyboard();
                mCountryPicker.dismiss();
            }
        });
        txtCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManagerss = getSupportFragmentManager();
                mCountryPicker.show(fragmentManagerss, "COUNTRY_PICKER");
            }
        });


    }

    /**
     * Age calculation logics   -----  STARTS HERE  -----
     */

    private static Age calculateAge(Date birthDate) {
        int years = 0;
        int months = 0;
        int days = 0;
        //create calendar object for birth day
        Calendar birthDay = Calendar.getInstance();
        birthDay.setTimeInMillis(birthDate.getTime());
        //create calendar object for current day
        long currentTime = System.currentTimeMillis();
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(currentTime);
        //Get difference between years
        years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
        int currMonth = now.get(Calendar.MONTH) + 1;
        int birthMonth = birthDay.get(Calendar.MONTH) + 1;
        //Get difference between months
        months = currMonth - birthMonth;
        //if month difference is in negative then reduce years by one and calculate the number of months.
        if (months < 0) {
            years--;
            months = 12 - birthMonth + currMonth;
            if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
                months--;
        } else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
            years--;
            months = 11;
        }
        //Calculate the days
        if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE))
            days = now.get(Calendar.DATE) - birthDay.get(Calendar.DATE);
        else if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
            int today = now.get(Calendar.DAY_OF_MONTH);
            now.add(Calendar.MONTH, -1);
            days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH) + today;
        } else {
            days = 0;
            if (months == 12) {
                years++;
                months = 0;
            }
        }
        //Create new Age object
        return new Age(days, months, years);
    }

    static class Age {
        private int days;
        private int months;
        private int years;

        private Age() {
            //Prevent default constructor
        }

        public Age(int days, int months, int years) {
            this.days = days;
            this.months = months;
            this.years = years;
        }

        public int getDays() {
            return this.days;
        }

        public int getMonths() {
            return this.months;
        }

        public int getYears() {
            return this.years;
        }

        @Override
        public String toString() {
            return years + " Years, " + months + " Months, " + days + " Days";
        }
    }

    /**
     * Age calculation logics   -----  ENDS HERE  -----
     */

    public static InputFilter SPACE_FILTER = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (Character.isWhitespace(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        }
    };
    public static InputFilter EMOJI_FILTER = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int index = start; index < end; index++) {

                int type = Character.getType(source.charAt(index));

                if (type == Character.SURROGATE) {
                    return "";
                }
            }
            return null;
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_linear:
                CaptureImage();
                break;
            case R.id.register_create_button:

                if (validateRegisterField()) {
                    hideKeyboard();
                    if (socialValues.equals("")) { //redirected from Create a new account
                        if (profile_image_set)
                            RegisterAPI();
                        else
                            RegisterAPI();
                    } else {// redirected from facebook login
                        apiCallFacebookRegistration();
                    }
                }

                break;
            case R.id.layoutLogin:
                Intent in = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(in);
                finish();
                break;
        }
    }

    /**
     * API call to create an account
     */
    void RegisterAPI() {
        if (!App.isOnline()) {
            App.showToast(R.string.noNet);
            return;
        }
        customLoader.getCommanLoading();
        Log.e(TAG, "apiCallRegister:  " + Config.ApiUrls.REGISTER);
        StringRequest apiCallRegister = new StringRequest(Request.Method.POST, Config.ApiUrls.REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customLoader.stopLoading();
                        Log.e(TAG, "apiCallRegister response ===" + response);
                        try {
                            JSONObject responseobj = new JSONObject(response);
                            String stat = responseobj.getString("status");
                            if (stat.equals("true")) {
                                parseRegisterResponse(response);
                            } else {
                                String msg = responseobj.getString("status_message");
                                Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER_HORIZONTAL, 20, 50);
                                toast.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        customLoader.stopLoading();
                        Toast.makeText(RegisterActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params = getRegisterParams();
                return params;
            }
        };
        queue = Volley.newRequestQueue(this);
        queue.add(apiCallRegister).setRetryPolicy(new RetryPolicy() {
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
                        Log.e("VOLLEY", "loginselfie TimeOut");
                    }
                }).start();
            }
        });
    }

    /**
     *
     * @param response response received from web server
     */

    private void parseRegisterResponse(String response) {
        try {
            JSONObject responseObj = new JSONObject(response);
            String msg = responseObj.getString("status_message");
            App.showToast(msg);
            storeRegisterDetails(responseObj);
            navAfterRegistration();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigation to home on successful registration
     */
    private void navAfterRegistration() {
        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     *
     * @param responseObj Parse the json object and store the details locally
     */
    private void storeRegisterDetails(JSONObject responseObj) {
        try {
            Preferences pref = App.preference();
            JSONObject result = responseObj.getJSONObject("details");
            pref.setUserId(result.getString("id"));
            pref.setAccessToken(result.getString("access_token"));
            pref.setUserName(result.getString("username"));
            pref.setProfileImage(result.getString("profile_pic"));
            pref.setFirstName(result.getString("first_name"));
            pref.setEmailAddress(result.has("email") ? result.getString("email") : emailid.getText().toString().toLowerCase().trim());
            pref.setGender(result.getString("gender"));
            pref.setDescription(result.getString("description"));
            pref.setBirthDate(result.getString("dob"));

            pref.setLastName(result.getString("last_name"));
            pref.setCountry(result.getString("country"));
            pref.setCountryName(txtCountry.getText().toString());
            pref.setState(result.getString("state"));
            pref.setIsPrivate(result.getString("is_private"));
            pref.setIsNotify(result.getString("is_notify"));
            pref.setFollowerCount(result.getString("follower_count"));
            pref.setFollowingCount(result.getString("following_count"));
            pref.setPostCount(result.getString("post_count"));
            pref.setUnreadCount(result.getString("unread_count"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, String> getRegisterParams() {
        HashMap<String, String> params = new HashMap<>();
        params.put("first_name", firstname.getText().toString());
        params.put("last_name", lastname.getText().toString());
        params.put("username", username.getText().toString());
        params.put("password", password.getText().toString());
        params.put("email", emailid.getText().toString());
        params.put("profile_pic", profile_image_set ? imageUrlUpload : "");
        params.put("dob", dateofbirth.getText().toString());
        params.put("gender", gender_selected);
        params.put("state", txtState.getText().toString());
        String country=txtCountry.getTag()!=null?(String)txtCountry.getTag():"";
        params.put("country", country);
        params.put("description", description.getText().toString().trim());
        params.put("player_id", App.preference().getPlayerId());
        params.put("device_token", "");
        params.put("device_type", "android");
        Log.e(TAG, "apiCallRegister params " + params.toString());
        return params;
    }


    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = RegisterActivity.this.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    /**
     * All fields validation
     * @return true on successful validation; false on unsuccessfull validation
     */
    private boolean validateRegisterField() {
        boolean validateFields = true;
        if (TextUtils.isEmpty(firstname.getText().toString())) {
            validateFields = false;
            Config.showToast(this, Config.getStringRes(this, R.string.str_msg_reg_empty_first_name));
        } else if (TextUtils.isEmpty(lastname.getText().toString())) {
            validateFields = false;
            Config.showToast(this, Config.getStringRes(this, R.string.str_msg_reg_empty_last_name));

        } else if (TextUtils.isEmpty(emailid.getText().toString())) {
            validateFields = false;
            Config.showToast(this, Config.getStringRes(this, R.string.str_msg_reg_empty_email));

        } else if (!Config.checkEmail(emailid.getText().toString())) {
            validateFields = false;
            Config.showToast(this, Config.getStringRes(this, R.string.str_msg_reg_valid_email));

        } else if (TextUtils.isEmpty(username.getText().toString())) {
            validateFields = false;
            Config.showToast(this, Config.getStringRes(this, R.string.str_msg_reg_empty_username));

        } else if (TextUtils.isEmpty(txtCountry.getText().toString())) {
            validateFields = false;
            Config.showToast(this, Config.getStringRes(this, R.string.str_msg_reg_country_name));

        } else if (TextUtils.isEmpty(txtState.getText().toString())) {
            validateFields = false;
            Config.showToast(this, Config.getStringRes(this, R.string.str_msg_reg_state_name));

        } else if (TextUtils.isEmpty(password.getText().toString()) && TextUtils.isEmpty(socialValues)) {
            validateFields = false;
            Config.showToast(this, Config.getStringRes(this, R.string.str_msg_reg_pass_empty));

        } else if (TextUtils.isEmpty(register_confirmpassword_edit.getText().toString()) && TextUtils.isEmpty(socialValues)) {
            validateFields = false;
            Config.showToast(this, Config.getStringRes(this, R.string.str_msg_reg_pass_confirm_empty));

        }

        else if (!password.getText().toString().equals(register_confirmpassword_edit.getText().toString()) && TextUtils.isEmpty(socialValues)) {
            validateFields = false;
            Config.showToast(this, Config.getStringRes(this, R.string.str_msg_reg_pass_not_equal));

        } else if ((password.getText().toString().length()<=5||register_confirmpassword_edit.getText().toString().length()<=5)   && TextUtils.isEmpty(socialValues)) {
            validateFields = false;
            Config.showToast(this, "Password should have minimum 6 characters");
        }
        return validateFields;
    }

    private HashMap<String, String> getSocialLoginRegisterParam() {
        HashMap<String, String> params = new HashMap<>();
        params.put("auth_id", soc_id);
        params.put("auth_type", "facebook");
        params.put("first_name", firstname.getText().toString());
        params.put("last_name", lastname.getText().toString());
        params.put("username", username.getText().toString());
        params.put("email", emailid.getText().toString());
        params.put("profile_pic", profile_image_set ? imageUrlUpload : "");
        params.put("dob", dateofbirth.getText().toString());
        params.put("gender", gender_selected);
        params.put("state", txtState.getText().toString());
        String country=txtCountry.getTag()!=null?(String)txtCountry.getTag():"";
        params.put("country", country);
        params.put("description", description.getText().toString().trim());
        params.put("player_id", App.preference().getPlayerId());
        params.put("device_token", "");
        params.put("device_type", "android");
        Log.e(TAG, "apiCallSocialRegister params " + params.toString());
        return params;
    }

    /**
     * Async taks to download image
     */
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... URL) {
            String imageURL = URL[0];
            Bitmap bitmap = null;
            try {
                final DefaultHttpClient client = new DefaultHttpClient();
                final HttpGet getRequest = new HttpGet(imageURL);

                try {
                    HttpResponse response = client.execute(getRequest);
                    final int statusCode = response.getStatusLine().getStatusCode();

                    if (statusCode != HttpStatus.SC_OK) {
                        return null;
                    }

                    final HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        InputStream inputStream = null;
                        try {
                            inputStream = entity.getContent();
                            bitmap = BitmapFactory.decodeStream(inputStream);
                            return bitmap;
                        } finally {
                            if (inputStream != null) {
                                inputStream.close();
                            }
                            entity.consumeContent();
                        }
                    }
                } catch (Exception e) {
                    getRequest.abort();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            saveImage(result);
        }
    }

    /** save image as bitmap**/
    private void saveImage(final Bitmap image) {

        File mFileTemp = null;
        register_profile_image.setImageBitmap(image);

        try {
            File dir = getDir(App.Foldername, Context.MODE_PRIVATE);
            try {
                if (dir.mkdir()) {
                    //Directory created

                } else {
                    //Directory is not created
                }
                mFileTemp = new File(dir, App.Filename + ".png");

            } catch (Exception e) {
                e.printStackTrace();
            }

            FileOutputStream outStream = new FileOutputStream(mFileTemp);
            image.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.close();

            String cropped_img = mFileTemp.getAbsolutePath();

            File IMAGE_FILE = new File(cropped_img);
            uploadPicture(IMAGE_FILE);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * api call to register a facebook user if not already registered
     */
    void apiCallFacebookRegistration() {

        if (!App.isOnline()) {
            App.showToast(R.string.noNet);
            return;
        }
        customLoader.getCommanLoading();
        Log.e(TAG, "apiCallRegister:  " + Config.ApiUrls.FACEBOOK_LOGIN);
        StringRequest apiCallRegister = new StringRequest(Request.Method.POST, Config.ApiUrls.FACEBOOK_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customLoader.stopLoading();
                        Log.e(TAG, "apiCallSocialRegister response ===" + response);
                        try {
//                            App.preference().setIssociallogin("1");
                            App.preference().setSocialLogin(true);
                            JSONObject responseobj = new JSONObject(response);
                            String stat = responseobj.getString("status");
                            if (stat.equals("true")) {
                                parseRegisterResponse(response);
                            } else {
                                String msg = responseobj.getString("status_message");
                                Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER_HORIZONTAL, 20, 50);
                                toast.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        customLoader.stopLoading();
                        Toast.makeText(RegisterActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params = getSocialLoginRegisterParam();
                return params;
            }
        };
        queue = Volley.newRequestQueue(this);
        queue.add(apiCallRegister).setRetryPolicy(new RetryPolicy() {
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
                    }
                }).start();
            }
        });
    }


}
