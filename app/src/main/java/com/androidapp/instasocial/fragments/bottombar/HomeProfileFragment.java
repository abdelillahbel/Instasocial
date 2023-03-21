/**
 * Company : Bsetec
 * Product: Instasocial
 * Email : support@bsetec.com
 * Copyright © 2018 BSEtec. All rights reserved.
 **/
package com.androidapp.instasocial.fragments.bottombar;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androidapp.instasocial.App;
import com.androidapp.instasocial.R;
import com.androidapp.instasocial.activity.HomeActivity;
import com.androidapp.instasocial.fragments.profile.ApiProfileInfo;
import com.androidapp.instasocial.fragments.profile.Profile;
import com.androidapp.instasocial.fragments.profile.ProfilePhotosFragment;
import com.androidapp.instasocial.fragments.profile.ProfilePostGridFragment;
import com.androidapp.instasocial.fragments.profile.ProfilePostListFragment;
import com.androidapp.instasocial.fragments.profile.ProfileVideosFragment;
import com.androidapp.instasocial.fragments.profile.RefreshListener;
import com.androidapp.instasocial.fragments.profile.UserSettingsFragment;
import com.androidapp.instasocial.fragments.profile.ViewPagerAdapter;
import com.androidapp.instasocial.modules.follow.FollowersFragment;
import com.androidapp.instasocial.modules.follow.FollowingsFragment;
import com.androidapp.instasocial.modules.profile.ProfileActivity;
import com.androidapp.instasocial.ui.CompatImageView;
import com.androidapp.instasocial.ui.CompatTextView;
import com.androidapp.instasocial.ui.CustomLoader;
import com.androidapp.instasocial.ui.SeamLessViewPager.tools.ScrollableFragmentListener;
import com.androidapp.instasocial.ui.SeamLessViewPager.tools.ScrollableListener;
import com.androidapp.instasocial.ui.SeamLessViewPager.tools.ViewPagerHeaderHelper;
import com.androidapp.instasocial.ui.SeamLessViewPager.widget.PullToRefreshLayout;
import com.androidapp.instasocial.ui.SeamLessViewPager.widget.SlidingTabLayout;
import com.androidapp.instasocial.ui.SeamLessViewPager.widget.TouchCallbackLayout;
import com.androidapp.instasocial.utils.AspectRatio;
import com.androidapp.instasocial.utils.BottomTabs;
import com.androidapp.instasocial.utils.Config;
import com.androidapp.instasocial.utils.MarshMallowPermission;
import com.androidapp.instasocial.utils.NetworkReceiver;
import com.androidapp.instasocial.utils.ProfilePicListener;
import com.baoyz.widget.PullRefreshLayout;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * Main profile page
 */
