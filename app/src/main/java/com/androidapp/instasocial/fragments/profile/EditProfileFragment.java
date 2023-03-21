/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.fragments.profile;

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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.androidapp.instasocial.ui.CompatImageView;
import com.androidapp.instasocial.ui.CompatTextView;
import com.androidapp.instasocial.ui.CustomLoader;
import com.androidapp.instasocial.ui.country.Country;
import com.androidapp.instasocial.ui.country.CountryPicker;
import com.androidapp.instasocial.ui.country.CountryPickerListener;
import com.androidapp.instasocial.utils.BitmapUtils;
import com.androidapp.instasocial.utils.Config;
import com.androidapp.instasocial.utils.MarshMallowPermission;
import com.androidapp.instasocial.utils.Preferences;
import com.androidapp.instasocial.utils.ProfilePicListener;
import com.fenchtose.nocropper.CropperView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
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
 * This class is used to edit user's profile
 */

public class EditProfileFragment extends Fragment implements View.OnClickListener {
    /**
     * Member variables declarations/initializations
     */
    View rootView;
    MarshMallowPermission permission;
    public static final String TAG = EditProfileFragment.class.getSimpleName();
    CountryPicker mCountryPicker;
    CompatImageView user_sett_back;
    ProfilePicListener profileLocalPicListener=null;
    String[] items = new String[]{"Take from camera",
            "Select from gallery"};
    ArrayAdapter<String> adapter;
    AlertDialog.Builder builder;
    String gender_selected;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    private static final int REQUEST_CODE_READ_PERMISSION = 4;
    private static final int MY_REQUEST_CODE = 5;
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
    Calendar myCalendar;
    RequestQueue queue;
    EditText firstname, lastname, emailid, username, description;
    ImageView register_profile_add_image;
    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    Button crop_button;
    TextView edtUpdateButton;
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

