package com.huya.myvideos;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * Created by even on 2015-12-29.
 */
public class ViewActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //接收参数
        Bundle bundle = new Bundle();
        bundle = this.getIntent().getExtras();
        //启动 indent
        Intent intent = new Intent();
       // intent.setPackage("com.anroid.chrome");
        intent.setAction("android.intent.action.VIEW");
        String vid = bundle.getString("vid");
        String url = "http://m.v.huya.com/play/" + vid + ".html";
        intent.setData(Uri.parse(url));//设置一个URI地址
        startActivity(intent);
        finish();
    }
}
