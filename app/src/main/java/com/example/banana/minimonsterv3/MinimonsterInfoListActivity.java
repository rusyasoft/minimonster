package com.example.banana.minimonsterv3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MinimonsterInfoListActivity extends Activity {
    static class ViewHolder {
        ImageView exerciseImg;
        TextView exerciseName;
    }
    WorkoutRepo workoutRepo;

    private ArrayList<String> exercises;

    private InformationListAdapter infoListAdapter;

    WorkoutRepo.ExerciseTotalExerciseInformation total_info;

    TextView total_time;
    TextView total_calories;

    private class InformationListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public InformationListAdapter(Context context) {
            super();
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public String getExerciseName(int index){
            return exercises.get(index);
        }

        @Override
        public int getCount() {
            return exercises.size();
        }

        @Override
        public Object getItem(int i) {
            return exercises.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            MinimonsterInfoListActivity.ViewHolder viewHolder;
            if (view == null) {
                view = mInflater.inflate(R.layout.info_exercise_list, viewGroup, false);

                viewHolder = new MinimonsterInfoListActivity.ViewHolder();
                viewHolder.exerciseImg = (ImageView) view.findViewById(R.id.info_list_exercise_image);
                viewHolder.exerciseName = (TextView) view.findViewById(R.id.info_list_exersie_name);

                view.setTag(viewHolder);
            } else {
                viewHolder = (MinimonsterInfoListActivity.ViewHolder) view.getTag();
            }
            if(exercises.get(i).equals("benchpress")){
                viewHolder.exerciseImg.setImageResource(R.drawable.ic_action_dumbell);
                viewHolder.exerciseImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                viewHolder.exerciseName.setText("Bench Press");
            }else{
                viewHolder.exerciseImg.setImageResource(R.drawable.ic_action_dumbell);
                viewHolder.exerciseImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                viewHolder.exerciseName.setText("Dumbbell");
            }

            return view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minimonster_infolist);

        workoutRepo = new WorkoutRepo(this);
        exercises = new ArrayList<>();

        total_time = (TextView) findViewById(R.id.total_time);
        total_calories = (TextView) findViewById(R.id.total_calories);

        infoListAdapter = new MinimonsterInfoListActivity.InformationListAdapter(this);
        ListView listView = (ListView)findViewById(R.id.info_list);
        listView.setAdapter(infoListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String exerciseName = infoListAdapter.getExerciseName(position);
                if (exerciseName == null) return;
                final Intent intent = new Intent(MinimonsterInfoListActivity.this, MinimonsterInformationActivity.class);
                intent.putExtra(MinimonsterInformationActivity.EXTRAS_EXERCISE_NAME, exerciseName);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        exercises = workoutRepo.checkExercise();
        total_info = workoutRepo.getTotalInformation();
        total_time.setText(total_info.time+" s");
        total_calories.setText(total_info.calories+" cal");
        infoListAdapter.notifyDataSetChanged();
    }
}
