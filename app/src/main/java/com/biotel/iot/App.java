package com.biotel.iot;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.biotel.iot.util.AppPref;

/**
 * Created By Piyush Pandey on 20-07-2020
 */
public class App extends Application {

    private AppPref pref;
    private static App mInstance ;

    public static void showMsg(Context context, String msg) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        } else  {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static synchronized App getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        pref = new AppPref(this);
    }

    public AppPref getAppPref() {
        return pref;
    }
}
