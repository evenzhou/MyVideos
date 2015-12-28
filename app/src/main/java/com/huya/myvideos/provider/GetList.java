package com.huya.myvideos.provider;

import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.huya.myvideos.UILApplication;

import org.json.JSONArray;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by even on 2015-12-24.
 */
public class GetList {
    private final String TAG = "GetList";
    public final String apiUrl = "http://api.v.huya.com/index.php?r=video/list&channelId=lol&appKey=hyapi_cs";
    public int pageSize = 20;

    public List<Map<String, String>> getData(int page) throws Exception {
        final JSONArray jsonArray;
        page = page > 0 ? page : 1;
        String json = null;
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map = null;
        String location = apiUrl + "&page=" + page + "&pageSize=" + pageSize;
        //用volley请求数据玩玩
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(location,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "onResponse: "+response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        //添加
        UILApplication.getInstance().addToRequestQueue(jsonArrayRequest);

      /*  URL url = new URL(location);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();// 利用HttpURLConnection对象,我们可以从网络中获取网页数据.
        conn.setConnectTimeout(5 * 1000);   // 单位是毫秒，设置超时时间为5秒
        conn.setRequestMethod("GET");       // HttpURLConnection是通过HTTP协议请求path路径的，所以需要设置请求方式,可以不设置，因为默认为GET
        if (conn.getResponseCode() == 200) {// 判断请求码是否是200码，否则失败
            InputStream is = conn.getInputStream(); // 获取输入流
            byte[] data = readStream(is);   // 把输入流转换成字符数组
            json = new String(data);        // 把字符数组转换成字符串

            JSONArray jsonArray; //数据直接为一个数组形式，所以可以直接 用android提供的框架JSONArray读取JSON数据，转换成Array
            jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i); //每条记录又由几个Object对象组成
                map = new HashMap<String, String>(); // 存放到MAP里面

                map.put("id", item.getString("vid"));
                map.put("title", item.getString("video_title"));
                map.put("image", item.getString("cover"));
                map.put("dateline", item.getString("upload_start_time"));
                map.put("times", item.getString("play_sum"));
                list.add(map);
            }
        }*/
        return list;
    }


    /**
     * 把输入流转换成字符数组
     *
     * @param inputStream 输入流
     * @return 字符数组
     * @throws Exception
     */
    public static byte[] readStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            bout.write(buffer, 0, len);
        }
        bout.close();
        inputStream.close();

        return bout.toByteArray();
    }
}
