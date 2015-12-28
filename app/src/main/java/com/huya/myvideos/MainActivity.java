package com.huya.myvideos;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.huya.myvideos.provider.GetList;
import com.huya.myvideos.provider.Video;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public  class MainActivity extends Activity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private List<Map<String, String>> list_item = null;
    boolean flag = false;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;
    private ListView lv;
    private ProgressDialog pDialog;
    private String url = "http://wx.liansuoerp.com/api.php?a=1";
    private ArrayList<Video> videos;
    private ItemListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pDialog=new ProgressDialog(this);
        pDialog.setMessage("loading....");
        pDialog.show();

        lv = (ListView)findViewById(R.id.list);
        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.l1) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.l1) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.l1) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
               // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                .build(); // 构建完成

        videos = new ArrayList<Video>();
        //请求数据
        fetchItems();
        adapter = new ItemListAdapter(videos);
        //将adapter 设置到listviewer
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "You click: " + position, Toast.LENGTH_SHORT).show();
                ListView listView = (ListView)parent;
                HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
                String vid = map.get("id");
                String url = "http://m.v.huya.com/play/"+vid+".html";
                //super.onListItemClick(l, v, position, id);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));//设置一个URI地址
                MainActivity.this.startActivity(intent);//用startActivity打开这个指定的网页。
            }

        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    private void fetchItems() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //塞入到对象中
                        for (int i=0; i < response.length();i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                Video video = new Video();
                                video.setDateline(obj.optString("upload_start_time"));
                                video.setImage(obj.optString("cover"));
                                video.setTimes(obj.optString("play_sum"));
                                video.setVid(obj.optString("vid"));
                                videos.add(video);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        hidePDialog();
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("main_activity", error.getMessage());
                        hidePDialog();
                    }
                }
        );
        UILApplication.getInstance().addToRequestQueue(jsonArrayRequest);
    }
    @Override
    public void onStop() {
        super.onStop();
    }

    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }
    public void hidePDialog(){
        if(pDialog!=null)
        pDialog.dismiss();
        pDialog=null;
    }

    class ItemListAdapter extends BaseAdapter {

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
            holder.title.setText(videos.get(position).getTitle());
            holder.dateline.setText(videos.get(position).getDateline());
            holder.times.setText(videos.get(position).getTimes());
            MainActivity.this.imageLoader.displayImage(videos.get(position).getImage(), holder.image, options);
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

