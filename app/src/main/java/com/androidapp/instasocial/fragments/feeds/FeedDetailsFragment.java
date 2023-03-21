/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.fragments.feeds;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baoyz.widget.PullRefreshLayout;
import com.androidapp.instasocial.App;
import com.androidapp.instasocial.R;
import com.androidapp.instasocial.activity.HomeActivity;
import com.androidapp.instasocial.fragments.bottombar.HomeFeedFragment;
import com.androidapp.instasocial.fragments.bottombar.HomeProfileFragment;
import com.androidapp.instasocial.modules.feed.FeedBean;
import com.androidapp.instasocial.modules.feed.FeedComment;
import com.androidapp.instasocial.modules.feed.FeedDetailHolder;
import com.androidapp.instasocial.modules.feed.FeedReportBean;
import com.androidapp.instasocial.modules.feed.LikeApiCallback;
import com.androidapp.instasocial.modules.feed.api.ApiCommentAdd;
import com.androidapp.instasocial.modules.feed.api.ApiCommentDelete;
import com.androidapp.instasocial.modules.feed.api.ApiComments;
import com.androidapp.instasocial.modules.feed.api.ApiFeedDelete;
import com.androidapp.instasocial.modules.feed.api.ApiLike;
import com.androidapp.instasocial.ui.CommentOptionsDialog;
import com.androidapp.instasocial.ui.CompatTextView;
import com.androidapp.instasocial.ui.SeamLessViewPager.widget.PullToRefreshLayout;
import com.androidapp.instasocial.ui.TextureVideoView;
import com.androidapp.instasocial.utils.Config;
import com.androidapp.instasocial.utils.NetworkReceiver;
import com.androidapp.instasocial.utils.Preferences;
import com.androidapp.instasocial.webservice.ApiParams;
import com.androidapp.instasocial.webservice.ApiTags;
import com.androidapp.instasocial.webservice.RequestApiCall;
import com.androidapp.instasocial.webservice.ResponseCallBack;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;


/**
 * This class is used to display detailed view of feed
 */

public class FeedDetailsFragment extends Fragment implements EditFeedFragment.EditFeedCallBack, ResponseCallBack {

    /**
     * Member variables declarations/initializations
     */

    private static final String TAG = "FeedDetailsFragment";
    public FeedBean feedBean;
    FeedDetailHolder holder;

    RequestApiCall requestApiCall;
    Context context;

    int commentPageNo = 1;
    boolean is_Swipe = false;
    boolean loadmore = true;
    LinearLayoutManager linearLayoutManager;
    RelativeLayout progress_lay;
    ProgressWheel progress_wheel_bottom;
    ArrayList<FeedComment> feedCommentArrayList;
    ArrayList<FeedReportBean> feedReportBeans;
    PullToRefreshLayout pullRefreshLayout;
    boolean isEdit = false;
    String editingCommentID = "";
    EditText edtCommentTxt;
    int editPosition = -1;
    Activity activity;
    CommentAdapter commentAdapter_adapter;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    LinearLayout reportlayout;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 1;
    RelativeLayout progress_lay_comments;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    TextureVideoView currentVideoView;
    HomeFeedFragment fragment;
    public updateFeedDetailCallBack updateFeedDetailCallBacks;
    public int position;
    Dialog popupMore, popupreport;


    /**
     * Fragment instance creation
     * @param feedBean
     * @param position
     * @param updateFeedDetailCallBack
     * @return
     */
    public static FeedDetailsFragment newInstance(FeedBean feedBean, int position, updateFeedDetailCallBack updateFeedDetailCallBack) {
        FeedDetailsFragment feedDetailsFragment = new FeedDetailsFragment();
        feedDetailsFragment.feedBean = feedBean;
        feedDetailsFragment.position = position;
        feedDetailsFragment.updateFeedDetailCallBacks = updateFeedDetailCallBack;

        return feedDetailsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_feed_details, null);
        holder = new FeedDetailHolder(view);
        feedReportBeans = new ArrayList<>();
        progress_lay = view.findViewById(R.id.progress_lay);
        final FeedBean feed = feedBean;

        requestApiCall = new RequestApiCall(getActivity());
        holder.init();
        if (feed.post_type.equals("video")) {
            currentVideoView = holder.videoView;
            holder.image_feed.post(new Runnable() {
                @Override
                public void run() {
                    holder.resizeVideoView();
                }
            });
        }

        holder.title_text.setVisibility(feed.post_text.isEmpty() ? View.GONE : View.VISIBLE);
        holder.title_text.setVisibility(feed.post_text.isEmpty() ? View.GONE : View.VISIBLE);
        holder.title_text.setText(feed.post_text);

        holder.likecount.setText(feed.formattedLikesCount());
        holder.commentcount.setText(feed.post_comment_count);

