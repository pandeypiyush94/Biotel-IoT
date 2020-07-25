package com.biotel.iot.ui.main.recyclerview.holder

import android.widget.TextView

interface CommonDeviceHolder {
    val deviceName: TextView
    val deviceAddress: TextView
    val deviceRssi: TextView
    val deviceLastUpdated: TextView
}