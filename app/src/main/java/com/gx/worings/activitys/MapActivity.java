package com.gx.worings.activitys;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.gx.worings.R;
import com.gx.worings.Util.MySqlUtil;
import com.gx.worings.Util.PimDao;

import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;


import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


public class MapActivity extends BaseActivity implements OnClickListener, LocationSource, AMapLocationListener {

    private final String TAG = "MapActivity";
    public static final int REFRESH_DELAY = 4000;

    private MapView mapView;
    private AMap aMap;
    private RadioButton radioButton_bz;
    private RadioButton radioButton_wx;
    private CheckBox checkBox_Traffic;
    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;
    private LocationSource.OnLocationChangedListener mListener = null;

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
        findViews();
        setListeners();

        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap.setTrafficEnabled(true);
        //MyLocationStyle myLocationStyle;
        //myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        //myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        //aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        //aMap.setMyLocationEnabled(true);
        //myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);//只定位一次。
        //myLocationStyle.showMyLocation(true);//设置是否显示定位小蓝点，用于满足只想使用定位，不想使用定位小蓝点的场景，设置false以后图面上不再有定位蓝点的概念，但是会持续回调位置信息。
        new updatePoint().execute();
    }

    @Override
    public void findViews() {
        radioButton_bz = findViewById(R.id.radioButton_bz);
        radioButton_wx = findViewById(R.id.radioButton_wx);
        checkBox_Traffic = findViewById(R.id.checkBox_Traffic);
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
    }

    class updatePoint extends AsyncTask<Object, String, Object> {
        @Override
        protected Object doInBackground(Object... objects) {
            try {
                return PimDao.selectOne("t_location", "1 = 1", "ORDER BY time desc LIMIT 0,1");
            } catch (Exception e) {
                Log.e("eeeeeeeeeeeeeee", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Object s) {
            LatLng latLng = null;
            if (s != null) {
                Map<String, String> params = (Map<String, String>) s;
                if (params.size() > 0) {
                    try {
                        Log.d("dasdas", params.toString());
                        Double lat = Double.valueOf(params.get("lat"));   // 这里的jcourse得到的数据就是huangt-test.
                        Double lng = Double.valueOf(params.get("lng"));
                        latLng = new LatLng(lat, lng);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    for (int i = 0; i<array.length();i++){
//                    }
                }
                aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                // 将地图移动到定位点
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
                Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title("YY").snippet("DefaultMarker"));
            }
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        Log.i(TAG, "onPointerCaptureChanged----hasCapture:" + hasCapture);
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        Log.i(TAG, "activate()");
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {

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

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

// 解析AMapLocation对象
        // 判断AMapLocation对象不为空，当定位错误码类型为0时定位成功
        if (aMapLocation != null) {
            Log.i(TAG, "onLocationChanged()--aMapLocation.getErrorCode():" + aMapLocation.getErrorCode());
            if (aMapLocation.getErrorCode() == 0) {
                int locationType = aMapLocation.getLocationType(); // 获取当前定位结果来源，如网络定位结果，详见定位类型表
                double latitude = aMapLocation.getLatitude(); // 获取纬度
                double longitude = aMapLocation.getLongitude(); // 获取经度
                float accuracy = aMapLocation.getAccuracy(); // 获取精度信息
                String address = aMapLocation.getAddress(); // 地址，如果option中设置isNeedAddress为false，则没有此结果，
                // 网络定位结果中会有地址信息，GPS定位不返回地址信息。
                String country = aMapLocation.getCountry(); // 国家信息
                String province = aMapLocation.getProvince(); // 省信息
                String city = aMapLocation.getCity(); // 城市信息
                String district = aMapLocation.getDistrict(); // 城区信息
                String street = aMapLocation.getStreet(); // 街道信息
                String streetNum = aMapLocation.getStreetNum(); // 街道门牌号信息
                String cityCode = aMapLocation.getCityCode(); // 城市编码
                String adCode = aMapLocation.getAdCode(); // 地区编码
                String aoiName = aMapLocation.getAoiName(); // 获取当前定位点的AOI信息
                String buildingId = aMapLocation.getBuildingId(); // 获取当前室内定位的建筑物Id
                String floor = aMapLocation.getFloor(); // 获取当前室内定位的楼层
                int gpsAccuracyStatus = aMapLocation.getGpsAccuracyStatus(); //获取GPS的当前状态
                // 获取定位时间
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(aMapLocation.getTime());
                df.format(date);
                // 设置缩放级别
                aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                // 将地图移动到定位点
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                // 点击定位按钮 能够将地图的中心移动到定位点
                mListener.onLocationChanged(aMapLocation);

            } else {
                // 定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("HLQ_Struggle", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    @Override
    public void onClick(View v) {

    }
}
