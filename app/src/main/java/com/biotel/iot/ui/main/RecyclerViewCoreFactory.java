package com.biotel.iot.ui.main;

import android.content.Context;

import com.biotel.iot.R;
import com.biotel.iot.ui.common.Navigation;
import com.biotel.iot.ui.common.recyclerview.RecyclerViewBinderCore;
import com.biotel.iot.ui.main.recyclerview.binder.IBeaconBinder;
import com.biotel.iot.ui.main.recyclerview.binder.LeDeviceBinder;
import com.biotel.iot.ui.main.recyclerview.holder.IBeaconHolder;
import com.biotel.iot.ui.main.recyclerview.holder.LeDeviceHolder;

final class RecyclerViewCoreFactory {

    public static RecyclerViewBinderCore create(final Context context, final Navigation navigation) {
        final RecyclerViewBinderCore core = new RecyclerViewBinderCore();

        core.add(new IBeaconBinder(context, navigation), IBeaconHolder.class, R.layout.list_item_device_ibeacon);
        core.add(new LeDeviceBinder(context, navigation), LeDeviceHolder.class, R.layout.list_item_device_le);

        return core;
    }

}
