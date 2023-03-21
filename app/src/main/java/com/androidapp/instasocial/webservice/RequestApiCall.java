package com.androidapp.instasocial.webservice;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androidapp.instasocial.App;
import com.androidapp.instasocial.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Common class to send api requests to web server
 */
public class RequestApiCall {
    Context context;
    String statusCode = "";

    public RequestApiCall(Context context) {
        this.context = context;
    }

    /**
     * send request using GET method
     * @param responseCallBack a response call back
     * @param url API url
     * @param TAG identifier
     * @param pageNo
     */
    public void getRequestMethodApiCall(final ResponseCallBack responseCallBack, String url, final String TAG, final int pageNo) {
       Log.e("getUrl Method URL "+TAG,url);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                responseCallBack.onResponse(response, pageNo, TAG);
            }
          }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        responseCallBack.onErrorResponse(error.networkResponse.data.toString(), pageNo, TAG);
                        App.showToast(App.getStringRes(R.string.str_txt_try_again_msg));
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            protected Map<String, String> getParams() {

                HashMap<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                statusCode = String.valueOf(response.statusCode);
                return super.parseNetworkResponse(response);
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
                    }
                }).start();
            }
        });
        App.instance().addToRequestQueue(request,TAG);
    }

    /**
     * send request using POST method
     * @param responseCallBack a callback
     * @param url api url
     * @param params fields required for post call
     * @param TAG identifier
     * @param pageNo
     */
    public void postRequestMethodApiCall(final ResponseCallBack responseCallBack, String url, final HashMap<String, String> params, final String TAG, final int pageNo) {
        if (!App.isOnline()) {
            App.showToast(R.string.noNet);
            return;
        }
        StringRequest login = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       responseCallBack.onResponse(response,pageNo,TAG);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       responseCallBack.onErrorResponse(error.networkResponse.data.toString(),pageNo,TAG);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                   return params;
            }
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                statusCode = String.valueOf(response.statusCode);
                return super.parseNetworkResponse(response);
            }
        };

        login.setRetryPolicy(new RetryPolicy() {
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
        App.instance().addToRequestQueue(login,TAG);
    }
}
