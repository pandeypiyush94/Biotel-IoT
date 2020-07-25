package com.biotel.iot.ui.details.recyclerview.holder

import android.view.View
import android.widget.TextView
import com.biotel.iot.R
import com.biotel.iot.ui.common.recyclerview.BaseViewHolder
import com.biotel.iot.ui.details.recyclerview.model.AdRecordItem

class AdRecordHolder(itemView: View) : BaseViewHolder<AdRecordItem>(itemView) {
    val stringTextView: TextView = itemView.findViewById<View>(R.id.data_as_string) as TextView
    val lengthTextView: TextView = itemView.findViewById<View>(R.id.length) as TextView
    val arrayTextView: TextView = itemView.findViewById<View>(R.id.data_as_array) as TextView
    val charactersTextView: TextView = itemView.findViewById<View>(R.id.data_as_characters) as TextView
    val titleTextView: TextView = itemView.findViewById<View>(R.id.title) as TextView
}