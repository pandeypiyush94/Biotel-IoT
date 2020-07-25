package com.biotel.iot.services

import android.os.Binder

/**
 * Created By Piyush Pandey on 24-07-2020
 */
class ServerBinder(val service : WebServerService) : Binder()