        /**
         * Share feed
         */
        holder.imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseVideoPlayback();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                getActivity().startActivity(Intent.createChooser(sendIntent, "Share"));
            }
        });
        holder.txtUserName.setVisibility(!feed.user_name.isEmpty() && !feed.user_name.equalsIgnoreCase("null") ? VISIBLE : View.GONE);
        holder.txtUserName.setText(feed.user_name);
        Picasso.with(getActivity()).load(feed.user_image)
                .placeholder(R.drawable.ic_profile_circle_trans)
                .error(R.drawable.ic_profile_circle_trans)
                .fit()
                .into(holder.imgUserPic);
        holder.txtPostedTime.setText(feed.created_at_ago);

        /**
         * Redirect to profile page
         */
        holder.imgUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.addFragment(HomeProfileFragment.instance(feed.user_id, feed.user_name, false,false), getActivity().getSupportFragmentManager());
            }
        });
        /**
         * Redirect to profile page
         */
        holder.txtUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.addFragment(HomeProfileFragment.instance(feed.user_id, feed.user_name, false,false), getActivity().getSupportFragmentManager());
            }
        });

        /**
         * Back button onclick
         */
        holder.icNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        try {
            if (feed.post_type.equals("video") || feed.post_type.equals("")) {

                holder.setVisibilityVideo(true);
                holder.friendLayout.setVisibility(View.GONE);
                holder.layoutMedia.setVisibility(View.VISIBLE);
                holder.feedLoader.setVisibility(View.VISIBLE);
                holder.initVideoView(feed.mediaDetails.get(0).media_name);

                try {
                    holder.image_feed.setVisibility(View.VISIBLE);
                    holder.icVideoCam.setVisibility(View.GONE);
                          } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (feed.post_type.equals("photo")) {
                holder.friendLayout.setVisibility(View.GONE);
                holder.icVideoCam.setVisibility(View.GONE);
                holder.feedLoader.setVisibility(View.VISIBLE);
                holder.layoutMedia.setVisibility(View.VISIBLE);

                holder.image_feed.setVisibility(View.VISIBLE);
                holder.layoutMedia.setVisibility(View.VISIBLE);
                holder.loadFeedImage(feed.mediaDetails.get(0).media_name, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.setLoaderVisible(false);
                        holder.setVisibilityMediaBase(false);
                    }

                    @Override
                    public void onError() {
                        holder.setLoaderVisible(false);
                        holder.setVisibilityMediaBase(true);
                    }
                });

            }

            /**
             * Update like/unlike icon
             */
            if (feed.is_like.equals("1")) {
                holder.like_icon.setText(getActivity().getResources().getString(R.string.like_icon));
            } else {
                holder.like_icon.setText(getActivity().getResources().getString(R.string.unlike_icon));
            }


            /**
             * Like Unlike logical part
             */
            View.OnClickListener likeClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.enableLikeViews(false);
                    if (feed.is_like.equals("1")) {
                        apiCallLikeFeed(feed, new LikeApiCallback() {
                            @Override
                            public void onResponse(boolean status, String code, String message) {
                                holder.enableLikeViews(true);
                                if (!status) {
                                    holder.like_icon.setText(getActivity().getResources().getString(R.string.like_icon));
                                } else {
                                    int like_count = Integer.parseInt(feed.post_like_count) - 1;
                                    feed.post_like_count = String.valueOf(like_count);
                                    holder.likecount.setText(String.valueOf(like_count));
                                    feed.is_like = "0";
                                    holder.like_icon.setText(getActivity().getResources().getString(R.string.unlike_icon));

                                }

                            }
                        });


                    } else {
                        apiCallLikeFeed(feed, new LikeApiCallback() {
                            @Override
                            public void onResponse(boolean status, String code, String message) {
                                holder.enableLikeViews(true);
                                if (!status) {
                                    holder.like_icon.setText(getActivity().getResources().getString(R.string.unlike_icon));
                                } else {
                                    feed.incrementLikes();
                                    holder.likecount.setText(feed.formattedLikesCount());
                                    feed.is_like = "1";
                                    holder.like_icon.setText(getActivity().getResources().getString(R.string.like_icon));
                                }
                            }
                        });
                    }
                }
            };
            holder.like_icon.setOnClickListener(likeClickListener);

            /**
             * comment click
             */
            View.OnClickListener commentClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OpenDialogForCommentsListings(feed.post_id);
                }
            };
            holder.commentLayout.setOnClickListener(commentClickListener);


            /**
             * More onclick
             */
            holder.layoutMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    popupMore = new Dialog(getActivity(), R.style.DialogSlideAnim);
                    popupMore.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                    popupMore.setContentView(R.layout.layout_feed_more_dialog);
                    popupMore.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    popupMore.setCancelable(true);
                    popupMore.setCanceledOnTouchOutside(true);
                    View txtCancel = popupMore.findViewById(R.id.txtCancel);
                    View txtShare = popupMore.findViewById(R.id.txtShare);
                    View txtDelete = popupMore.findViewById(R.id.txtDelete);
                    View txtUnfollow = popupMore.findViewById(R.id.txtUnfollow);
                    View txtReport = popupMore.findViewById(R.id.txtReport);
                    View txtView = popupMore.findViewById(R.id.divider1);
                    View txtView2 = popupMore.findViewById(R.id.divider2);
                    View divideredit = popupMore.findViewById(R.id.divideredit);
                    View txtEdit = popupMore.findViewById(R.id.txtEdit);

                    if (feed.author_pic.equalsIgnoreCase(App.preference().getUserId())) {
                        txtDelete.setVisibility(View.VISIBLE);
                        txtEdit.setVisibility(View.VISIBLE);
                        txtUnfollow.setVisibility(View.GONE);
                        txtReport.setVisibility(View.GONE);
                        txtView.setVisibility(View.GONE);
                        txtView2.setVisibility(View.GONE);
                        divideredit.setVisibility(View.VISIBLE);
                    } else {
                        txtDelete.setVisibility(View.GONE);
                        txtEdit.setVisibility(View.GONE);
                        txtUnfollow.setVisibility(View.VISIBLE);
                        txtReport.setVisibility(View.VISIBLE);
                        txtView.setVisibility(View.GONE);
                        divideredit.setVisibility(View.GONE);
                        txtView2.setVisibility(View.VISIBLE);
                    }

                    txtShare.setVisibility(View.GONE);

                    popupMore.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            startVideoPlayback();
                        }
                    });
                    pauseVideoPlayback();
                    txtCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            popupMore.dismiss();
                        }
                    });
                    txtEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            HomeActivity.addInnerFragment(getActivity().getSupportFragmentManager(), EditFeedFragment.newInstance(feed, position, FeedDetailsFragment.this));
                            popupMore.dismiss();
                        }
                    });
                    txtShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            //    sendIntent.putExtra(Intent.EXTRA_TEXT, feed.getLink());
                            sendIntent.setType("text/plain");
                            getActivity().startActivity(Intent.createChooser(sendIntent, "Share"));
                            popupMore.dismiss();
                        }
                    });

                    txtUnfollow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            popupMore.dismiss();
                            UnFollowAPICall(feed.user_id);
                        }
                    });

                    //OnCLick for Report Feed
                    txtReport.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            popupMore.dismiss();
                            popupreport = new Dialog(getActivity(), R.style.DialogSlideAnim);
                            popupreport.setContentView(R.layout.layout_feed_report_dialog);
                            popupreport.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            popupreport.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            popupreport.show();
                            popupreport.setCancelable(true);
                            reportlayout = popupreport.findViewById(R.id.reportLayout);
                            final View imgDialogClose = popupreport.findViewById(R.id.imgDialogClose);
                            int feed_id = Integer.parseInt(feed.post_id);
                            getFeedReportType(feed_id);

                            imgDialogClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    popupreport.dismiss();
                                }
                            });
                        }
                    });

                    /**
                     * Delete feed
                     */
                    txtDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final Dialog popupDelete;
                            popupDelete = new Dialog(getActivity(), R.style.DialogSlideAnim);
                            popupDelete.setContentView(R.layout.layout_feed_comment_dialog_delete);
                            popupDelete.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            popupDelete.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            popupDelete.show();
                            // popupDelete.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                            popupDelete.setCancelable(true);
                            TextView deleteHead = (TextView) popupDelete.findViewById(R.id.deleteHead);
                            deleteHead.setText("DELETE");
                            final TextView edit_list_title = popupDelete.findViewById(R.id.edit_list_title);
                            edit_list_title.setText("Are your sure want to delete this feed ?");
                            View imgDialogClose = popupDelete.findViewById(R.id.imgDialogClose);
                            TextView btn_yes = (TextView) popupDelete.findViewById(R.id.btn_yes);
                            TextView btn_no = (TextView) popupDelete.findViewById(R.id.btn_no);


                            btn_no.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    popupDelete.dismiss();
                                }
                            });
                            imgDialogClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    popupDelete.dismiss();
                                }
                            });
                            btn_yes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    popupDelete.dismiss();
                                    popupMore.dismiss();
                                    GoToDeleteFeed(feed.post_id, position);
                                }
                            });


                        }
                    });

                    popupMore.show();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void getFeedReportType(int post_id) {
        requestApiCall.getRequestMethodApiCall(this, Config.ApiUrls.FEED_REPORT_TYPE_LIST + "?" + "userid=" + App.preference().getUserId() + "&access_token=" + App.preference().getAccessToken(), ApiTags.TAG_REPORT_TYPE_LIST, post_id);
    }

    //1-follow,2-block,3-unfollow,4-unblock
    private void UnFollowAPICall(final String user_id) {
        requestApiCall.postRequestMethodApiCall(this, Config.ApiUrls.FOLLOW_UNFOLLOW_BLOCK, getParamsForFollowingAction("3", user_id), ApiTags.TAG_FOLLOW_UNFOLLOW, position);
    }

    public HashMap<String, String> getParamsForFollowingAction(String type, String memberID) {
        HashMap<String, String> params = new HashMap<>();
        params.put(ApiParams.USER_ID, App.preference().getUserId());
        params.put(ApiParams.ACCESS_TOKEN, App.preference().getAccessToken());
        params.put(ApiParams.TYPE, type);
        params.put(ApiParams.MEMBER_ID, memberID);
        return params;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }


    /**
     * API call to like a feed
     */
    public void apiCallLikeFeed(final FeedBean feed, final LikeApiCallback callback) {
        if (NetworkReceiver.isOnline(getActivity())) {
            //customLoader.getCommanLoading();
            progress_lay.setVisibility(View.VISIBLE);
            final ApiLike apiLike = new ApiLike(feed.post_id);
            Log.e(TAG, "apiCallLikeFeed " + apiLike.toString());
            StringRequest request = new StringRequest(Request.Method.POST, Config.ApiUrls.FEED_ACTIONS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progress_lay.setVisibility(View.GONE);
                            boolean status = false;
                            String code = "";
                            String msg = "";
                            Log.d(TAG, "apiCallLikeFeed response" + response);
                            if (response != null && !response.equals("")) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    status = jsonObject.has("status") ? jsonObject.getString("status").equalsIgnoreCase("true") : false;
                                    code = jsonObject.has("status") ? jsonObject.getString("status") : "";
                                    msg = jsonObject.has("message") ? jsonObject.getString("message") : "";
                                    App.showToast(msg);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.e(TAG, "apiCallLikeFeed  api response error; response: " + response);
                            }
                            if (callback != null) callback.onResponse(status, code, msg);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progress_lay.setVisibility(View.GONE);
                    Log.e(TAG, "apiCallLikeFeedApiError: " + error.toString());
                    if (callback != null) callback.onResponse(false, "", "");
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return apiLike.getDefaultHeaders();
                }

                @Override
                protected Map getParams() {
                    //  return apiLike.asPostParam();
                    HashMap<String, String> params = new HashMap<>();
                    params.put("user_id", App.preference().getUserId());
                    params.put("access_token", App.preference().getAccessToken());
                    params.put("post_id", feed.post_id);
                    params.put("action_type", "2");
                    params.put("cmt_desc", "");
                    params.put("comment_id", "");

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
                    Log.e(TAG, "apiCallLikeFeedApiRetryError: " + error.toString());
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) callback.onResponse(false, "", "");
                        }
                    });
                }
            });
            App.instance().addToRequestQueue(request, TAG);
        } else {

        }
    }

    TextView txtCommentNoData;

    /**
     * List comments in a dialog
     */
    public void OpenDialogForCommentsListings(final String wall_id) {


        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        dialog.setContentView(R.layout.layout_feed_comment_list);

        pullRefreshLayout = (PullToRefreshLayout) dialog.findViewById(R.id.swipeComments);
        pullRefreshLayout.setPullable(true);

        progress_wheel_bottom = (ProgressWheel) dialog.findViewById(R.id.progress_wheel_bottom);
        feedCommentArrayList = new ArrayList<>();
        feedCommentArrayList.clear();
        progress_lay_comments = (RelativeLayout) dialog.findViewById(R.id.progress_lay);

        final RecyclerView commentsRecycle = (RecyclerView) dialog.findViewById(R.id.commentsRecycle);
        commentAdapter_adapter = new CommentAdapter(progress_lay_comments, feedCommentArrayList, position);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        commentsRecycle.setLayoutManager(linearLayoutManager);
        commentsRecycle.setAdapter(commentAdapter_adapter);
        commentPageNo = 1;
        txtCommentNoData = (TextView) dialog.findViewById(R.id.noData);
        txtCommentNoData.setVisibility(GONE);
        final TextView cmtAddText = (TextView) dialog.findViewById(R.id.cmtAddText);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                startVideoPlayback();
            }
        });
        pauseVideoPlayback();

        loadmore = true;
        is_Swipe = false;
        edtCommentTxt = (EditText) dialog.findViewById(R.id.edtCommentTxt);

