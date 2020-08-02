package com.biotel.iot.ui.main;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.biotel.iot.R;
import com.biotel.iot.services.BluetoothLeService;
import com.biotel.iot.services.WebServerService;
import com.biotel.iot.ui.common.Navigation;
import com.biotel.iot.ui.common.recyclerview.RecyclerViewBinderCore;
import com.biotel.iot.util.BluetoothLeScanner;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;

import com.biotel.iot.containers.BaseScreen;
import com.biotel.iot.containers.BluetoothLeDeviceStore;

import static com.biotel.iot.services.WebServerService.ADD_IOT;
import static com.biotel.iot.services.WebServerService.BUTTON_EVENT;
import static com.biotel.iot.services.WebServerService.GATT_EVENT;
import static com.biotel.iot.services.WebServerService.GATT_INTENT;
import static com.biotel.iot.services.WebServerService.SCREEN_INTENT;
import static com.biotel.iot.services.WebServerService.SERVER_EVENT;
import static com.biotel.iot.services.WebServerService.SERVER_INTENT;
import static com.biotel.iot.services.WebServerService.getServiceInstance;

/**
 * Created By Piyush Pandey on 18-07-2020
 */
public class MainActivity extends BaseScreen {
    private RecyclerViewBinderCore mCore;
    private BluetoothLeScanner mScanner;
    private BluetoothLeDeviceStore mDeviceStore;
    private DeviceRecyclerAdapter mRecyclerAdapter;
    private View view;

    private String deviceName;
    private ScreenReceiver receiver;
    private GattReceiver gattReceiver;
    private BluetoothLeDevice iotDevice;

    private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            final BluetoothLeDevice deviceLe = new BluetoothLeDevice(device, rssi, scanRecord, System.currentTimeMillis());
            //Check if Iot Device has been found
            if (ADD_IOT.equals(deviceLe.getAddress())){
                iotDevice = deviceLe;
                deviceName = iotDevice.getName();
                setBleStatus("Found "+iotDevice.getName());
                mScanner.stopScan("Device Found");
                //Connect To Iot Device
                getServiceInstance().connectBluetooth(iotDevice.getAddress());
                invalidateOptionsMenu();
            }
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = new View(this);

        setMainScreenView(view);

        receiver = new ScreenReceiver();
        registerReceiver(receiver, new IntentFilter(SCREEN_INTENT));

        gattReceiver = new GattReceiver();
        registerReceiver(gattReceiver, new IntentFilter(GATT_INTENT));

        //Initializing the Background Service
        initService();

        view.setServerListener(v -> {
            if (isConnectedToNetwork()){
                if (!isServiceRunning()) {
                    startService();
                }
                //Pass the button click event to Background Service
                Intent serverIntent = new Intent(SERVER_INTENT);
                serverIntent.putExtra(BUTTON_EVENT, true);
                sendBroadcast(serverIntent);
            } else {
                Toast.makeText(this, "Connect to wifi to start server", Toast.LENGTH_SHORT).show();
            }
        });

        mCore = RecyclerViewCoreFactory.create(this, new Navigation(this));
        mDeviceStore = new BluetoothLeDeviceStore();
        mScanner = appPref.getBtScanner();
        mScanner.setScanCallbackListener(mLeScanCallback);
    }

    private void initService() {
        //Checking If Background Service is Already Running or not
        if (!isServiceRunning()) {
            //Start Background Service If Not Started
            startService();
        } else {
            //Set the UI Based on the Background Service Status
            if (getServiceInstance().isServerRunning()) {
                setServerUI(true);
                setBleStatus(appPref.getLastStatus());
            }
        }
        view.setIpAddress(getIPAddress());
    }

    private void startService() {
        Log.e("check_server", "Starting Service");
        ContextCompat.startForegroundService(this, new Intent(this, WebServerService.class));
    }

    private void stopService() {
        Log.e("check_server", "Stopping Service");
        stopService(new Intent(this, WebServerService.class));
    }

    private void setServerUI(boolean isStarted) {
        view.setIpAddress(getIPAddress());
        if (isStarted) {
            view.changeServerText(getString(R.string.label_stop_server));
        } else {
            //stopService();
            view.changeServerText(getString(R.string.label_start_server));
            Toast.makeText(this, "Server Stopped", Toast.LENGTH_SHORT).show();
        }
    }

    private void setBleStatus(String status) {
        view.setBleStatus(status);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanner.isScanning()) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_progress_indeterminate);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                startScanPrepare();
                break;
            case R.id.menu_stop:
                mScanner.stopScan("menu");
                invalidateOptionsMenu();
                break;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScanner.stopScan("onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appPref.storeLastStatus(view.getBleStatus());
        unregisterReceiver(receiver);
        unregisterReceiver(gattReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        view.setBluetoothEnabled(mScanner.getBluetoothAdapter().isBluetoothOn());
        view.setBluetoothLeSupported(mScanner.getBluetoothAdapter().isBluetoothLeSupported());
        invalidateOptionsMenu();
    }

    private void startScanPrepare() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final String permission;
            final int message;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permission = Manifest.permission.ACCESS_FINE_LOCATION;
                message = R.string.permission_not_granted_fine_location;
            } else {
                permission = Manifest.permission.ACCESS_COARSE_LOCATION;
                message = R.string.permission_not_granted_coarse_location;
            }

            PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(this,
                    new String[]{permission}, new PermissionsResultAction() {

                        @Override
                        public void onGranted() {
                            startScan();
                        }

                        @Override
                        public void onDenied(String permission) {
                            Toast.makeText(MainActivity.this,
                                    message,
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
        } else {
            startScan();
        }
    }

    private void startScan() {
        final boolean isBluetoothOn = mScanner.getBluetoothAdapter().isBluetoothOn();
        final boolean isBluetoothLePresent = mScanner.getBluetoothAdapter().isBluetoothLeSupported();
        if (!isBluetoothLePresent) {
            Toast.makeText(this, "This device does not support BTLE. Cannot scan...", Toast.LENGTH_LONG).show();
            return;
        }

        mDeviceStore.clear();

        mRecyclerAdapter = new DeviceRecyclerAdapter(mCore);
        view.setListAdapter(mRecyclerAdapter);

        mScanner.getBluetoothAdapter().askUserToEnableBluetoothIfNeeded(this);
        if (isBluetoothOn) {
            //Start Bluetooth Scanning
            setBleStatus("Scanning Device...");
            mScanner.startScan();
            invalidateOptionsMenu();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    private class ScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Receive the Web Server Event From Background Service
            boolean isStarted = intent.getBooleanExtra(SERVER_EVENT, false);
            if (isStarted) {
                Toast.makeText(MainActivity.this, "Server Started", Toast.LENGTH_SHORT).show();
            }
            setServerUI(isStarted);
        }
    }
    private class GattReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra(GATT_EVENT);
            //Set Text According to Iot Device Connectivity Status
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(status)) {
                setBleStatus("Connected to LOCK-01");
                getServiceInstance().setBltScanner(appPref);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(status)) {
                setBleStatus("LOCK-01 Disconnected");
            } else if (BluetoothLeService.ACTION_GATT_CONNECTING.equals(status)) {
                setBleStatus("Connecting to LOCK-01...");
            }
        }
    }
}