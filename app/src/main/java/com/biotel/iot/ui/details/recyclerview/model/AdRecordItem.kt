package com.biotel.iot.ui.details.recyclerview.model

import com.biotel.iot.kt.ByteArrayExt.toCharString
import com.biotel.iot.ui.common.recyclerview.RecyclerViewItem
import uk.co.alt236.bluetoothlelib.device.adrecord.AdRecord
import uk.co.alt236.bluetoothlelib.util.AdRecordUtils

class AdRecordItem(val title: String,
                   record: AdRecord) : RecyclerViewItem {

    val data: ByteArray = record.data ?: ByteArray(0)
    val dataAsString: String = AdRecordUtils.getRecordDataAsString(record)
    val dataAsChars: String = data.toCharString()
}