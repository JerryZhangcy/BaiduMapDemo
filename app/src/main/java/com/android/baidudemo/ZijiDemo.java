package com.android.baidudemo;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ZoomControls;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class ZijiDemo extends Activity implements SensorEventListener, View.OnClickListener {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private ImageView mModeImage;

    private SensorManager mSensorManager;
    private LocationClient mLocClient;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    private MyLocationData locData;
    boolean isFirstLoc = true; // 是否首次定位
    private Double lastX = 0.0;
    private boolean mIsMoved = false;

    private MyBDAbstractLocationListener myListener = new MyBDAbstractLocationListener();
    private MyLocationConfiguration.LocationMode mCurrentMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zijimap_activity);
        mMapView = findViewById(R.id.bmapView);
        mMapView.showZoomControls(true);
        mModeImage = findViewById(R.id.mode_img);
        mModeImage.setOnClickListener(this);

        //mMapView.getChildAt(2).setPadding(0, 0, 100, 100);//这是控制缩放控件的位置
        //mMapView.getChildAt(1).setPadding(0, 0, 100, 100);//这是控制logo的位置
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
        mBaiduMap = mMapView.getMap();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//获取传感器管理服务
        mCurrentMode = LocationMode.FOLLOWING;
        mModeImage.setImageDrawable(getResources().getDrawable(R.drawable.main_icon_follow));
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                mCurrentMode, true, null));

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();

        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                Log.e("fuck", "------->motionEvent = " + motionEvent.getAction());
                mModeImage.setImageDrawable(getResources().getDrawable(R.drawable.nsdk_ipo_location_car_point));
                mIsMoved = true;
            }
        });
    }


    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStop() {
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double x = event.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mModeImage.getId()) {
            if (mIsMoved) {
                mIsMoved = false;
                mCurrentMode = LocationMode.FOLLOWING;
                mModeImage.setImageDrawable(getResources().getDrawable(R.drawable.main_icon_follow));
                mBaiduMap
                        .setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentMode, true, null));
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.overlook(0);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            } else {
                switch (mCurrentMode) {
                    case COMPASS:
                        mCurrentMode = LocationMode.FOLLOWING;
                        mModeImage.setImageDrawable(getResources().getDrawable(R.drawable.main_icon_follow));
                        mBaiduMap
                                .setMyLocationConfiguration(new MyLocationConfiguration(
                                        mCurrentMode, true, null));
                        MapStatus.Builder builder = new MapStatus.Builder();
                        builder.overlook(0);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                        break;
                    case FOLLOWING:
                        mCurrentMode = LocationMode.COMPASS;
                        mModeImage.setImageDrawable(getResources().getDrawable(R.drawable.main_icon_compass));
                        mBaiduMap
                                .setMyLocationConfiguration(new MyLocationConfiguration(
                                        mCurrentMode, true, null));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 定位SDK监听函数
     */
    public class MyBDAbstractLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            if (!mIsMoved)
                mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }
    }
}
