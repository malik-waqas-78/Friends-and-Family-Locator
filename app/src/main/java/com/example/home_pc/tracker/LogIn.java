package com.example.home_pc.tracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class LogIn extends AppCompatActivity {
    EditText EDTNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        EDTNumber=findViewById(R.id.EDTNumber);
    }

    public void BuNext(View view) {
        GlobalInfo.UserPhoneNumber=GlobalInfo.FormatPhoneNumber(EDTNumber.getText().toString());
        GlobalInfo.updateInfo(GlobalInfo.UserPhoneNumber);
        finish();
        Intent intent=new Intent(this,MyTrackers.class);
        startActivity(intent);
    }
}
