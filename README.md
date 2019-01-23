# MyBaiduMap  百度地图合成控件
  ## 实现功能：
  1、百度地图的展示
  2、定位
  3、地图上移动当前定位
  4、获取新移动定位的地址
   

## XML文件里面添加MyMapView控件
```
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <com.example.mybaidumap.util.MyMapView
        android:id="@+id/bmapView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

</RelativeLayout>
```


## 在当前Activity里面调用下面的方法


```
private MyMapView myMapView = null;
    /**
     * 编译经纬度为详细地址
     */
    GeoCoder getGeoCoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取地图控件引用
        myMapView = (MyMapView) findViewById(R.id.bmapView);
        initView();
    }

    protected void initView() {
        try {
            //注册监听函数
            initViewMap();
            myMapView.startLocationClient();

            myMapView.setMyMapViewClickListener(new MyMapView.MyMapLocationListener() {
                @Override
                public void updateLocationView(BDLocation bdLocation) {
                    //定位完成，开始重新绘制目标点
                    myMapView.setMapViewAddress(bdLocation.getLatitude(), bdLocation.getLongitude());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "界面加载失败", Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }

    private void initViewMap() {
        getGeoCoder = GeoCoder.newInstance();
        //设置地址或经纬度反编译后的监听,这里有两个回调方法,
        getGeoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            //经纬度转换成地址
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(MainActivity.this, "找不到该地址!", Toast.LENGTH_SHORT).show();

                }
                Toast.makeText(MainActivity.this, "地址：" + result.getAddress(), Toast.LENGTH_SHORT).show();
            }

            //把地址转换成经纬度
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
            }
        });

        //设置单击事件
        myMapView.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            /**
             * 地图单击事件回调函数
             * @param point 点击的地理坐标
             */
            @Override
            public void onMapClick(LatLng point) {
                if (myMapView.getBdLocation() != null) {
                    //得到改变的经纬度
                    myMapView.updateLocationView(point.latitude, point.longitude);
                    // 设置反地理经纬度坐标,请求位置时,需要一个经纬度
                    getGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(point));
                } else {
                    Toast.makeText(MainActivity.this, "无法获取当前定位信息，请打开定位服务", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        myMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        myMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        myMapView.onDestoryView();
    }
 ```
