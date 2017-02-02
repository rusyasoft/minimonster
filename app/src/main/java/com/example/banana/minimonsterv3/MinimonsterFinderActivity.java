package com.example.banana.minimonsterv3;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
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

public class MinimonsterFinderActivity extends Activity {
    // 블루투스 관련
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning = false;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 100000;
    private static final int REQUEST_ENABLE_BT = 1;

    // DB 관련
    private DeviceRepo deviceRepo;
    private WorkoutRepo workoutRepo;

    private LeDeviceListAdapter deviceListAdapter;
    static class ViewHolder {
        ImageView exerciseImg;
        TextView exerciseName;
        TextView exerciseInfo;
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback(){
        @Override
        public void onLeScan(final BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(deviceRepo.checkDevice(bluetoothDevice.getName())){
                        deviceListAdapter.addmMonster(bluetoothDevice);
                    }
                }
            });
        }
    };

    private void scanLeDevice(final boolean enable){
        if(enable){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run(){
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    /*
        Adapter는 View와 Data를 연결하여 연결된 뷰를 반환해준다.
     */
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> devices;
        private LayoutInflater mInflater;

        // 생성자
        public LeDeviceListAdapter(Context context) {
            // 상위 클래스의 생성자 호출
            super();

            devices = new ArrayList<>();
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addmMonster(BluetoothDevice mMonster) {
            if(!devices.contains(mMonster)) {
                devices.add(mMonster);
                notifyDataSetChanged();
            }
        }

        public BluetoothDevice getmMonster(int position) {
            return devices.get(position);
        }

        public void clear(){
            devices.clear();
        }

        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int i) {
            return devices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                // ble_scan_list를 작은 뷰로 하겠다.
                view = mInflater.inflate(R.layout.ble_scan_list, viewGroup, false);

                // viewHolder 위치할당
                viewHolder = new ViewHolder();
                viewHolder.exerciseImg = (ImageView) view.findViewById(R.id.ble_exercise_image);
                viewHolder.exerciseName = (TextView) view.findViewById(R.id.ble_exersie_name);
                viewHolder.exerciseInfo = (TextView) view.findViewById(R.id.ble_exercise_info);

                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = devices.get(i);
            final String deviceName = device.getName();

            if (deviceName.equals("bench")) {
                viewHolder.exerciseImg.setImageResource(R.drawable.ic_action_dumbell);
                viewHolder.exerciseImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                viewHolder.exerciseName.setText("Bench Press");
                ArrayList lastExercise = workoutRepo.getLastExercise("benchpress");
                if(lastExercise!=null){
                    viewHolder.exerciseInfo.setText("Last Exercise : "+lastExercise.get(0)+", "+lastExercise.get(1)+"/"+lastExercise.get(2));
                }else{
                    viewHolder.exerciseInfo.setText("Last Exercise : no record!");
                }
            }else if (deviceName.equals("dumbbel")) {
                viewHolder.exerciseImg.setImageResource(R.drawable.ic_action_dumbell);
                viewHolder.exerciseImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                viewHolder.exerciseName.setText("Dumbbell");
                ArrayList lastExercise = workoutRepo.getLastExercise("dumbbell");
                if(lastExercise!=null){
                    viewHolder.exerciseInfo.setText("Last Exercise : "+lastExercise.get(0)+", "+lastExercise.get(1)+"/"+lastExercise.get(2));
                }else{
                    viewHolder.exerciseInfo.setText("Last Exercise : no record!");
                }
            }
            return view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minimonster_finder);

        mHandler = new Handler();

        deviceRepo = new DeviceRepo(this);
        workoutRepo = new WorkoutRepo(this);

        deviceListAdapter = new LeDeviceListAdapter(this);
        ListView listView = (ListView)findViewById(R.id.ble_list);
        listView.setAdapter(deviceListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final BluetoothDevice device = deviceListAdapter.getmMonster(position);
                if (device == null) return;
                final Intent intent = new Intent(MinimonsterFinderActivity.this, MinimonsterControlActivity.class);
                intent.putExtra(MinimonsterControlActivity.EXTRAS_MMONSTER_NAME, device.getName());
                intent.putExtra(MinimonsterControlActivity.EXTRAS_MMONSTER_ADDRESS, device.getAddress());
                if (mScanning) {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mScanning = false;
                }
                startActivity(intent);
            }
        });

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            finish();
        }
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            finish();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        deviceListAdapter.clear();
        scanLeDevice(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        deviceListAdapter.clear();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        mScanning = false;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        mScanning = false;
    }
}
