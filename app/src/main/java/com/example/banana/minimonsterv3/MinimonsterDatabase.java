package com.example.banana.minimonsterv3;

import android.provider.BaseColumns;

public final class MinimonsterDatabase {
    public static final class WorkoutDB implements BaseColumns{

        public static final String EXERCISE = "exercise";
        public static final String COUNT = "count";
        public static final String WEIGHT = "weight";
        public static final String PLACE = "place";
        public static final String TIME = "time";
        public static final String DATE = "date";
        public static final String _WORKOUT = "workout";

        public static final String _CREATE_WORKOUT =
                "create table "+_WORKOUT+"("+_ID+" integer primary key autoincrement, "
                        +EXERCISE+" text not null , "
                        +COUNT+" integer not null , "
                        +WEIGHT+" integer not null , "
                        +PLACE+" text not null , "
                        +TIME+" text not null , "
                        +DATE+" date not null );";
    }
    public static class Workout{
        public String exercise;
        public String count;
        public String weight;
        public String place;
        public String time;
        public String date;

        public Workout(){};

        public Workout(String exercise, String count, String weight, String place, String time, String date){
            this.exercise = exercise;
            this.count = count;
            this.weight = weight;
            this.place = place;
            this.time = time;
            this.date = date;
        }
    }

    public static final class DeviceDB implements BaseColumns{
        public static final String CATEGORY = "category";
        public static final String SERVICE_UUID = "service_uuid";
        public static final String NOTIFY_UUID = "notify_uuid";
        public static final String _DEVICE = "device";

        public static final String _CREATE_DEVICE =
                "create table "+_DEVICE+"("+_ID+" integer primary key autoincrement, "
                        +CATEGORY+" text not null , "
                        +SERVICE_UUID+" text not null , "
                        +NOTIFY_UUID+" text not null );";
    }

    public static class Device{
        public String category;
        public String service_uuid;
        public String notify_uuid;

        public Device(){};

        public Device(String category, String service_uuid, String notify_uuid){
            this.category = category;
            this.service_uuid = service_uuid;
            this.notify_uuid = notify_uuid;
        }
    }
}
