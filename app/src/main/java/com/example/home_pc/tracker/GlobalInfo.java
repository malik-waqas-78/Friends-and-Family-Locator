package com.example.home_pc.tracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GlobalInfo {
    public static String UserPhoneNumber="";
    SharedPreferences file;
    Context context;
    public static Map<String,String> myTrackers=new HashMap<String, String>();
    public GlobalInfo(Context context){
        this.context=context;
        file=context.getSharedPreferences("ref",Context.MODE_PRIVATE);
    }

    public static void updateInfo(String Phone){
        DateFormat df=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date=new Date();
        DatabaseReference mref= FirebaseDatabase.getInstance().getReference();
        if(!Phone.equals("Empty")){
            mref.child("Users").child(Phone).child("Updates").setValue(df.format(date).toString());
        }
    }
    public static String FormatPhoneNumber(String Oldnmber){
        try{
            String numberOnly= Oldnmber.replaceAll("[^0-9]", "");
            if (numberOnly.length()>11)
                numberOnly=numberOnly.substring(numberOnly.length()-11,numberOnly.length());
            if(Oldnmber.charAt(0)=='+') numberOnly="+" +numberOnly ;
            return(numberOnly);
        }
        catch (Exception ex){
            return(" ");
        }
    }


    public void saveData(){
        String MyTrackersList="";
        for(Map.Entry m:GlobalInfo.myTrackers.entrySet()){
            if(MyTrackersList.length()==0){
                MyTrackersList=m.getKey()+"%"+m.getValue();//030377866907%name
            }else{                                              //0          \\1  \\2      //3
                MyTrackersList+="%"+m.getKey()+"%"+m.getValue();//030377866907 name 03879690 nhem
            }
        }
        if(MyTrackersList.length()==0){
            MyTrackersList="Empty";
        }
        SharedPreferences.Editor edit=file.edit();
        edit.putString("MyTrackersList",MyTrackersList);
        edit.putString("UserPhoneNumber",UserPhoneNumber);
        edit.commit();
    }
    public void loadData(){
        myTrackers.clear();
        UserPhoneNumber=file.getString("UserPhoneNumber","Empty");
       String MyTrackersList =file.getString("MyTrackersList","Empty");
        if(!MyTrackersList.equals("Empty")){
            String[] users=MyTrackersList.split("%");
            for(int i=0;i<users.length;i=i+2){
                myTrackers.put(users[i],users[i+1]);
            }
        }
        if(UserPhoneNumber.equals("Empty")){
            Intent intent=new Intent(context,LogIn.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

        return ;
    }
}
