package com.example.home_pc.tracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

public class MyTrackers extends AppCompatActivity {
    ArrayList<AdapterItems> rows=new ArrayList<>();
    CustomizedAdapter adapter;
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trackers);
        //
        setTitle(GlobalInfo.UserPhoneNumber);
        list=findViewById(R.id.listView);
        int Layout=R.layout.single_row_conact;
        //rows.add(new AdapterItems("waqas","03115969574"));
        adapter=new CustomizedAdapter(this,Layout,rows);
        list.setAdapter(adapter);
        refresh();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GlobalInfo.myTrackers.remove(rows.get(position).PhoneNumber);
                DatabaseReference mref=FirebaseDatabase.getInstance().getReference();
                mref.child("Users").child(rows.get(position).PhoneNumber).child("finders").
                        child(GlobalInfo.UserPhoneNumber).removeValue();

                refresh();
            }
        });
    }
    public void refresh(){
        rows.clear();
        for(Map.Entry m:GlobalInfo.myTrackers.entrySet()){
            rows.add(new AdapterItems(m.getValue().toString(),m.getKey().toString()));
        }
        GlobalInfo globalInfo=new GlobalInfo(this);
        globalInfo.saveData();
        adapter.notifyDataSetChanged();
    }
    //Permmissions
    public void checkForPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) !=
                    PackageManager.PERMISSION_GRANTED) {
                if(!ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_CONTACTS)){
                    requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS
                            },
                            REQUEST_CODE_FOR_PERMISSION_OF_LOCATION_ACCESS);
                    return;
                }

            }


        }

        //check=true;
       pickContact();
    }
    final private int REQUEST_CODE_FOR_PERMISSION_OF_LOCATION_ACCESS = 123;
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,  int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_FOR_PERMISSION_OF_LOCATION_ACCESS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickContact();
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
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_contact_list,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.add:
                checkForPermission();
                return true;
            case R.id.goback:
                GlobalInfo globalInfo=new GlobalInfo(this);
                globalInfo.saveData();
                finish();
                return true;
            default:
                return  super.onOptionsItemSelected(item);

        }
    }
    //
    public void pickContact(){
        Intent intent=new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent,REQ_CODE);
    }
    public static final int REQ_CODE=1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case REQ_CODE:
                if(resultCode== Activity.RESULT_OK){
                    Uri contactData=data.getData();
                    Cursor contactDetails=getContentResolver().query(contactData,null,null,null,null);
                    if(contactDetails.moveToFirst()){
                        String id=contactDetails.getString(contactDetails.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String hasNumber=contactDetails.getString(contactDetails.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        String cNumber="";
                        if(hasNumber.equalsIgnoreCase("1")){
                            Cursor phone=getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = ?",
                                    new String[]{id},
                                    null);
                            if(phone.moveToFirst());
                            cNumber=phone.getString(phone.getColumnIndex("data1"));
                        }
                        String cName=contactDetails.getString(contactDetails.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                        Toast.makeText(this, cName+" = "+cNumber, Toast.LENGTH_SHORT).show();
                        //add to global info
                        GlobalInfo.myTrackers.put(cNumber,cName);
                        //add to Firebasee-database
                        DatabaseReference mref= FirebaseDatabase.getInstance().getReference();
                        mref.child("Users").child(cNumber).child("finders").child(GlobalInfo.UserPhoneNumber).setValue("true");
                        refresh();
                        //add to trakerlist


                    }
                }

        }
    }

    private class CustomizedAdapter extends BaseAdapter{
        int Layout;
        Context context;
        ArrayList<AdapterItems> data;
        LayoutInflater inflater;

        public CustomizedAdapter(Context context,int layout, ArrayList<AdapterItems> data) {
            Layout = layout;
            this.context = context;
            this.data = data;
            inflater=LayoutInflater.from(context);
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
            if(v==null){
                v=inflater.inflate(Layout,null);
                TextView name=v.findViewById(R.id.tv_user_name);
                TextView phno=v.findViewById(R.id.tv_phone);

                AdapterItems item=data.get(position);

                name.setText(item.getUserNmae());
                phno.setText(item.getPhoneNumber());
            }
            return v;
        }
    }
}
