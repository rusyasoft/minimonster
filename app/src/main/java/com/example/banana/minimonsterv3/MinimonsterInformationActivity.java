package com.example.banana.minimonsterv3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MinimonsterInformationActivity extends Activity{
    public static final String EXTRAS_EXERCISE_NAME = "EXERCISE_NAME";
    private String EXERCISE_NAME;

    ArrayList<MinimonsterDatabase.Workout> workoutRecords;

    private RecordListAdapter recordListAdapter;

    private WorkoutRepo workoutRepo;
    WorkoutRepo.ExerciseTotalExerciseInformation total_info_exercise;

    ImageView exercise_image;
    TextView exercise_name;
    TextView total_time_exercise;
    TextView total_calories_exercise;

    static class ViewHolder {
        TextView date;
        TextView count;
        TextView weight;
        TextView time;
    }

    private class RecordListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public RecordListAdapter(Context context) {
            // 상위 클래스의 생성자 호출
            super();
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return workoutRecords.size();
        }

        @Override
        public Object getItem(int i) {
            return workoutRecords.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = mInflater.inflate(R.layout.record_list, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.date = (TextView) view.findViewById(R.id.record_date);
                viewHolder.count = (TextView) view.findViewById(R.id.record_count);
                viewHolder.weight = (TextView) view.findViewById(R.id.record_weight);
                viewHolder.time = (TextView) view.findViewById(R.id.record_time);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            MinimonsterDatabase.Workout workoutRecord = workoutRecords.get(i);
            viewHolder.date.setText(workoutRecord.date);
            viewHolder.count.setText(workoutRecord.count+" time");
            viewHolder.weight.setText(workoutRecord.weight);
            viewHolder.time.setText(workoutRecord.time+" s");

            return view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minimonster_info);

        total_time_exercise = (TextView) findViewById(R.id.total_time_exercise);
        total_calories_exercise = (TextView) findViewById(R.id.total_calories_exercise);
        exercise_image = (ImageView) findViewById(R.id.info_exercise_image);
        exercise_name = (TextView) findViewById(R.id.info_exercise_name);
        exercise_image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    }

    @Override
    protected void onResume(){
        super.onResume();

        final Intent intent = getIntent();
        EXERCISE_NAME = intent.getStringExtra(EXTRAS_EXERCISE_NAME);

        if (EXERCISE_NAME.equals("benchpress")) {
            exercise_image.setImageResource(R.drawable.ic_action_dumbell);
            exercise_name.setText("Bench Press");
        } else if (EXERCISE_NAME.equals("dumbbell")) {
            exercise_image.setImageResource(R.drawable.ic_action_dumbell);
            exercise_name.setText("Dumbbell");
        }

        workoutRepo = new WorkoutRepo(this);

        total_info_exercise = workoutRepo.getExerciseTotalInformation(EXERCISE_NAME);
        total_calories_exercise.setText(total_info_exercise.calories+" cal");
        total_time_exercise.setText(total_info_exercise.time+" s");

        workoutRecords = workoutRepo.getExerciseRecord(EXERCISE_NAME);
        ListView recordListView  = (ListView)findViewById(R.id.info_record);
        recordListAdapter = new RecordListAdapter(this);
        recordListView.setAdapter(recordListAdapter);
        recordListAdapter.notifyDataSetChanged();
    }
}
