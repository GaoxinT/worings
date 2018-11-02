package com.gx.worings.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gx.worings.Util.Amap.LocationService;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent mBootIntent = new Intent(context, LocationService.class);
        context.startService(mBootIntent);
    }
}
