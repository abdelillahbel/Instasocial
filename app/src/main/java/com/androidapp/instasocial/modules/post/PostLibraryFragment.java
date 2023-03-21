/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.modules.post;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.androidapp.instasocial.App;
import com.androidapp.instasocial.R;
import com.androidapp.instasocial.ui.CustomLoader;
import com.androidapp.instasocial.ui.Observablescroll.ObservableGridView;
import com.androidapp.instasocial.ui.Observablescroll.ObservableScrollViewCallbacks;
import com.androidapp.instasocial.ui.Observablescroll.ScrollState;
import com.androidapp.instasocial.ui.Observablescroll.ScrollUtils;
import com.androidapp.instasocial.utils.AspectRatio;
import com.fenchtose.nocropper.CropperImageView;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class is used to display gallery of images (instagram like gallery)
 */

public class PostLibraryFragment extends Fragment implements ObservableScrollViewCallbacks {

    /**
     * Member variables declarations/initializations
     */
    View rootview;
    ObservableGridView sdcardImages;
    ImageAdapter imageAdapter;
    ImageView close_img, btn_drop_down;
    ImageView resize_pickimg;
    TextView gallery_name, next_filter;
    CropperImageView gallery_pick_img;
    ListView listView;
    ArrayList<String> CopyimageList = new ArrayList<String>();
    ArrayList<String> imageList = new ArrayList<String>();
    LinearLayout popup_panel;
    boolean select_flag = false;
    ArrayList<String> folderNames = new ArrayList<String>();
    private Bitmap b;
    private boolean isResize = false;
    String imgpath, cropped_img;
    View stickyViewPlaceholder;
    RelativeLayout gallery_lay;
    View mToolbarView;
    RelativeLayout mHeaderView;
    int mBaseTranslationY;
    VideoView videoPreview;
    int rotate;
    Bitmap mBitmap, rotated;
    int i = 0;
    File mFileTemp;
    VideoRequestHandler videoRequestHandler;
    Picasso picassoInstance;
    String from = "", desc = "", postingFrom = "";
    Bundle data;
    Animation animFade;
    private boolean isSnappedToCenter = false;
    CustomLoader customLoader;


    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.add_post_lib_fragment, null);
        data = getArguments();

        /**
         * UI control initializations
         */
        imageList.clear();
        CopyimageList.clear();
        from = data.getString("from", "");
        desc = data.getString("desc", "");
        postingFrom = data.getString("postingFrom", "");
        animFade = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        videoRequestHandler = new VideoRequestHandler();
        picassoInstance = new Picasso.Builder(getActivity()).addRequestHandler(videoRequestHandler).build();
        close_img = (ImageView) rootview.findViewById(R.id.close_img_lib);
        gallery_name = (TextView) rootview.findViewById(R.id.head_txt);
        next_filter = (TextView) rootview.findViewById(R.id.nxt_txt);
        sdcardImages = (ObservableGridView) rootview.findViewById(R.id.camera_gridView);
        btn_drop_down = (ImageView) rootview.findViewById(R.id.btn_drop_down);
        listView = (ListView) rootview.findViewById(R.id.listView);
        popup_panel = (LinearLayout) rootview.findViewById(R.id.popup_panel);
        gallery_pick_img = (CropperImageView) rootview.findViewById(R.id.gallery_click_img);
        gallery_pick_img.setMakeSquare(false);
        resize_pickimg = (ImageView) rootview.findViewById(R.id.resize_img);
        gallery_lay = (RelativeLayout) rootview.findViewById(R.id.gallery_lay);
