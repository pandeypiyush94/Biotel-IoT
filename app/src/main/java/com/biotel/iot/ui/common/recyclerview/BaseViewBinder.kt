package com.biotel.iot.ui.common.recyclerview

import android.content.Context
import androidx.annotation.StringRes
import com.biotel.iot.R

abstract class BaseViewBinder<T : RecyclerViewItem>(protected val context: Context) {
    abstract fun bind(holder: BaseViewHolder<T>, item: T)
    abstract fun canBind(item: RecyclerViewItem): Boolean

    protected fun getString(@StringRes id: Int): String {
        return context.getString(id)
    }

    protected fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String {
        return context.getString(resId, *formatArgs)
    }

    protected fun getQuotedString(vararg formatArgs: Any?): String {
        return getString(R.string.formatter_single_quoted_string, *formatArgs)
    }
}