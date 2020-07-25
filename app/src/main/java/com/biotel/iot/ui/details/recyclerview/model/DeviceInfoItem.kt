package com.biotel.iot.ui.details.recyclerview.model

import com.biotel.iot.ui.common.recyclerview.RecyclerViewItem
import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice
import uk.co.alt236.bluetoothlelib.device.BluetoothService

class DeviceInfoItem(private val mDevice: BluetoothLeDevice) : RecyclerViewItem {

    val bluetoothDeviceKnownSupportedServices: Set<BluetoothService>
        get() = mDevice.bluetoothDeviceKnownSupportedServices

    val bluetoothDeviceBondState: String
        get() = mDevice.bluetoothDeviceBondState

    val bluetoothDeviceMajorClassName: String
        get() = mDevice.bluetoothDeviceMajorClassName

    val bluetoothDeviceClassName: String
        get() = mDevice.bluetoothDeviceClassName

    val address: String
        get() = mDevice.address

    val name: String
        get() = mDevice.name ?: ""

}