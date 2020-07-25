package com.biotel.iot.services

/**
 * Created By Piyush Pandey on 18-07-2020
 */
interface OnQueryListener {
    fun onQueryParamsReceived(params : Map<String, String>?) : String
}