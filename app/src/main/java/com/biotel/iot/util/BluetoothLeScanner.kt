package com.biotel.iot.util

import android.bluetooth.BluetoothAdapter.LeScanCallback
import android.os.Handler
import android.util.Log

@Suppress("MemberVisibilityCanBePrivate")
class BluetoothLeScanner(private val bluetoothAdapterWrapper: BluetoothAdapterWrapper) {
    private val mHandler: Handler = Handler()

    private var leScanCallback: LeScanCallback? = null

    var isScanning = false
        private set

    fun startScan() {
        scanLeDevice(duration = -1)
    }

    fun scanLeDevice(duration: Int) {
        bluetoothAdapterWrapper.bluetoothAdapter?.let { btAdapter ->
            if (isScanning) {
                return
            }
            // Stops scanning after a pre-defined scan period.
            if (duration > 0) {
                mHandler.postDelayed({
                    stopScan("timeout")
                }, duration.toLong())
            }

            Log.d(TAG, "~ Starting Scan (duration: $duration")
            isScanning = true
            val isStarted : Boolean = btAdapter.startLeScan(leScanCallback)
        }
    }

    fun stopScan(reason: String = "[not given]") {
        bluetoothAdapterWrapper.bluetoothAdapter?.let { btAdapter ->
            Log.d(TAG, "~ Stopping Scan - reason: $reason")
            isScanning = false
            btAdapter.stopLeScan(leScanCallback)
        }
    }

    fun setScanCallbackListener(leScanCallback: LeScanCallback) {
        this.leScanCallback = leScanCallback
    }

    fun removeScanCallbackListener() {
        this.leScanCallback = null
    }

    fun getBluetoothAdapter() : BluetoothAdapterWrapper {
        return bluetoothAdapterWrapper
    }

    private companion object {
        val TAG = BluetoothLeScanner::class.java.simpleName
    }

}