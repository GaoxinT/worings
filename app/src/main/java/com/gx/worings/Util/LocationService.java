package com.gx.worings.Util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class LocationService extends Service {
    @Nullable

    public void onCreate(){
        super.onCreate();
        Log.d("AAAAAAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAAAAAAAAAA");

    }

    @Override
    public IBinder onBind(Intent intent){
        Log.d("BBBBBBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBBBBBBBBBB");
        //Toast.makeText(getApplicationContext(), "不默认Toast样式", Toast.LENGTH_LONG).show();
        try {
            LocationUtil location = new LocationUtil(this);
            location.getLngLatByGD();
        }catch (Exception es){
            Log.d("AAAAAAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAAAAAAAAAA");
        }

        return null;
    }
}
