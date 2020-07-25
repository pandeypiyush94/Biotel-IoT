package com.biotel.iot.ui.details.recyclerview.model

import com.biotel.iot.ui.common.recyclerview.RecyclerViewItem
import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice

class RssiItem(private val mDevice: BluetoothLeDevice) : RecyclerViewItem {
    val rssi: Int
        get() = mDevice.rssi

    val runningAverageRssi: Double
        get() = mDevice.runningAverageRssi

    val firstRssi: Int
        get() = mDevice.firstRssi

    val firstTimestamp: Long
        get() = mDevice.firstTimestamp

    val timestamp: Long
        get() = mDevice.timestamp

}