/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.fragments.bottombar;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidapp.instasocial.R;
import com.androidapp.instasocial.modules.search.SearchProfileAdapter;
import com.androidapp.instasocial.modules.search.SearchProfileBean;
import com.androidapp.instasocial.ui.CompatEditText;
import com.androidapp.instasocial.ui.CompatImageView;
import com.androidapp.instasocial.ui.CompatTextView;
import com.androidapp.instasocial.utils.Config;
import com.androidapp.instasocial.utils.DividerItemDecorator;
import com.androidapp.instasocial.webservice.ApiParams;
import com.androidapp.instasocial.webservice.ApiTags;
import com.androidapp.instasocial.webservice.RequestApiCall;
import com.androidapp.instasocial.webservice.ResponseCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This class is used to search an user from the list by keyword
 */

public class SearchProfileFragment extends Fragment implements ResponseCallBack ,SearchProfileAdapter.UserFollowStatusUpdate {

    /**
     * Member variables declarations/initializations
     */
    View rootView;
    SearchProfileAdapter searchProfileAdapter;
    RecyclerView searchProfileList;
    RequestApiCall requestApiCall;
    CompatImageView icNavigation;
    TextView txtNoData;
    boolean hasActionBar = false;
    boolean hasBack = false;
    CompatEditText edtSearchProfile;
    ArrayList<SearchProfileBean> searchProfileBeans = new ArrayList<>();
    ArrayList<SearchProfileBean> searchProfileBeanTemp = new ArrayList<>();
    ArrayList<SearchProfileBean> searchProfileBeanResult = new ArrayList<>();
    int pageNo = 1, searchPageNo = 1;
    RelativeLayout progress_lay;
    LinearLayoutManager linearLayoutManager;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 1;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    boolean isProfileSearchAll = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.search_profile, null);
        initUiControls();
        initWebControls();
        if (isProfileSearchAll)
            apiCallForSearch();
        return rootView;

    }
     CompatTextView icMore;
     View layoutActionbar;
     View icSearch;

    /**
     * UI controls initialization
     */
    private void initUiControls() {
        edtSearchProfile = rootView.findViewById(R.id.edtSearchProfile);
        txtNoData = rootView.findViewById(R.id.txtNoData);
        txtNoData.setVisibility(View.GONE);
        searchProfileAdapter = new SearchProfileAdapter(getActivity(), searchProfileBeans,this);
        icNavigation = rootView.findViewById(R.id.icNavigation);
        progress_lay = rootView.findViewById(R.id.progress_lay);
        searchProfileList = rootView.findViewById(R.id.searchProfileList);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        searchProfileList.setLayoutManager(linearLayoutManager);
        searchProfileList.setAdapter(searchProfileAdapter);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(getActivity(), R.drawable.divider));
        searchProfileList.addItemDecoration(dividerItemDecoration);
        icMore = rootView.findViewById(R.id.ic_more);
        icSearch = rootView.findViewById(R.id.icSearch);
        layoutActionbar=rootView.findViewById(R.id.layoutActionbar);
        icNavigation = rootView.findViewById(R.id.icNavigation);
        icNavigation.setImageResource(R.drawable.ic_back);
        icSearch.setVisibility(View.GONE);
        icMore.setVisibility(View.GONE);
        layoutActionbar.setVisibility(hasActionBar?View.VISIBLE:View.GONE);
        icNavigation.setVisibility(hasBack?View.VISIBLE:View.GONE);

        icNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                //Find the currently focused view, so we can grab the correct window token from it.
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        init();

    }

    private void init() {

        /**
         * Search logic on tapping the searchbar
         */
        edtSearchProfile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isProfileSearchAll = false;
                searchPageNo = 1;
                if (s.toString().length() > 0) {
                    makeSearchWithKeyword();//if edittext contains keyword to search, call search function
                } else {
                    loadAllProfileIssues();//list all people
                }
            }
        });


        /**
         * People list, loadmore functionality
         */
        searchProfileList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = searchProfileList.getChildCount();
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
                    if (edtSearchProfile.getText().toString().equals("")) {
                        pageNo = pageNo + 1;
                        isProfileSearchAll = true;
                    } else
                        searchPageNo = searchPageNo + 1;
                    apiCallForSearch();
                    loading = true;
                }
            }

        });
    }

    /**
     * API call initializations
     */
    private void initWebControls() {
        requestApiCall = new RequestApiCall(getActivity());
    }


    /**
     * Response of search people
     * @param response JSON response
     * @param pageNo
     * @param TAG
     */
    @Override
    public void onResponse(String response, int pageNo, String TAG) {
        progressVisibility(View.GONE);
        if (TAG.equals(ApiTags.TAG_SEARCH_PROFILE)) {
            parseResponse(response);
        }
    }

    /**
     * called when data is not returned
     * @param response
     * @param pageNo
     * @param TAG
     */
    @Override
    public void onErrorResponse(String response, int pageNo, String TAG) {
        txtNoData.setVisibility(searchProfileBeans.isEmpty()?View.VISIBLE:View.GONE);
    }

    /**
     * Fragment instance creation
     * @param hasActionBar boolean that checks whether action bar is present or not
     * @param hasBack boolean that checks whether back icon is present or not
     * @return
     */
    public static SearchProfileFragment newInstance(boolean hasActionBar,boolean hasBack) {
        SearchProfileFragment searchProfileFragment = new SearchProfileFragment();
        searchProfileFragment.hasActionBar = hasActionBar;
        searchProfileFragment.hasBack=hasBack;
        return searchProfileFragment;
    }

    /**
     * API call to make a search
     */
    private void apiCallForSearch() {
        progressVisibility(View.VISIBLE);
        txtNoData.setVisibility(View.GONE);
        requestApiCall.getRequestMethodApiCall(this, getSearchUrl(), ApiTags.TAG_SEARCH_PROFILE, pageNo);
    }

    /**
     * will retunrs the api url either for search ot to list all people
     * @return
     */
    private String getSearchUrl() {
        if (edtSearchProfile.getText().toString().equals(""))
            return Config.ApiUrls.SEARCH_PROFILE_LIST + "?" +
                    ApiParams.getUserCredential() + "&" +
                    ApiParams.KEYWORD + "=" + edtSearchProfile.getText().toString().replace(" ","%20") + "&" +
                    ApiParams.SORT + "=" + "&" +
                    ApiParams.PAGE_NO + "=" + pageNo;
        else
            return Config.ApiUrls.SEARCH_PROFILE_LIST + "?" +
                    ApiParams.getUserCredential() + "&" +
                    ApiParams.KEYWORD + "=" + edtSearchProfile.getText().toString().replace(" ","%20") + "&" +
                    ApiParams.SORT + "=" + "&" +
                    ApiParams.PAGE_NO + "=" + searchPageNo;
    }

    /**
     * Takes response and parses
     * @param response
     */

    private void parseResponse(String response) {
        try {
            JSONObject respObj = new JSONObject(response);
            if (respObj.getString("status").equals("true")) {
                JSONArray resultArray = respObj.getJSONArray("result");
                if (searchPageNo == 1) {
                    searchProfileBeanResult.clear();
                }
                for (int i = 0; i < resultArray.length(); i++) {
                    addToArrayList(resultArray.getJSONObject(i));
                }
                searchProfileBeans.clear();
                isProfileSearchAll = false;

                if (edtSearchProfile.getText().toString().equals("")) {
                    searchProfileBeans.addAll(searchProfileBeanTemp);
                } else {
                    searchProfileBeans.addAll(searchProfileBeanResult);
                }
                searchProfileAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        txtNoData.setVisibility(searchProfileBeans.isEmpty()?View.VISIBLE:View.GONE);
    }

    /**
     * Response is parsed and added to list
     * @param resultObj
     */
    private void addToArrayList(JSONObject resultObj) {
        SearchProfileBean bean = new SearchProfileBean();
        try {
            bean.user_id = resultObj.has("user_id") ? resultObj.getString("user_id") : "";
            bean.name = resultObj.has("name") ? resultObj.getString("name") : "";
            bean.first_name = resultObj.has("first_name") ? resultObj.getString("first_name") : "";
            bean.last_name = resultObj.has("last_name") ? resultObj.getString("last_name") : "";
            bean.username = resultObj.has("username") ? resultObj.getString("username") : "";
            bean.email = resultObj.has("email") ? resultObj.getString("email") : "";
            bean.state = resultObj.has("state") ? resultObj.getString("state") : "";

            bean.country = resultObj.has("country") ? resultObj.getString("country") : "";
            bean.dob = resultObj.has("dob") ? resultObj.getString("dob") : "";
            bean.gender = resultObj.has("gender") ? resultObj.getString("gender") : "";
            bean.description = resultObj.has("description") ? resultObj.getString("description") : "";
            bean.follower_count = resultObj.has("follower_count") ? resultObj.getString("follower_count") : "";
            bean.following_count = resultObj.has("following_count") ? resultObj.getString("following_count") : "";
            bean.post_count = resultObj.has("post_count") ? resultObj.getString("post_count") : "";
            bean.created_at = resultObj.has("created_at") ? resultObj.getString("created_at") : "";
            bean.role = resultObj.has("role") ? resultObj.getString("role") : "";
            bean.is_private = resultObj.has("is_private") ? resultObj.getString("is_private") : "";
            bean.is_follow = resultObj.has("is_follow") ? resultObj.getString("is_follow") : "";
            bean.is_block = resultObj.has("is_block") ? resultObj.getString("is_block") : "";
            bean.follow_status = resultObj.has("follow_status") ? resultObj.getString("follow_status") : "";
            bean.serial_no = resultObj.has("serial_no") ? resultObj.getString("serial_no") : "";
            bean.user_follow_status = resultObj.has("user_follow_status") ? resultObj.getString("user_follow_status") : "";
            if (edtSearchProfile.getText().toString().equals("")) {
                if (isProfileSearchAll) {
                    searchProfileBeanTemp.add(bean);
                }
            } else {
                searchProfileBeanResult.add(bean);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Toggles visisbility of loader
     * @param visibility
     */
    private void progressVisibility(int visibility) {
        progress_lay.setVisibility(visibility);
    }


    /**
     * Updates follow status, in the array list
     * @param userFollowStatusUpdate
     * @param id
     */
    @Override
    public void onUserFollowStatusUpdate(String userFollowStatusUpdate,String id) {
        for (SearchProfileBean bean : searchProfileBeanTemp) {
            if(bean.user_id.equals(id)){
              bean.user_follow_status = userFollowStatusUpdate;
            }
        }
    }

    /**
     * calling api to perform search
     */
    public void makeSearchWithKeyword(){
        searchProfileBeans.clear();
        searchProfileBeanResult.clear();
        searchProfileAdapter.notifyDataSetChanged();
        apiCallForSearch();
    }

    /**
     * Used to load all people in list
     */
    private void loadAllProfileIssues(){
        searchPageNo = 1;
        searchProfileBeanResult.clear();
        searchProfileBeans.clear();
        searchProfileBeans.addAll(searchProfileBeanTemp);
        searchProfileAdapter.notifyDataSetChanged();
    }
}
