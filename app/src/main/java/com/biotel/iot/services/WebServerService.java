package com.biotel.iot.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.biotel.iot.R;
import com.biotel.iot.ui.main.MainActivity;
import com.biotel.iot.util.AppPref;
import com.biotel.iot.util.AppServer;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created By Piyush Pandey on 24-07-2020
 */
public class WebServerService extends Service {

    public static final String SERVER_INTENT = "com.biotel.iot.set_server";
    public static final String SCREEN_INTENT = "com.biotel.iot.set_screen";
    public static final String CHANNEL_ID = "com.biotel.iot.server_channel";
    public static final String SERVER_EVENT = "com.biotel.iot.server_event";
    public static final String BUTTON_EVENT = "com.biotel.iot.button_event";
    public static final String GATT_EVENT = "com.biotel.iot.gatt_event";
    public static final String GATT_INTENT = "com.biotel.iot.gatt_intent";
    public static final String PREF_INTENT = "com.biotel.iot.pref_intent";

    public static final String ADD_IOT = "EC:21:E5:0A:DD:B9";
    public static final String ADD_THERMAL = "EC:21:E5:B6:55:9D";

    private IBinder mBinder;
    private AppServer mServer;
    private ServiceReceiver serviceReceiver;

    private AppPref pref;
    private BluetoothLeService bltService;

    private boolean isServerStarted;

    private static WebServerService serviceInstance;
    public static WebServerService getServiceInstance() {
        return serviceInstance;
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTING);
        return intentFilter;
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //Bluetooth Service Got Connected
            bltService = ((LocalBinder) service).getService();
            bltService.setPref(pref);
            if (!bltService.initialize()) {
                Log.e("check_blt","Unable to initialize Bluetooth");
            }
            mServer.setListener(bltService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bltService = null;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        isServerStarted = false;

        serviceReceiver = new ServiceReceiver();
        mServer = new AppServer(this);
        mBinder = new ServerBinder(this);

        registerReceiver(serviceReceiver, new IntentFilter(SERVER_INTENT));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        pref = intent.getParcelableExtra(PREF_INTENT);

        serviceInstance = this;

        createNotificationChannel();
        startForeground(1, createNotification());
        Log.e("check_server", "Service Started On Foreground");
        initBluetoothService();

        //do heavy work on a background thread
        //stopSelf();
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(serviceReceiver);
        unregisterReceiver(mGattUpdateReceiver);
        unbindService(serviceConnection);
        Log.e("check_server", "Service Stopped");
    }

    private boolean stopServer() {
        if (mServer != null && isServerStarted) {
            isServerStarted = false;
            mServer.stop();
            return true;
        }
        return false;
    }
    private boolean startServer() {
        try {
            if (mServer != null && !isServerStarted) {
                isServerStarted = true;
                mServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Server Exception : $e", Toast.LENGTH_SHORT).show();
            return false;
        }
        return false;
    }
    public boolean isServerRunning() {
        return isServerStarted;
    }

    private void initBluetoothService() {
        //Initialize Bluetooth Service
        final Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        Log.e("check_server", "Bluetooth Initialized");
    }

    public AppServer getServer() {
        return mServer;
    }
    public void connectBluetooth(String deviceAddress) {
        bltService.connect(deviceAddress);
    }
    public void setBltScanner(AppPref pref) {
        bltService.setPref(pref);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Web Server Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Biotel")
                .setContentText("Running Web Server...")
                .setSmallIcon(R.drawable.icon_web)
                .build();
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            //Receive Iot Device Connectivity Status From Bluetooth Service
            Log.e("check_btl","Sending Broadcast :"+action);
            if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                bltService.filterCharacteristic(bltService.getSupportedGattServices());
            }
            Intent gattIntent = new Intent(GATT_INTENT);
            gattIntent.putExtra(GATT_EVENT, action);
            sendBroadcast(gattIntent);
            //Send Iot Device Connectivity Status to App Screen
        }
    };
    private class ServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Receive the button event from App Screen
            boolean startServer = intent.getBooleanExtra(BUTTON_EVENT, false);
            if (startServer) {
                Intent screenIntent = new Intent(SCREEN_INTENT);
                if (!isServerStarted && startServer()) {
                    //Start Web Server if not started
                    screenIntent.putExtra(SERVER_EVENT, true);
                } else if (isServerStarted && stopServer()){
                    //Stop Web Server if started
                    screenIntent.putExtra(SERVER_EVENT, false);
                }
                //Send Web Server Event to App Screen
                sendBroadcast(screenIntent);
            }
        }
    }
}
