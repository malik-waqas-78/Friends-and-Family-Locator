package com.example.home_pc.tracker;


import android.app.NotificationManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;

import android.os.Bundle;
import android.support.v4.app.NotificationCompat;


public class TrackMyLocation implements LocationListener {
    public static Location location;
    public static  Context context;
    public static Boolean isRunning=false;
    public TrackMyLocation(){
        isRunning=true;
        location=new Location("not defined");
        location.setLatitude(0);
        location.setLongitude(0);
    }
    @Override
    public void onLocationChanged(Location location) {
        TrackMyLocation.location=location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        NotificationCompat.Builder notification=new NotificationCompat.Builder(context);
        notification.setSmallIcon(R.drawable.cover).
        setContentText("Turn on your GPS.").
                setContentTitle("TRACKER");
        NotificationManager nm=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1,notification.build());
    }
}
