package com.huya.myvideos;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huya.myvideos.provider.Video;

import java.lang.reflect.Type;
import java.util.ArrayList;

import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Created by even on 16/1/3.
 */
public class VideoFragment extends ListFragment implements AbsListView.OnScrollListener {
    public ArrayList<Video> videos;
    private final static int INI_VIDEO_LIST = 1;
    private VideoListAdapter videoListAdapter;
    private Context context;
    public Activity activity;
    private ProgressDialog pDialog;
    private ListView lv;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar pb;
    private View moreView;
    private int cur_page;
    private Boolean isEnd;
    private Boolean noMore;
    private Boolean is_loading;
    private View convertView;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && noMore && isEnd) {
            Toast.makeText(context, "数据全部加载完成，没有更多数据！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // 计算最后可见条目的索引
        int lastVisibleIndex = firstVisibleItem + visibleItemCount;
        //滑到最后
        isEnd = lastVisibleIndex == totalItemCount;
        //列表还有更多，且不是在加载中
        if (isEnd && !is_loading && !noMore) {
            cur_page++;
            pb.setVisibility(View.VISIBLE);
            (new ProviderVideo()).getVideos(cur_page);
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case VideoFragment.INI_VIDEO_LIST:
                    videoListAdapter.notifyDataSetChanged();
                    is_loading = false;
                    pb.setVisibility(View.GONE);
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
        this.context = getActivity();
        this.activity = getActivity();
        //初始化参数
        pDialog = new ProgressDialog(this.context);
        pDialog.setMessage("loading....");
        pDialog.show();
        //初始化第一页
        cur_page = 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.videofrag, container, false);
            moreView = inflater.inflate(R.layout.load_more, null);
            pb = (ProgressBar) moreView.findViewById(R.id.process);
            swipeRefreshLayout = (SwipeRefreshLayout) convertView.findViewById(R.id.swipe);
        }
        ViewGroup parent = (ViewGroup) convertView.getParent();
        if (parent != null) {
            parent.removeView(convertView);
        }
        return convertView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Video video = (Video) l.getItemAtPosition(position);
        String vid = video.getVid();
        Bundle bundle = new Bundle();
        bundle.putString("vid", vid);
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lv = getListView();
        lv.addFooterView(moreView);

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initVideoList();
        pDialog.hide();
    }

    public void initVideoList() {
        videos = new ArrayList<>();
        videoListAdapter = new VideoListAdapter(this.context, this.videos);
        setListAdapter(videoListAdapter);
        (new ProviderVideo()).getVideos(cur_page);
        noMore = false;
        //// FIXME: 2016-01-04  页码判断
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cur_page = 1;
                noMore = false;
                (new ProviderVideo()).getVideos(cur_page);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.background_light, android.R.color.holo_green_light, android.R.color.holo_green_light);
        lv.setOnScrollListener(this);
    }

    public void postHandler() {


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
                message.what = INI_VIDEO_LIST;
                handler.handleMessage(message);
            }
        }, 1000);
//        message.what = INI_VIDEO_LIST;
//        handler.handleMessage(message);
    }

    public class ProviderVideo {

        private String url = "http://wx.liansuoerp.com/index.php?";
        private Gson gson;

        public void getVideos(final int page) {
            url = url + "&page=" + page;
            gson = new Gson();
            is_loading = true;
            //final ArrayList<Video> videos = new ArrayList<Video>();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Type type = new TypeToken<ArrayList<Video>>() {
                    }.getType();
                    ArrayList<Video> videos1 = gson.fromJson(response, type);
                    Log.e("tatat", videos1.toString());
                    if (videos1.isEmpty()) {
                        noMore = true;
                    }
                    //如果page 等于 1， 代表下拉刷新，清掉原来的列表
                    if (page == 1) {
                        videos.clear();
                        videos.addAll(videos1);
                    } else {
                        //去重
                        for (Video v : videos1) {
                            //// FIXME: 2016-01-04 
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
                    postHandler();
                }
            });
            UILApplication.getInstance().addToRequestQueue(stringRequest);
        }

    }

}
