package com.example.banana.minimonsterv3;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;

import static android.content.ContentValues.TAG;

public class MqttPreService extends Service {
    private static String IP = "tcp://117.16.136.173:1883";
    private static String TOPIC_PUB = "/iotgym/fromandroid";
    private MqttAndroidClient client;
    private String clientId;
    public boolean isConnected = false;
    private final IBinder mBinder = new MqttPreService.LocalBinder();
    public class LocalBinder extends Binder {
        MqttPreService getService() {
            return MqttPreService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent){
        return mBinder;
    }

    public boolean onUnbind(Intent intent) {
        disconnect();
        return super.onUnbind(intent);
    }

    public boolean initialize(Context context) {
        clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(context, IP, clientId);
        if(client!=null) return true;
        else return false;
    }

    public void connect(){
        try {
            IMqttToken token = client.connect();
            Log.d(TAG, "MQTT Connection Started");
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "MQTT Connection onSuccess");
                    isConnected = true;
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "MQTT Connection onFailure");
                    Log.d(TAG, exception.toString());
                    isConnected = false;
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            IMqttToken disconToken = client.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishMessage(String payload) {
        try {
            MqttMessage message = new MqttMessage(payload.getBytes());
            client.publish(TOPIC_PUB, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
