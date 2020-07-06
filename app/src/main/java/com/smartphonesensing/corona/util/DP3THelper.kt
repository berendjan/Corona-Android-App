package com.smartphonesensing.corona.util

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Pair
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.smartphonesensing.corona.R
import com.smartphonesensing.corona.encounters.ContactItem
import com.smartphonesensing.corona.storage.SecureStorage
import org.dpppt.android.sdk.internal.AppConfigManager
import org.dpppt.android.sdk.internal.BluetoothAdvertiseMode
import org.dpppt.android.sdk.internal.BluetoothScanMode
import org.dpppt.android.sdk.internal.BluetoothTxPowerLevel
import org.dpppt.android.sdk.internal.backend.BackendBucketRepository
import org.dpppt.android.sdk.internal.backend.models.ExposeeRequest
import org.dpppt.android.sdk.internal.crypto.ContactsFactory
import org.dpppt.android.sdk.internal.crypto.CryptoModule
import org.dpppt.android.sdk.internal.crypto.EphId
import org.dpppt.android.sdk.internal.crypto.SKList
import org.dpppt.android.sdk.internal.database.Database
import org.dpppt.android.sdk.internal.database.models.Contact
import org.dpppt.android.sdk.internal.database.models.Handshake
import org.dpppt.android.sdk.internal.util.Base64Util
import org.dpppt.android.sdk.internal.util.DayDate
import java.util.*

object DP3THelper {

    private const val NUMBER_OF_DAYS_EXPOSED = 2

    private const val WINDOW_DURATION = 5 * 60 * 1000L

    private lateinit var context: Context

    private val _diagnosedContacts = MutableLiveData<List<Contact>>()
    val diagnosedContacts: LiveData<List<Contact>>
        get() = _diagnosedContacts

    private val _contacts = MutableLiveData<List<Contact>>()
    val contacts: LiveData<List<Contact>>
        get() = _contacts

    fun setContext(c: Context) {
        context = c
    }

    fun getNumberOfDaysExposed() : Int {
        return NUMBER_OF_DAYS_EXPOSED
    }

    val cryptoModule: CryptoModule
        get() {
            return CryptoModule.getInstance(context)
        }

    val appConfigManager: AppConfigManager
        get() {
            return AppConfigManager.getInstance(context)
        }

    val database: Database
        get() {
            return Database(context)
        }

    fun getSKForDayDate(dayDate: DayDate) : ExposeeRequest? {
        return cryptoModule.getSecretKeyForPublishing(dayDate, null)
    }

    fun getSKList() : SKList {
        val secretKeyList = SKList()
        var dayDate = DayDate()
        for (i in 0 until NUMBER_OF_DAYS_EXPOSED) {
            getSKForDayDate(dayDate)?.let {
                secretKeyList.add(Pair(dayDate, Base64Util.fromBase64(it.key)))
            }
            dayDate = dayDate.subtractDays(1)
        }
        return secretKeyList
    }

    fun setupAppConfiguration(
        bluetoothTxPowerLevel: BluetoothTxPowerLevel,
        bluetoothAdvertiseMode: BluetoothAdvertiseMode,
        bluetoothScanMode: BluetoothScanMode) {
        appConfigManager.setBluetoothPowerLevel(bluetoothTxPowerLevel)
        appConfigManager.bluetoothAdvertiseMode = bluetoothAdvertiseMode
        appConfigManager.bluetoothScanMode = bluetoothScanMode
        setAttenuationLevel(85.0f)
    }

    fun setAttenuationLevel(attenuationThreshold: Float) {
        appConfigManager.contactAttenuationThreshold = attenuationThreshold
    }

    fun generateContactsForHandshakes() {
        database.generateContactsFromHandshakes(context)
    }

    fun updateContactsFromDatabase() {
        if (database.contacts.size > 0) {
            _contacts.value = database.contacts
        }
    }

