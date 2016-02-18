package com.nigelcardozo.screenscraper;

import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;

import java.util.HashMap;
import java.util.Map;

public class VolleyJsonObjectRequestHelper extends JsonObjectRequest{

    //This is a helper class for making the JsonObjectRequest via Volley

    public VolleyJsonObjectRequestHelper(int method, String url, JSONObject jsonRequest,
                                   Response.Listener<JSONObject> listener,
                                   Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        return headers;
    }

    @Override
    public RetryPolicy getRetryPolicy() {
        return super.getRetryPolicy();
    }
}
