package com.nigelcardozo.screenscraper;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

public class VolleyRequestQueueHelper {

    //This is a helper class for the Volley library
    //It is implemented using the singleton pattern
    //to ensure we don't have multiple queues.

    private static VolleyRequestQueueHelper mInstance;
    private static Context mCtx;
    private RequestQueue mRequestQueue;

    private VolleyRequestQueueHelper(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueueInstance();
    }

    public static synchronized VolleyRequestQueueHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyRequestQueueHelper(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueueInstance() {

        //Singleton implementation to ensure we only have one queue setup.
        //If it doesn't exist, create it, either way return it!

        if (mRequestQueue == null) {
            Cache cache = new DiskBasedCache(mCtx.getCacheDir(), 10 * 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(cache, network);

            //Start the Volley request queue
            mRequestQueue.start();
        }

        return mRequestQueue;
    }

}
