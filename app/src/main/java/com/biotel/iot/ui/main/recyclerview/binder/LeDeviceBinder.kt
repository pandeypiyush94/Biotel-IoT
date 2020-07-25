package com.biotel.iot.ui.main.recyclerview.binder

import android.content.Context
import com.biotel.iot.ui.common.Navigation
import com.biotel.iot.ui.common.recyclerview.BaseViewBinder
import com.biotel.iot.ui.common.recyclerview.BaseViewHolder
import com.biotel.iot.ui.common.recyclerview.RecyclerViewItem
import com.biotel.iot.ui.main.recyclerview.holder.LeDeviceHolder
import com.biotel.iot.ui.main.recyclerview.model.LeDeviceItem

class LeDeviceBinder(context: Context, private val navigation: Navigation) : BaseViewBinder<LeDeviceItem>(context) {

    override fun bind(holder: BaseViewHolder<LeDeviceItem>, item: LeDeviceItem) {
        val actualHolder = holder as LeDeviceHolder
        val device = item.device

        CommonBinding.bind(context, actualHolder, device)
        actualHolder.view.setOnClickListener {
            navigation.openDetailsActivity(device)
        }
    }

    override fun canBind(item: RecyclerViewItem): Boolean {
        return item is LeDeviceItem
    }

}