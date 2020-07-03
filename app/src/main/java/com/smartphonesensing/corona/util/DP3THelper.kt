package com.smartphonesensing.corona.util

import android.content.Context
import android.util.Pair
import org.dpppt.android.sdk.internal.AppConfigManager
import org.dpppt.android.sdk.internal.backend.models.ExposeeRequest
import org.dpppt.android.sdk.internal.crypto.CryptoModule
import org.dpppt.android.sdk.internal.crypto.SKList
import org.dpppt.android.sdk.internal.database.Database
import org.dpppt.android.sdk.internal.database.models.Contact
import org.dpppt.android.sdk.internal.util.Base64Util
import org.dpppt.android.sdk.internal.util.DayDate

object DP3THelper {

    private const val NUMBER_OF_DAYS_EXPOSED = 10

    private lateinit var context: Context

    fun setContext(c: Context) {
        context = c
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

    fun checkContactsForSKListForNDays(SKList: SKList, nDays: Int = NUMBER_OF_DAYS_EXPOSED) : List<Contact> {
        val contacts : MutableList<Contact> = mutableListOf<Contact>()
        for (pair : android.util.Pair<DayDate, ByteArray> in SKList) {
            cryptoModule.checkContacts(pair.second,
                DayDate().subtractDays(nDays).startOfDayTimestamp,
                DayDate().startOfDayTimestamp,
                { timeFrom, timeUntil -> database.getContacts(timeFrom, timeUntil) },
                { contact: Contact? ->
                    contact?.let { contacts.add(contact) }
                })
        }
        return contacts
    }

    fun checkContacts(secretKey: ByteArray, fromDate: DayDate, toDate: DayDate) {

        cryptoModule.checkContacts(secretKey,
            fromDate.startOfDayTimestamp,
            toDate.startOfDayTimestamp,
            { timeFrom, timeUntil -> database.getContacts(timeFrom, timeUntil) },
            { contact: Contact? ->
                    // contact?.let {  } TODO: Do something with exposed contact.
                })
    }

}