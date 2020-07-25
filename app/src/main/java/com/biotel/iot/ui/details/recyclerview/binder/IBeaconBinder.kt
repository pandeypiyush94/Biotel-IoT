package com.biotel.iot.ui.details.recyclerview.binder

import android.content.Context
import com.biotel.iot.R
import com.biotel.iot.ui.common.recyclerview.BaseViewBinder
import com.biotel.iot.ui.common.recyclerview.BaseViewHolder
import com.biotel.iot.ui.common.recyclerview.RecyclerViewItem
import com.biotel.iot.ui.details.recyclerview.holder.IBeaconHolder
import com.biotel.iot.ui.details.recyclerview.model.IBeaconItem
import com.biotel.iot.util.TimeFormatter
import uk.co.alt236.bluetoothlelib.resolvers.CompanyIdentifierResolver
import java.util.*

class IBeaconBinder(context: Context) : BaseViewBinder<IBeaconItem>(context) {

    override fun bind(holder: BaseViewHolder<IBeaconItem>, item: IBeaconItem) {
        val actualHolder = holder as IBeaconHolder
        val companyName = CompanyIdentifierResolver.getCompanyName(
                item.companyIdentifier,
                context.getString(R.string.unknown))
        actualHolder.companyId.text = getWithHexEncode(companyName, item.companyIdentifier)
        actualHolder.advert.text = getWithHexEncode(item.iBeaconAdvertisement)
        actualHolder.uuid.text = item.uuid
        actualHolder.major.text = getWithHexEncode(item.major)
        actualHolder.minor.text = getWithHexEncode(item.minor)
        actualHolder.txPower.text = getWithHexEncode(item.calibratedTxPower)
    }

    override fun canBind(item: RecyclerViewItem): Boolean {
        return item is IBeaconItem
    }

    companion object {
        private const val STRING_FORMAT = "%s (%s)"

        private fun getWithHexEncode(first: String, value: Int): String {
            return createLine(first, hexEncode(value))
        }

        private fun getWithHexEncode(value: Int): String {
            return createLine(value.toString(), hexEncode(value))
        }

        private fun createLine(first: String, second: String): String {
            return String.format(Locale.US, STRING_FORMAT, first, second)
        }

        private fun hexEncode(integer: Int): String {
            return "0x" + Integer.toHexString(integer).toUpperCase(Locale.US)
        }
    }
}