package com.android.baidudemo.cloud;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.android.baidudemo.cloud.CloudSearchActivity;
import com.android.baidudemo.R;

public class CloudSearchDemo extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_search_demo);
    }

    public void startCloudSearchDemo(View view) {
        Intent intent = new Intent();
        intent.setClass(this, CloudSearchActivity.class);
        startActivity(intent);

    }
}
