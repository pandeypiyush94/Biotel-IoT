package com.biotel.iot.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created By Piyush Pandey on 20-07-2020
 */
public class AppPref {

    private BluetoothLeScanner mScanner;

    private static final String PREF_LAST_STATUS = "biotel_iot_pref_last_status";
    private static final String PREFERENCE_NAME = "biotel_iot_pref";
    private SharedPreferences preference;
    private SharedPreferences.Editor editor;

    public BluetoothLeScanner getBtScanner() {
        return mScanner;
    }

    public AppPref(Context context) {
        mScanner = new BluetoothLeScanner(new BluetoothAdapterWrapper(context));
        preference = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        editor = preference.edit();
        editor.apply();
    }

    public void storeLastStatus(String lastStatus) {
        editor.putString(PREF_LAST_STATUS, lastStatus);
        editor.apply();
    }

    public String getLastStatus() {
        return preference.getString(PREF_LAST_STATUS, "No Found Found");
    }
}
