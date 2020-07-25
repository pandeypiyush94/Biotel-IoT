package com.biotel.iot.ui.main

import android.app.Activity
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.biotel.iot.R
import com.biotel.iot.ui.common.recyclerview.BaseRecyclerViewAdapter

class View(activity: Activity) {
    private val mTvBluetoothLeStatus: TextView = activity.findViewById(R.id.tvBluetoothLe)
    private var mTvBluetoothStatus: TextView = activity.findViewById(R.id.tvBluetoothStatus)
    private var mList: RecyclerView = activity.findViewById(android.R.id.list)
    private var btnServer: Button = activity.findViewById(R.id.btn_server)
    private var tvServerIP: TextView = activity.findViewById(R.id.server_ip)

    init {
        mList.layoutManager = LinearLayoutManager(activity)
    }

    fun setBluetoothEnabled(enabled: Boolean) {
        if (enabled) {
            mTvBluetoothStatus.setText(R.string.on)
        } else {
            mTvBluetoothStatus.setText(R.string.off)
        }
    }

    fun setBluetoothLeSupported(supported: Boolean) {
        if (supported) {
            mTvBluetoothLeStatus.setText(R.string.supported)
        } else {
            mTvBluetoothLeStatus.setText(R.string.not_supported)
        }
    }

    fun setListAdapter(adapter: BaseRecyclerViewAdapter) {
        mList.adapter = adapter
    }

    fun setServerListener(listener: View.OnClickListener) {
        btnServer.setOnClickListener(listener)
    }

    fun changeServerText(serverText : String) {
        btnServer.text = serverText
    }

    fun setIpAddress(address : String) {
        tvServerIP.text = address
    }
}