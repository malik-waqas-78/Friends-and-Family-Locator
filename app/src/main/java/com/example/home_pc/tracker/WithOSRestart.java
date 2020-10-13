package com.example.home_pc.tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

public class WithOSRestart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equalsIgnoreCase("android.intent.action.BOOT_COMPLETED")){
            GlobalInfo globalInfo = new GlobalInfo(context);
            globalInfo.loadData();
            if (!TrackMyLocation.isRunning) {
                TrackMyLocation trackMyLocation = new TrackMyLocation();
                LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, trackMyLocation);
            }
            if (!MyService.IsRunning) {
                Intent intent1 = new Intent(context, MyService.class);
                context.startService(intent1);
            }


        }
    }
}
