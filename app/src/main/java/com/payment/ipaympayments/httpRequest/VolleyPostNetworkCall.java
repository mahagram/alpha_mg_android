package com.payment.ipaympayments.httpRequest;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.payment.ipaympayments.utill.Print;

import java.util.Map;

public class VolleyPostNetworkCall {

    private RequestResponseLis listener;
    private Context context;
    private String apiUrl;


    public VolleyPostNetworkCall(RequestResponseLis listener, Context context, String apiUrl) {
        this.listener = listener;
        this.context = context;
        this.apiUrl = apiUrl;
    }

    public void netWorkCall(final Map<String, String> map) {

        StringRequest sendRequest = new StringRequest(Request.Method.POST,
                apiUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String Response) {
                        Print.P("Volley Form Response : " + Response);
                        listener.onSuccessRequest(Response);
                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Print.P("Not able to connect with server");
                listener.onFailRequest("Network connection error");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //Log.e("Demo", "###################################");
                //System.out.print("Network Parameters: " + new JSONObject(map));
                return map;
            }
        };

        sendRequest.setRetryPolicy(new
                DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        );

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(sendRequest);
    }

    public interface RequestResponseLis {
        void onSuccessRequest(String JSonResponse);

        void onFailRequest(String msg);
    }


}