    /**
     * Fragment instance creation
     * @param profileLocalPicListener
     * @return
     */
    public static EditProfileFragment newFragment(ProfilePicListener profileLocalPicListener){
        EditProfileFragment frag=new EditProfileFragment();
        frag.profileLocalPicListener=profileLocalPicListener;
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout._edit_profile_page_, null);
        hideKeyboard();
        customLoader = new CustomLoader(getActivity());
        permission = new MarshMallowPermission(getActivity());
        Initialization();
        onClicks();
        loadData();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
//        loadData();
    }

    private void hideKeyboard() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }


    /**
     * register click events
     */
    void onClicks() {
        image_layout.setOnClickListener(this);
        edtUpdateButton.setOnClickListener(this);

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
                    new File(getActivity().getFilesDir(),
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
        return ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    //Request permission
    private void askForGalleryPermission() {
        ActivityCompat.requestPermissions(getActivity(),
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
                Toast.makeText(getActivity(), "Gallery permission not granted", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "Storage and camera permission required", Toast.LENGTH_SHORT).show();
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
        popup = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
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
        //editprofile_profile_add_image.setImageBitmap(bitmap);
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
            File dir = getActivity().getDir(folderName, Context.MODE_PRIVATE);
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
//

    }

    /**
     * Determine whether capture from camera or fetch from gallery or to crop
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != getActivity().RESULT_OK) {
            Log.e("RESuLT OK", "REsult OK");
            return;
        }

        switch (requestCode) {
            case PICK_FROM_CAMERA:
                File imgFile = new File(getActivity().getFilesDir(), "/SELFIEIMAGES" + String.valueOf(1) + "Selfie_IMG.jpg");
                String absPaths = BitmapUtils.getFilePathFromUri(getActivity(), Uri.fromFile(imgFile));
                createCropDialog(absPaths);
                break;

            case PICK_FROM_FILE:
                  String absPath = BitmapUtils.getFilePathFromUri(getActivity(), data.getData());
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
                    Log.e("Image", "URi" + mImageCaptureUri);
                    IMAGE_FILE = new File(getPath(mImageCaptureUri));
                    uploadPicture(IMAGE_FILE);
                }
        }

    }

    /**
     * Method to upload profile pictue
     * @param file
     * @return
     */

    public String uploadPicture(File file) {

        customLoader.getCommanLoading();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        try {
            params.put("file", file);
            params.put("type","avatar");
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
     * UI controls initialization
     */
    void Initialization() {
        user_sett_back=rootView.findViewById(R.id.user_sett_back);
        adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.select_dialog_item, items);
        builder = new AlertDialog.Builder(getActivity());

        description = rootView.findViewById(R.id.description);

        rdoGrpGender = rootView.findViewById(R.id.radioGroupGender);
        rdoGrpGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                gender_selected = ((RadioButton) rdoGrpGender.findViewById(rdoGrpGender.getCheckedRadioButtonId())).getText().toString().toLowerCase();
            }
        });
        rdoGrpGender.check(R.id.rdoUnspecified);
        register_master_scroll = (ScrollView) rootView.findViewById(R.id.register_master_scroll);

        firstname = (EditText) rootView.findViewById(R.id.register_first_edit);
        lastname = (EditText) rootView.findViewById(R.id.register_lastname_edit);
        emailid = (EditText) rootView.findViewById(R.id.register_email_edit);
        dateofbirth = rootView.findViewById(R.id.register_dataofbirth_edit);

        username = rootView.findViewById(R.id.userName);
        edtUpdateButton = rootView.findViewById(R.id.edtUpdateButton);
        image_layout = (LinearLayout) rootView.findViewById(R.id.top_linear);
        register_profile_image = (CircleImageView) rootView.findViewById(R.id.register_profile_image);
        register_upload_text = (TextView) rootView.findViewById(R.id.register_upload_text);
        register_profile_add_image = (ImageView) rootView.findViewById(R.id.register_profile_add_image);
        txtCountry = rootView.findViewById(R.id.txtCountry);
        txtState = rootView.findViewById(R.id.txtState);
        link_login = (TextView) rootView.findViewById(R.id.link_login);
        firstname.setFilters(new InputFilter[]{EMOJI_FILTER});
        lastname.setFilters(new InputFilter[]{EMOJI_FILTER});
        emailid.setFilters(new InputFilter[]{EMOJI_FILTER, SPACE_FILTER});
        username.setFilters(new InputFilter[]{EMOJI_FILTER, SPACE_FILTER});
        txtCountry.setOnClickListener(this);


        emailid.setText(App.preference().getEmailAddress());
        username.setText(App.preference().getUserName());
        emailid.setEnabled(false);
        username.setEnabled(false);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());


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

        dateofbirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.YEAR, -18);
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
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
                            Toast.makeText(getActivity(), "You have to be more than 18 of age to register", Toast.LENGTH_LONG).show();
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
                FragmentManager fragmentManagerss = getActivity().getSupportFragmentManager();
                mCountryPicker.show(fragmentManagerss, "COUNTRY_PICKER");
            }
        });
        user_sett_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


    }

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
            case R.id.edtUpdateButton:
                if (validateRegisterField()) {
                    hideKeyboard();
                    updateProfileEdit();
                }

                break;

        }
    }

    /**
     * Get path from uri
     * @param uri of the image
     * @return
     */
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
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
     * Method definitions to select/capture and upload profile picture   -----  ENDS HERE  -----
     */


    /**
     * Validations on Edit profile fields
     * @returns true on success
     */
    private boolean validateRegisterField() {
        boolean validateFields = true;
        if (firstname.getText().toString().equals("")) {
            validateFields = false;
            Config.showToast(getActivity(), Config.getStringRes(getActivity(), R.string.str_msg_reg_empty_first_name));
        } else if (lastname.getText().toString().equals("")) {
            validateFields = false;
            Config.showToast(getActivity(), Config.getStringRes(getActivity(), R.string.str_msg_reg_empty_last_name));

        }
        else if (txtCountry.getText().toString().equals("")) {
            validateFields = false;
            Config.showToast(getActivity(), Config.getStringRes(getActivity(), R.string.str_msg_reg_country_name));

        } else if (txtState.getText().toString().equals("")) {
            validateFields = false;
            Config.showToast(getActivity(), Config.getStringRes(getActivity(), R.string.str_msg_reg_state_name));

        }

        return validateFields;
    }

    /**
     * Load data on the appropriate fields
     */
    public void loadData() {
        Picasso.with(getActivity()).load(App.preference().getProfileImage()).fit().placeholder(R.drawable.ic_profile_circle_trans).error(R.drawable.ic_profile_circle_trans).into(register_profile_image);
        firstname.setText(App.preference().getFirstName());
        lastname.setText(App.preference().getLastName());
        for (Country c : Country.getAllCountries()) {
            if (c.getCode().equalsIgnoreCase(App.preference().getCountry())){
                txtCountry.setText(c.getName());
                txtCountry.setTag(c.getCode());
                break;
            }
        }
        txtState.setText(App.preference().getState());
        dateofbirth.setText(App.preference().getBirthDate());
        description.setText(App.preference().getDescription());
        if(App.preference().getGender().equals("male")){
            rdoGrpGender.check(R.id.rdoMale);
        }else if(App.preference().getGender().equals("female")){
            rdoGrpGender.check(R.id.rdoFemale);
        }else{
            rdoGrpGender.check(R.id.rdoUnspecified);
        }

    }

    /**
     * API call to update the edited information to server
     */
    public void updateProfileEdit() {
        if (!App.isOnline()) {
            App.showToast(R.string.noNet);
            return;
        }
        customLoader.getCommanLoading();
        Log.e(TAG, "apiCallUpdateProfile:  " + Config.ApiUrls.UPDATE_PROFILE);
        StringRequest apiCallUpdateProfile = new StringRequest(Request.Method.PUT, Config.ApiUrls.UPDATE_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customLoader.stopLoading();
                        try {
                            if (profileLocalPicListener != null &&IMAGE_FILE!=null && IMAGE_FILE.exists())
                                profileLocalPicListener.onProfilePicUpdated(IMAGE_FILE.toString());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        Log.e(TAG, "apiCallUpdateProfile response ===" + response);
                        try {
                            JSONObject responseobj = new JSONObject(response);
                            String stat = responseobj.getString("status");
                            if (stat.equals("true")) {
                                storeRegisterDetails(new JSONObject(response));
                            } else {
                                String msg = responseobj.getString("status_message");
                                Toast toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
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
                        Toast.makeText(getActivity(), "Try Again", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params = getUpdateProfileParam();
                return params;
            }
        };
        queue = Volley.newRequestQueue(getActivity());
        queue.add(apiCallUpdateProfile).setRetryPolicy(new RetryPolicy() {
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
     * Build the post params as Haspmap
     * @return
     */
    private HashMap<String, String> getUpdateProfileParam() {
        HashMap<String, String> param = new HashMap<>();
        param.put("userid", App.preference().getUserId());
        param.put("access_token", App.preference().getAccessToken());
        param.put("first_name", firstname.getText().toString());
        param.put("last_name", lastname.getText().toString());
        param.put("state", txtState.getText().toString());
        String country=txtCountry.getTag()!=null?(String)txtCountry.getTag():"";
        param.put("country", country);
        param.put("dob", dateofbirth.getText().toString());
        param.put("gender", gender_selected);
        param.put("description", description.getText().toString());
        param.put("profile_pic", profile_image_set ? imageUrlUpload : "");

        Log.e("Update profile param " , param.toString());
        return param;
    }

    /**
     * Parse and store response locally
     * @param responseObj
     */
    private void storeRegisterDetails(JSONObject responseObj){
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
            pref.setRole(result.getString("role"));
            Config.showToast(getActivity(),responseObj.getString("status_message"));
            getActivity().getSupportFragmentManager().popBackStack();
            // pref.setUnreadCount(result.getString("unread_count"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
