package com.android.baidudemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

public class BaiduMapMainActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }

    public void enteyBaiduDemo(View v) {
        Intent i = new Intent();
        i.setClass(this, BMapApiDemoMain.class);
        startActivity(i);
    }

    public void enteyZijiDemo(View v) {
        Intent i = new Intent();
        i.setClass(this, ZijiDemo.class);
        startActivity(i);
    }
}
