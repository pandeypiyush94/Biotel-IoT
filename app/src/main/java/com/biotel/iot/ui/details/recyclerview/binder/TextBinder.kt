package com.biotel.iot.ui.details.recyclerview.binder

import android.content.Context
import com.biotel.iot.ui.common.recyclerview.BaseViewBinder
import com.biotel.iot.ui.common.recyclerview.BaseViewHolder
import com.biotel.iot.ui.common.recyclerview.RecyclerViewItem
import com.biotel.iot.ui.details.recyclerview.holder.TextHolder
import com.biotel.iot.ui.details.recyclerview.model.TextItem

class TextBinder(context: Context) : BaseViewBinder<TextItem>(context) {

    override fun bind(holder: BaseViewHolder<TextItem>, item: TextItem) {
        val actualHolder = holder as TextHolder
        actualHolder.textView.text = item.text
    }

    override fun canBind(item: RecyclerViewItem): Boolean {
        return item is TextItem
    }

}