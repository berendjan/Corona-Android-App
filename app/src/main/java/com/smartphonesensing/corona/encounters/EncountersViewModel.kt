package com.smartphonesensing.corona.encounters

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.smartphonesensing.corona.util.DP3THelper
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import nl.tudelft.ipv8.util.toHex
import org.dpppt.android.sdk.internal.database.models.Contact
import org.dpppt.android.sdk.internal.database.models.Handshake
import java.text.SimpleDateFormat
import java.util.*

class EncountersViewModel : ViewModel() {

    private val MAX_NUMBER_OF_MISSING_HANDSHAKES = 3

    private val _encountersList = MutableLiveData<List<ContactItem>>()
    val encountersList: LiveData<List<ContactItem>>
        get() = _encountersList

    private val _encounterSelected = MutableLiveData<ContactItem>()
    val encounterSelected: LiveData<ContactItem>
        get() = _encounterSelected

    private val _contactsList = MutableLiveData<List<Contact>>()
    val contactsList: LiveData<List<Contact>>
        get() = _contactsList

    var scanInterval: Long = 0
    var scanDuration: Long = 0

    fun updateEncountersList(handshakes: List<Handshake>, riskyHandshakesOnly: Boolean) {

        _encountersList.value = mergeHandshakes(handshakes, riskyHandshakesOnly)

    }

    fun updateContactsList(contacts: List<Contact>) {

        _contactsList.value = contacts

    }

    private fun mergeHandshakes(handshakes: List<Handshake>, riskyHandshakesOnly: Boolean): List<ContactItem> {

        val contacts: List<ContactItem> = DP3THelper.mergeHandshakesToContacts(handshakes, riskyHandshakesOnly)

        return contacts.sortedWith(kotlin.Comparator { c1: ContactItem, c2: ContactItem ->
            c1.endtime.compareTo(c2.endtime)
        })
    }

    fun onEncounterClicked(encounterItem: ContactItem) {
        _encounterSelected.value = encounterItem
    }

}


@Parcelize
class EncounterItem (
    var identifier: String = "",
    var dateTimeStart: String = "",
    var dateTimeEnd: String = "",
    var starttime: Long = 0,
    var endtime: Long = 0,
    var count: Int = 0,
    var expectedCount: Int = 0,
    var maxRSSI: Float = 0.0F,
    var maxTxPowerLevel: Float = 0.0F,
    var handshakes : @RawValue MutableList<Handshake> = mutableListOf()
) : Parcelable {

    companion object : Parceler<EncounterItem> {
        override fun EncounterItem.write(parcel: Parcel, flags: Int) {
            parcel.writeString(identifier)
            parcel.writeString(dateTimeStart)
            parcel.writeString(dateTimeEnd)
            parcel.writeLong(starttime)
            parcel.writeLong(endtime)
            parcel.writeInt(count)
            parcel.writeInt(expectedCount)
            parcel.writeFloat(maxRSSI)
            parcel.writeFloat(maxTxPowerLevel)
            parcel.writeList(listOf(handshakes))
        }

        override fun create(parcel: Parcel): EncounterItem  = EncounterItem(
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readLong(),
            parcel.readLong(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readFloat(),
            parcel.readFloat(),
            readMutableList(parcel)
        )

        private fun readMutableList(parcel: Parcel): MutableList<Handshake> {
            val list = mutableListOf<Handshake>()
            parcel.readList(list as List<*>, Handshake::class.java.classLoader)
            return list
        }
    }
}

class ContactItem (
    val contact: Contact,
    val starttime: Long,
    val endtime: Long,
    val handshakes: List<Handshake>,
    val avgRSSI : Double,
    val avgTxPowerLevel : Double,
    val avgAttenuationLevel: Double
) {
    val sdf = SimpleDateFormat("dd.MM HH:mm:ss")
    val starttimeString : String = sdf.format(Date(starttime))
    val endtimeString : String = sdf.format(Date(endtime))
    val identifier: String = contact.ephId.data.toHex()
    val avgRSSIInt: Int = avgRSSI.toInt()
    val avgTxPowerLevelInt: Int = avgTxPowerLevel.toInt()
    val avgAttenuationLevelInt: Int = avgAttenuationLevel.toInt()
}