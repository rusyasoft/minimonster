package com.example.banana.minimonsterv3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class DeviceRepo {
    private DBHelper dbHelper;

    public DeviceRepo(Context context){
        dbHelper = new DBHelper(context);
    }

    public boolean insert(MinimonsterDatabase.Device device){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MinimonsterDatabase.DeviceDB.CATEGORY, device.category);
        contentValues.put(MinimonsterDatabase.DeviceDB.SERVICE_UUID, device.service_uuid);
        contentValues.put(MinimonsterDatabase.DeviceDB.NOTIFY_UUID, device.notify_uuid);

        if(db.insert(MinimonsterDatabase.DeviceDB._DEVICE, null, contentValues)<0){
            db.close();
            return false;
        }
        db.close();
        return true;
    }

    public boolean checkDevice(String deviceName){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "select * from "+MinimonsterDatabase.DeviceDB._DEVICE+" where "+ MinimonsterDatabase.DeviceDB.CATEGORY+" = '"+ deviceName +"'";
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.getCount()<=0){
            cursor.close();
            db.close();
            return false;
        }
        else {
            cursor.close();
            db.close();
            return true;
        }
    }

    public ArrayList getUUID(String deviceName){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "select * from "+MinimonsterDatabase.DeviceDB._DEVICE+" where "+ MinimonsterDatabase.DeviceDB.CATEGORY+" = '"+ deviceName +"'";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        String service_uuid;
        String notify_uuid;
        service_uuid = cursor.getString(cursor.getColumnIndex(MinimonsterDatabase.DeviceDB.SERVICE_UUID));
        notify_uuid = cursor.getString(cursor.getColumnIndex(MinimonsterDatabase.DeviceDB.NOTIFY_UUID));

        ArrayList<String> uuids = new ArrayList<>();
        uuids.add(service_uuid);
        uuids.add(notify_uuid);

        if(cursor.getCount()<=0){
            cursor.close();
            db.close();
            return null;
        }
        else {
            cursor.close();
            db.close();
            return uuids;
        }
    }

    public boolean initDevices(){
        boolean val;

        MinimonsterDatabase.Device device_1 = new MinimonsterDatabase.Device("bench","83692593-7bab-42af-b186-ae389b44a01f","8661e735-70c1-4418-ba55-b661b5f094bd");
        MinimonsterDatabase.Device device_2 = new MinimonsterDatabase.Device("dumbbel","83692593-7bab-42af-b186-ae389b44a01f","2578ad1d-105a-437f-9490-63f4cc97446a");

        val = insert(device_1)&&insert(device_2);

        return val;
    }
}
