package com.gx.worings.Util.Amap;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.gx.worings.Util.PimDao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/17.
 * 获取用户的地理位置
 */
public class LocationUtil {

    private static LocationUtil instance;
    private Context mContext;
    private LocationManager locationManager;

    private LocationUtil(Context context) {
        this.mContext = context;
    }

    public static LocationUtil getInstance(Context context) {
        if (instance == null) {
            instance = new LocationUtil(context);
        }
        return instance;
    }

    /**
     * 获取经纬度
     *
     * @return
     */
    public String getLngAndLat() {
        double latitude = 0.0;
        double longitude = 0.0;

        String locationProvider = null;
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);

        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }

        //获取Location
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //Toast.makeText(mContext,"权限获取失败", Toast.LENGTH_SHORT);
            return null;
        }
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            //不为空,显示地理位置经纬度
            Log.d("经度：", location.getLatitude() + "");
            Log.d("纬度：", location.getLongitude() + "");
            if (!"0.0".equals(location.getLatitude())) {
                updataLocation_local u = new updataLocation_local();
                u.execute(location);
            } else {
                Log.e("--------------------", "位置获取失败");
            }

        }
        //监视地理位置变化
        locationManager.requestLocationUpdates(locationProvider, 1000 * 60 * 2, 1, locationListener);
        return null;
    }


    public LocationListener locationListener = new LocationListener() {

        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {

        }

        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {

        }

        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @Override
        public void onLocationChanged(Location location) {
            updataLocation_local u = new updataLocation_local();
            u.execute(location);
        }
    };

    public void removeListener() {
        locationManager.removeUpdates(locationListener);
    }

    class updataLocation_local extends AsyncTask<Object, String, String> {
        @Override
        protected String doInBackground(Object... objects) {
            Location location = (Location) objects[0];
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
            Date date = new Date(System.currentTimeMillis());//获取当前时间
            Map<String, String> params = new HashMap<>();
            try {
                params.put("lat", String.valueOf(location.getLatitude()));
                params.put("lng", String.valueOf(location.getLongitude()));
                params.put("lng_lat", location.getLongitude() + "," + location.getLatitude());
                params.put("address", "");
                params.put("time", simpleDateFormat.format(date));
                params.put("phone", android.os.Build.BRAND + " " + Build.MODEL);
                PimDao.insert("t_location", params);
            } catch (Exception re) {
                return "false";
            }
            return "true";
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("---------------------", s + "位置保存success");
        }
    }

    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            //Toast.makeText(mContext, aMapLocation.toString(),Toast.LENGTH_SHORT).show();
            String location = aMapLocation.getLatitude()+"";
            if (!"0.0".equals(location)) {
                new updataLocation_gd().execute(aMapLocation);
            } else {
                Log.e("--------------------", "位置获取失败");
            }
        }
    };

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;

    public void getLngLatByGD() {
        mLocationClient = new AMapLocationClient(mContext);
        mLocationOption = new AMapLocationClientOption();
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(1000 * 60 * 2);
        //mLocationOption.setInterval(2000 );
        mLocationOption.setNeedAddress(true);
        mLocationOption.setLocationCacheEnable(false);
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    class updataLocation_gd extends AsyncTask<Object, String, String> {
        @Override
        protected String doInBackground(Object... objects) {
            AMapLocation location = (AMapLocation) objects[0];
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
            Date date = new Date(System.currentTimeMillis());//获取当前时间
            Map<String, String> params = new HashMap<>();
            try {
                params.put("lat", String.valueOf(location.getLatitude()));
                params.put("lng", String.valueOf(location.getLongitude()));
                params.put("lng_lat", location.getLongitude() + "," + location.getLatitude());
                params.put("address", location.getAddress());
                params.put("time", simpleDateFormat.format(date));
                params.put("phone", android.os.Build.BRAND + " " + Build.MODEL);
                int result = PimDao.insert("t_location", params);
                if(-1 == result){
                    return "false";
                }
            } catch (Exception re) {
                return "false";
            }
            return "success";
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Toast.makeText(mContext,""+ s , Toast.LENGTH_SHORT).show();
            Log.d("---------------------", "位置保存-------------------------------------" + s);
        }
    }
}