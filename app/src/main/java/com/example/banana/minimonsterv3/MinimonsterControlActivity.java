package com.example.banana.minimonsterv3;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MinimonsterControlActivity extends Activity implements CompoundButton.OnCheckedChangeListener{

    public static final String EXTRAS_MMONSTER_NAME = "MMONSTER_NAME";
    public static final String EXTRAS_MMONSTER_ADDRESS = "MONSTER_ADDRESS";
    private BluetoothLeService mBluetoothLeService;
    private MqttPreService mMqttPreService;
    ToggleButton tb = null;
    Spinner weightSpinner = null;
    private boolean mBound = false;
    private TextView mCounter;
    private TextView mBalance;
    private String deviceName;
    private String deviceAddress;
    private String service_uuid;
    private String notify_uuid;
    private long start;
    private long end;
    private String weight = null;
    private String count = "0";
    private WorkoutRepo workoutRepo;
    private DeviceRepo deviceRepo;
    private final ServiceConnection bleServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            BluetoothLeService.LocalBinder bleBinder = (BluetoothLeService.LocalBinder) service;
            mBluetoothLeService = bleBinder.getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e("[DeviceControlActivity]", "Unable to initialize Bluetooth");
                finish();
            }
            mBluetoothLeService.connect(deviceAddress);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    private final ServiceConnection mqttServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MqttPreService.LocalBinder mqttBinder = (MqttPreService.LocalBinder) iBinder;
            mMqttPreService = mqttBinder.getService();
            if (!mMqttPreService.initialize(getApplicationContext())) {
                Log.e("[DeviceControlActivity]", "Unable to initialize MQTT");
                finish();
            }
            mMqttPreService.connect();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mMqttPreService = null;
        }
    };

    private void updateCount(String data) {
        if (data != null) {
            mCounter.setText(data);
            count = data;
        }
    }
    private void updateBalance(String data) {
        if(!data.equals("0")) {
            mBalance.setText("Wrong Balance!");
        }else{
            mBalance.setText("count :");
        }
    }

    // 서비스로부터 데이터를 받는 부분
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                updateCount(intent.getStringExtra(BluetoothLeService.COUNT));
                updateBalance(intent.getStringExtra(BluetoothLeService.BALANCE));
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            mBluetoothLeService.setNotifyCounter(service_uuid, notify_uuid);
            if(weightSpinner!=null){
                weightSpinner.setEnabled(false);
            }
            start = System.currentTimeMillis();
        }else{
            mBluetoothLeService.stopNotifyCounter();
            if(weightSpinner!=null){
                weightSpinner.setEnabled(true);
            }
            end = System.currentTimeMillis();
            long time = (end-start)/1000;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = sdf.format(new Date());
            String exerciseName = WorkoutInformation.getExerciseNameForMqtt(deviceName);
            //if(!count.equals("0")){
                workoutRepo.insert(new MinimonsterDatabase.Workout(exerciseName, count, weight, "COEX", Long.toString(time), currentDate));
                mMqttPreService.publishMessage("{\"user_id\" : \"1\", \"count\" : "+count+" , \"weight\" : "+weight.subSequence(0,2)+" , \"time\" : "+Long.toString(time)+" , \"exercise_event\" : \""+exerciseName+"\" , \"date_bar\" : \""+currentDate+"\"}");
            //}
            finish();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minimonster_counter);

        workoutRepo = new WorkoutRepo(this);
        deviceRepo = new DeviceRepo(this);

        // 이전 Activity로 부터 데이터 받아오기
        final Intent intent = getIntent();
        deviceName = intent.getStringExtra(EXTRAS_MMONSTER_NAME);
        deviceAddress = intent.getStringExtra(EXTRAS_MMONSTER_ADDRESS);
        ArrayList<String> uuids = deviceRepo.getUUID(deviceName);
        service_uuid = uuids.get(0);
        notify_uuid = uuids.get(1);

        TextView ExerciseName = (TextView)findViewById(R.id.exercise_name);

        if(deviceName.equals("bench")){
            ExerciseName.setText("Bench Press");
        }
        if(deviceName.equals("dumbbel")){
            ExerciseName.setText("Dumbbell");
        }

        mCounter = (TextView)findViewById(R.id.counter);
        mBalance = (TextView)findViewById(R.id.balance);

        final List<String> categories = new ArrayList<String>();
        categories.add("10kg");
        categories.add("20kg");
        categories.add("30kg");
        categories.add("40kg");
        categories.add("50kg");
        categories.add("60kg");
        categories.add("70kg");
        categories.add("80kg");
        weightSpinner = (Spinner) findViewById(R.id.weight_spinner);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weightSpinner.setAdapter(dataAdapter);
        weightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                weight = categories.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tb = (ToggleButton)this.findViewById(R.id.tBtn_counter);
        tb.setOnCheckedChangeListener(this);
        tb.setChecked(false);
    }

    @Override
    protected void onStart(){
        super.onStart();
        bindService(new Intent(this, BluetoothLeService.class), bleServiceConnection, BIND_AUTO_CREATE);
        bindService(new Intent(this, MqttPreService.class), mqttServiceConnection, BIND_AUTO_CREATE);
        mBound = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            Log.d("[JUNG]","----------------------------- 1");
            mBluetoothLeService.connect(deviceAddress);
        }
        if (mMqttPreService != null) {
            mMqttPreService.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(mBound) {
            unbindService(bleServiceConnection);
            unbindService(mqttServiceConnection);
        }
        mBound = false;
    }
}
