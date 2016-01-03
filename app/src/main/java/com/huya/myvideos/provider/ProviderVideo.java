package com.huya.myvideos.provider;

import android.app.Fragment;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huya.myvideos.UILApplication;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by even on 16/1/3.
 */
public class ProviderVideo {

    private ArrayList<Video> videos = new ArrayList<Video>();
    private String url="http://wx.liansuoerp.com/index.php?";
    private Gson gson;
    private Boolean noMore = false;
    private Fragment fragment;
    public ProviderVideo(Fragment fragment, ArrayList<Video> videos) {
        this.fragment = fragment;
        this.videos = videos;
    }

    public void getVideos(final int page) {
        url = url + "&page=" + page;
        gson = new Gson();
        //final ArrayList<Video> videos = new ArrayList<Video>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //接收参数，用gson
                Type type = new TypeToken<ArrayList<Video>>() {
                }.getType();
                ArrayList<Video> videos1 = gson.fromJson(response, type);
                Log.e("tatat", videos1.toString());
                if (videos1.isEmpty()) {
                    noMore = true;
                }

                //如果page 等于 1， 代表下拉刷新，清掉原来的列表
                if (page == 1) {
                    if (!videos.isEmpty()) {
                        videos.clear();
                    }
                    videos.addAll(videos1);
                    //videos = (ArrayList<Video>) videos1.clone();
                } else {
                    //去重
                    for (Video v:videos1) {
                        if(videos.contains(v)) {
                            videos1.remove(v);
                        }
                    }
                    //返回的数据添加到末尾
                    videos.addAll(videos1);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("main_activity", error.getMessage());
            }
        });
        UILApplication.getInstance().addToRequestQueue(stringRequest);
    }

}