//        edtCommentTxt.setFilters(new InputFilter[]{App.EMOJI_FILTER});


        TextView txtComments = (TextView) dialog.findViewById(R.id.txtComments);

        final View close_icon = dialog.findViewById(R.id.close_icon);
        close_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        /**
         * Add a comment implementation
         */
        cmtAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEdit = false;
//                CommonMethods.getInstance((Activity) getActivity()).hideSoftKeyboardAdapter();
                App.hideKeyboard(cmtAddText);
                edtCommentTxt.clearFocus();
                if (edtCommentTxt.getText().toString().length() > 0) {
                    if (!isEdit) {
                        if (checkFullLetter()) {
                            try {
                                int commentCount = Integer.parseInt(feedBean.post_comment_count);
                                commentCount = commentCount + 1;
                                feedBean.post_comment_count = String.valueOf(commentCount);

                                // holder.likecount.setText(feed.formattedLikesCount());
                                holder.commentcount.setText(feedBean.post_comment_count);
                                makeCommentCountUpdation();
                                // notifyDataSetChanged();
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                            String comment_txt = "";
                            if (edtCommentTxt.getText().toString().contains("youtu.be")) {
                                comment_txt = edtCommentTxt.getText().toString();
                            } else {
                                comment_txt = edtCommentTxt.getText().toString().toLowerCase();

                            }
                            apiCallAddEditComment(progress_lay_comments, txtCommentNoData, commentAdapter_adapter, comment_txt, wall_id, position, "");
                            edtCommentTxt.setText("");
                        } else {
                            App.showToast("There is no texts");
                        }
                    } else {
                        if (checkFullLetter()) {
                            apiCallAddEditComment(progress_lay_comments, txtCommentNoData, commentAdapter_adapter, edtCommentTxt.getText().toString().toLowerCase(), wall_id, position, editingCommentID);
                            editingCommentID = "";
                            edtCommentTxt.setText("");
                        } else {
                            App.showToast("There is no texts");
                        }
                    }

                } else {
                    App.showToast("Please Enter Comment");
                }
            }
        });

        TextView add_comment = (TextView) dialog.findViewById(R.id.add_comment);
        TextView txtCancel = (TextView) dialog.findViewById(R.id.txtCancel);

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEdit) {
                    try {
                        int commentCount = Integer.parseInt(feedBean.post_comment_count);
                        commentCount = commentCount + 1;
                        feedBean.post_comment_count = String.valueOf(commentCount);
                        // holder.likecount.setText(feed.formattedLikesCount());
                        holder.commentcount.setText(feedBean.post_comment_count);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    apiCallAddEditComment(progress_lay_comments, null, commentAdapter_adapter, edtCommentTxt.getText().toString(), wall_id, position, "");
                    edtCommentTxt.setText("");
                } else {
                    apiCallAddEditComment(progress_lay_comments, null, commentAdapter_adapter, edtCommentTxt.getText().toString(), wall_id, position, editingCommentID);
                    editingCommentID = "";
                    edtCommentTxt.setText("");
                }
            }
        });

        /**
         * Refresh feeds
         */

        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                commentPageNo = 1;
                is_Swipe = true;
                loadmore = true;
                previousTotal = 0;
                loading = true;
                visibleThreshold = 1;
                firstVisibleItem = 0;
                visibleItemCount = 0;
                totalItemCount = 0;
                apiCallCommentsList(txtCommentNoData, progress_wheel_bottom, pullRefreshLayout, commentPageNo, feedCommentArrayList, commentAdapter_adapter, wall_id);
            }
        });

        /**
         * Loadmore for comments list
         */
        commentsRecycle.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = commentsRecycle.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached

                    //   Log.i("Yaeye!", "end called");

                    // Do something
                    commentPageNo = commentPageNo + 1;
                    is_Swipe = false;
                    apiCallCommentsList(txtCommentNoData, progress_wheel_bottom, pullRefreshLayout, commentPageNo, feedCommentArrayList, commentAdapter_adapter, wall_id);
                    loading = true;
                }
            }
        });

        dialog.show();
        apiCallCommentsList(txtCommentNoData, progress_wheel_bottom, pullRefreshLayout, commentPageNo, feedCommentArrayList, commentAdapter_adapter, wall_id);


    }


    /**
     * API call to edit a comment
     * @param progress_lay
     * @param noData
     * @param commentAdapter
     * @param cmtText
     * @param wallId
     * @param position
     * @param cmtID
     */



    public void apiCallAddEditComment(final RelativeLayout progress_lay, final TextView noData, final CommentAdapter commentAdapter, final String cmtText,
                                      final String wallId, final int position, final String cmtID) {
        progress_lay.setVisibility(View.VISIBLE);
        if (NetworkReceiver.isOnline(getActivity())) {

            final ApiCommentAdd apiCommentAdd = new ApiCommentAdd(wallId, cmtText, cmtID);
            Log.d(TAG, "apiCallAddEditComment " + apiCommentAdd);
            StringRequest request = new StringRequest(Request.Method.POST, apiCommentAdd.getUrl(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progress_lay.setVisibility(View.GONE);
                            if (response != null && !response.equals("")) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String status = jsonObject.getString("status");
                                    if (status.equalsIgnoreCase("true")) {

                                        JSONObject result = jsonObject.has("result") ? jsonObject.getJSONObject("result") : null;


                                        FeedComment feedComment = new FeedComment();
                                        feedComment.setCmt_id(
                                                result != null && result.has("comment_id") ? result.getString("comment_id") : ""
                                        );
                                        feedComment.setCmt_text(
                                                result != null && result.has("cmt_text") ? cmtText : cmtText
                                        );
                                        feedComment.setCmt_objectid(
                                                result != null && result.has("cmt_feed_id") ? wallId : wallId
                                        );
                                        feedComment.setCmt_author_id(
                                                result != null && result.has("cmt_author_id") ? App.preference().getUserId() : App.preference().getUserId()
                                        );
                                        feedComment.setCmtAuthorPic(
                                                result != null && result.has("cmt_author_image") ? Config.ApiUrls.PROFILE_URL + App.preference().getUserId() : Config.ApiUrls.PROFILE_URL + App.preference().getUserId()
                                        );

                                        feedComment.setCmt_user_name(
                                                result != null && result.has("cmt_user_name") ? App.preference().getUserName() : App.preference().getUserName()
                                        );
                                        feedComment.setModule_name(
                                                result != null && result.has("module_name") ? result.getString("module_name") : ""
                                        );

                                        String time = "just now";

                                        feedComment.setTime(
                                                time
                                        );

                                        if (!isEdit) {
                                            feedCommentArrayList.add(0, feedComment);
                                            commentAdapter.notifyDataSetChanged();
                                        } else {
                                            feedComment.setTime(time);
                                            feedCommentArrayList.set(position, feedComment);
                                            commentAdapter.notifyDataSetChanged();
                                            progress_lay.setVisibility(View.GONE);
                                        }
                                        if (noData != null) {
                                            if (noData.getVisibility() == View.VISIBLE) {
                                                noData.setVisibility(View.GONE);
                                            }
                                        }
                                    } else {
                                        App.showToast("Try Again");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.e(TAG, "apiCallAddEditComment  api response error; response: " + response);
                            }
//                            loadMoreProgress.setVisibility(View.GONE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "apiCallAddEditComment : " + error.toString());

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return apiCommentAdd.getDefaultHeaders();
                }

                @Override
                protected Map getParams() {
                    return apiCommentAdd.asPostParam();
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
                    Log.e(TAG, "apiCallAddEditComment retry error: " + error.toString());
                }
            });
            App.instance().addToRequestQueue(request, TAG);
        }
    }

    /**
     * API call to list comments
     * @param noData
     * @param progressWheel
     * @param pullRefreshLayout
     * @param pageNo
     * @param feedCommentBeen
     * @param recyclerView
     * @param Wall_id
     */

    public void apiCallCommentsList(final TextView noData, final ProgressWheel progressWheel, final PullToRefreshLayout pullRefreshLayout, final int pageNo, final ArrayList<FeedComment> feedCommentBeen,
                                    final CommentAdapter recyclerView, String Wall_id) {
        if (NetworkReceiver.isOnline(getActivity())) {
            noData.setVisibility(GONE);
            if (pageNo == 1) {
                progressWheel.setVisibility(View.GONE);
                if (!is_Swipe)
                    progress_lay_comments.setVisibility(View.VISIBLE);
            }

            if (is_Swipe) {
                progressWheel.setVisibility(View.GONE);
                progress_lay_comments.setVisibility(View.GONE);
            }


            final ApiComments apiComments = new ApiComments(pageNo, Wall_id);
            System.out.println(TAG + "apiComments " + apiComments);

            StringRequest request = new StringRequest(Request.Method.GET, apiComments.getUrl(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //customLoader.stopLoading();
                            progress_lay_comments.setVisibility(View.GONE);
                            JSONArray resultObject;
                            System.out.println(TAG + " apiComments Response: " + response);
                            if (response != null && !response.equals("")) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    ArrayList<FeedComment> feedsArrayList = GoToParseCommentsList(jsonObject);
                                    if (feedsArrayList.size() == 0) {
                                        if (pageNo == 1) {
                                            noData.setVisibility(View.VISIBLE);

                                        } else {
                                            noData.setVisibility(View.GONE);
                                        }
                                        loadmore = false;
                                    }
                                    if (is_Swipe) {
                                        pullRefreshLayout.setRefreshing(false);
                                    }
                                    if (pageNo == 1) {
                                        feedCommentArrayList.clear();
                                    }
                                    System.out.println("News Feed array List size " + feedsArrayList.size());
                                    for (int i = 0; i < feedsArrayList.size(); i++) {
                                        feedCommentBeen.add(feedsArrayList.get(i));
                                    }
                                    if (pageNo == 1)
                                        //customLoader.stopLoading();
                                        progress_lay.setVisibility(View.GONE);
                                    else
                                        progressWheel.setVisibility(View.GONE);
                                    recyclerView.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "apiCallHomePage Connection timeout");
//                    CommonMethods.SnackBar(root_layout_dashboard, "Connection Timeout");
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return apiComments.getDefaultHeaders();
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return apiComments.asPostParam();
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
                    Log.e(TAG, "apiCallNonRetReqApiRetryError: " + error.toString());
                }
            });
            App.instance().addToRequestQueue(request, TAG);
        } else {

        }
    }

    /**
     * Parse comments list response
     * @param commentObj
     * @return
     */
    public ArrayList<FeedComment> GoToParseCommentsList(JSONObject commentObj) {
        ArrayList<FeedComment> feedCommentBeenTemp = new ArrayList<>();
        try {
            String status = commentObj.has("status") ? commentObj.getString("status") : "false";
            JSONArray commentArray = commentObj.getJSONArray("result");
            if (status.equals("true")) {
                System.out.println("News feed Array Length " + commentArray.length());
                for (int i = 0; i < commentArray.length(); i++) {
                    FeedComment newFeedComment = new FeedComment();
                    JSONObject commentContent = commentArray.getJSONObject(i);
                    newFeedComment.setCmt_id(commentContent.has("cmt_id ") ? commentContent.getString("cmt_id ") : null);
                    newFeedComment.setCmt_text(commentContent.has("cmt_text") ? commentContent.getString("cmt_text") : null);
                    newFeedComment.setCmt_objectid(commentContent.has("post_id") ? commentContent.getString("post_id") : null);
                    newFeedComment.setCmt_author_id(commentContent.has("author_pic") ? commentContent.getString("author_pic") : null);
                    newFeedComment.setCmtAuthorPic(commentContent.has("author_pic") ? Config.ApiUrls.PROFILE_URL + commentContent.getString("author_pic") : "pic-not-available");
                    newFeedComment.setCmt_user_name(commentContent.has("user_name") ? commentContent.getString("user_name") : null);
                    newFeedComment.setTime(commentContent.has("created_at") ? commentContent.getString("created_at") : null);
                    newFeedComment.setModule_name(commentContent.has("module_name") ? commentContent.getString("module_name") : null);
                    feedCommentBeenTemp.add(newFeedComment);
                    //likes.owner.link
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return feedCommentBeenTemp;
    }

    /**
     * Delete comment api call
     * @param commentId
     * @param moduleName
     * @param feed_id
     */
    public void GoToDeleteComment(final String commentId, String moduleName, final String feed_id) {
        progress_lay_comments.setVisibility(View.VISIBLE);
        if (NetworkReceiver.isOnline(getActivity())) {
            final ApiCommentDelete apiCommentDelete = new ApiCommentDelete(moduleName, feed_id, commentId);
            StringRequest request = new StringRequest(Request.Method.POST, apiCommentDelete.getUrl(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //customLoader.stopLoading();
                            progress_lay_comments.setVisibility(View.GONE);
                            Log.d(TAG, "apiCommentDelete response" + response);
                            if (response != null && !response.equals("")) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    App.showToast(jsonObject.getString("status_msg"));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.e(TAG, "apiCommentDelete  api response error; response: " + response);
                            }
//                            loadMoreProgress.setVisibility(View.GONE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "apiCommentDelete: " + error.toString());

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return apiCommentDelete.getDefaultHeaders();
                }

                @Override
                protected Map getParams() {
                    Map<String, String> param = new HashMap<>();
                    //   param.put(MODULE_NAME,  getModuleName());
                    param.put("post_id", feed_id);
                    param.put("cmt_desc", "");
                    param.put("comment_id", commentId);

                    param.put("user_id", App.preference().getUserId());
                    param.put("access_token", App.preference().getAccessToken());
                    param.put("action_type", "4");
                    return param;
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
                    Log.e(TAG, "apiCommentDelete: " + error.toString());
                }
            });
            App.instance().addToRequestQueue(request, TAG);
        }
    }


    //Delete Feed api Call
    public void GoToDeleteFeed(String feed_id, final int position) {
        //customLoader.getCommanLoading();
        progress_lay.setVisibility(View.VISIBLE);
        if (NetworkReceiver.isOnline(context)) {
            final ApiFeedDelete apiDelete = new ApiFeedDelete(feed_id);
            Log.d(TAG, "ApiFeedDelete " + apiDelete);
            StringRequest request = new StringRequest(Request.Method.POST, apiDelete.getUrl(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //customLoader.stopLoading();
                            progress_lay.setVisibility(View.GONE);
                            Log.d(TAG, "apiCallNonRetReq response" + response);
                            if (response != null && !response.equals("")) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean status = jsonObject.has("status") ? jsonObject.getString("status").equalsIgnoreCase("true") : false;
                                    String statusCode = jsonObject.has("status_code") ? jsonObject.getString("status_code") : "";
                                    if (status) {
                                        App.showToast(jsonObject.getString("message"));
                                    }
                                    updateFeedDetailCallBacks.updateFeedDetail(null, position);
                                    getActivity().getSupportFragmentManager().popBackStack();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.e(TAG, "apiCallNonRetReq  api response error; response: " + response);
                            }
//                            loadMoreProgress.setVisibility(View.GONE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "apiCallNonRetReqApiError: " + error.toString());

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return apiDelete.getDefaultHeaders();
                }

                @Override
                protected Map getParams() {
                    return apiDelete.asPostParam();
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
                    return 3;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {
                    Log.e(TAG, "apiCallNonRetReqApiRetryError: " + error.toString());
                }
            });
            App.instance().addToRequestQueue(request, TAG);
        }
    }

    @Override
    public void onResponse(String response, int pageNo, String TAG) {


        try {
            if (TAG.equals(ApiTags.TAG_FOLLOW_UNFOLLOW)) {


                if (response != null && !response.equals("")) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean status = jsonObject.has("status") ? jsonObject.getString("status").equalsIgnoreCase("true") : false;
                        String statusCode = jsonObject.has("status_code") ? jsonObject.getString("status_code") : "";
                        if (status) {
                            App.showToast(jsonObject.getString("message"));
                            updateFeedDetailCallBacks.updateFeedDetail(null, position);
                            getActivity().getSupportFragmentManager().popBackStack();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e(TAG, "apiCallNonRetReq  api response error; response: " + response);
                }
            } else if (TAG.equals(ApiTags.TAG_REPORT_TYPE_LIST)) {
                if (response != null && !response.equals("")) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean status = jsonObject.has("status") ? jsonObject.getString("status").equalsIgnoreCase("true") : false;
                        String statusCode = jsonObject.has("status_code") ? jsonObject.getString("status_code") : "";
                        if (status) {
                            parseReportList(response, pageNo);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e(TAG, "apiCallNonRetReq  api response error; response: " + response);
                }
            } else if (TAG.equals(ApiTags.TAG_REPORT_STATUS)) {
                if (response != null && !response.equals("")) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean status = jsonObject.has("status") ? jsonObject.getString("status").equalsIgnoreCase("true") : false;
                        if (status) {
                            App.showToast(jsonObject.getString("message"));
                            popupreport.dismiss();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e(TAG, "apiCallNonRetReq  api response error; response: " + response);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Get Feed Report List
    private void parseReportList(String response, final int feed_id) {
        try {
            JSONObject respObj = new JSONObject(response);
            if (respObj.getString("status").equals("true")) {
                JSONArray resultArray = respObj.getJSONArray("reports");
                reportlayout.removeAllViews();
                for (int i = 0; i < resultArray.length(); i++) {
                    final JSONObject data = resultArray.getJSONObject(i);
                    View child_report_view = getActivity().getLayoutInflater().inflate(R.layout.feed_report_item, null);
                    CompatTextView reportItem = child_report_view.findViewById(R.id.txtreport);
                    reportItem.setText(data.getString("types"));
                    reportlayout.addView(child_report_view);
                    // addToFeedReportList(resultArray.getJSONObject(i));
                    reportItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                feedReportAPIcall(feed_id, data.getInt("report_id"), data.getString("types"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Feed Report
    private void feedReportAPIcall(int feed_id, int report_id, String report_txt) {
        requestApiCall.postRequestMethodApiCall(this, Config.ApiUrls.FEED_REPORT_STATUS, getParamsForReportAction(feed_id, report_id, report_txt), ApiTags.TAG_REPORT_STATUS, position);
    }

    public HashMap<String, String> getParamsForReportAction(int feed_id, int report_id, String report_txt) {
        HashMap<String, String> params = new HashMap<>();
        params.put("userid", App.preference().getUserId());
        params.put(ApiParams.ACCESS_TOKEN, App.preference().getAccessToken());
        params.put(ApiParams.POST_ID, String.valueOf(feed_id));
        params.put("report_id", String.valueOf(report_id));
        params.put("message", report_txt);
        return params;
    }

    @Override
    public void onErrorResponse(String response, int pageNo, String TAG) {

    }

    //comment adapter-----------------------------------------------------------------
    public class CommentAdapter extends RecyclerView.Adapter {
        ArrayList<FeedComment> feedCommentsArray;
        int positions;
        RelativeLayout progress_lay_comments;
        int selectedPosition = -1;

        public CommentAdapter(RelativeLayout progress_lay_comments, ArrayList<FeedComment> feedCommentBeen, int position) {
            this.feedCommentsArray = feedCommentBeen;
            this.positions = position;
            this.progress_lay_comments = progress_lay_comments;
        }

        private void makeSelection(int position) {
            // Updating old as well as new positions
            notifyItemChanged(selectedPosition);
            selectedPosition = position;
            notifyItemChanged(selectedPosition);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CommentAdapter.CommentsHolder commentsHolder = null;
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_feed_comment_item, parent, false);

            commentsHolder = new CommentAdapter.CommentsHolder(view);
            return commentsHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder mholder, final int position) {
            if (mholder instanceof CommentAdapter.CommentsHolder) {
                final CommentAdapter.CommentsHolder holder = (CommentAdapter.CommentsHolder) mholder;
                holder.commentUsrTxt.setText(feedCommentsArray.get(position).getCmt_text());

                holder.root.setSelected(position == selectedPosition);
                if (selectedPosition == position) {
                    holder.root.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimaryLight));
                } else {
                    holder.root.setBackgroundColor(Color.TRANSPARENT);
                }
                holder.root.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (App.preference().getUserId().equals(feedCommentsArray.get(position).getCmt_author_id()))
                            makeSelection(position);
//                        PopupMenu popup = new PopupMenu(getActivity(), view);
//                        popup.getMenuInflater().inflate(R.menu.comment_options_menu, popup.getMenu());
//                        popup.show();
                        CommentOptionsDialog dialog = new CommentOptionsDialog(getActivity());
                        dialog.setOnButtonClickListener(new CommentOptionsDialog.ButtonClickListener() {
                            @Override
                            public void onEditAction(CommentOptionsDialog dialog) {
                                dialog.dismiss();
                                final Dialog popupEdit;
                                popupEdit = new Dialog(getActivity());
                                popupEdit.setContentView(R.layout.layout_feed_comment_dialog_edit);
                                popupEdit.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                popupEdit.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                popupEdit.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                                popupEdit.show();
                                popupEdit.setCancelable(true);
                                // popupEdit.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                                //   popupDelete.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

                                View imgDialogClose = popupEdit.findViewById(R.id.imgDialogClose);
                                TextView txtSave = (TextView) popupEdit.findViewById(R.id.txtSave);

                                final EditText edit_list_title = (EditText) popupEdit.findViewById(R.id.edit_list_title);

                                isEdit = true;
                                editingCommentID = feedCommentsArray.get(position).getCmt_id();
                                edit_list_title.setText(feedCommentsArray.get(position).getCmt_text());
                                editPosition = position;
                                TextView txtTitle = (TextView) popupEdit.findViewById(R.id.txtTitle);
                                imgDialogClose.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        App.hideKeyboard(view);
//                                CommonMethods.getInstance((Activity) getActivity()).hideSoftKeyboardAdapter();
                                        popupEdit.dismiss();

                                    }
                                });
                                txtSave.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        popupEdit.dismiss();

                                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                                        if (!isEdit) {
                                            if (checkFullLetterEdit(edit_list_title)) {
                                                try {
                                                    int commentCount = Integer.parseInt(feedBean.post_comment_count);
                                                    commentCount = commentCount + 1;
                                                    feedBean.post_comment_count = (String.valueOf(commentCount));
                                                    notifyDataSetChanged();
                                                } catch (NumberFormatException e) {
                                                    e.printStackTrace();
                                                }
                                                apiCallAddEditComment(progress_lay_comments, null, commentAdapter_adapter, edit_list_title.getText().toString(), feedCommentsArray.get(position).getCmt_objectid(), position, feedCommentsArray.get(position).getCmt_id());
                                                edtCommentTxt.setText("");
                                            } else {
                                                App.showToast("There is no texts");
                                            }
                                        } else {
                                            if (checkFullLetterEdit(edit_list_title)) {
                                                apiCallAddEditComment(progress_lay_comments, null, commentAdapter_adapter, edit_list_title.getText().toString(), feedCommentsArray.get(position).getCmt_objectid(), position, editingCommentID);
                                                editingCommentID = "";
                                                edtCommentTxt.setText("");
                                            } else {
                                                App.showToast("There is no texts");
                                            }
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onDeleteAction(CommentOptionsDialog dialog) {
                                dialog.dismiss();
                                final Dialog popupDelete;
                                popupDelete = new Dialog(getActivity(), R.style.DialogSlideAnim);
                                popupDelete.setContentView(R.layout.layout_feed_comment_dialog_delete);
                                popupDelete.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                popupDelete.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                popupDelete.show();
                                // popupDelete.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                                popupDelete.setCancelable(true);
                                TextView deleteHead = (TextView) popupDelete.findViewById(R.id.deleteHead);


                                View imgDialogClose = popupDelete.findViewById(R.id.imgDialogClose);
                                TextView btn_yes = (TextView) popupDelete.findViewById(R.id.btn_yes);
                                TextView btn_no = (TextView) popupDelete.findViewById(R.id.btn_no);


                                btn_no.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        popupDelete.dismiss();
                                    }
                                });
                                imgDialogClose.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        popupDelete.dismiss();
                                    }
                                });
                                btn_yes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        try {
                                            int commentCount = Integer.parseInt(feedBean.post_comment_count);
                                            commentCount = commentCount - 1;
                                            if (commentCount <= 0)
                                                commentCount = 0;
                                            feedBean.post_comment_count = (String.valueOf(commentCount));

                                            makeCommentCountUpdation();
                                            notifyDataSetChanged();
                                        } catch (NumberFormatException e) {
                                            e.printStackTrace();
                                        }
                                        Log.e("Comment id ", feedCommentsArray.get(position).getCmt_id());
                                        GoToDeleteComment(feedCommentsArray.get(position).getCmt_id(), feedCommentsArray.get(position).getModule_name(), feedCommentsArray.get(position).getCmt_objectid());
                                        feedCommentsArray.remove(position);
                                        notifyDataSetChanged();
                                        popupDelete.dismiss();
                                    }
                                });

                            }
                        });
                        if (App.preference().getUserId().equals(feedCommentsArray.get(position).getCmt_author_id()))
                            dialog.show();
                        return true;
                    }
                });

                holder.commentUsrTime.setText(feedCommentsArray.get(position).getTime());
                holder.userName.setText(Html.fromHtml("<b>" + feedCommentsArray.get(position).getCmt_user_name() + "</b>"));


                try {
                    Picasso.with(getActivity())
                            .load(feedCommentsArray.get(position).getCmtAuthorPic())
                            .placeholder(R.drawable.ic_profile_circle_trans)
                            .error(R.drawable.ic_profile_circle_trans)
                            .fit()
                            .into(holder.commentUsrImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (feedCommentsArray.get(position).getCmt_author_id().equalsIgnoreCase(Preferences.getInstance(getActivity()).getUserId())) {
                    holder.layoutEditDeleteComment.setVisibility(View.VISIBLE);
                } else {
                    holder.layoutEditDeleteComment.setVisibility(View.GONE);
                }

                /*
                Delete comment click event
                 */
                holder.deleteComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final Dialog popupDelete;
                        popupDelete = new Dialog(getActivity(), R.style.DialogSlideAnim);
                        popupDelete.setContentView(R.layout.layout_feed_comment_dialog_delete);
                        popupDelete.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        popupDelete.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        popupDelete.show();
                        // popupDelete.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                        popupDelete.setCancelable(true);
                        TextView deleteHead = (TextView) popupDelete.findViewById(R.id.deleteHead);


                        View imgDialogClose = popupDelete.findViewById(R.id.imgDialogClose);
                        TextView btn_yes = (TextView) popupDelete.findViewById(R.id.btn_yes);
                        TextView btn_no = (TextView) popupDelete.findViewById(R.id.btn_no);


                        btn_no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                popupDelete.dismiss();
                            }
                        });
                        imgDialogClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                popupDelete.dismiss();
                            }
                        });
                        btn_yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    int commentCount = Integer.parseInt(feedBean.post_comment_count);
                                    commentCount = commentCount - 1;
                                    if (commentCount <= 0)
                                        commentCount = 0;
                                    feedBean.post_comment_count = (String.valueOf(commentCount));
                                    makeCommentCountUpdation();
                                    //   notifyDataSetChanged();
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                GoToDeleteComment(feedCommentsArray.get(position).getCmt_id(), feedCommentsArray.get(position).getModule_name(), feedCommentsArray.get(position).getCmt_objectid());
                                feedCommentsArray.remove(position);
                                notifyDataSetChanged();
                                popupDelete.dismiss();
                            }
                        });

                    }
                });

                /*
                Edit comment click event
                 */
                holder.editComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final Dialog popupEdit;
                        popupEdit = new Dialog(getActivity());
                        popupEdit.setContentView(R.layout.layout_feed_comment_dialog_edit);
                        popupEdit.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        popupEdit.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        popupEdit.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                        popupEdit.show();
                        popupEdit.setCancelable(true);
                        // popupEdit.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                        //   popupDelete.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

                        View imgDialogClose = popupEdit.findViewById(R.id.imgDialogClose);
                        TextView txtSave = (TextView) popupEdit.findViewById(R.id.txtSave);

                        final EditText edit_list_title = (EditText) popupEdit.findViewById(R.id.edit_list_title);

                        isEdit = true;
                        editingCommentID = feedCommentsArray.get(position).getCmt_id();
                        edit_list_title.setText(feedCommentsArray.get(position).getCmt_text());
                        editPosition = position;
                        TextView txtTitle = (TextView) popupEdit.findViewById(R.id.txtTitle);
                        imgDialogClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                App.hideKeyboard(view);
