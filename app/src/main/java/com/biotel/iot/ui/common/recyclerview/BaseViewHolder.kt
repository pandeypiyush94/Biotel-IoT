package com.biotel.iot.ui.common.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder

abstract class BaseViewHolder<T : RecyclerViewItem>(val view: View) : ViewHolder(view)