package com.biotel.iot.util;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created By Piyush Pandey on 20-07-2020
 */
public class AppPref {

    private BluetoothLeScanner mScanner;

    public BluetoothLeScanner getBtScanner() {
        return mScanner;
    }

    public AppPref(Context context) {
        mScanner = new BluetoothLeScanner(new BluetoothAdapterWrapper(context));
    }
}
