package com.example.banana.minimonsterv3;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;

import static android.content.ContentValues.TAG;

public class MainActivity extends TabActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBHelper mDBHelper = new DBHelper(this);
        DeviceRepo deviceRepo = new DeviceRepo(this);
        if(!deviceRepo.initDevices()){
            finish();
        }

        TabHost tabHost = getTabHost();

        TabHost.TabSpec tabSpecExer = tabHost.newTabSpec("FINDER");
        tabSpecExer.setIndicator("EXERCISE");
        Intent findertapIntent = new Intent(this, MinimonsterFinderActivity.class);
        tabSpecExer.setContent(findertapIntent);
        tabHost.addTab(tabSpecExer);

        TabHost.TabSpec tabSpecInfo = tabHost.newTabSpec("INFO");
        tabSpecInfo.setIndicator("MY MONSTER");
        Intent infotapIntent = new Intent(this, MinimonsterInfoListActivity.class);
        tabSpecInfo.setContent(infotapIntent);
        tabHost.addTab(tabSpecInfo);

        tabHost.setCurrentTab(0);
    }
    protected void onDestroy(){
        super.onDestroy();
    }
}
