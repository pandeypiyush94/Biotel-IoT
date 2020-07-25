package com.biotel.iot.ui.details;

import android.content.Context;

import com.biotel.iot.R;
import com.biotel.iot.ui.common.recyclerview.RecyclerViewBinderCore;
import com.biotel.iot.ui.details.recyclerview.binder.AdRecordBinder;
import com.biotel.iot.ui.details.recyclerview.binder.DeviceInfoBinder;
import com.biotel.iot.ui.details.recyclerview.binder.HeaderBinder;
import com.biotel.iot.ui.details.recyclerview.binder.IBeaconBinder;
import com.biotel.iot.ui.details.recyclerview.binder.RssiBinder;
import com.biotel.iot.ui.details.recyclerview.binder.TextBinder;
import com.biotel.iot.ui.details.recyclerview.holder.AdRecordHolder;
import com.biotel.iot.ui.details.recyclerview.holder.DeviceInfoHolder;
import com.biotel.iot.ui.details.recyclerview.holder.HeaderHolder;
import com.biotel.iot.ui.details.recyclerview.holder.IBeaconHolder;
import com.biotel.iot.ui.details.recyclerview.holder.RssiInfoHolder;
import com.biotel.iot.ui.details.recyclerview.holder.TextHolder;

final class RecyclerViewCoreFactory {

    public static RecyclerViewBinderCore create(final Context context) {
        final RecyclerViewBinderCore core = new RecyclerViewBinderCore();

        core.add(new TextBinder(context), TextHolder.class, R.layout.list_item_view_textview);
        core.add(new HeaderBinder(context), HeaderHolder.class, R.layout.list_item_view_header);
        core.add(new AdRecordBinder(context), AdRecordHolder.class, R.layout.list_item_view_adrecord);
        core.add(new RssiBinder(context), RssiInfoHolder.class, R.layout.list_item_view_rssi_info);
        core.add(new DeviceInfoBinder(context), DeviceInfoHolder.class, R.layout.list_item_view_device_info);
        core.add(new IBeaconBinder(context), IBeaconHolder.class, R.layout.list_item_view_ibeacon_details);

        return core;
    }

}
