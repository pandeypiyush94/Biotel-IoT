package com.biotel.iot.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.biotel.iot.R;
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

    private IBinder mBinder;
    private AppServer mServer;
    private ServiceReceiver receiver;

    private boolean isServerStarted;

    private static WebServerService serviceInstance;
    public static WebServerService getServiceInstance() {
        return serviceInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        isServerStarted = false;

        receiver = new ServiceReceiver();
        mServer = new AppServer(this);
        mBinder = new ServerBinder(this);

        registerReceiver(receiver, new IntentFilter(SERVER_INTENT));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //String input = intent.getStringExtra("inputExtra");

        serviceInstance = this;

        createNotificationChannel();
        startForeground(1, createNotification());
        Log.e("check_server", "Service Started On Foreground");

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

    public AppServer getServer() {
        return mServer;
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

    private class ServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean startServer = intent.getBooleanExtra(BUTTON_EVENT, false);
            if (startServer) {
                Intent screenIntent = new Intent(SCREEN_INTENT);
                if (!isServerStarted && startServer()) {
                    screenIntent.putExtra(SERVER_EVENT, true);
                } else if (isServerStarted && stopServer()){
                    screenIntent.putExtra(SERVER_EVENT, false);
                }
                sendBroadcast(screenIntent);
            }
        }
    }
}