//        gallery_lay.getLayoutParams().width= App.getScreenWidth();
//        gallery_lay.getLayoutParams().height=App.getScreenWidth();
        mToolbarView = rootview.findViewById(R.id.toolbar);
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = getScreenHeight() / 2 + 100;
        mHeaderView = (RelativeLayout) rootview.findViewById(R.id.header);
        videoPreview = (VideoView) rootview.findViewById(R.id.videoPreview);
        final int videoLayoutHeight = App.getScreenWidth();//added code
        final int videoLayoutWidth = App.getScreenWidth();//added code
        videoPreview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {//added code
            @Override
            public void onPrepared(MediaPlayer mp) {
                AspectRatio videoRatio=new AspectRatio(mp.getVideoWidth(),mp.getVideoHeight());
                int srcW = mp.getVideoWidth();
                int srcH = mp.getVideoHeight();
                if (srcW>srcH){
                    videoPreview.getLayoutParams().width=videoLayoutWidth;
                    videoPreview.getLayoutParams().height=videoRatio.getHeightBy(videoLayoutWidth);
                }else if (srcW<srcH){
                    videoPreview.getLayoutParams().height=videoLayoutHeight;
                    videoPreview.getLayoutParams().width=videoRatio.getWidthBy(videoLayoutHeight);

                }else {
                    videoPreview.getLayoutParams().width=App.getScreenWidth();
                    videoPreview.getLayoutParams().height=App.getScreenWidth();
                }
                videoPreview.start();
            }
        });
        // listView.setLayoutParams(params);
        sdcardImages.setScrollViewCallbacks(this);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FilterImages(folderNames.get(position));
                gallery_name.setText(folderNames.get(position));
                popup_panel.setVisibility(View.GONE);
            }
        });
        sdcardImages.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    if (popup_panel.getVisibility() == View.VISIBLE) {
                        popup_panel.setVisibility(View.GONE);
                    }
                return false;
            }
        });
        // new ProgressLoader().execute();
        LayoutInflater inflate = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listHeader = inflate.inflate(R.layout.grid_header, null);
        //top_rela.bringToFront();
        stickyViewPlaceholder = listHeader.findViewById(R.id.stickyViewPlaceholder);
        LayoutInflater inflaters = LayoutInflater.from(getActivity());
        sdcardImages.addHeaderView(inflaters.inflate(R.layout.padding, sdcardImages, false)); // toolbar
        sdcardImages.addHeaderView(inflaters.inflate(R.layout.padding1, sdcardImages, false)); // sticky view
        imageAdapter = new ImageAdapter(getActivity(), CopyimageList);
        sdcardImages.setAdapter(imageAdapter);

        customLoader = new CustomLoader(getActivity());

        /**
         * Scroll handling for gallery images
         */
        sdcardImages.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //  Log.v("", "onScrollStateChanged: " + scrollState);

                final Picasso picasso = Picasso.with(getActivity());
                if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    picasso.resumeTag(getActivity());
                } else {
                    picasso.pauseTag(getActivity());
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.v("", "onScroll: firstVisibleItem: " + firstVisibleItem + " visibleItemCount: " + visibleItemCount + " totalItemCount: " + totalItemCount);
                if (firstVisibleItem == 0) {
                    showToolbar();
                }

            }
        });

        /**
         * To display the available folders in mobile's gallery
         */
        gallery_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDropDown();
            }
        });

        btn_drop_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDropDown();
            }
        });


        /**
         * Gallery item onclick
         */
        sdcardImages.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                videoPreview.stopPlayback();
                i = (int) id;
                try {
                    Log.e("i value", String.valueOf(i));
                    imgpath = imageList.get(i);
                    //  gallery_pick_img.setImageURI(Uri.parse(imgpath));
                    Log.e("imagesssspath === ", "" + imgpath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                showToolbar();

                //Selected item is a video
                if (imgpath.contains(".mp4")) {
                    videoPreview.setVisibility(View.VISIBLE);
                    gallery_pick_img.setVisibility(View.GONE);
                    resize_pickimg.setVisibility(View.GONE);
                    videoPreview.setVideoPath(imgpath);
                    videoPreview.start();
                } else { //selected item is an image
                    videoPreview.setVisibility(View.GONE);
                    gallery_pick_img.setVisibility(View.VISIBLE);
                    resize_pickimg.setVisibility(View.VISIBLE);
                    mBitmap = BitmapFactory.decodeFile(imgpath);
                    Log.i("fdfd", "bitmap: " + mBitmap.getWidth() + " " + mBitmap.getHeight());

                    int maxP = Math.max(mBitmap.getWidth(), mBitmap.getHeight());
                    float scale1280 = (float) maxP / 1280;

                    if (gallery_pick_img.getWidth() != 0) {
                        gallery_pick_img.setMaxZoom(gallery_pick_img.getWidth() * 2 / 1280f);
                    } else {

                        ViewTreeObserver vto = gallery_pick_img.getViewTreeObserver();
                        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                            @Override
                            public boolean onPreDraw() {
                                gallery_pick_img.getViewTreeObserver().removeOnPreDrawListener(this);
                                gallery_pick_img.setMaxZoom(gallery_pick_img.getWidth() * 2 / 1280f);
                                return true;
                            }
                        });

                    }

                    mBitmap = Bitmap.createScaledBitmap(mBitmap, (int) (mBitmap.getWidth() / scale1280),
                            (int) (mBitmap.getHeight() / scale1280), true);
                    gallery_pick_img.setImageBitmap(mBitmap);

                    gallery_pick_img.startAnimation(animFade);


                }
            }
        });

        /**
         * Resixe image
         */
        resize_pickimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    snapImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //Redirect to filter screen
        next_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imgpath == null) {
                    Toast toast = Toast.makeText(getActivity(), "Please Select Image", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL, 20, 50);
                    toast.show();
                } else {
                    if (imgpath.contains(".mp4")) {
                        Intent nxt = new Intent(getActivity(), PostVideoFilterActivity.class);
                        nxt.putExtra("videopath", imgpath);
                        nxt.putExtra("from", from);
                        nxt.putExtra("desc", desc);
                        nxt.putExtra("postingFrom", postingFrom);

                        startActivity(nxt);
                        getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_main);
                        videoPreview.stopPlayback();
                    } else {
                         customLoader.getCommanLoading();

                        Cropping runner = new Cropping();
                        runner.execute();
                    }
                }
            }
        });


        close_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 videoPreview.stopPlayback();
                getActivity().finish();
            }
        });
        ProgressLoader runner = new ProgressLoader();
        runner.execute();
        return rootview;
    }

    /**
     * Crop the selected image
     * @return
     */
    private boolean cropImage() {

        try {
            Bitmap bitmap = gallery_pick_img.getCroppedBitmap();

            saveImage("InstaSocial", "cropped_img", bitmap);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Save the cropped image
     * @param folderName
     * @param fileName
     * @param image
     */

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

            cropped_img = mFileTemp.getAbsolutePath();

            File save_img = new File(Environment.getExternalStorageDirectory(), "Cropeddemoimage.jpg");
            FileOutputStream outStreams = new FileOutputStream(save_img);
            image.compress(Bitmap.CompressFormat.PNG, 100, outStreams);
            outStreams.close();
            Log.e("CROPEDSAATION ---->> ", save_img.getAbsolutePath());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

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

    public static String UppercaseFirstLetters(String str) {
        boolean prevWasWhiteSp = true;
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (Character.isLetter(chars[i])) {
                if (prevWasWhiteSp) {
                    chars[i] = Character.toUpperCase(chars[i]);
                }
                prevWasWhiteSp = false;
            } else {
                prevWasWhiteSp = Character.isWhitespace(chars[i]);
            }
        }
        return new String(chars);
    }

    /**
     * Load data in gallery folders list
     */
    public void handleDropDown() {
        if (popup_panel.getVisibility() == View.GONE) {
            popup_panel.setVisibility(View.VISIBLE);
            HashSet hs = new HashSet();
            hs.addAll(folderNames);
            folderNames.clear();
            folderNames.addAll(hs);
            Collections.sort(folderNames, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return s1.compareToIgnoreCase(s2);
                }
            });

            Log.e("sizee", "is" + hs.size());

            listView.setAdapter(new FolderAdapter(folderNames, getActivity()));
        } else
            popup_panel.setVisibility(View.GONE);
    }

    /**
     * Fetch gallery images from Mediastore
     * @param activity
     */
    public void getFilePaths(Activity activity) {
        String selection;
        //  Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri u = MediaStore.Files.getContentUri("external");
        String[] projection = {

                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE,
        };
// Return only video and image metadata.
        if (from.equalsIgnoreCase("story")) {
            selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
        } else {
            selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
        }

        Cursor c = null;
        SortedSet<String> dirList = new TreeSet<String>();
        ArrayList<String> resultIAV = new ArrayList<String>();

        String[] directories = null;
        ArrayList<File> userDirectories = new ArrayList<>();
        String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";
        if (u != null)

            try {
                c = activity.getContentResolver().query(u, projection, selection, null, orderBy);
            } catch (Exception e) {
                e.printStackTrace();
            }

        if ((c != null) && (c.moveToFirst())) {
            String date;

            do {
                String tempDir = c.getString(0);
                //date = c.getString(dateColumn);
                tempDir = tempDir.substring(0, tempDir.lastIndexOf("/"));
                try {
                    dirList.add(tempDir);
                    userDirectories.add(new File(tempDir));
                } catch (Exception e) {
                }
            } while (c.moveToNext());
            directories = new String[userDirectories.size()];
            dirList.toArray(directories);
        }

        Set setItems = new LinkedHashSet(userDirectories);
        userDirectories.clear();
        userDirectories.addAll(setItems);

        File[] dirs = userDirectories.toArray(new File[0]);
//        Arrays.sort(dirs, LastModifiedFileComparator.LASTMODIFIED_REVERSE);

        for (int i = 0; i < dirs.length; i++) {
            File imageDir = dirs[i];
            File[] imageLists = imageDir.listFiles();
            if (imageLists != null && imageLists.length > 1) {
                Arrays.sort(imageLists, new Comparator<File>() {
                    @Override
                    public int compare(File object1, File object2) {
                        if ((object1.lastModified()) > (object2.lastModified())) {
                            return -1;
                        } else if ((object1).lastModified() < (object2.lastModified())) {
                            return +1;
                        } else {
                            return 0;
                        }
//                        return (int) ((object1.lastModified() > object2.lastModified()) ? object1.lastModified(): object2.lastModified());
                    }
                });
            }
            if (imageList == null) {
                imageList = new ArrayList<String>();
                continue;
            } else if (imageLists != null && imageLists.length > 0) {
//                Arrays.sort(imageLists, Collections.reverseOrder());
                ArrayList<File> sortedFiles = new ArrayList<>();
                File[] imageFiles;
                for (File imagePath : imageLists) {
                    try {
                        if (imagePath.isDirectory())
                            imageLists = imagePath.listFiles();
                        if (imagePath.getName().contains(".mp4") || imagePath.getName().contains(".jpg") || imagePath.getName().contains(".JPG") || imagePath.getName().contains(".jpeg") || imagePath.getName().contains(".JPEG") || imagePath.getName().contains(".png") || imagePath.getName().contains(".PNG") || imagePath.getName().contains(".bmp") || imagePath.getName().contains(".BMP")) {
                            String path = imagePath.getAbsolutePath();
                            resultIAV.add(path);
                            if (imagePath.exists()) {
                                if (imagePath.length() != 0 && !imagePath.getAbsolutePath().equalsIgnoreCase("default")) {
                                    sortedFiles.add(new File(imagePath.getAbsolutePath()));
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                imageFiles = (File[]) sortedFiles.toArray(new File[0]);


                for (File invidual : imageFiles) {
                    imageList.add(invidual.getAbsolutePath());
                    CopyimageList.add(invidual.getAbsolutePath());
                    folderNames.add(UppercaseFirstLetters(invidual.getParentFile().getName()));
                }
                folderNames.add(0, "All Media");
                HashSet hs = new HashSet();
                hs.addAll(folderNames);
                folderNames.clear();
                folderNames.addAll(hs);
                Collections.sort(folderNames, new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        return s1.compareToIgnoreCase(s2);
                    }
                });

                ViewGroup.LayoutParams params = listView.getLayoutParams();
                params.height = getScreenHeight() / 2 + 100;

            }
        }
    }

    /**
     * Helper methods to hide/view toolbar --- STARTS HERE ---
       */

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        if (dragging) {
            int toolbarHeight = mToolbarView.getHeight();
            //    int toolbarHeight = 300;
            if (sdcardImages.getCurrentScrollY() == 0) {
                showToolbar();

            }
            if (firstScroll) {
                System.out.println("FirstScroll");
                float currentHeaderTranslationY = ViewHelper.getTranslationY(mHeaderView);
                if (-toolbarHeight < currentHeaderTranslationY) {
                    mBaseTranslationY = scrollY;
                }
            }
            float headerTranslationY = ScrollUtils.getFloat(-(scrollY - mBaseTranslationY), -toolbarHeight, 0);
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewHelper.setTranslationY(mHeaderView, headerTranslationY);
        }
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        mBaseTranslationY = 0;

        if (scrollState == ScrollState.DOWN) {
            int toolbarHeight = mToolbarView.getHeight();
            if (sdcardImages.getCurrentScrollY() == 0) {
                showToolbar();

            }
            int scrollY = sdcardImages.getCurrentScrollY();
            if (toolbarHeight <= scrollY) {
                System.out.println("*****downif" + scrollY);

                hideToolbar();

            } else {
                System.out.println("*****downelse" + scrollY);
                showToolbar();
            }

        } else if (scrollState == ScrollState.UP) {
            int toolbarHeight = mToolbarView.getHeight();
            int scrollY = sdcardImages.getCurrentScrollY();
            if (toolbarHeight <= scrollY) {
                System.out.println("++++upif" + scrollY);
                hideToolbar();
            } else {
                System.out.println("++++upelse" + scrollY);
                showToolbar();
            }
            if (sdcardImages.getCurrentScrollY() == 0) {
                showToolbar();
            }
        } else {
            if (!toolbarIsShown() && !toolbarIsHidden()) {
                showToolbar();
            }
        }
    }

    private boolean toolbarIsShown() {
        return ViewHelper.getTranslationY(mHeaderView) == 0;
    }

    private boolean toolbarIsHidden() {
        return ViewHelper.getTranslationY(mHeaderView) == -mToolbarView.getHeight();
    }

    private void showToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
        if (headerTranslationY != 0) {
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewPropertyAnimator.animate(mHeaderView).translationY(0).setDuration(200).start();
        }
    }

    private void hideToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
        int toolbarHeight = mToolbarView.getHeight();
        if (headerTranslationY != -toolbarHeight) {
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewPropertyAnimator.animate(mHeaderView).translationY(-toolbarHeight).setDuration(200).start();
        }
    }

    /**
     * Helper methods to hide/view toolbar --- ENDS HERE ---
     */


    /**
     * Adapter class to load data in Sdcardimages grid
     */
    class ImageAdapter extends BaseAdapter {

        private final LayoutInflater mLayoutInflater;
        public Typeface gotham_book;
        private Context context;
        private ArrayList<String> imageList;
        ViewHolder holder = null;
        ViewHolder holder1;

        public ImageAdapter(Context localContext, ArrayList<String> imageList) {
            context = localContext;
            this.imageList = imageList;

            System.out.println("ImageListSize" + imageList.size());
            this.mLayoutInflater = LayoutInflater.from(context);
        }

        class ViewHolder {
            ImageView camera_image, cam_alto, cam1x, video_icon;
        }

        @Override
        public int getCount() {
            return this.imageList.size();
        }

        @Override
        public Object getItem(int position) {
            return imageList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override

        public int getItemViewType(int position) {
            return 0;
        }

        @Override

        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            holder = new ViewHolder();

            convertView = mLayoutInflater.inflate(R.layout.layout_camera_listitem, parent, false);
            holder.camera_image = (ImageView) convertView.findViewById(R.id.cam_item_image);
            holder.video_icon = (ImageView) convertView.findViewById(R.id.video_icon);
            holder.cam_alto = (ImageView) convertView.findViewById(R.id.cam_item_alto);
            holder.cam1x = (ImageView) convertView.findViewById(R.id.cam_1x);

            holder.cam_alto.setVisibility(View.GONE);
            holder.cam1x.setVisibility(View.GONE);
            holder.camera_image.clearColorFilter();
            holder.camera_image.setVisibility(View.VISIBLE);
            holder1 = holder;
            convertView.setTag(holder);

            if (position == 0) {
                showToolbar();
            }
            if (sdcardImages.getCurrentScrollY() == 0) {
                showToolbar();
            }
            if (imageList.get(position).contains(".mp4")) {
                picassoInstance.load(videoRequestHandler.SCHEME_VIEDEO + ":" + imageList.get(position)).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).fit().centerCrop().into(holder.camera_image);
                holder.video_icon.setVisibility(View.VISIBLE);
            } else {
                holder.video_icon.setVisibility(View.INVISIBLE);
                Picasso.with(context).load(new File(imageList.get(position)))
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .fit()
                        .centerCrop()
                        .into(holder.camera_image);
            }
            return convertView;
        }
    }

    /**
     * Filter and display images in grid based on the folder selected
     * For instance, if 'Recent' is selected -- show only the recent images instead of all
     * @param keyWord
     */
    public void FilterImages(String keyWord) {
        imageList = new ArrayList<String>();
        System.out.println("keyWord : " + keyWord);
        if (keyWord.compareToIgnoreCase("All Media") == 0)
            imageList = CopyimageList;
        else {
            for (String fileName : CopyimageList) {
                if (!fileName.equalsIgnoreCase("default")) {
                    if (new File(fileName).getParentFile().getName().compareToIgnoreCase(keyWord) == 0) {
                        imageList.add(fileName);
                        System.out.println("fileName filtered : " + fileName);
                    }
                }
            }
        }
        select_flag = true;

        gallery_pick_img.setImageBitmap(decodeSampledBitmapFromResource(imageList.get(0), 200, 200));
        if (imageList.get(0).contains(".mp4")) {
            videoPreview.setVisibility(View.VISIBLE);
            gallery_pick_img.setVisibility(View.GONE);
            resize_pickimg.setVisibility(View.GONE);
//            your_story_txt.setVisibility(View.GONE);
            videoPreview.setVideoPath(imageList.get(0));
            videoPreview.start();
            imgpath = imageList.get(0);

        } else {

            videoPreview.setVisibility(View.GONE);
            gallery_pick_img.setVisibility(View.VISIBLE);
            resize_pickimg.setVisibility(View.VISIBLE);
//            your_story_txt.setVisibility(View.VISIBLE);
            imgpath = imageList.get(0);

            gallery_pick_img.setImageBitmap(decodeSampledBitmapFromResource(imageList.get(0), 200, 200));
        }

        imageAdapter = new ImageAdapter(getActivity(), imageList);
        sdcardImages.setAdapter(imageAdapter);
    }


    /**
     * Adapter class to load the list of folders
     */
    public class FolderAdapter extends BaseAdapter {

        ArrayList<String> data;
        Context context;
        LayoutInflater layoutInflater;

        public FolderAdapter(ArrayList<String> data, Context context) {
            super();
            this.data = data;
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {

            return data.size();
        }

        @Override
        public Object getItem(int position) {

            return null;
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = layoutInflater.inflate(R.layout.layout_camera_roll_popup, null);
            TextView folder_name = (TextView) convertView.findViewById(R.id.folder_name);
            folder_name.setText(data.get(position));
            return convertView;
        }

    }

    public void onResume() {
        super.onResume();

    }

    public class VideoRequestHandler extends RequestHandler {
        public String SCHEME_VIEDEO = "video";

        @Override
        public boolean canHandleRequest(Request data) {
            String scheme = data.uri.getScheme();
            return (SCHEME_VIEDEO.equals(scheme));
        }

        @Override
        public Result load(Request data, int arg1) throws IOException {
            Bitmap bm = ThumbnailUtils.createVideoThumbnail(data.uri.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
            return new Result(bm, Picasso.LoadedFrom.DISK);
        }
    }

    /**
     * Asynchronous class for loader
     */
    public class ProgressLoader extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            getFilePaths(getActivity());
            return null;
        }

        @Override
        protected void onPostExecute(Void res) {
            try {
                if (imageList.get(0).contains(".mp4")) {
                    videoPreview.setVisibility(View.VISIBLE);
                    gallery_pick_img.setVisibility(View.GONE);
                    resize_pickimg.setVisibility(View.GONE);
//                    your_story_txt.setVisibility(View.VISIBLE);
                    videoPreview.setVideoPath(imageList.get(0));
                    videoPreview.start();
                    imgpath = imageList.get(0);

                } else {

                    videoPreview.setVisibility(View.GONE);
                    gallery_pick_img.setVisibility(View.VISIBLE);
                    resize_pickimg.setVisibility(View.VISIBLE);
//                    your_story_txt.setVisibility(View.VISIBLE);
                    imgpath = imageList.get(0);
                    gallery_pick_img.setImageBitmap(decodeSampledBitmapFromResource(imageList.get(0), 200, 200));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Redirect to Image filter class
     */
    public class Cropping extends AsyncTask<Void, Void, Void> {
        boolean success = false;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            success = cropImage();
            return null;
        }

        @Override
        protected void onPostExecute(Void res) {
            if (success) {
                if (from.equalsIgnoreCase("story")) {
                } else {
                    customLoader.stopLoading();
                    Intent nxt = new Intent(getActivity(), PostImageFilterActivity.class);
                    nxt.putExtra("imageviewpath", cropped_img);
                    nxt.putExtra("from", from);
                    nxt.putExtra("desc", desc);
                    nxt.putExtra("postingFrom", postingFrom);
                    startActivity(nxt);
                }
            } else {
                customLoader.stopLoading();
                Toast.makeText(getContext(), "Please crop image", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void snapImage() {
        if (isSnappedToCenter) {
            gallery_pick_img.cropToCenter();
        } else {
            gallery_pick_img.fitToCenter();
        }

        isSnappedToCenter = !isSnappedToCenter;
    }

}
