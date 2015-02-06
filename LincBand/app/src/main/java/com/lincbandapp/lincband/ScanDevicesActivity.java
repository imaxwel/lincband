package com.lincbandapp.lincband;

import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class ScanDevicesActivity extends Activity {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int STATE_DISCONNECTED  = 0x00000000;
    private static final String EXTRA_STATE = "android.bluetooth.headset.extra.STATE";
    private static final String TAG = "BluetoothReceiver";
    private static final String ACTION_BT_HEADSET_STATE_CHANGED  = "android.bluetooth.headset.action.STATE_CHANGED";
    private static final String ACTION_BT_HEADSET_FORCE_ON = "android.bluetooth.headset.action.FORCE_ON";
    private static final String ACTION_BT_HEADSET_FORCE_OFF = "android.bluetooth.headset.action.FORCE_OFF";

    private ListView listDevicesFound;
    ImageButton scanBluetooth;
    private ArrayAdapter<String> BTArrayAdapter;

    BluetoothHeadset mBluetoothHeadset;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothManager mBluetoothManager;
    AudioManager mAudioManager;
    List<BluetoothDevice> connectedDevices;
    BluetoothDevice mConnectedHeadset;

    public int btConState;
    boolean audioConnection;
    int state = BluetoothHeadset.STATE_DISCONNECTED;
    int previousState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_scan_devices);

        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        //Monitor profile events
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED);
        filter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothHeadset.ACTION_VENDOR_SPECIFIC_HEADSET_EVENT);
        registerReceiver(ActionFoundReceiver, filter);

        //Get the default adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Establish connection to the proxy.
        mBluetoothAdapter.getProfileProxy(this, mProfileListener, BluetoothProfile.HEADSET);

        scanBluetooth = (ImageButton) findViewById(R.id.scanButton);
        final ListView listDevicesFound = (ListView) findViewById(R.id.scannedList);

        //create an array adapter for devices
        BTArrayAdapter = new ArrayAdapter<String>(ScanDevicesActivity.this, android.R.layout.simple_list_item_checked);
        listDevicesFound.setAdapter(BTArrayAdapter);

        scanBluetooth.setOnClickListener(scanBluetoothOnClickListener);

        findViewById(R.id.progressBar).setVisibility(View.GONE);

    }

    // Define Service Listener of BluetoothProfile
    private BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothProfile.HEADSET) {
                mBluetoothHeadset = (BluetoothHeadset) proxy;

                // no devices are connected at first
                connectedDevices = mBluetoothHeadset.getConnectedDevices();

                //the one paired (and disconnected) speaker is returned here
                int[] statesToCheck = {BluetoothHeadset.STATE_DISCONNECTED};
                List<BluetoothDevice> disconnectedDevices = mBluetoothHeadset.getDevicesMatchingConnectionStates(statesToCheck);

                mConnectedHeadset = connectedDevices.get(0);
                audioConnection = mBluetoothHeadset.isAudioConnected(mConnectedHeadset);

                if (audioConnection == true) {
                    mAudioManager.setMode(0);
                    mAudioManager.setBluetoothScoOn(true);
                    mAudioManager.startBluetoothSco();
                    mAudioManager.setMode(AudioManager.MODE_IN_CALL);
                    Log.i(TAG, "Bluetooth Headset On " + mAudioManager.getMode());
                    Log.i(TAG, "A2DP: " + mAudioManager.isBluetoothA2dpOn() + ". SCO: " + mAudioManager.isBluetoothScoAvailableOffCall());

                }

            }
        }

        @Override
        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothProfile.HEADSET) {
                mBluetoothHeadset = null;
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Destroying HeadsetService...");
        mBluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, mBluetoothHeadset);
        unregisterReceiver(ActionFoundReceiver);
    }

    private ImageButton.OnClickListener scanBluetoothOnClickListener
            = new ImageButton.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            BTArrayAdapter.clear();
            mBluetoothAdapter.startDiscovery();
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        }
    };

    private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            Log.i(TAG,"onReceive - BluetoothBroadcast");
           mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
           String action = intent.getAction();
            if (action.equals(BluetoothHeadset.ACTION_VENDOR_SPECIFIC_HEADSET_EVENT)) {
                final int extraData = intent.getIntExtra(EXTRA_STATE, STATE_DISCONNECTED);
                if (extraData == STATE_DISCONNECTED) {
                    mAudioManager.setBluetoothScoOn(false);
                    mAudioManager.stopBluetoothSco();
                    mAudioManager.setMode(AudioManager.MODE_NORMAL);
                    Log.i(TAG, "Bluetooth Headset Off " + mAudioManager.getMode());
                    Log.i(TAG, "A2DP: " + mAudioManager.isBluetoothA2dpOn() + ". SCO: " + mAudioManager.isBluetoothScoAvailableOffCall());
                } else {
                    mAudioManager.setMode(0);
                    mAudioManager.setBluetoothScoOn(true);
                    mAudioManager.startBluetoothSco();
                    mAudioManager.setMode(AudioManager.MODE_IN_CALL);
                    Log.i(TAG, "Bluetooth Headset On " + mAudioManager.getMode());
                    Log.i(TAG, "A2DP: " + mAudioManager.isBluetoothA2dpOn() + ". SCO: " + mAudioManager.isBluetoothScoAvailableOffCall());
                }
            }

            if (action.equals(ACTION_BT_HEADSET_FORCE_ON)) {
                mAudioManager.setMode(0);
                mAudioManager.setBluetoothScoOn(true);
                mAudioManager.startBluetoothSco();
                mAudioManager.setMode(AudioManager.MODE_IN_CALL);
                Log.i(TAG, "Bluetooth Headset On " + mAudioManager.getMode());
                Log.i(TAG, "A2DP: " + mAudioManager.isBluetoothA2dpOn() + ". SCO: " + mAudioManager.isBluetoothScoAvailableOffCall());
            }

            if (action.equals(ACTION_BT_HEADSET_FORCE_OFF)) {
                mAudioManager.setBluetoothScoOn(false);
                mAudioManager.stopBluetoothSco();
                mAudioManager.setMode(AudioManager.MODE_NORMAL);
                Log.i(TAG, "Bluetooth Headset Off " + mAudioManager.getMode());
                Log.i(TAG, "A2DP: " + mAudioManager.isBluetoothA2dpOn() + ". SCO: " + mAudioManager.isBluetoothScoAvailableOffCall());
            }
        }
    };
}


