package com.lincbandapp.lincband;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;

import com.lincbandapp.lincband.R;

public class ScanDevicesActivity extends Activity {
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter myBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private Set<BluetoothDevice> notPairedDevices;
    private ListView availableDevices;
    private ArrayAdapter<String> BTArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_scan_devices);

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        ImageButton scanBluetooth = (ImageButton) findViewById(R.id.scanButton);
        scanBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                find(view);
            }
        });

        availableDevices = (ListView)findViewById(R.id.scannedList);

        //create an array adapter for devices
        BTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice);
        availableDevices.setAdapter(BTArrayAdapter);


        pairedDevices = myBluetoothAdapter.getBondedDevices();

        for (BluetoothDevice device : pairedDevices) {
            BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
        }

    }

    public void list(View view) {
        pairedDevices = myBluetoothAdapter.getBondedDevices();

        for (BluetoothDevice device : pairedDevices) {
            BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
        }
        Toast.makeText(getApplicationContext(), "Show paired devices", Toast.LENGTH_SHORT).show();
    }


    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                notPairedDevices.add(device);
                BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                BTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

   // IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);


    public void find(View view) {
        if (myBluetoothAdapter.isDiscovering()) {
            myBluetoothAdapter.cancelDiscovery();
        }
        else {
            BTArrayAdapter.clear();
            myBluetoothAdapter.startDiscovery();

            registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            //registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bReceiver);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scan_devices, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