public class HomeProfileFragment extends Fragment implements
        ScrollableFragmentListener, RefreshListener, ProfilePicListener {

    /**
     * Member variables declarations/initializations
     */
    public static final String TAG = "ProfileFragment";
    private static final long DEFAULT_DURATION = 300L;
    private static final float DEFAULT_DAMPING = 1.5f;
    private long downtime = -1;
    private int countPushEnd = 0, countPullEnd = 0;
    private int mTouchSlop;
    private int mTabHeight;
    private int mHeaderHeight;
    private boolean isFirstPull = true;
    private boolean isViewDragged = true;
    private SparseArrayCompat<ScrollableListener> mScrollableListenerArrays = new SparseArrayCompat<>();
    private int currentTabPosition;
    MarshMallowPermission permission;
    private String memberId;
    private String memberName;
    private Views views;
    private boolean isOwnProfile = false;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    private static final int REQUEST_CODE_READ_PERMISSION = 4;
    private static final int MY_REQUEST_CODE = 5;
    RelativeLayout layoutActionbar;
    boolean hasActionBar = false;
    boolean showFollowButton = false;
    boolean hasBack = false;
    CompatImageView icNavigation;
    CompatTextView icMore;
    public View icSearch;
    CustomLoader customLoader;
    RelativeLayout followerLay, followingLay;
    public boolean is_Block;


    /**
     * Set profile picture on updation
     */
    ProfilePicListener profileLocalPicListener=new ProfilePicListener() {
        @Override
        public void onProfilePicUpdated(String profilePic) {
            try {
                Picasso.with(getContext())
                        .load(new File(profilePic))
                        .fit()
                        .centerCrop()
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .into(views.imgProfilePic);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    public HomeProfileFragment() {
        // Required empty public constructor
    }

    // getter and setter methods of member id
    public String getMemberId() {
        return memberId != null ? memberId : App.preference().getUserId();
    }

    public String getMemberName() {
        return memberName != null ? memberName : App.preference().getUserName();
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
    // getter and setter methods of member id

    /**
     * instance of Homeprofile fragment class
     * @param memberId id of the user
     * @param memberName name of the user
     * @param hasActionBar boolean to determine the presence of actionbar
     * @param hasBack boolean to determine the presence of back button
     * @return
     */
    public static HomeProfileFragment instance(String memberId, String memberName, boolean hasActionBar, boolean hasBack) {
        HomeProfileFragment fragment = new HomeProfileFragment();
        fragment.setMemberId(memberId);
        fragment.setMemberName(memberName);
        fragment.hasActionBar = hasActionBar;
        fragment.hasBack = hasBack;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isOwnProfile = getMemberId().equalsIgnoreCase(App.preference().getUserId());
        permission = new MarshMallowPermission(getActivity());
        App.preference().setProfilePicListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "userId: " + memberId + " userName: " + memberName);
        customLoader = new CustomLoader(getActivity());
        views = new Views(inflater.inflate(R.layout.profile_main_fragment, container, false));
        apiCallProfileInfo(memberId);
        return views.root;
    }

    @Override
    public void onRefreshed() {
        views.pullRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onProfilePicUpdated(String profilePic) {
        try {
            Picasso.with(getContext())
                    .load(profilePic)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .fit()
                    .centerCrop()
                    .into(views.imgProfilePic);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * class that handles UI controls initialization
     */
    public class Views implements TouchCallbackLayout.TouchEventListener,
            ViewPagerHeaderHelper.OnViewPagerTouchListener {
        public final View root;
        public PullToRefreshLayout pullRefreshLayout;
        public TouchCallbackLayout touchCallbackLayout;
        ViewPagerHeaderHelper mViewPagerHeaderHelper;
        View mHeaderLayoutView, headerTopContentLayout;
        SlidingTabLayout slidingTabLayout;
        private Interpolator mInterpolator = new DecelerateInterpolator();
        private ViewPager mViewPager;
        ViewPagerAdapter adapter;
        TabSelector tabSelector;
        RelativeLayout progress_lay;
        public final TextView txtPrivate;
        public final ImageView imgProfilePic;
        public final ImageView imgEditAvatar;
        public final TextView txtUserName, txtGender, txtAbout, txtPhotoCount, txtVideoCount, txtFriendCount;
        public final FloatingActionButton faAddFriend;
        public final FloatingActionButton faUnFriend,faRequested;
        CompatTextView blockUnblock;
        TextView txtLocation;
        public Views(View root) {
            this.root = root;
            faRequested=root.findViewById(R.id.faRequested);
            faRequested.setVisibility(View.GONE);
            layoutActionbar = root.findViewById(R.id.layoutActionbar);
            pullRefreshLayout = root.findViewById(R.id.pullToRefreshLayout);
            imgEditAvatar = root.findViewById(R.id.imgEditAvatar);
            blockUnblock = root.findViewById(R.id.block_unblock);
            followerLay = root.findViewById(R.id.followerLay);
            progress_lay = root.findViewById(R.id.progress_lay);
            followingLay = root.findViewById(R.id.followingLay);
            txtPrivate=root.findViewById(R.id.txtPrivate);
            txtLocation=root.findViewById(R.id.txtLocation);
            txtPrivate.setVisibility(View.GONE);
            followingLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() instanceof HomeActivity) {
                        ((HomeActivity) getActivity()).addFragment(FollowingsFragment.newInstance(memberId), getActivity().getSupportFragmentManager());
                    } else if (getActivity() instanceof ProfileActivity) {
                        ((ProfileActivity) getActivity()).replaceFragment(FollowingsFragment.newInstance(memberId), true);
                    }
                }
            });
            followerLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (getActivity() instanceof HomeActivity) {
                        ((HomeActivity) getActivity()).addFragment(FollowersFragment.newInstance(memberId), getActivity().getSupportFragmentManager());
                    } else if (getActivity() instanceof ProfileActivity) {
                        ((ProfileActivity) getActivity()).replaceFragment(FollowersFragment.newInstance(memberId), true);
                    }

                }
            });
            pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Fragment fragment = adapter.getFragment(currentTabPosition);
//                    Log.e("Test", "fragment: " + fragment + " currentTabPosition: " + currentTabPosition);
                    if (fragment != null) {
                        apiCallProfileInfo(memberId);
                        if (fragment instanceof ProfilePostListFragment) {
                            //call post fragment refresh function
                            ((ProfilePostListFragment) fragment).reloadFeed();
                        } else if (fragment instanceof ProfilePostGridFragment) {
                            //call post fragment refresh function
                            ((ProfilePostGridFragment) fragment).reloadFeed();
                        } else if (fragment instanceof ProfilePhotosFragment) {
                            //call photo fragment refresh function
                            ((ProfilePhotosFragment) fragment).reloadFeed();
                        } else if (fragment instanceof ProfileVideosFragment) {
                            //call post fragment refresh function
                            ((ProfileVideosFragment) fragment).reloadFeed();
                        }

                    }

                }
            });
            headerTopContentLayout = root.findViewById(R.id.profile_top_content_layout);
            AspectRatio ratio = new AspectRatio(4, 3);
            int width = App.getScreenWidth();
            int height = ratio.getHeightBy(App.getScreenWidth());
            headerTopContentLayout.getLayoutParams().width = width;
            headerTopContentLayout.getLayoutParams().height = height;
            icMore = root.findViewById(R.id.ic_more);
            icMore.setVisibility(View.VISIBLE);
            icSearch = root.findViewById(R.id.icSearch);
            if (getActivity() instanceof ProfileActivity) icSearch.setVisibility(View.GONE);
            layoutActionbar = root.findViewById(R.id.layoutActionbar);
            layoutActionbar.setVisibility(hasActionBar ? View.VISIBLE : View.GONE);
            icNavigation = root.findViewById(R.id.icNavigation);
            icNavigation.setImageResource(R.drawable.ic_back);
            icNavigation.setVisibility(getActivity() instanceof ProfileActivity ? View.VISIBLE : View.GONE);
            icSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    HomeActivity.addFragment(SearchProfileFragment.newInstance(true, true), getActivity().getSupportFragmentManager());
                    try{
                        ((HomeActivity)getActivity()).views.bottomTabs.select(BottomTabs.Tab.friends,false);
                    }catch (Exception e){

                    }
                }
            });

            tabSelector = new TabSelector(root, this, isOwnProfile);
            mTouchSlop = ViewConfiguration.get(getActivity()).getScaledTouchSlop();
            mTabHeight = getResources().getDimensionPixelSize(R.dimen.tabs_height);
