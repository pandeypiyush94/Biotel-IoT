package com.biotel.iot.services

import android.os.Binder

class LocalBinder(val service: BluetoothLeService) : Binder()