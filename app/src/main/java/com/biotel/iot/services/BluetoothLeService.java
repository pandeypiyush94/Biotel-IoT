/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.biotel.iot.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.biotel.iot.util.BluetoothLeScanner;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;
import uk.co.alt236.bluetoothlelib.device.adrecord.AdRecord;
import uk.co.alt236.bluetoothlelib.util.ByteUtils;
import com.biotel.iot.util.AppPref;

import static com.biotel.iot.services.WebServerService.ADD_IOT;
import static com.biotel.iot.services.WebServerService.ADD_THERMAL;

/**
 * Created By Piyush Pandey on 18-07-2020
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService extends Service implements OnQueryListener {
    public final static String ACTION_GATT_CONNECTED = BluetoothLeService.class.getName() + ".ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_CONNECTING = BluetoothLeService.class.getName() + ".ACTION_GATT_CONNECTING";
    public final static String ACTION_GATT_DISCONNECTED = BluetoothLeService.class.getName() + ".ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = BluetoothLeService.class.getName() + ".ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = BluetoothLeService.class.getName() + ".ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA_RAW = BluetoothLeService.class.getName() + ".EXTRA_DATA_RAW";
    public final static String EXTRA_UUID_CHAR = BluetoothLeService.class.getName() + ".EXTRA_UUID_CHAR";

    private final static String TAG = BluetoothLeService.class.getSimpleName();
    private final IBinder mBinder = new LocalBinder(this);
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;

    private AppPref pref;
    private BluetoothLeScanner mScanner;
    private String thermalDataResponse = "Device Not Found";

    private BluetoothGattService gattService = null;
    private BluetoothGattCharacteristic gattCharacteristic = null;

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(characteristic);
        }

        @Override
        public void onCharacteristicRead(final BluetoothGatt gatt,
                                         final BluetoothGattCharacteristic characteristic,
                                         final int status) {

            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(characteristic);
            }
        }

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt,
                                            final int status,
                                            final int newState) {

            Log.d(TAG, "onConnectionStateChange: status=" + status + ", newState=" + newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                setConnectionState(State.CONNECTED, true);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // Make sure we tidy up. On certain devices reusing a Gatt after a disconnection
                // can cause problems.
                disconnect();

                setConnectionState(State.DISCONNECTED, true);
                Log.i(TAG, "Disconnected from GATT server.");
                //Connect Again to Iot Device if got Disconnected
                connect(ADD_IOT);
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    gatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
                }
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }
    };

    private final BluetoothAdapter.LeScanCallback callback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            final BluetoothLeDevice deviceLe = new BluetoothLeDevice(device, rssi, scanRecord, System.currentTimeMillis());
            //Check for Thermal Device
            if (deviceLe.getAddress().equals(ADD_THERMAL)) {
                final Collection<AdRecord> adRecords = deviceLe.getAdRecordStore().getRecordsAsCollection();
                if (adRecords.size() > 0) {
                    for (final AdRecord record : adRecords) {
                        //Filter out Thermal Array from Device's Advertisements
                        if (record.getType() == 255) {
                            thermalDataResponse = ByteUtils.byteArrayToHexString(record.getData());
                            //Stop Scan
                            mScanner.stopScan("Thermal Device Found");
                        }
                    }
                }
            }
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        //Send Iot Device Connectivity Status To Background Service
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intent.putExtra(EXTRA_UUID_CHAR, characteristic.getUuid().toString());

        // Always try to add the RAW value
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            intent.putExtra(EXTRA_DATA_RAW, data);
        }

        sendBroadcast(intent);
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final String address) {

        final boolean retVal;
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            retVal = false;

            // Previously connected device.  Try to reconnect.
        } else if (mBluetoothDeviceAddress != null
                && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {

            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                Log.d(TAG, "Connection attempt OK.");
                setConnectionState(State.CONNECTING, true);
                retVal = true;
            } else {
                Log.w(TAG, "Connection attempt failed.");
                setConnectionState(State.DISCONNECTED, true);
                retVal = false;
            }
        } else {

            final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            if (device == null) {
                Log.w(TAG, "Device not found.  Unable to connect.");
                retVal = false;
            } else {
                // We want to directly connect to the device, so we are setting the autoConnect
                // parameter to false.

                Log.d(TAG, "Trying to create a new connection.");
                mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
                mBluetoothDeviceAddress = address;
                setConnectionState(State.CONNECTING, true);
                retVal = true;
            }
        }

        return retVal;
    }

    private synchronized void setConnectionState(final State newState, final boolean broadCast) {
        Log.i(TAG, "Setting internal state to " + newState);

        final String broadcastAction;

        switch (newState) {
            case CONNECTED:
                broadcastAction = ACTION_GATT_CONNECTED;
                break;
            case CONNECTING:
                broadcastAction = ACTION_GATT_CONNECTING;
                break;
            case DISCONNECTED:
                broadcastAction = ACTION_GATT_DISCONNECTED;
                break;
            default:
                throw new IllegalArgumentException("Unknown state: " + newState);
        }

        if (broadCast) {
            Log.i(TAG, "Broadcasting " + broadcastAction);
            broadcastUpdate(broadcastAction);
        }

    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        Log.e("check_blt", (mBluetoothAdapter == null) +":" + (mBluetoothGatt == null) );
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();

        // Reusing a Gatt after disconnecting can cause problems
        mBluetoothGatt = null;
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    public void setPref(AppPref pref) {
        this.pref = pref;
    }

    private String lockUnlockDoor(String key) {
        if (gattCharacteristic != null) {
            int characProp = gattCharacteristic.getProperties();
            if ((characProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                //Send/Write Door Key To IoT Device
                writeCharacteristic(gattCharacteristic, key);
                if (key.equalsIgnoreCase("5A01A5")) {
                    return "Door Locked";
                } else {
                    return "Door Unlocked";
                }
            }
        } else {
            return "Door Lock Not Found";
        }
        return "Issue With IoT Device";
    }

    private String scanSendThermalData() {
        //Get the Bluetooth Scanner Object
        mScanner = pref.getBtScanner();
        mScanner.setScanCallbackListener(callback);
        if (mScanner.getBluetoothAdapter().isBluetoothOn()) {
            //Start Bluetooth Scan For Thermal Device
            mScanner.startScan();
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        //Stop Bluetooth Scan After 2000ms(2s)
        mScanner.stopScan("Returning Thermal Device");
        mScanner.removeScanCallbackListener();
        mScanner = null;
        return thermalDataResponse;
    }

    public void filterCharacteristic(List<BluetoothGattService> gattServices) {
        for (BluetoothGattService service : gattServices) {
            if ("00005453-4220-7479-7065-204100000001".equals(service.getUuid().toString())) {
                gattService = service;
                break;
            }
        }
        if (gattService != null) {
            for (BluetoothGattCharacteristic charac : gattService.getCharacteristics()) {
                if ("00005453-4220-7479-7065-204100000301".equals(charac.getUuid().toString())) {
                    gattCharacteristic = charac;
                    break;
                }
            }
        }
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        return super.onUnbind(intent);
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(final BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    public void writeCharacteristic(final BluetoothGattCharacteristic characteristic, String value) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter isn't Initialized");
            return;
        }
        int len = value.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(value.charAt(i), 16) << 4)
                    + Character.digit(value.charAt(i + 1), 16));
        }
        characteristic.setValue(data);
        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(final BluetoothGattCharacteristic characteristic, final boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
    }

    @Override
    @NotNull
    public String onQueryParamsReceived(@Nullable Map<String, String> params) {
        Log.e("check_params", params.toString());
        if (params != null && !params.isEmpty()) {
            String doorAction = params.get("doorAction");
            String thermalData = params.get("thermalData");
            if (doorAction != null) {
                //Perform Door Operation if its Door Param
                return lockUnlockDoor(doorAction);
            } else if (thermalData != null) {
                //Perform Thermal Operation if its Thermal Param
                return scanSendThermalData();
            }
        }
        return "Success";
    }
}