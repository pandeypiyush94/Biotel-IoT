package com.biotel.iot.ui.main.recyclerview.binder;

import android.content.Context;

import androidx.annotation.NonNull;

import com.biotel.iot.R;
import com.biotel.iot.ui.common.Navigation;
import com.biotel.iot.ui.common.recyclerview.BaseViewBinder;
import com.biotel.iot.ui.common.recyclerview.BaseViewHolder;
import com.biotel.iot.ui.common.recyclerview.RecyclerViewItem;
import com.biotel.iot.ui.main.recyclerview.holder.IBeaconHolder;

import uk.co.alt236.bluetoothlelib.device.beacon.ibeacon.IBeaconDevice;

import com.biotel.iot.ui.main.recyclerview.model.IBeaconItem;
import com.biotel.iot.util.Constants;

/**
 * Created By Piyush Pandey on 18-07-2020
 */
public class IBeaconBinder extends BaseViewBinder<IBeaconItem> {

    private final Navigation navigation;

    public IBeaconBinder(Context context, Navigation navigation) {
        super(context);
        this.navigation = navigation;
    }

    @Override
    public void bind(@NonNull BaseViewHolder<IBeaconItem> holder, @NonNull IBeaconItem item) {

        final IBeaconHolder actualHolder = (IBeaconHolder) holder;
        final IBeaconDevice device = item.getDevice();

        final String accuracy = Constants.DOUBLE_TWO_DIGIT_ACCURACY.format(device.getAccuracy());

        actualHolder.getIbeaconMajor().setText(String.valueOf(device.getMajor()));
        actualHolder.getIbeaconMinor().setText(String.valueOf(device.getMinor()));
        actualHolder.getIbeaconTxPower().setText(String.valueOf(device.getCalibratedTxPower()));
        actualHolder.getIbeaconUUID().setText(device.getUUID());
        actualHolder.getIbeaconDistance().setText(
                getContext().getString(R.string.formatter_meters, accuracy));
        actualHolder.getIbeaconDistanceDescriptor().setText(device.getDistanceDescriptor().toString());

        CommonBinding.bind(getContext(), actualHolder, device);
        actualHolder.getView().setOnClickListener(view -> navigation.openDetailsActivity(device));
    }

    @Override
    public boolean canBind(RecyclerViewItem item) {
        return item instanceof IBeaconItem;
    }
}
