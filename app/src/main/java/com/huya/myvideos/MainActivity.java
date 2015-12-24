package com.huya.myvideos;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


//import com.huya.myvideos.provider.ListData;

import com.huya.myvideos.provider.GetList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public  class MainActivity extends ListActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private List<Map<String, String>> list_item = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //拉取数据
        new Thread(new Runnable(){
            @Override
            public void run() {
                try{
                    list_item = (new GetList()).getData(1);
                } catch (Exception e) {
                    Log.e("test", e.toString());
                }

            }
        }).start();
        SimpleAdapter adapter = new SimpleAdapter(this,list_item,R.layout.vlist,
        new String[]{"title","times","img", "dateline"},
        new int[]{R.id.title,R.id.times, R.id.img,R.id.dateline});
        setListAdapter(adapter);
    }
    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("title", "信一解说：神龙落地斩！雷切狮子狗教学。");
        map.put("times", "google 2");
        map.put("dateline", "2012-12-1");
        map.put("img", R.drawable.l1);
        list.add(map);
        map = new HashMap<>();
        map.put("title", "G2");
        map.put("times", "google 2");
        map.put("dateline", "2012-12-1");
        map.put("img", R.drawable.l2);
        list.add(map);
        map = new HashMap<>();
        map.put("title", "G3");
        map.put("times", "google 2");
        map.put("dateline", "2012-12-1");
        map.put("img", R.drawable.l3);
       list.add(map);

       return list;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Toast.makeText(MainActivity.this, "You click: " + position, Toast.LENGTH_SHORT).show();
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent();
        //或者是像下面这样的写,这里是用来了意图（intent）里面的标准action这里面还有许多的标准意图可以使用
        //   intent.setAction("android.intent.action.VIEW"); //这里面的意图可以自已写也可以像下面一句这样直接用Intent里面的常量
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://www.baidu.com"));//设置一个URI地址
        MainActivity.this.startActivity(intent);//用startActivity打开这个指定的网页。
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
}
