package com.huya.myvideos;
import android.app.Activity;
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

import com.huya.myvideos.provider.GetList;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //拉取数据
        new Thread(new Runnable(){
            @Override
            public void run() {
                try{
                    list_item = (new GetList()).getData(1);
                    MainActivity.this.flag = true;
                } catch (Exception e) {
                    Log.e("test", e.toString());
                }

            }
        }).start();
        //等待数据返回
        while(!flag){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
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
        lv.setAdapter(new ItemListAdapter());
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

    @Override
    public void onStop() {
        super.onStop();
    }

    class ItemListAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return list_item.size();
        }

        @Override
        public Object getItem(int position) {
            return list_item.get(position);
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
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                holder.dateline = (TextView) convertView.findViewById(R.id.dateline);
                holder.times = (TextView) convertView.findViewById(R.id.times);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Map<String, String> map = MainActivity.this.list_item.get(position);
            holder.title.setText(map.get("title"));
            holder.dateline.setText(map.get("dateline"));
            holder.times.setText(map.get("times"));
            String aUrl = "http://static.oschina.net/uploads/img/201208/13122559_L8G0.png";
            MainActivity.this.imageLoader.displayImage(map.get("image"), holder.img, options);
            return convertView;
        }


        class ViewHolder {
            public ImageView img;
            public TextView dateline;
            public TextView times;
            public TextView title;
        }
    }

}

