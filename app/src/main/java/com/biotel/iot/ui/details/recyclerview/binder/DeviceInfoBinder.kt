package com.biotel.iot.ui.details.recyclerview.binder

import android.content.Context
import com.biotel.iot.R
import com.biotel.iot.ui.common.recyclerview.BaseViewBinder
import com.biotel.iot.ui.common.recyclerview.BaseViewHolder
import com.biotel.iot.ui.common.recyclerview.RecyclerViewItem
import com.biotel.iot.ui.details.recyclerview.holder.DeviceInfoHolder
import com.biotel.iot.ui.details.recyclerview.model.DeviceInfoItem

class DeviceInfoBinder(context: Context) : BaseViewBinder<DeviceInfoItem>(context) {

    override fun bind(holder: BaseViewHolder<DeviceInfoItem>, item: DeviceInfoItem) {
        val actualHolder = holder as DeviceInfoHolder
        actualHolder.name.text = item.name
        actualHolder.address.text = item.address
        actualHolder.deviceClass.text = item.bluetoothDeviceClassName
        actualHolder.majorClass.text = item.bluetoothDeviceMajorClassName
        actualHolder.bondingState.text = item.bluetoothDeviceBondState
        actualHolder.services.text = createSupportedDevicesString(item)
    }

    private fun createSupportedDevicesString(item: DeviceInfoItem): String {
        val retVal: String
        retVal = if (item.bluetoothDeviceKnownSupportedServices.isEmpty()) {
            context.getString(R.string.no_known_services)
        } else {
            val sb = StringBuilder()
            for (service in item.bluetoothDeviceKnownSupportedServices) {
                if (sb.isNotEmpty()) {
                    sb.append(", ")
                }
                sb.append(service)
            }
            sb.toString()
        }
        return retVal
    }

    override fun canBind(item: RecyclerViewItem): Boolean {
        return item is DeviceInfoItem
    }
}