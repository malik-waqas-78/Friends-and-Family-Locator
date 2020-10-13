package com.example.home_pc.tracker;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyService extends Service {
    public static boolean IsRunning=false;
    DatabaseReference dbr;
    TrackMyLocation trackLocation;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
        IsRunning=true;
        dbr= FirebaseDatabase.getInstance().getReference();

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        GlobalInfo globalInfo= new GlobalInfo(this);
        globalInfo.loadData();
        trackLocation = new TrackMyLocation();
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, trackLocation);

        dbr.child("Users").child(GlobalInfo.UserPhoneNumber).
                child("Updates").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (TrackMyLocation.location==null)return;
                dbr.child("Users").
                        child(GlobalInfo.UserPhoneNumber).child("Location").child("lat")
                        .setValue( TrackMyLocation.location.getLatitude());

                dbr.child("Users").
                        child(GlobalInfo.UserPhoneNumber).child("Location").child("lag")
                        .setValue( TrackMyLocation.location.getLongitude());

                DateFormat df= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date= new Date();
                dbr.child("Users").
                        child(GlobalInfo.UserPhoneNumber).child("Location").
                        child("LastonlineDate")
                        .setValue(df.format(date).toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return START_NOT_STICKY;
    }



    }





