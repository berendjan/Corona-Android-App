package com.smartphonesensing.corona.encounters

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import org.dpppt.android.sdk.internal.database.models.Handshake
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil

class EncountersViewModel : ViewModel() {

    private val MAX_NUMBER_OF_MISSING_HANDSHAKES = 3

    private val _encountersList = MutableLiveData<List<EncounterItem>>()
    val encountersList: LiveData<List<EncounterItem>>
        get() = _encountersList

    var scanInterval: Long = 0
    var scanDuration: Long = 0

    fun updateEncountersList(handshakes: List<Handshake>) {

        _encountersList.value = mergeHandshakes(handshakes)

    }

    private fun mergeHandshakes(handshakes: List<Handshake>): List<EncounterItem> {

        val groupedHandshakes = HashMap<String, MutableList<Handshake>?>()

        val sdf: SimpleDateFormat = SimpleDateFormat("dd.MM HH:mm:ss")

        for (handshake in handshakes) {
            val head = ByteArray(4)
            for (i in 0..3) {
                head[i] = handshake.ephId.data[i]
            }
            val identifier = String(head)
            if (!groupedHandshakes.containsKey(identifier)) {
                groupedHandshakes[identifier] = java.util.ArrayList()
            }
            groupedHandshakes[identifier]!!.add(handshake)
        }

        val result: MutableList<EncounterItem> = java.util.ArrayList()

        for ((key, value) in groupedHandshakes) {
            value?.sortWith(kotlin.Comparator { h1: Handshake, h2: Handshake ->
                h1.timestamp.compareTo(h2.timestamp)
            })
            var start = 0
            var end = 1
            while (end < value!!.size) {
                if (value[end].timestamp - value[end - 1].timestamp >
                    MAX_NUMBER_OF_MISSING_HANDSHAKES * scanInterval
                ) {
                    val interval = EncounterItem()
                    interval.identifier = key
                    interval.starttime = value[start].timestamp
                    interval.endtime = value[end - 1].timestamp

                    interval.dateTimeStart = sdf.format(Date(interval.starttime))
                    interval.dateTimeEnd = sdf.format(Date(interval.endtime))

                    interval.handshakes.add(value[end - 1])

                    interval.count = end - start
                    interval.expectedCount = 1 + ceil((interval.endtime - interval.starttime) *
                            1.0 / scanInterval).toInt() * (scanDuration.toInt() / 5120)
                    result.add(interval)
                    start = end
                }
                end++
            }
            val interval = EncounterItem()
            interval.identifier = key
            interval.starttime = value[start].timestamp
            interval.endtime = value[end - 1].timestamp

            interval.handshakes.add(value[end - 1])

            interval.dateTimeStart = sdf.format(Date(interval.starttime))
            interval.dateTimeEnd = sdf.format(Date(interval.endtime))

            interval.count = end - start
            interval.expectedCount = 1 + ceil((interval.endtime - interval.starttime) *
                    1.0 / scanInterval).toInt() * (scanDuration.toInt() / 5120)
            result.add(interval)
        }
        result.sortWith(kotlin.Comparator { h1: EncounterItem, h2: EncounterItem ->
            h2.endtime.compareTo(h1.endtime)
        })
        return result
    }

    fun onEncounterClicked(encounterItem: EncounterItem) {
        Log.i("Handshakes", encounterItem.handshakes.toString())
    }

}


data class User(var uid: String, var upcomingEvents: List<MutableMap<String, Any>>) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        readUpcomingEvents(parcel)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeList(upcomingEvents)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }

        private fun readUpcomingEvents(parcel: Parcel): List<MutableMap<String, Any>> {
            val list = mutableListOf<MutableMap<String, Any>>()
            parcel.readList(list as List<*>, MutableMap::class.java.classLoader)

            return list
        }
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

