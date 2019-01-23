package com.example.mymapview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;


/**
 * 自定义组合百度地图控件
 *
 * @author Admin
 */
public class MyMapView extends RelativeLayout {

    private static final String TAG = "MyMapView：";
    private Context mContext;


    /**
     * 控件
     */
    private MapView mMapView;
    private ImageView mMapLocationIcon;

    /**
     * 百度地图相关类
     */
    private LocationClient mLocationClient = null;
    private BaiduMap mBaiduMap;
    private BDLocation bdLocation;
    MyLocationListener myLocationListener;


    /**
     * 判断是否是错入日志信息
     */
    private Boolean isShowMapError = false;

    /**
     * 判断是否是第一次进入当前界面
     */
    private boolean isFirstEntry = true;


    /**
     * 暴露的外部接口
     */
    MyMapLocationListener mapLocationListener;

    public MyMapView(Context context) {
        super(context);
        initView(context);
    }

    public MyMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }


    /**
     * 初始化地图
     */
    public void initView(Context context) {

        mContext = context;

        View view = LayoutInflater.from(context).inflate(R.layout.my_map_view, null);
        mMapView = view.findViewById(R.id.mapView);
        mMapLocationIcon = view.findViewById(R.id.img_mymap_registration);


        //给定位按钮注册点击事件
        mMapLocationIcon.setOnClickListener(new MapViewClickListener());


        myLocationListener = new MyLocationListener();


        mLocationClient = new LocationClient(mContext.getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myLocationListener);


        mLocationClient.setLocOption(BaiDuMapInitUtil.getLocationClientOption());
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明

        mBaiduMap = mMapView.getMap();

        this.addView(view);
    }


    public void setOnMapClickListener(BaiduMap.OnMapClickListener listener) {
        mBaiduMap.setOnMapClickListener(listener);
    }


    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //获取纬度信息
            double latitude = location.getLatitude();
            //获取经度信息
            double longitude = location.getLongitude();
            //获取定位精度，默认值为0.0f
            float radius = location.getRadius();
            String coorType = location.getCoorType();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            int errorCode = location.getLocType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明 经度
            Log.e("返回的信息", "维度：" + latitude + "\n经度："
                    + longitude + "\n定位经度：" + radius + "\n坐标：" + coorType + "\n定位类型："
                    + errorCode + "\n地址：" + location.getAddrStr());


            Log.e("是否是国外：", "" + (location.getLocationWhere() != BDLocation.LOCATION_WHERE_IN_CN));
            //BDLocation.getLocationWhere()方法可获得当前定位点是否是国内，它的取值及含义如下：
            //BDLocation.LOCATION_WHERE_IN_CN：当前定位点在国内；
            //BDLocation.LOCATION_WHERE_OUT_CN：当前定位点在海外；

            if (location.getLocationWhere() != BDLocation.LOCATION_WHERE_IN_CN) {
                //定位失败会优先调用onLocDiagnosticMessage方法，该方法已经提示了错误信息了，所以就不用再提示
                if (isShowMapError) {
                    Toast.makeText(mContext, "定位失败，请打开定位服务", Toast.LENGTH_SHORT).show();
                }
                mLocationClient.stop();
                bdLocation = null;
            } else {
                bdLocation = location;
                setMapViewAddress(location.getLatitude(), location.getLongitude());
            }

        }

        @Override
        public void onLocDiagnosticMessage(int result, int type, String s) {
            super.onLocDiagnosticMessage(result, type, s);
            MapErrorUtil.showMapError((Activity) mContext, result, type);
            //由于该方法是先于定位执行，所以在弹出提示之后就禁止在弹出错误信息
            isShowMapError = false;
        }
    }

    public void setMapViewAddress(Double latitude, Double longitude) {
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        // 构造定位数据
        // 此处设置开发者获取到的方向信息，顺时针0-360
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(0)
                .direction(0).latitude(latitude)
                .longitude(longitude).build();

        // 设置定位数据
        mBaiduMap.setMyLocationData(locData);

        // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,
                true, BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding));
        mBaiduMap.setMyLocationConfiguration(config);

        //切换地图到当前定位显示个人位置图标
        LatLng ll = new LatLng(latitude, longitude);
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
        // 移动到某经纬度
        mBaiduMap.animateMapStatus(update);

        //第一次进入进行缩放比例，后面改变位置缩放比例不变
        if (isFirstEntry) {
            update = MapStatusUpdateFactory.zoomBy(5f);
            //放大
            mBaiduMap.animateMapStatus(update);
            isFirstEntry = false;
            if (mapLocationListener != null) {
                this.mapLocationListener.updateLocationView(bdLocation);
            }
        }
    }


    public void updateLocationView(Double latitude, Double longitude) {
        Log.e(TAG, "地址：" + latitude + "--" + longitude);
        bdLocation.setLatitude(latitude);
        bdLocation.setLongitude(longitude);
        setMapViewAddress(latitude, longitude);
    }


    private class MapViewClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.img_mymap_registration) {
                try {
                    startLocationClient();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "权限不足，请打开权限", Toast.LENGTH_SHORT).show();
                }

            } else {

            }
        }
    }

    /**
     * 启动定位
     */
    public void startLocationClient() {
        Toast.makeText(mContext, "正在定位中...", Toast.LENGTH_LONG).show();
        if (mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
        mLocationClient.start();
    }

    public void onResume() {
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    public void onPause() {

        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
        mLocationClient.stop();
    }


    public void onDestoryView() {
        // 退出时销毁定位
        mLocationClient.unRegisterLocationListener(myLocationListener);
        mLocationClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        if (mMapView != null) {
            mMapView.onDestroy();
            mMapView = null;
        }
    }


    /**
     * 给外界的接口
     */
    public interface MyMapLocationListener {
        void updateLocationView(BDLocation bdLocation);
    }

    public void setMyMapViewClickListener(MyMapLocationListener listener) {
        this.mapLocationListener = listener;
    }


    public MapView getmMapView() {
        return mMapView;
    }

    public void setmMapView(MapView mMapView) {
        this.mMapView = mMapView;
    }

    public BaiduMap getmBaiduMap() {
        return mBaiduMap;
    }

    public BDLocation getBdLocation() {
        return bdLocation;
    }


    /**
     * 取消定位,关闭定位图标
     */
    public void closeLocation() {
        mMapLocationIcon.setVisibility(GONE);
    }


    /**
     * 关闭百度地图的缩放按钮，所有手势
     */
    public void closeMapGesture() {
        closeZoomControls();
        closeLocation();
        //禁止所有手势操作
        UiSettings uiSettings = mBaiduMap.getUiSettings();
        uiSettings.setAllGesturesEnabled(false);
    }


    /**
     * 隐藏缩放按钮
     */
    public void closeZoomControls() {

        mMapView.showZoomControls(false);
    }


}
