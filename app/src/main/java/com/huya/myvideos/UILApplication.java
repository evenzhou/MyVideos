package com.huya.myvideos;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by even on 2015-12-25.
 */
public class UILApplication extends Application {
    public static final String TAG = "UILApplication";
    private RequestQueue requestQueue;
    private static UILApplication sInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader(getApplicationContext());
        //initVolley(getApplicationContext());
        sInstance = this;
    }
    public static synchronized UILApplication getInstance() {
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        if(requestQueue == null) {
            synchronized(UILApplication.class) {
                if(requestQueue == null) {
                    requestQueue = Volley.newRequestQueue(getApplicationContext());
                }
            }
        }
        return requestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        getRequestQueue().add(req);
    }

    public static  void initVolley(Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }

    public static void initImageLoader(Context context) {
        //缓存文件的目录
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(context);
      /*  File cacheDir = StorageUtils.getOwnCacheDirectory(context, "universalimageloader/Cache");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();*/
        //全局初始化此配置
        ImageLoader.getInstance().init(config);
    }
}