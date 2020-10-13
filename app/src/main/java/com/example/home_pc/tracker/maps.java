package com.example.home_pc.tracker;


import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class maps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    DatabaseReference databaseReference;
    MyThreed t;
    String lastonlineDate;
    Bundle bundle;
    boolean isRunning=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        bundle=getIntent().getExtras();
        t=new MyThreed();
        isRunning=true;
        loadMap();

    }
  public void loadLocation(String phone){

     // Toast.makeText(this, ""+phone, Toast.LENGTH_SHORT).show();
        databaseReference.child("Users").child(phone).child("Location")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String,Object> map=(HashMap<String, Object>)dataSnapshot.getValue();
                        if (map==null) return;
                        double lat= Double.parseDouble(map.get("lat").toString());
                        double loong=Double.parseDouble(map.get("long").toString());
                        lastonlineDate=map.get("LastonLineDate").toString();
                       if(mMap!=null) mMap.clear();
                       LatLng sydney=new LatLng(lat,loong);
                        mMap.addMarker(new MarkerOptions().position(sydney).title("Last on Line Date "+lastonlineDate));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15));
                        //loadMap();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

  }
  public void loadMap(){
      // Obtain the SupportMapFragment and get notified when the map is ready to be used.
      try {
      SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
              .findFragmentById(R.id.map);

          mapFragment.getMapAsync(this);
      }catch(Exception e){
         // finish();
          //alert();
      }
  }
public void alert(){
    AlertDialog.Builder alert=new AlertDialog.Builder(this);
    alert.setTitle("Message").
            setMessage("No Internet Connection.").
            setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            })
            .show();
}

    @Override
    protected void onStop() {
        super.onStop();
        isRunning=false;
        Toast.makeText(this, "Stoped", Toast.LENGTH_SHORT).show();
       finish();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);

        loadLocation(bundle.getString("PhoneNumber"));
        // Add a marker in Sydney and move the camera
//        LatLng sydney =new LatLng(33.8688,151.2093);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Last on Line Date "+lastonlineDate));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15));
        Toast.makeText(maps.this, "threed started", Toast.LENGTH_SHORT).show();
        t.start();
    }
    class MyThreed extends Thread {
        @Override
        public void run() {
            super.run();
            while(isRunning){

                GlobalInfo.updateInfo(bundle.getString("PhoneNumber"));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadLocation(bundle.getString("PhoneNumber"));

                    }
                });

                try{
                    Thread.sleep(3000);
                }catch(Exception e){
                    // alert();
                }
            }
        }
    }
}
