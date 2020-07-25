package com.biotel.iot.ui.details.recyclerview.binder

import android.content.Context
import com.biotel.iot.R
import com.biotel.iot.ui.common.recyclerview.BaseViewBinder
import com.biotel.iot.ui.common.recyclerview.BaseViewHolder
import com.biotel.iot.ui.common.recyclerview.RecyclerViewItem
import com.biotel.iot.ui.details.recyclerview.holder.RssiInfoHolder
import com.biotel.iot.ui.details.recyclerview.model.RssiItem
import com.biotel.iot.util.TimeFormatter

class RssiBinder(context: Context) : BaseViewBinder<RssiItem>(context) {

    override fun bind(holder: BaseViewHolder<RssiItem>, item: RssiItem) {
        val actualHolder = holder as RssiInfoHolder
        actualHolder.firstTimestamp.text = formatTime(item.firstTimestamp)
        actualHolder.firstRssi.text = formatRssi(item.firstRssi)
        actualHolder.lastTimestamp.text = formatTime(item.timestamp)
        actualHolder.lastRssi.text = formatRssi(item.rssi)
        actualHolder.runningAverageRssi.text = formatRssi(item.runningAverageRssi)
    }

    override fun canBind(item: RecyclerViewItem): Boolean {
        return item is RssiItem
    }

    private fun formatRssi(rssi: Double): String {
        return getString(R.string.formatter_db, rssi.toString())
    }

    private fun formatRssi(rssi: Int): String {
        return getString(R.string.formatter_db, rssi.toString())
    }

    companion object {
        private fun formatTime(time: Long): String {
            return TimeFormatter.getIsoDateTime(time)
        }
    }
}