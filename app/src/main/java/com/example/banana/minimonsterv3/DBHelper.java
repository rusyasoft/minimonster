package com.example.banana.minimonsterv3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 18 ;
    public static final String _DATABASE_NAME = "MiniMonsterDatabase.db";

    public DBHelper(Context context){
        super(context, _DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(MinimonsterDatabase.WorkoutDB._CREATE_WORKOUT);
        db.execSQL(MinimonsterDatabase.DeviceDB._CREATE_DEVICE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS "+MinimonsterDatabase.WorkoutDB._WORKOUT);
        db.execSQL("DROP TABLE IF EXISTS "+MinimonsterDatabase.DeviceDB._DEVICE);
        onCreate(db);
    }
}
