package com.biotel.iot.ui.details.recyclerview.binder

import android.content.Context
import com.biotel.iot.ui.common.recyclerview.BaseViewBinder
import com.biotel.iot.ui.common.recyclerview.BaseViewHolder
import com.biotel.iot.ui.common.recyclerview.RecyclerViewItem
import com.biotel.iot.ui.details.recyclerview.holder.HeaderHolder
import com.biotel.iot.ui.details.recyclerview.model.HeaderItem

class HeaderBinder(context: Context) : BaseViewBinder<HeaderItem>(context) {

    override fun bind(holder: BaseViewHolder<HeaderItem>, item: HeaderItem) {
        val actualHolder = holder as HeaderHolder
        actualHolder.textView.text = item.text
    }

    override fun canBind(item: RecyclerViewItem): Boolean {
        return item is HeaderItem
    }

}