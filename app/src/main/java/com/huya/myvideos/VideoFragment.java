package com.huya.myvideos;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huya.myvideos.provider.ProviderVideo;
import com.huya.myvideos.provider.Video;

import java.lang.reflect.Type;
import java.util.ArrayList;

import android.os.Handler;

/**
 * Created by even on 16/1/3.
 */
public class VideoFragment extends ListFragment {
    private Callbacks activityCallback;
    public ArrayList<Video> videos;
    private final static int INI_VIDEO_LIST = 1;
    private VideoListAdapter videoListAdapter;
    private ListView listView;
    private View convertView;
    private Context context;
    private int page;


    //用于回调的
    public interface Callbacks {
        public void onVideoSelected(int id);
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case VideoFragment.INI_VIDEO_LIST:
                    videoListAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化参数
        page = 1;
        //initVideoList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        convertView = inflater.inflate(R.layout.videofrag, container, false);
        return convertView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //initVideoList();
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.context = getActivity();

        initVideoList();

    }

    @TargetApi(Build.VERSION_CODES.M)
    public void initVideoList() {
        videos = new ArrayList<Video>();
        videoListAdapter = new VideoListAdapter(this.context, this.videos);
        setListAdapter(videoListAdapter);
        (new ProviderVideo()).getVideos(page);
    }

    public void postHandler() {
        Message message = handler.obtainMessage();
        message.what = this.INI_VIDEO_LIST;
        handler.handleMessage(message);
    }

    public class ProviderVideo {

        private String url = "http://wx.liansuoerp.com/index.php?";
        private Gson gson;
        private Boolean noMore = false;

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
                        for (Video v : videos1) {
                            if (videos.contains(v)) {
                                videos1.remove(v);
                            }
                        }
                        //返回的数据添加到末尾
                        videos.addAll(videos1);
                    }
                    postHandler();
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

}
