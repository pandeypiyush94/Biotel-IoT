package com.biotel.iot.ui.details.recyclerview.binder

import android.content.Context
import com.biotel.iot.ui.common.recyclerview.BaseViewBinder
import com.biotel.iot.ui.common.recyclerview.BaseViewHolder
import com.biotel.iot.ui.common.recyclerview.RecyclerViewItem
import com.biotel.iot.ui.details.recyclerview.holder.AdRecordHolder
import com.biotel.iot.ui.details.recyclerview.model.AdRecordItem
import uk.co.alt236.bluetoothlelib.util.ByteUtils

class AdRecordBinder(context: Context) : BaseViewBinder<AdRecordItem>(context) {

    override fun bind(holder: BaseViewHolder<AdRecordItem>, item: AdRecordItem) {
        val actualHolder = holder as AdRecordHolder

        actualHolder.titleTextView.text = item.title
        actualHolder.lengthTextView.text = item.data.size.toString()

        actualHolder.stringTextView.text = getQuotedString(item.dataAsString)

        val hexString = ByteUtils.byteArrayToHexString(item.data)
        actualHolder.arrayTextView.text = getQuotedString(hexString)

        val charString = item.dataAsChars
        actualHolder.charactersTextView.text = getQuotedString(charString)
    }

    override fun canBind(item: RecyclerViewItem): Boolean {
        return item is AdRecordItem
    }
}