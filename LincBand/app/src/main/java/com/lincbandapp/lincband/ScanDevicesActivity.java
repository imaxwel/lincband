package com.lincbandapp.lincband;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.lincbandapp.lincband.R;

public class ScanDevicesActivity extends Activity {
    private static final int REQUEST_ENABLE_BT = 1;
    private ListView listDevicesFound;
    ImageButton scanBluetooth;
    private BluetoothAdapter myBluetoothAdapter;
    private ArrayAdapter<String> BTArrayAdapter;
    BluetoothHeadset mBluetoothHeadset;
    public Boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_scan_devices);

        scanBluetooth = (ImageButton) findViewById(R.id.scanButton);
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listDevicesFound = (ListView)findViewById(R.id.scannedList);

        //create an array adapter for devices
        BTArrayAdapter = new ArrayAdapter<String>(ScanDevicesActivity.this, android.R.layout.simple_list_item_single_choice);
        listDevicesFound.setAdapter(BTArrayAdapter);

        scanBluetooth.setOnClickListener(scanBluetoothOnClickListener);
       // scanBluetooth.setOnClickListener((View.OnClickListener) mProfileListener);
        registerReceiver(ActionFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));


    }
    private BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothProfile.HEADSET) {
                mBluetoothHeadset = (BluetoothHeadset) proxy;
            }
        }
        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothProfile.HEADSET) {
                mBluetoothHeadset = null;
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(ActionFoundReceiver);
    }

    private ImageButton.OnClickListener scanBluetoothOnClickListener
            = new ImageButton.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            BTArrayAdapter.clear();
            myBluetoothAdapter.startDiscovery();
            
        }};

    private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            // Establish connection to the proxy.
            myBluetoothAdapter.getProfileProxy(context, mProfileListener, BluetoothProfile.HEADSET);

            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                BTArrayAdapter.notifyDataSetChanged();
            }
        }};

}