//            mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.viewpager_header_height);
            mHeaderHeight = height;

            mViewPagerHeaderHelper = new ViewPagerHeaderHelper(root.getContext(), this);

            imgProfilePic = root.findViewById(R.id.user_profile_image);
            txtUserName = root.findViewById(R.id.txtUserName);
            txtGender = root.findViewById(R.id.txtGender);
            txtAbout = root.findViewById(R.id.txtAbout);

            txtPhotoCount = root.findViewById(R.id.txtPhotoCount);
            txtVideoCount = root.findViewById(R.id.txtVideoCount);
            txtFriendCount = root.findViewById(R.id.txtFriendCount);
            faAddFriend = root.findViewById(R.id.faAddFriend);
            faUnFriend = root.findViewById(R.id.faUnFriend);
            faAddFriend.setVisibility(View.GONE);
            faUnFriend.setVisibility(View.GONE);
            mHeaderLayoutView = root.findViewById(R.id.header);
            blockUnblock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (is_Block == true) {
                        apiCallFollowUnFollow(memberId, "4");
                    } else {
                        apiCallFollowUnFollow(memberId, "2");
                    }

                }
            });

            touchCallbackLayout = (TouchCallbackLayout) root.findViewById(R.id.layout);
            touchCallbackLayout.setTouchEventListener(this);


            slidingTabLayout = (SlidingTabLayout) root.findViewById(R.id.tabs);
            slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(android.R.color.transparent);
                }
            });

            mViewPager = (ViewPager) root.findViewById(R.id.viewpager);
            mViewPager.setOffscreenPageLimit(4);
            CharSequence title[] = new CharSequence[]{"new1", "new1", "new1", "new1", "new1"};
            adapter = new ViewPagerAdapter(getChildFragmentManager(), title, getMemberId(), getMemberName(), isOwnProfile);
            mViewPager.setAdapter(adapter);

            slidingTabLayout.setViewPager(mViewPager);

            ViewCompat.setTranslationY(mViewPager, mHeaderHeight);
            slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    slidingTabLayout.setSelected(true);

                }

                @Override
                public void onPageSelected(int position) {
                    switch (position) {
                        case 0:
                            tabSelector.selectPostGridTab();
                            break;
                        case 1:
                            tabSelector.selectPostListTab();
                            break;
                        case 2:
                            tabSelector.selectPhotosTab();
                            break;
                        case 3:
                            tabSelector.selectVideosTab();
                            break;
                    }
                    currentTabPosition = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            loadProfile(null);//method call to load profile data
            icNavigation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() instanceof HomeActivity) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    } else if (getActivity() instanceof ProfileActivity) {
                        getActivity().finish();
                    }
                }
            });

            icMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (memberName != null && !memberName.equalsIgnoreCase(App.preference().getUserName())) {
                        blockUnblock.setVisibility(View.VISIBLE);
                    } else if (memberId != null && memberId.equalsIgnoreCase(App.preference().getUserId()) && getActivity() instanceof HomeActivity) {
                        HomeActivity.addFragment(UserSettingsFragment.newInstance(profileLocalPicListener), getActivity().getSupportFragmentManager());                    }
                }
            });

            faAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    apiCallFollowUnFollow(memberId, "1");
                }
            });
            faUnFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    apiCallFollowUnFollow(memberId, "3");

                }
            });
            faRequested.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    App.showToast(App.getStringRes(R.string.str_requested));
                }
            });
        }

        /**
         * Used to follow or unfollow a member
         *
         * @param memberID profile id of the member be followed/unfollowed
         * @param type     1-follow,2-block,3-unfollow,4-unblock
         */

        public void apiCallFollowUnFollow(final String memberID, final String type) {
            //customLoader.getCommanLoading();
            progress_lay.setVisibility(View.VISIBLE);
            if (NetworkReceiver.isOnline(getActivity())) {
                Log.e(TAG,"Apicall: "+Config.ApiUrls.FOLLOW_UNFOLLOW_BLOCK);
                StringRequest request = new StringRequest(Request.Method.POST, Config.ApiUrls.FOLLOW_UNFOLLOW_BLOCK,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progress_lay.setVisibility(View.GONE);
                                if (response != null && !response.equals("")) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        boolean status = jsonObject.has("status") ? jsonObject.getString("status").equalsIgnoreCase("true") : false;
                                        if (status) {
                                            String followStatus=jsonObject.getString("user_follow_status");
                                            switch (followStatus) {
                                                case "1":
                                                    faAddFriend.setVisibility(View.GONE);
                                                    faRequested.setVisibility(View.GONE);
                                                    faUnFriend.setVisibility(View.VISIBLE);
                                                    App.showToast(App.getStringRes(R.string.str_txt_follow_success));
                                                    break;
                                                case "0":
                                                    faAddFriend.setVisibility(View.VISIBLE);
                                                    faUnFriend.setVisibility(View.GONE);
                                                    faRequested.setVisibility(View.GONE);
                                                    App.showToast(App.getStringRes(R.string.str_txt_un_follow_success));
                                                    break;
                                                case "2":
                                                    faAddFriend.setVisibility(View.GONE);
                                                    faUnFriend.setVisibility(View.GONE);
                                                    faRequested.setVisibility(View.VISIBLE);
                                                    App.showToast(App.getStringRes(R.string.str_txt_request_success));
                                                    break;
                                            }
                                            if (jsonObject.getString("is_block").equalsIgnoreCase("1")) {
                                                is_Block = true;
                                                blockUnblock.setText("UnBlock");
                                                blockUnblock.setVisibility(View.GONE);
                                            } else {
                                                is_Block = false;
                                                blockUnblock.setText("Block");
                                                blockUnblock.setVisibility(View.GONE);
                                            }
                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Log.e("", "apiCallNonRetReq  api response error; response: " + response);
                                }
//                            loadMoreProgress.setVisibility(View.GONE);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("", "apiCallNonRetReqApiError: " + error.toString());

                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return new HashMap<String, String>();
                    }

                    @Override
                    protected Map getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("user_id", App.preference().getUserId());
                        params.put("access_token", App.preference().getAccessToken());
                        params.put("type", type);
                        params.put("member_id", memberID);
                        Log.e("Params of followers", params.toString());
                        return params;
                    }
                };
                request.setShouldCache(false);
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
                        Log.e("", "apiCallNonRetReqApiRetryError: " + error.toString());
                    }
                });
                App.instance().addToRequestQueue(request, "");
            }
        }


        /**
         * Method to set the profile data
         */

        public void loadProfile(final Profile profile) {
            if (profile == null) {
                txtUserName.setVisibility(View.GONE);
                txtGender.setVisibility(View.GONE);
                txtAbout.setVisibility(View.GONE);
                Picasso.with(getContext())
                        .load(Config.ApiUrls.PROFILE_URL + memberId)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .fit()
                        .centerCrop()
                        .into(imgProfilePic);

                return;
            }

            txtGender.setText(profile.getGender());
            String username = profile.getUserName().substring(0, 1).toUpperCase() + profile.getUserName().substring(1);
            txtUserName.setText(username);
            txtUserName.setVisibility(View.VISIBLE);
            txtAbout.setText(profile.getDescription());
            txtPhotoCount.setText(String.valueOf(profile.getPhotoCount()));
            txtVideoCount.setText(String.valueOf(profile.getVideoCount()));
            txtFriendCount.setText(String.valueOf(profile.getFriendsCount()));

        }

        /**
         * Helper methods for stick to top implementation of recycler   -----  STARTS HERE  -----
         */
        @Override
        public boolean onLayoutInterceptTouchEvent(MotionEvent ev) {
            int visiblePercent = App.getVisiblePercent(mHeaderLayoutView);
            boolean val = mViewPagerHeaderHelper.onLayoutInterceptTouchEvent(ev, mTabHeight + mHeaderHeight);
            boolean isPullable = !isViewDragged && !val && visiblePercent == 100 && !isFirstPull;
//            boolean isPullable = isViewDragged && !val && visiblePercent == 100 && !isFirstPull;
            if (isFirstPull) {
                isFirstPull = false;
            }
            pullRefreshLayout.setPullable(isPullable);
//            Log.e("1234", "onLayoutInterceptTouchEvent() returned: " + val + " visiblePercent: " + visiblePercent + " isViewDragged: " + isViewDragged + " isPullable: " + isPullable);
            return val;
        }

        @Override
        public boolean onLayoutTouchEvent(MotionEvent ev) {
            boolean val = mViewPagerHeaderHelper.onLayoutTouchEvent(ev);
//            Log.e("1234", "onLayoutTouchEvent() returned: " + val);
            return val;
        }

        @Override
        public boolean isViewBeingDragged(MotionEvent event) {
            boolean val = mScrollableListenerArrays.valueAt(mViewPager.getCurrentItem()).isViewBeingDragged(event);
            isViewDragged = val;
//            Log.e("1234", "isViewBeingDragged() returned: " + val);
            return val;
        }

        @Override
        public void onMoveStarted(float eventY) {

        }

        @Override
        public void onMove(float eventY, float yDx) {
            float headerTranslationY = ViewCompat.getTranslationY(mHeaderLayoutView) + yDx;
            if (headerTranslationY >= 0) { // pull end
                headerExpand(0L);

                //Log.d("kaede", "pull end");
                if (countPullEnd >= 1) {
                    if (countPullEnd == 1) {
                        downtime = SystemClock.uptimeMillis();
                        simulateTouchEvent(mViewPager, downtime, SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 250, eventY + mHeaderHeight);
                    }
                    simulateTouchEvent(mViewPager, downtime, SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, 250, eventY + mHeaderHeight);
                }
                countPullEnd++;

            } else if (headerTranslationY <= -mHeaderHeight) { // push end
                headerFold(0L);

                if (countPushEnd >= 1) {
                    if (countPushEnd == 1) {
                        downtime = SystemClock.uptimeMillis();
                        simulateTouchEvent(mViewPager, downtime, SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 250, eventY + mHeaderHeight);
                    }
                    simulateTouchEvent(mViewPager, downtime, SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, 250, eventY + mHeaderHeight);
                }
                countPushEnd++;

            } else {



                ViewCompat.animate(mHeaderLayoutView)
                        .translationY(headerTranslationY)
                        .setDuration(0)
                        .start();
                ViewCompat.animate(mViewPager)
                        .translationY(headerTranslationY + mHeaderHeight)
                        .setDuration(0)
                        .start();
            }
        }

        @Override
        public void onMoveEnded(boolean isFling, float flingVelocityY) {

            //Log.d("kaede", "move end");
            countPushEnd = countPullEnd = 0;

            float headerY = ViewCompat.getTranslationY(mHeaderLayoutView); // 0到负数
            if (headerY == 0 || headerY == -mHeaderHeight) {
                return;
            }

            if (mViewPagerHeaderHelper.getInitialMotionY() - mViewPagerHeaderHelper.getLastMotionY()
                    < -mTouchSlop) {  // pull > mTouchSlop = expand
                headerExpand(headerMoveDuration(true, headerY, isFling, flingVelocityY));
            } else if (mViewPagerHeaderHelper.getInitialMotionY()
                    - mViewPagerHeaderHelper.getLastMotionY()
                    > mTouchSlop) { // push > mTouchSlop = fold
                headerFold(headerMoveDuration(false, headerY, isFling, flingVelocityY));
            } else {
                if (headerY > -mHeaderHeight / 2f) {  // headerY > header/2 = expand
                    headerExpand(headerMoveDuration(true, headerY, isFling, flingVelocityY));
                } else { // headerY < header/2= fold
                    headerFold(headerMoveDuration(false, headerY, isFling, flingVelocityY));
                }
            }
        }

        private void simulateTouchEvent(View dispatcher, long downTime, long eventTime, int action, float x, float y) {
            MotionEvent motionEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), action, x, y, 0);
            try {
                dispatcher.dispatchTouchEvent(motionEvent);
            } catch (Throwable e) {
                Log.e(TAG, "simulateTouchEvent error: " + e.toString());
            } finally {
                motionEvent.recycle();
            }
        }

        private long headerMoveDuration(boolean isExpand, float currentHeaderY, boolean isFling,
                                        float velocityY) {

            long defaultDuration = DEFAULT_DURATION;

            if (isFling) {

                float distance = isExpand ? Math.abs(mHeaderHeight) - Math.abs(currentHeaderY)
                        : Math.abs(currentHeaderY);
                velocityY = Math.abs(velocityY) / 1000;

                defaultDuration = (long) (distance / velocityY * DEFAULT_DAMPING);

                defaultDuration =
                        defaultDuration > DEFAULT_DURATION ? DEFAULT_DURATION : defaultDuration;
            }

            return defaultDuration;
        }

        private void headerFold(long duration) {
            ViewCompat.animate(mHeaderLayoutView)
                    .translationY(-mHeaderHeight)
                    .setDuration(duration)
                    .setInterpolator(mInterpolator)
                    .start();

            ViewCompat.animate(mViewPager).translationY(0).
                    setDuration(duration).setInterpolator(mInterpolator).start();

            mViewPagerHeaderHelper.setHeaderExpand(false);
        }

        private void headerExpand(long duration) {
            ViewCompat.animate(mHeaderLayoutView)
                    .translationY(0)
                    .setDuration(duration)
                    .setInterpolator(mInterpolator)
                    .start();

            ViewCompat.animate(mViewPager)
                    .translationY(mHeaderHeight)
                    .setDuration(duration)
                    .setInterpolator(mInterpolator)
                    .start();
            mViewPagerHeaderHelper.setHeaderExpand(true);
        }


    }

    /**
     * Helper methods for stick to top implementation of recycler   -----  ENDS HERE  -----
     */

    @Override
    public void onFragmentAttached(ScrollableListener listener, int position) {
//        Log.e("test", "onFragmentAttached: " + position);
        mScrollableListenerArrays.put(position, listener);
    }

    @Override
    public void onFragmentDetached(ScrollableListener listener, int position) {
//        Log.e("test", "onFragmentDetached: " + position);
        mScrollableListenerArrays.remove(position);
    }


    /**
     * Tab selection and updating UI based on user's clicks ---- STARTS HERE
     */
    public class TabSelector {
        final LinearLayout layoutMenu;
        final TextView txtPostList;
        final TextView txtPhotos;
        final TextView txtVideos;
        final TextView txtPostGrid;
        Views views;

        public TabSelector(View root, final Views views, boolean isHideFriendsTab) {
            txtPostGrid = root.findViewById(R.id.txtPostGrid);
            txtPostList = root.findViewById(R.id.txtPostList);
            txtPhotos = root.findViewById(R.id.txtPhotos);
            txtVideos = root.findViewById(R.id.txtVideos);
            layoutMenu = root.findViewById(R.id.menu_linear);

            this.views = views;
            selectPostGridTab();
            txtPostGrid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    views.mViewPager.setCurrentItem(0);
                    selectPostGridTab();
                }
            });
            txtPostList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    views.mViewPager.setCurrentItem(1);
                    selectPostListTab();
                }
            });
            txtPhotos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    views.mViewPager.setCurrentItem(2);
                    selectPhotosTab();
                }
            });
            txtVideos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    views.mViewPager.setCurrentItem(3);
                    selectVideosTab();
                }
            });
        }

        public void selectPostListTab() {
            if (txtPostList == null || txtPhotos == null || txtVideos == null || txtPostGrid == null)
                return;

            txtPostList.setBackgroundResource(R.drawable.bg_tab);
            txtPhotos.setBackgroundResource(android.R.color.transparent);
            txtVideos.setBackgroundResource(android.R.color.transparent);
            txtPostGrid.setBackgroundResource(android.R.color.transparent);

            txtPostList.setTextColor(App.getColorRes(R.color.colorPrimary));
            txtPhotos.setTextColor(App.getColorRes(R.color.colorPrimaryLightDark));
            txtVideos.setTextColor(App.getColorRes(R.color.colorPrimaryLightDark));
            txtPostGrid.setTextColor(App.getColorRes(R.color.colorPrimaryLightDark));

            txtPostList.setTypeface(txtPostList.getTypeface(), Typeface.BOLD);
            txtPhotos.setTypeface(txtPostList.getTypeface(), Typeface.NORMAL);
            txtVideos.setTypeface(txtPostList.getTypeface(), Typeface.NORMAL);
            txtPostGrid.setTypeface(txtPostList.getTypeface(), Typeface.NORMAL);

        }

        public void selectPhotosTab() {
            if (txtPostList == null || txtPhotos == null || txtVideos == null || txtPostGrid == null)
                return;

            txtPostList.setBackgroundResource(android.R.color.transparent);
            txtPhotos.setBackgroundResource(R.drawable.bg_tab);
            txtVideos.setBackgroundResource(android.R.color.transparent);
            txtPostGrid.setBackgroundResource(android.R.color.transparent);

            txtPostList.setTextColor(App.getColorRes(R.color.colorPrimaryLightDark));
            txtPhotos.setTextColor(App.getColorRes(R.color.colorPrimary));
            txtVideos.setTextColor(App.getColorRes(R.color.colorPrimaryLightDark));
            txtPostGrid.setTextColor(App.getColorRes(R.color.colorPrimaryLightDark));

            txtPostList.setTypeface(txtPostList.getTypeface(), Typeface.NORMAL);
            txtPhotos.setTypeface(txtPostList.getTypeface(), Typeface.BOLD);
            txtVideos.setTypeface(txtPostList.getTypeface(), Typeface.NORMAL);
            txtPostGrid.setTypeface(txtPostList.getTypeface(), Typeface.NORMAL);

        }

        public void selectVideosTab() {
            if (txtPostList == null || txtPhotos == null || txtVideos == null || txtPostGrid == null)
                return;

            txtPostList.setBackgroundResource(android.R.color.transparent);
            txtPhotos.setBackgroundResource(android.R.color.transparent);
            txtVideos.setBackgroundResource(R.drawable.bg_tab);
            txtPostGrid.setBackgroundResource(android.R.color.transparent);

            txtPostList.setTextColor(App.getColorRes(R.color.colorPrimaryLightDark));
            txtPhotos.setTextColor(App.getColorRes(R.color.colorPrimaryLightDark));
            txtVideos.setTextColor(App.getColorRes(R.color.colorPrimary));
            txtPostGrid.setTextColor(App.getColorRes(R.color.colorPrimaryLightDark));

            txtPostList.setTypeface(txtPostList.getTypeface(), Typeface.NORMAL);
            txtPhotos.setTypeface(txtPostList.getTypeface(), Typeface.NORMAL);
            txtVideos.setTypeface(txtPostList.getTypeface(), Typeface.BOLD);
            txtPostGrid.setTypeface(txtPostList.getTypeface(), Typeface.NORMAL);

        }

        public void selectPostGridTab() {
            if (txtPostList == null || txtPhotos == null || txtVideos == null || txtPostGrid == null)
                return;

            txtPostList.setBackgroundResource(android.R.color.transparent);
            txtPhotos.setBackgroundResource(android.R.color.transparent);
            txtVideos.setBackgroundResource(android.R.color.transparent);
            txtPostGrid.setBackgroundResource(R.drawable.bg_tab);

            txtPostList.setTextColor(App.getColorRes(R.color.colorPrimaryLightDark));
            txtPhotos.setTextColor(App.getColorRes(R.color.colorPrimaryLightDark));
            txtVideos.setTextColor(App.getColorRes(R.color.colorPrimaryLightDark));
            txtPostGrid.setTextColor(App.getColorRes(R.color.colorPrimary));

            txtPostList.setTypeface(txtPostList.getTypeface(), Typeface.NORMAL);
            txtPhotos.setTypeface(txtPostList.getTypeface(), Typeface.NORMAL);
            txtVideos.setTypeface(txtPostList.getTypeface(), Typeface.NORMAL);
            txtPostGrid.setTypeface(txtPostList.getTypeface(), Typeface.BOLD);

        }
    }


    /**
     * Tab selection and updating UI based on user's clicks ---- ENDS HERE
     */


    /**
     * API call to get profile info of the user
     * @param user_id
     */
    public void apiCallProfileInfo(String user_id) {
        final ApiProfileInfo api = new ApiProfileInfo(user_id);
        api.setMemberId(memberId);
        api.setUserName(memberName);
        if (!App.isOnline()) {
            App.showToast(R.string.noNet);
            return;
        }
        Log.e("apiCallProfileInfo", api.toString());
        StringRequest request = new StringRequest(Request.Method.GET, api.getBaseUrl(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("apiCallProfileInfo", "response: " + response);
                    JSONObject jResponse = new JSONObject(response);
                    JSONObject detailsObj = jResponse.getJSONObject("details");
                    updateUIAfterApiProfileInfo(detailsObj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Please try again", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return api.getDefaultHeaders();
            }

            @Override
            protected Map<String, String> getParams() {

                return api.asPostParam();
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
                        Log.e("VOLLEY", "request TimeOut");
                    }
                }).start();
            }
        });
        App.instance().addToRequestQueue(request);
    }




    /**
     * Parse the response that is in JSONobject format and update UI
     * @param detailsObj
     */
    private void updateUIAfterApiProfileInfo(JSONObject detailsObj) {

        try {
            views.txtPhotoCount.setText(detailsObj.getString("post_count"));
            views.txtVideoCount.setText(detailsObj.getString("follower_count"));
            views.txtFriendCount.setText(detailsObj.getString("following_count"));
            views.txtUserName.setVisibility(View.VISIBLE);
            views.txtUserName.setText(detailsObj.getString("username"));
            String about=detailsObj.getString("description");
            String state=detailsObj.getString("state");
            String country=detailsObj.getString("country");
            String location=App.preference().getUserId().equalsIgnoreCase(memberId)?App.preference().getStateCountry():(state+", "+country);
            views.txtLocation.setText(location);
            views.txtLocation.setVisibility(location != null && !location.isEmpty() ? View.VISIBLE : View.GONE);
            if (about!=null && !about.isEmpty()){
                views.txtAbout.setVisibility(View.VISIBLE);
                views.txtAbout.setText(about);
            }else {
                views.txtAbout.setVisibility(View.GONE);
            }
            boolean isPrivate=detailsObj.has("is_private")?detailsObj.getString("is_private").equalsIgnoreCase("1"):false;
            boolean isFollowed=detailsObj.has("is_follow")?detailsObj.getString("is_follow").equalsIgnoreCase("1"):false;
            boolean isRequested=detailsObj.has("is_requested")?detailsObj.getString("is_requested").equalsIgnoreCase("1"):false;

            String followStatus=detailsObj.has("follow_status")?detailsObj.getString("follow_status"):"";
            views.txtPrivate.setVisibility(isPrivate?View.VISIBLE:View.GONE);
            is_Block = detailsObj.getBoolean("is_block");
            App.preference().setProfileImage(detailsObj.getString("profile_pic"));
            if (detailsObj.getString("is_follow_button").equals("true")) {
                showFollowButton = true;
            } else {
                showFollowButton = false;
            }
            if (memberId.equalsIgnoreCase(App.preference().getUserId())) {
                views.faAddFriend.setVisibility(View.GONE);
                views.faUnFriend.setVisibility(View.GONE);
                views.faRequested.setVisibility(View.GONE);
            } else {

                if (followStatus.equalsIgnoreCase("request")){
                    views.faRequested.setVisibility(View.VISIBLE);
                    views.faAddFriend.setVisibility(View.GONE);
                    views.faUnFriend.setVisibility(View.GONE);

                }else if (followStatus.equalsIgnoreCase("follow")){
                    views.faRequested.setVisibility(View.GONE);
                    views.faAddFriend.setVisibility(View.GONE);
                    views.faUnFriend.setVisibility(View.VISIBLE);
                }else {
                    views.faRequested.setVisibility(View.GONE);
                    views.faAddFriend.setVisibility(View.VISIBLE);
                    views.faUnFriend.setVisibility(View.GONE);
                }
                if (detailsObj.getBoolean("is_block")) {
                    views.blockUnblock.setText("UnBlock");
                } else {
                    views.blockUnblock.setText("Block");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * When a feed is deleted, update its corresponding count in UI
     */
    public void onFeedDeleted() {
        try {
            int count = Integer.parseInt(views.txtPhotoCount.getText().toString());
            if (count > 0) count = count - 1;
            views.txtPhotoCount.setText(String.valueOf(count));
        } catch (Exception e) {

        }
    }
}
