package com.biotel.iot.ui.details.recyclerview.holder

import android.view.View
import android.widget.TextView
import com.biotel.iot.R
import com.biotel.iot.ui.common.recyclerview.BaseViewHolder
import com.biotel.iot.ui.details.recyclerview.model.TextItem

class TextHolder(itemView: View) : BaseViewHolder<TextItem>(itemView) {
    val textView: TextView = itemView.findViewById<View>(R.id.text) as TextView
}