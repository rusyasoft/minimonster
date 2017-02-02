package com.example.banana.minimonsterv3;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class WorkoutRepo {
    private static DBHelper dbHelper;

    static class ExerciseTotalExerciseInformation{
        String time;
        String calories;

        ExerciseTotalExerciseInformation(String total_calories, String total_time){
            this.calories = total_calories;
            this.time = total_time;
        }
    }

    public WorkoutRepo(Context context){
        dbHelper = new DBHelper(context);
    }

    public int insert(MinimonsterDatabase.Workout workout){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MinimonsterDatabase.WorkoutDB.EXERCISE, workout.exercise);
        contentValues.put(MinimonsterDatabase.WorkoutDB.COUNT, workout.count);
        contentValues.put(MinimonsterDatabase.WorkoutDB.WEIGHT, workout.weight);
        contentValues.put(MinimonsterDatabase.WorkoutDB.PLACE, workout.place);
        contentValues.put(MinimonsterDatabase.WorkoutDB.TIME, workout.time);
        contentValues.put(MinimonsterDatabase.WorkoutDB.DATE, workout.date);

        long val = db.insert(MinimonsterDatabase.WorkoutDB._WORKOUT, null, contentValues);
        db.close();

        return (int)val;
    }

    public static ArrayList checkExercise(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<String> exercises = new ArrayList<>();
        String exercise = null;
        String query1 = "select * from "+MinimonsterDatabase.WorkoutDB._WORKOUT+" where "+MinimonsterDatabase.WorkoutDB.EXERCISE+" LIKE "+"'benchpress'";
        String query2 = "select * from "+MinimonsterDatabase.WorkoutDB._WORKOUT+" where "+MinimonsterDatabase.WorkoutDB.EXERCISE+" LIKE "+"'dumbbell'";
        Cursor cursor1 = db.rawQuery(query1, null);
        Cursor cursor2 = db.rawQuery(query2, null);
        if(cursor1.getCount()!=0){
            exercises.add("benchpress");
        }
        if(cursor2.getCount()!=0){
            exercises.add("dumbbell");
        }
        cursor1.close();
        cursor2.close();
//        cursor.moveToFirst();
//        while(cursor.isAfterLast() == false) {
//            if(exercise!=null){
//                if(!exercise.equals(cursor.getString(cursor.getColumnIndex(MinimonsterDatabase.WorkoutDB.EXERCISE)))){
//                    exercises.add(exercise);
//                }
//            }else{
//                exercise = cursor.getString(cursor.getColumnIndex(MinimonsterDatabase.WorkoutDB.EXERCISE));
//                exercises.add(exercise);
//            }
//            cursor.moveToNext();
//        }
        db.close();
        //cursor.close();
        if(exercises != null){
            return exercises;
        }else return null;
    }

    public static ArrayList getLastExercise(String exercise){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "select * from "+MinimonsterDatabase.WorkoutDB._WORKOUT+" where "+MinimonsterDatabase.WorkoutDB.EXERCISE+" = '"+ exercise +"'";
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount()==0) {
            return null;
        }else{
            cursor.moveToLast();
            String date = cursor.getString(cursor.getColumnIndex(MinimonsterDatabase.WorkoutDB.DATE));
            String count = cursor.getString(cursor.getColumnIndex(MinimonsterDatabase.WorkoutDB.COUNT));
            String weight = cursor.getString(cursor.getColumnIndex(MinimonsterDatabase.WorkoutDB.WEIGHT));
            ArrayList<String> exercise_info = new ArrayList<>();

            exercise_info.add(date);
            exercise_info.add(count);
            exercise_info.add(weight);

            db.close();
            cursor.close();

            return exercise_info;
        }
    }

    public ArrayList getExerciseRecord(String exercise){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<MinimonsterDatabase.Workout> workoutRecords = new ArrayList<>();

        String query = "select * from "+MinimonsterDatabase.WorkoutDB._WORKOUT+" where "+MinimonsterDatabase.WorkoutDB.EXERCISE+" = '"+ exercise +"'";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while(cursor.isAfterLast() == false){
            MinimonsterDatabase.Workout workoutRecord = new MinimonsterDatabase.Workout();
            workoutRecord.count = cursor.getString(cursor.getColumnIndex(MinimonsterDatabase.WorkoutDB.COUNT));
            workoutRecord.weight= cursor.getString(cursor.getColumnIndex(MinimonsterDatabase.WorkoutDB.WEIGHT));
            workoutRecord.place = cursor.getString(cursor.getColumnIndex(MinimonsterDatabase.WorkoutDB.PLACE));
            workoutRecord.time = cursor.getString(cursor.getColumnIndex(MinimonsterDatabase.WorkoutDB.TIME));
            workoutRecord.date = cursor.getString(cursor.getColumnIndex(MinimonsterDatabase.WorkoutDB.DATE));

            workoutRecords.add(workoutRecord);
            cursor.moveToNext();
        }
        db.close();
        cursor.close();
        return workoutRecords;
    }

    public ExerciseTotalExerciseInformation getExerciseTotalInformation(String exercise){
        ArrayList<MinimonsterDatabase.Workout> workoutRecords = getExerciseRecord(exercise);
        WorkoutInformation workoutInformation = new WorkoutInformation();
        int total_time = 0;
        double total_calories;
        int total_count = 0;
        for(int i=0; i<workoutRecords.size();i++){
            total_count += Integer.parseInt(workoutRecords.get(i).count);
            total_time += Integer.parseInt(workoutRecords.get(i).time);
        }
        total_calories = total_count*workoutInformation.getCalorie(exercise);
        return (new ExerciseTotalExerciseInformation(String.format("%.2f", total_calories), Integer.toString(total_time)));
    }

    public ExerciseTotalExerciseInformation getTotalInformation(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        WorkoutInformation workoutInformation = new WorkoutInformation();
        int total_time = 0;
        double total_calories = 0;
        String query = "select * from "+MinimonsterDatabase.WorkoutDB._WORKOUT;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while(cursor.isAfterLast() == false){
            if(cursor.getString(cursor.getColumnIndex(MinimonsterDatabase.WorkoutDB.EXERCISE)).equals("bench press")){
                total_calories += Integer.parseInt(cursor.getString(cursor.getColumnIndex(MinimonsterDatabase.WorkoutDB.COUNT))) * workoutInformation.getCalorie("bench press");
            }else if(cursor.getString(cursor.getColumnIndex(MinimonsterDatabase.WorkoutDB.EXERCISE)).equals("dumbbell")){
                total_calories += Integer.parseInt(cursor.getString(cursor.getColumnIndex(MinimonsterDatabase.WorkoutDB.COUNT))) * workoutInformation.getCalorie("dumbbell");
            }
            total_time += Integer.parseInt(cursor.getString(cursor.getColumnIndex(MinimonsterDatabase.WorkoutDB.TIME)));
            cursor.moveToNext();
        }
        return (new ExerciseTotalExerciseInformation(String.format("%.2f", total_calories), Integer.toString(total_time)));
    }
}
