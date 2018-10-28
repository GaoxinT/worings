package com.gx.worings.Util;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/4/17.
 * 获取用户的地理位置
 */
public class LocationUtil {

    private static LocationUtil instance;
    private Context mContext;
    private LocationManager locationManager;
    public LocationUtil(Context context) {
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
            Toast.makeText(mContext, location.getLatitude()+ "" + location.getLongitude(), Toast.LENGTH_SHORT).show();
            System.out.print(location.getLatitude()+ "" + location.getLongitude());
            Log.d("经度：", location.getLatitude() + "");
            Log.d("纬度：", location.getLongitude() + "");
            updataLocation_local u = new updataLocation_local();
            u.execute(location);

        }
        //监视地理位置变化
        locationManager.requestLocationUpdates(locationProvider, 1000 * 60 * 5, 1, locationListener);
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

            Toast.makeText(mContext, location.getLatitude()+ "" + location.getLongitude(), Toast.LENGTH_SHORT).show();
            Log.d("经度：", location.getLatitude() + "");
            Log.d("纬度：", location.getLongitude() + "");
            System.out.print(location.getLatitude()+ "" + location.getLongitude());
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
            try{
                double lat = location.getLatitude();
                double longs = location.getLongitude();
                String time = simpleDateFormat.format(date);
                String phone = android.os.Build.BRAND + Build.MODEL;
                String sql = "insert into t_location VALUES('"+ phone +"','"+
                        lat  +"','"+ longs +"','"+ longs + "," + lat +"','"+ time +"')";
                MySqlUtil.execSQL(sql);
            }catch (Exception re){
                return "false";
            }
            return "true";
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(mContext,""+ s , Toast.LENGTH_SHORT).show();
        }
    }
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            //Toast.makeText(mContext, aMapLocation.toString(),Toast.LENGTH_SHORT).show();
            new updataLocation_gd().execute(aMapLocation);
        }
    };
    public AMapLocationClientOption mLocationOption = null;
    public void getLngLatByGD(){
        mLocationClient = new AMapLocationClient(mContext);
        mLocationOption = new AMapLocationClientOption();
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(1000 * 60 * 5);
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
            String sql = null;
            try{
                double lat = location.getLatitude();
                double longs = location.getLongitude();
                String address = location.getAddress();
                String time = simpleDateFormat.format(date);
                String phone = android.os.Build.BRAND + Build.MODEL;
                sql = "insert into t_location(phone,lat,lng,long_lat,time,address) VALUES('"+ phone +"','"+
                        lat  +"','"+ longs +"','"+ longs + "," + lat +"','"+ time +"','"+ address +"')";
                MySqlUtil.execSQL(sql);
            }catch (Exception re){
                return "false";
            }
            return sql;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Toast.makeText(mContext,""+ s , Toast.LENGTH_SHORT).show();
            Log.d("---------------------",s + "----success");
        }
    }
}