//                                CommonMethods.getInstance((Activity) getActivity()).hideSoftKeyboardAdapter();
                                popupEdit.dismiss();

                            }
                        });
                        txtSave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                popupEdit.dismiss();

                                if (!isEdit) {
                                    if (checkFullLetterEdit(edit_list_title)) {
                                        try {
                                            int commentCount = Integer.parseInt(feedBean.post_comment_count);
                                            commentCount = commentCount + 1;
                                            feedBean.post_comment_count = (String.valueOf(commentCount));
                                            makeCommentCountUpdation();
                                            notifyDataSetChanged();
                                        } catch (NumberFormatException e) {
                                            e.printStackTrace();
                                        }
                                        apiCallAddEditComment(progress_lay_comments, null, commentAdapter_adapter, edit_list_title.getText().toString(), feedCommentsArray.get(position).getCmt_objectid(), position, "");
                                        edtCommentTxt.setText("");
                                    } else {
                                        App.showToast("There is no texts");
                                    }
                                } else {
                                    if (checkFullLetterEdit(edit_list_title)) {
                                        apiCallAddEditComment(progress_lay_comments, null, commentAdapter_adapter, edit_list_title.getText().toString(), feedCommentsArray.get(position).getCmt_objectid(), position, editingCommentID);
                                        editingCommentID = "";
                                        edtCommentTxt.setText("");
                                    } else {
                                        App.showToast("There is no texts");
                                    }
                                }
                            }
                        });


                    }
                });

            }
        }

        @Override
        public int getItemCount() {
            return feedCommentsArray.size();
        }

        /*
        Comment layout holder class
         */
        public class CommentsHolder extends RecyclerView.ViewHolder {
            public final View root;
            public RelativeLayout deleteLayout;
            ImageView commentUsrImage;
            TextView userName, commentUsrTxt, commentUsrTime, txtclock;
            TextView editComment, deleteComment;
            LinearLayout layoutEditDeleteComment;
            boolean selected = false;

            public void setSelected(boolean isSelected) {
                selected = isSelected;
            }

            public CommentsHolder(View itemView) {
                super(itemView);
                root = itemView.findViewById(R.id.layoutRoot);
                deleteLayout = itemView.findViewById(R.id.layoutDelete);
                commentUsrImage = (ImageView) itemView.findViewById(R.id.commentUsrImage);
                userName = (TextView) itemView.findViewById(R.id.userName);
                commentUsrTxt = (TextView) itemView.findViewById(R.id.commentUsrTxt);
                commentUsrTime = (TextView) itemView.findViewById(R.id.commentUsrTime);
                deleteComment = (TextView) itemView.findViewById(R.id.deleteComment);
                editComment = (TextView) itemView.findViewById(R.id.editComment);
                txtclock = (TextView) itemView.findViewById(R.id.txtclock);
                layoutEditDeleteComment = (LinearLayout) itemView.findViewById(R.id.layoutEditDeleteComment);
            }

        }


    }



    boolean checkFullLetter() {
        boolean isFullLetter = false;
        char[] chars = edtCommentTxt.getText().toString().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == ' ') {
                isFullLetter = false;
            } else {
                isFullLetter = true;
                return isFullLetter;

            }
        }
        return isFullLetter;
    }

    boolean checkFullLetterEdit(EditText editText) {
        boolean isFullLetter = false;
        char[] chars = editText.getText().toString().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == ' ') {
                isFullLetter = false;
            } else {
                isFullLetter = true;
                return isFullLetter;

            }
        }
        return isFullLetter;
    }

    /**
     * If videoview is not null and NOT being played, then start the video
     */
    public void startVideoPlayback() {
        Log.e(TAG, "startVideoPlayback currentVideoView Null: " + (currentVideoView == null) + " isPlaying: " + (currentVideoView != null && currentVideoView.isPlaying()));
        try {
            if (currentVideoView != null && !currentVideoView.isPlaying()) {
                currentVideoView.start();
                Log.e(TAG, "currentVideoView playback started");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * If videoview is not null and if video is being played, then pause the video
     */
    public void pauseVideoPlayback() {
        Log.e(TAG, "stopVideoPlayback currentVideoView Null: " + (currentVideoView == null) + " isPlaying: " + (currentVideoView != null && currentVideoView.isPlaying()));
        if (currentVideoView != null && currentVideoView.isPlaying()) {
            currentVideoView.pause();
            Log.e(TAG, "currentVideoView playback stopped");
        }
    }

    /**
     * interface declaration to update feed
     */
    public interface updateFeedDetailCallBack {
        public void updateFeedDetail(FeedBean feedBean, int position);
    }

    /**
     * Update UI on change of comment count
     */
    public void makeCommentCountUpdation() {
        // holder.likecount.setText(feed.formattedLikesCount());
        holder.commentcount.setText(feedBean.post_comment_count);
        updateFeedDetailCallBacks.updateFeedDetail(feedBean, position);
    }

    @Override
    public void editFeedCallback(int position, String postText) {
        feedBean.post_text = postText;
        holder.title_text.setVisibility(View.VISIBLE);
        holder.title_text.setText(postText);
    }
}