    fun mergeHandshakesToContacts(handshakes: List<Handshake>, riskyContactsOnly: Boolean) : List<ContactItem> {
        val contactItems: MutableList<ContactItem> = mutableListOf()
        val handshakeMapping = HashMap<EphId, MutableList<Handshake>>()
        // group handhakes by id
        for (handshake: Handshake in handshakes) {
            handshakeMapping[handshake.ephId]?.add(handshake) ?: run {
                handshakeMapping[handshake.ephId] = mutableListOf()
                handshakeMapping[handshake.ephId]!!.add(handshake)
            }
        }

        //filter result to only contain actual contacts in close proximity
        for (handshakeList in handshakeMapping.values) {
            var contactCounter = 0
            val startTime: Long = minmax(handshakeList, true)
            val endTime: Long = minmax(handshakeList, false)
            var offset: Long = 0
            val means = mean(handshakeList, startTime, endTime)
            while (offset < CryptoModule.MILLISECONDS_PER_EPOCH) {
                val windowStart = startTime + offset
                val windowEnd = startTime + offset + WINDOW_DURATION
                val windowMean = mean(handshakeList, windowStart, windowEnd)
                if (windowMean.first != null && (!riskyContactsOnly || windowMean.first!! < appConfigManager.contactAttenuationThreshold)) {
                    contactCounter++
                }
                offset += WINDOW_DURATION
            }
            if (contactCounter > 0) {
                val contact = Contact(
                    -1,
                    handshakeList[0].timestamp -
                            (handshakeList[0].timestamp % BackendBucketRepository.BATCH_LENGTH),
                    handshakeList[0].ephId,
                    contactCounter,
                    0
                )
                if (means.first != null)
                contactItems.add(ContactItem(
                    contact,
                    startTime,
                    endTime,
                    handshakeList,
                    means.second ?: 0.0,
                    means.third ?: 0.0,
                    means.first ?: 0.0)
                )
            }
        }
        return contactItems
    }

    private fun minmax(values: List<Handshake>, min: Boolean) : Long {
        var res: Long = if (min) {
            System.currentTimeMillis()
        } else {
            0
        }
        for (hs in values) {
            if ((min && res > hs.timestamp) || (!min && res < hs.timestamp))
                res = hs.timestamp
        }
        return res
    }

    private fun mean(handshakes: List<Handshake>, windowStart: Long, windowEnd: Long): Triple<Double?, Double?, Double?> {
        var attenuationSum: Double? = null
        var rssiSum: Double? = null
        var txPowerLevel: Double? = null
        var count = 0
        for (handshake in handshakes) {
            if (handshake.timestamp in windowStart until windowEnd) {
                if (attenuationSum == null) {
                    attenuationSum = 0.0
                }
                if (rssiSum == null) {
                    rssiSum = 0.0
                }
                if (txPowerLevel == null) {
                    txPowerLevel = 0.0
                }
                attenuationSum += handshake.attenuation.toDouble()
                rssiSum += handshake.rssi.toDouble()
                txPowerLevel += handshake.txPowerLevel.toDouble()
                count++
            }
        }
        if (attenuationSum != null) {
            attenuationSum /= count
        }
        if (rssiSum != null) {
            rssiSum /= count
        }
        if (txPowerLevel != null) {
            txPowerLevel /= count
        }
        return Triple(attenuationSum, rssiSum, txPowerLevel)
    }

    fun checkContactsForSKList(SKList: SKList) {
        generateContactsForHandshakes()

        updateContactsFromDatabase()

        val newDiagnosedContacts : MutableList<Contact> = mutableListOf()
        for (pair : android.util.Pair<DayDate, ByteArray> in SKList) {
            val start: Long = pair.first.startOfDayTimestamp
            val end: Long = DayDate(pair.first.startOfDayTimestamp).nextDay.startOfDayTimestamp

            cryptoModule.checkContacts(pair.second, start, end,
                { timeFrom: Long, timeUntil: Long -> database.getContacts(timeFrom, timeUntil) },
                { contact: Contact? ->
                    contact?.let {
                        newDiagnosedContacts.add(contact)
                    }
                })
        }

        if (newDiagnosedContacts.size > 0) {
            _diagnosedContacts.value = newDiagnosedContacts
        }

    }

    private fun createNewContactNotifaction(
        context: Context,
        contactId: Int
    ) {
        val secureStorage: SecureStorage = SecureStorage.getInstance(context)!!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtil.createNotificationChannel(context)
        }
//        val resultIntent = Intent(context, MainActivity::class.java)
//        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//        resultIntent.action = ACTION_GOTO_REPORTS
//        val pendingIntent =
//            PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(
            context,
            NotificationUtil.NOTIFICATION_CHANNEL_ID
        )
            .setContentTitle(context.getString(R.string.push_exposed_title))
            .setContentText(context.getString(R.string.push_exposed_text))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setSmallIcon(R.drawable.ic_begegnungen)
//            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NotificationUtil.NOTIFICATION_ID_CONTACT, notification)
        secureStorage.isHotlineCallPending = true
        secureStorage.isReportsHeaderAnimationPending = true
        secureStorage.lastShownContactId = contactId
    }

}