package com.biotel.iot.ui.details.recyclerview.model

import com.biotel.iot.ui.common.recyclerview.RecyclerViewItem
import uk.co.alt236.bluetoothlelib.device.beacon.ibeacon.IBeaconManufacturerData

class IBeaconItem(iBeaconData: IBeaconManufacturerData) : RecyclerViewItem {
    val major: Int = iBeaconData.major
    val minor: Int = iBeaconData.minor
    val uuid: String = iBeaconData.uuid
    val companyIdentifier: Int = iBeaconData.companyIdentifier
    val iBeaconAdvertisement: Int = iBeaconData.iBeaconAdvertisement
    val calibratedTxPower: Int = iBeaconData.calibratedTxPower
}