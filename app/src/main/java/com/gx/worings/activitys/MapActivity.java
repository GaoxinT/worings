package com.gx.worings.activitys;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.CheckBox;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.gx.worings.R;
import com.gx.worings.Util.Amap.LocationUtil;
import com.gx.worings.Util.Amap.WalkRouteOverlay;
import com.gx.worings.Util.PimDao;

import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class MapActivity extends BaseActivity implements  RouteSearch.OnRouteSearchListener {

    private final String TAG = "MapActivity";
    public static final int REFRESH_DELAY = 4000;

    private MapView mapView;
    private ImageView ivCompass;
    private AMap aMap;
    private UiSettings mUiSettings;
    private RadioButton radioButton_bz;
    private RadioButton radioButton_wx;
    private RadioButton radioButton_jc;
    private RadioButton radioButton_gj;
    private RadioButton radioButton_bx;
    private CheckBox checkBox_Traffic;
    private LatLng latLng_YY;
    private LatLng latLng_GX;
    private RouteSearch routeSearch;
    private RotateAnimation rotateAnimation;
    MyLocationStyle myLocationStyle;
    private float lastBearing;
    private Circle c;
    private AMapLocationListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
    }

    @Override
    public void init() {

        setBackVisibity(true);
        setTitleText("Map");
        setOkVisibity(false);
        setOKImg(R.mipmap.ic_menu_moreoverflow_norma);
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        findViews();
        setListeners();
        mListener = LocationUtil.getInstance(getApplicationContext()).mLocationListener;
        aMap.setTrafficEnabled(true);
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);//只定位一次。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.setMyLocationEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setCompassEnabled(true);
        new updatePoint().execute();
    }

    @Override
    public void findViews() {
        radioButton_bz = findViewById(R.id.radioButton_bz);
        radioButton_wx = findViewById(R.id.radioButton_wx);
        checkBox_Traffic = findViewById(R.id.checkBox_Traffic);
        radioButton_jc = findViewById(R.id.radioButton_jc);
        radioButton_gj = findViewById(R.id.radioButton_gj);
        radioButton_bx = findViewById(R.id.radioButton_bx);
        ivCompass = findViewById(R.id.iv_compass);
    }

    @Override
    public void setListeners() {


        getBasetitle_back().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        radioButton_bz.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Boolean isbz = radioButton_bz.isChecked();
                if (isbz) {
                    aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 设置卫星地图模式，aMap是地图控制器对象。
                }
            }
        });

        radioButton_wx.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Boolean iswx = radioButton_wx.isChecked();
                if (iswx) {
                    aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 设置卫星地图模式，aMap是地图控制器对象。
                }
            }
        });

        checkBox_Traffic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Boolean isTraffic = checkBox_Traffic.isChecked();
                if (isTraffic) {
                    aMap.setTrafficEnabled(true);//显示实时路况图层，aMap是地图控制器对象。
                } else {
                    aMap.setTrafficEnabled(false);
                }
            }
        });
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                float bearing = 360 - cameraPosition.bearing;
                rotateAnimation = new RotateAnimation(lastBearing, bearing, 1, 0.5f, 1, 0.5f);
                rotateAnimation.setFillAfter(true);
                ivCompass.startAnimation(rotateAnimation);
                lastBearing = bearing;
            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {

            }
        });
        ivCompass.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraUpdateFactory cameraUpdateFactory = new CameraUpdateFactory();
                aMap.moveCamera(cameraUpdateFactory.changeBearing(360));
            }
        });

        radioButton_jc.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

            }
        });
        radioButton_gj.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

            }
        });
        radioButton_bx.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Boolean is_bx = radioButton_bx.isChecked();
                if (is_bx) {
                    routeSearch = new RouteSearch(getApplicationContext());
                    LatLonPoint latLonPoint_GX = new LatLonPoint(latLng_GX.latitude, latLng_GX.longitude);
                    LatLonPoint latLonPoint_YY = new LatLonPoint(latLng_YY.latitude, latLng_YY.longitude);
                    final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(latLonPoint_GX, latLonPoint_YY);
                    RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo);
                    routeSearch.calculateWalkRouteAsyn(query);
                    routeSearch.setRouteSearchListener(MapActivity.this);
                }
            }
        });
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int i) {
        if (result != null && result.getPaths() != null && result.getPaths().size() > 0) {
            WalkRouteResult walkRouteResult = result;
            WalkPath walkPath = walkRouteResult.getPaths().get(0);
            //aMap.clear();// 清理地图上的所有覆盖物
            WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(aMap, walkPath, walkRouteResult.getStartPos(), walkRouteResult.getTargetPos());
            //walkRouteOverlay.removeFromMap();
            walkRouteOverlay.addToMap();
            //walkRouteOverlay.zoomToSpan();
            Toast.makeText(getApplicationContext(), "路径规划成功！", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "路径规划失败！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    /**
     * 异步刷新
     */
    class updatePoint extends AsyncTask<Object, String, Object> {
        @Override
        protected Object doInBackground(Object... objects) {
            try {
                return PimDao.selectOne("t_location", "1 = 1 and id = '113'", "ORDER BY time desc LIMIT 0,1");
            } catch (Exception e) {
                Log.e("eeeeeeeeeeeeeee", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Object s) {
            if (s != null) {
                Map<String, String> params = (Map<String, String>) s;
                if (params.size() > 0) {
                    try {
                        Log.d("dasdas", params.toString());
                        Double lat = Double.valueOf(params.get("lat"));   // 这里的jcourse得到的数据就是huangt-test.
                        Double lng = Double.valueOf(params.get("lng"));
                        latLng_YY = new LatLng(lat, lng);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //获取当前位置
                AMapLocation aMapLocation = LocationUtil.getInstance(getApplicationContext()).mLocationClient.getLastKnownLocation();
                latLng_GX = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                drawPointLine(latLng_YY, latLng_GX);//绘制两点及连线
            }
        }

        /**
         * 画出两点
         *
         * @param latLng_YY
         * @param latLng_GX
         */
        public void drawPointLine(LatLng latLng_YY, LatLng latLng_GX) {
            List<LatLng> latLngs = new ArrayList<LatLng>();
            latLngs.add(latLng_YY);
            latLngs.add(latLng_GX);
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            boundsBuilder.include(latLng_YY);
            boundsBuilder.include(latLng_GX);
            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 300));
            MarkerOptions options = new MarkerOptions();
            options.position(latLng_GX);
            Marker marker = aMap.addMarker(options);
            options.position(latLng_YY);
            Marker marker1 = aMap.addMarker(options);
            com.amap.api.maps.model.animation.Animation markerAnimation = new ScaleAnimation(0, 1, 0, 1); //初始化生长效果动画
            markerAnimation.setDuration(1000);  //设置动画时间 单位毫秒
            marker.setAnimation(markerAnimation);
            marker.setTitle("YY");
            marker.startAnimation();
            marker1.setAnimation(markerAnimation);
            marker1.setTitle("GX");
            marker1.startAnimation();
            aMap.addPolyline(new PolylineOptions().addAll(latLngs).width(10).color(Color.RED));
        }

    }

    private long start;
    private circleTask mTimerTask;
    private Timer mTimer = new Timer();
    private final Interpolator interpolator1 = new LinearInterpolator();

    //加载精度圈动画
    public void Scalecircle(final Circle circle) {
        start = SystemClock.uptimeMillis();
        mTimerTask = new circleTask(circle, 1000);
        mTimer.schedule(mTimerTask, 0, 30);
    }

    //定位精度圈半径变化的定时器
    private  class circleTask extends TimerTask {
        private double r;
        private Circle circle;
        private long duration = 1000;

        public circleTask(Circle circle, long rate){
            this.circle = circle;
            this.r = circle.getRadius();
            if (rate > 0 ) {
                this.duration = rate;
            }
        }
        @Override
        public void run() {
            try {
                long elapsed = SystemClock.uptimeMillis() - start;
                float input = (float)elapsed / duration;
                //外圈放大后消失
                float t = interpolator1.getInterpolation(input);
                double r1 = (t + 1) * r;
                circle.setRadius(r1);
                if (input > 2){
                    start = SystemClock.uptimeMillis();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        Log.i(TAG, "onPointerCaptureChanged----hasCapture:" + hasCapture);
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    public void onLocationChanged(AMapLocation aMapLocation) {

        // 判断AMapLocation对象不为空，当定位错误码类型为0时定位成功
        if (aMapLocation != null) {
            Log.i(TAG, "onLocationChanged()--aMapLocation.getErrorCode():" + aMapLocation.getErrorCode());
            if (aMapLocation.getErrorCode() == 0) {
                int locationType = aMapLocation.getLocationType(); // 获取当前定位结果来源，如网络定位结果，详见定位类型表 double latitude = aMapLocation.getLatitude(); // 获取纬度      double longitude = aMapLocation.getLongitude(); // 获取经度             float accuracy = aMapLocation.getAccuracy(); // 获取精度信息                String address = aMapLocation.getAddress(); // 地址，如果option中设置isNeedAddress为false，则没有此结果，                // 网络定位结果中会有地址信息，GPS定位不返回地址信息。                String country = aMapLocation.getCountry(); // 国家信息                String province = aMapLocation.getProvince(); // 省信息                String city = aMapLocation.getCity(); // 城市信息                String district = aMapLocation.getDistrict(); // 城区信息                String street = aMapLocation.getStreet(); // 街道信息                String streetNum = aMapLocation.getStreetNum(); // 街道门牌号信息                String cityCode = aMapLocation.getCityCode(); // 城市编码                String adCode = aMapLocation.getAdCode(); // 地区编码                String aoiName = aMapLocation.getAoiName(); // 获取当前定位点的AOI信息                String buildingId = aMapLocation.getBuildingId(); // 获取当前室内定位的建筑物Id                String floor = aMapLocation.getFloor(); // 获取当前室内定位的楼层                int gpsAccuracyStatus = aMapLocation.getGpsAccuracyStatus(); //获取GPS的当前状态                // 获取定位时间
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(aMapLocation.getTime());
                df.format(date);
                // 将地图移动到定位点
                //aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                // 点击定位按钮 能够将地图的中心移动到定位点
                //mListener.onLocationChanged(aMapLocation);
                LatLng mylocation = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                float accuracy = aMapLocation.getAccuracy();
                c = aMap.addCircle(new CircleOptions().center(mylocation)
                        .fillColor(Color.argb(70, 255, 218, 185))
                        .radius(accuracy).strokeColor(Color.argb(255, 255, 228, 185))
                        .strokeWidth(0));
                Scalecircle(c);
            } else {
                // 定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("HLQ_Struggle", "location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
            }
        }
    }
}
