package com.example.home_pc.tracker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    CustomizedAdapter adapter;
    ArrayList<AdapterItems> rows = new ArrayList<>();
    ListView list;
    DatabaseReference dbr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TrackMyLocation.context=this;
        GlobalInfo globalInfo = new GlobalInfo(this);
        globalInfo.loadData();
        setTitle(GlobalInfo.UserPhoneNumber);
        dbr = FirebaseDatabase.getInstance().getReference();
        checkForPermission();
        //
        list = findViewById(R.id.listView);
        int Layout = R.layout.single_row_conact;
        //rows.add(new AdapterItems("waqas","03115969574"));
        adapter = new CustomizedAdapter(this, Layout, rows);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AdapterItems item=rows.get(position);
                GlobalInfo.updateInfo(item.getUserNmae());
                Intent intent=new Intent(MainActivity.this,maps.class);
                intent.putExtra("PhoneNumber",item.UserNmae);
                startActivity(intent);
            }
        });
        list.setAdapter(adapter);
    }
    @Override
    public  void onResume(){
        super.onResume();
        refresh();
u    }

    public void refresh() {
        rows.clear();
        dbr.child("Users").child(GlobalInfo.UserPhoneNumber)
                .child("finders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> amTracking = (HashMap<String, Object>) dataSnapshot.getValue();
                rows.clear();
                if (amTracking == null) {
                    rows.add(new AdapterItems("Empty", "Empty"));
                    adapter.notifyDataSetChanged();
                    return;
                }
                //get all contacts in phone
                ArrayList<AdapterItems> arraylist = new ArrayList<>();
                Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
                while (cursor.moveToNext()) {
                    String username = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phonneumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    arraylist.add(new AdapterItems(username, phonneumber));
                }

                for (String Number : amTracking.keySet()) {
                    for (AdapterItems item : arraylist) {
                        if (item.PhoneNumber.length() > 0) {
                            if (Number.contains(item.PhoneNumber)) {
                                rows.add(new AdapterItems(item.PhoneNumber, item.UserNmae));
                                break;
                            }
                        }
                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        });
        adapter.notifyDataSetChanged();
    }

    public void startServices() {
        if (!TrackMyLocation.isRunning) {
            TrackMyLocation trackMyLocation = new TrackMyLocation();
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, trackMyLocation);
        }
        if (!MyService.IsRunning) {
            Intent intent = new Intent(this, MyService.class);
            startService(intent);
        }
    }

    //Permmissions
    public void checkForPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                            },
                            REQUEST_CODE_FOR_PERMISSION_OF_LOCATION_ACCESS);
                    return;
                }

            }


        }

        //check=true;
        startServices();
    }

    final private int REQUEST_CODE_FOR_PERMISSION_OF_LOCATION_ACCESS = 56;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_FOR_PERMISSION_OF_LOCATION_ACCESS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startServices();
                    //check=true;
                } else {
                    Toast.makeText(this, "You Denied the Permission.", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    //
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:

                return true;
            case R.id.addtracker:
                Intent intent = new Intent(this, MyTrackers.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private class CustomizedAdapter extends BaseAdapter {
        int Layout;
        Context context;
        ArrayList<AdapterItems> data;
        LayoutInflater inflater;

        public CustomizedAdapter(Context context, int layout, ArrayList<AdapterItems> data) {
            Layout = layout;
            this.context = context;
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            if (v == null) {
                AdapterItems item = data.get(position);
                if (item.UserNmae.equals("Empty")) {+

                    v = inflater.inflate(R.layout.news_ticket_no_news, null);
                    return v;
                } else {
                    v = inflater.inflate(Layout, null);
                    TextView phno = v.findViewById(R.id.tv_phone);
                    TextView name = v.findViewById(R.id.tv_user_name);


                    name.setText(item.getUserNmae());
                    phno.setText(item.getPhoneNumber());
                    return v;
                }

            }
            return v;
        }
    }
}
