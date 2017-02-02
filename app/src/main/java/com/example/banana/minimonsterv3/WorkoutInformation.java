package com.example.banana.minimonsterv3;

import java.util.ArrayList;

public class WorkoutInformation {
    public static final double CALORIE_PER_ONE_BENCHPRESS = 0.03;
    public static final double CALORIE_PER_ONE_DUMBBELL = 0.02;

    public static final String EXERCISE_BENCHPRESS_FOR_MQTT = "benchpress";
    public static final String EXERCISE_DUMBBELL_FOR_MQTT = "dumbbell";

    public static double getCalorie(String exercise){
        if(exercise.equals("bench press")){
            return CALORIE_PER_ONE_BENCHPRESS;
        }else if(exercise.equals("dumbbell")){
            return CALORIE_PER_ONE_DUMBBELL;
        }else{
            return 0;
        }
    }
    public static String getExerciseNameForMqtt(String exercise){
        if(exercise.equals("bench")){
            return EXERCISE_BENCHPRESS_FOR_MQTT;
        }else if(exercise.equals("dumbbel")){
            return EXERCISE_DUMBBELL_FOR_MQTT;
        }else{
            return null;
        }
    }
}
