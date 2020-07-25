package com.biotel.iot.ui.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.biotel.iot.R;
import com.biotel.iot.containers.BaseScreen;
import com.biotel.iot.ui.common.Navigation;
import com.biotel.iot.ui.common.recyclerview.RecyclerViewBinderCore;
import com.biotel.iot.ui.common.recyclerview.RecyclerViewItem;
import com.biotel.iot.ui.details.recyclerview.model.AdRecordItem;
import com.biotel.iot.ui.details.recyclerview.model.DeviceInfoItem;
import com.biotel.iot.ui.details.recyclerview.model.HeaderItem;
import com.biotel.iot.ui.details.recyclerview.model.IBeaconItem;
import com.biotel.iot.ui.details.recyclerview.model.TextItem;

import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;
import uk.co.alt236.bluetoothlelib.device.adrecord.AdRecord;
import uk.co.alt236.bluetoothlelib.device.beacon.BeaconType;
import uk.co.alt236.bluetoothlelib.device.beacon.BeaconUtils;
import uk.co.alt236.bluetoothlelib.device.beacon.ibeacon.IBeaconManufacturerData;

/**
 * Created By Piyush Pandey on 18-07-2020
 */
public class DeviceDetailsActivity extends BaseScreen {
    private static final String EXTRA_DEVICE = DeviceDetailsActivity.class.getName() + ".EXTRA_DEVICE";
    private static final int LAYOUT_ID = R.layout.activity_details;

    protected RecyclerView mRecycler;
    private BluetoothLeDevice mDevice;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT_ID);
        mRecycler = findViewById(R.id.recycler);

        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mDevice = getIntent().getParcelableExtra(EXTRA_DEVICE);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mDevice.getName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        populateDetails(mDevice);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                new Navigation(this).startControlActivity(mDevice);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateDetails(final BluetoothLeDevice device) {
        final List<RecyclerViewItem> list = new ArrayList<>();

        if (device == null) {
            list.add(new HeaderItem(getString(R.string.header_device_info)));
            list.add(new TextItem(getString(R.string.invalid_device_data)));
        } else {
            list.add(new HeaderItem(getString(R.string.header_device_info)));
            list.add(new DeviceInfoItem(device));

            final Collection<AdRecord> adRecords = device.getAdRecordStore().getRecordsAsCollection();
            if (adRecords.size() > 0) {
                list.add(new HeaderItem(getString(R.string.header_raw_ad_records)));

                for (final AdRecord record : adRecords) {
                    final String title = "#" + record.getType() + " " + record.getHumanReadableType();
                    AdRecordItem item = new AdRecordItem(title, record);
                    list.add(item);
                }
            }

            final boolean isIBeacon = BeaconUtils.getBeaconType(device) == BeaconType.IBEACON;
            if (isIBeacon) {
                final IBeaconManufacturerData iBeaconData = new IBeaconManufacturerData(device);
                list.add(new HeaderItem(getString(R.string.header_ibeacon_data)));
                list.add(new IBeaconItem(iBeaconData));
            }

        }

        final RecyclerViewBinderCore core = RecyclerViewCoreFactory.create(this);
        mRecycler.setAdapter(new DetailsRecyclerAdapter(core, list));
    }

    public static Intent createIntent(Context context, BluetoothLeDevice device) {
        final Intent intent = new Intent(context, DeviceDetailsActivity.class);
        intent.putExtra(DeviceDetailsActivity.EXTRA_DEVICE, device);

        return intent;
    }
}
