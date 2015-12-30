package com.huya.myvideos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huya.myvideos.provider.Video;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity implements AbsListView.OnScrollListener {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    private DisplayImageOptions options;
    private ListView lv;
    private ProgressDialog pDialog;
    private String url = "http://api.v.huya.com/index.php?r=video/list&channelId=wiiu&appKey=hyapi_cs&pageSize=7";
    private ArrayList<Video> videos;
    private ItemListAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar pb;
    private View moreView;
    private int lastVisibleIndex;
    private int cur_page;
    private Boolean isEnd;
    private Boolean is_loading;
    private Gson gson = new Gson();
    private boolean noMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("loading....");
        pDialog.show();
        //初始化第一页
        cur_page = 1;
        lv = (ListView) findViewById(R.id.list);
        //上拉加载更多
        moreView = getLayoutInflater().inflate(R.layout.load_more, null);
        pb = (ProgressBar) moreView.findViewById(R.id.process);
        videos = new ArrayList<Video>();
        //请求数据
        fetchItems(cur_page);
        adapter = new ItemListAdapter(videos);
        //将adapter 设置到listviewer
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "You click: " + position, Toast.LENGTH_SHORT).show();
                ListView listView = (ListView) parent;
                Video video = (Video) listView.getItemAtPosition(position);
                String vid = video.getVid();
                Bundle bundle = new Bundle();
                bundle.putString("vid", vid);
                Intent intent = new Intent(MainActivity.this, ViewActivity.class);
                intent.putExtras(bundle);
                MainActivity.this.startActivity(intent);
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cur_page = 1;
                noMore = false;
                fetchItems(cur_page);
                Log.e("aaaa", String.valueOf(noMore));
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.background_light, android.R.color.holo_green_light, android.R.color.holo_green_light);

        lv.setOnScrollListener(this);
        //添加到list 底部
        lv.addFooterView(moreView);

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    private void fetchItems(final int page) {
        url = url + "&page=" + page;
        is_loading = true;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //接收参数，用gson
                Type type = new TypeToken<ArrayList<Video>>() {
                }.getType();
                ArrayList<Video> videos1 = gson.fromJson(response, type);
                if (videos1.isEmpty()) {
                    noMore = true;
                }
                Log.e("ttttt", String.valueOf(noMore));
                //如果page 等于 1， 代表下拉刷新，清掉原来的列表
                if (page == 1) {
                    if (!videos.isEmpty()) {
                        videos.clear();
                    }
                    videos.addAll(videos1);
                    //ArrayList<Video>  videos = (ArrayList<Video>) videos1.clone();
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
                hidePDialog();
                adapter.notifyDataSetChanged();
                pb.setVisibility(View.GONE);
                is_loading = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("main_activity", error.getMessage());
                hidePDialog();
                is_loading = false;
            }
        });
        UILApplication.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    public void hidePDialog() {
        if (pDialog != null)
            pDialog.dismiss();
        pDialog = null;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && noMore && isEnd) {
            Toast.makeText(this, "数据全部加载完成，没有更多数据！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // 计算最后可见条目的索引
        lastVisibleIndex = firstVisibleItem + visibleItemCount;
        //滑到最后
        isEnd = lastVisibleIndex == totalItemCount ? true : false;
        //列表还有更多，且不是在加载中
        if (isEnd && !is_loading && !noMore) {
            cur_page++;
            pb.setVisibility(View.VISIBLE);
            fetchItems(cur_page);
        }
    }

    class ItemListAdapter extends BaseAdapter {
        private ImageLoader imageLoader = ImageLoader.getInstance();
        private ArrayList<Video> videos;
        ItemListAdapter(ArrayList<Video> videos) {
            this.videos = videos;
        }

        @Override
        public int getCount() {
            return videos.size();
        }

        @Override
        public Object getItem(int position) {
            return videos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.vlist, parent, false);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.image = (ImageView) convertView.findViewById(R.id.img);
                holder.dateline = (TextView) convertView.findViewById(R.id.dateline);
                holder.times = (TextView) convertView.findViewById(R.id.times);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //用videos 的值填充
            holder.title.setText(videos.get(position).getVideo_title());
            holder.dateline.setText(videos.get(position).getUpload_start_time());
            holder.times.setText(videos.get(position).getPlay_sum());
            imageLoader.displayImage(videos.get(position).getCover(), holder.image);
            return convertView;
        }
        class ViewHolder {
            public ImageView image;
            public TextView dateline;
            public TextView times;
            public TextView title;
        }
    }

}

