package com.smartphonesensing.corona.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.io.IOException
import java.security.GeneralSecurityException

class SecureStorage private constructor(context: Context) {
    private lateinit var prefs: SharedPreferences
    private val forceUpdateLiveData: MutableLiveData<Boolean>
    private val hasInfoboxLiveData: MutableLiveData<Boolean>



    companion object {
        private const val PREFERENCES = "SecureStorage"
        private const val KEY_INFECTED_DATE = "infected_date"
        private const val KEY_INFORM_TIME_REQ = "inform_time_req"
        private const val KEY_INFORM_CODE_REQ = "inform_code_req"
        private const val KEY_INFORM_TOKEN_REQ = "inform_token_req"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_LAST_SHOWN_CONTACT_ID = "last_shown_contact_id"
        private const val KEY_HOTLINE_CALL_PENDING = "hotline_call_pending"
        private const val KEY_HOTLINE_LAST_CALL_TIMESTAMP = "hotline_ever_called_timestamp"
        private const val KEY_PENDING_REPORTS_HEADER_ANIMATION =
            "pending_reports_header_animation"
        private const val KEY_CONFIG_FORCE_UPDATE = "config_do_force_update"
        private const val KEY_CONFIG_HAS_INFOBOX = "has_ghettobox"
        private const val KEY_CONFIG_INFOBOX_TITLE = "ghettobox_title"
        private const val KEY_CONFIG_INFOBOX_TEXT = "ghettobox_text"
        private const val KEY_CONFIG_INFOBOX_LINK_TITLE = "ghettobox_link_title"
        private const val KEY_CONFIG_INFOBOX_LINK_URL = "ghettobox_link_url"
        private const val KEY_CONFIG_FORCED_TRACE_SHUTDOWN = "forced_trace_shutdown"
        private var instance: SecureStorage? = null

        fun getInstance(context: Context): SecureStorage? {
            if (instance == null) {
                instance = SecureStorage(context)
            }
            return instance
        }
    }

    init {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            prefs = EncryptedSharedPreferences
                .create(
                    context,
                    PREFERENCES,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        forceUpdateLiveData = MutableLiveData(doForceUpdate)
        hasInfoboxLiveData = MutableLiveData(hasInfobox)
    }


    fun getForceUpdateLiveData(): LiveData<Boolean> {
        return forceUpdateLiveData
    }

    val infoBoxLiveData: LiveData<Boolean>
        get() = hasInfoboxLiveData

    var infectedDate: Long
        get() = prefs.getLong(KEY_INFECTED_DATE, 0)
        set(date) {
            prefs.edit().putLong(KEY_INFECTED_DATE, date).apply()
        }

    fun saveInformTimeAndCodeAndToken(
        informCode: String?,
        informToken: String?
    ) {
        prefs.edit().putLong(
            KEY_INFORM_TIME_REQ,
            System.currentTimeMillis()
        )
            .putString(KEY_INFORM_CODE_REQ, informCode)
            .putString(KEY_INFORM_TOKEN_REQ, informToken)
            .apply()
    }

    fun clearInformTimeAndCodeAndToken() {
        prefs.edit().remove(KEY_INFORM_TIME_REQ)
            .remove(KEY_INFORM_CODE_REQ)
            .remove(KEY_INFORM_TOKEN_REQ)
            .apply()
    }

    val lastInformRequestTime: Long
        get() = prefs.getLong(KEY_INFORM_TIME_REQ, 0)

    val lastInformCode: String?
        get() = prefs.getString(KEY_INFORM_CODE_REQ, null)

    val lastInformToken: String?
        get() = prefs.getString(KEY_INFORM_TOKEN_REQ, null)

    var onboardingCompleted: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
        set(completed) {
            prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
        }

    var lastShownContactId: Int
        get() = prefs.getInt(KEY_LAST_SHOWN_CONTACT_ID, -1)
        set(contactId) {
            prefs.edit().putInt(KEY_LAST_SHOWN_CONTACT_ID, contactId).apply()
        }

    var isHotlineCallPending: Boolean
        get() = prefs.getBoolean(KEY_HOTLINE_CALL_PENDING, false)
        set(pending) {
            prefs.edit().putBoolean(KEY_HOTLINE_CALL_PENDING, pending).apply()
        }

    fun lastHotlineCallTimestamp(): Long {
        return prefs.getLong(KEY_HOTLINE_LAST_CALL_TIMESTAMP, 0)
    }

    fun justCalledHotline() {
        prefs.edit().putBoolean(KEY_HOTLINE_CALL_PENDING, false)
            .putLong(
                KEY_HOTLINE_LAST_CALL_TIMESTAMP,
                System.currentTimeMillis()
            )
            .apply()
    }

    var isReportsHeaderAnimationPending: Boolean
        get() = prefs.getBoolean(KEY_PENDING_REPORTS_HEADER_ANIMATION, false)
        set(pending) {
            prefs.edit()
                .putBoolean(KEY_PENDING_REPORTS_HEADER_ANIMATION, pending)
                .apply()
        }

    var doForceUpdate: Boolean
        get() = prefs.getBoolean(KEY_CONFIG_FORCE_UPDATE, false)
        set(doForceUpdate) {
            prefs.edit().putBoolean(KEY_CONFIG_FORCE_UPDATE, doForceUpdate)
                .apply()
            forceUpdateLiveData.postValue(doForceUpdate)
        }

    var hasInfobox: Boolean
        get() = prefs.getBoolean(KEY_CONFIG_HAS_INFOBOX, false)
        set(hasInfobox) {
            prefs.edit().putBoolean(KEY_CONFIG_HAS_INFOBOX, hasInfobox).apply()
            hasInfoboxLiveData.postValue(hasInfobox)
        }

    var infoboxTitle: String?
        get() = prefs.getString(KEY_CONFIG_INFOBOX_TITLE, null)
        set(title) {
            prefs.edit().putString(KEY_CONFIG_INFOBOX_TITLE, title).apply()
        }

    var infoboxText: String?
        get() = prefs.getString(KEY_CONFIG_INFOBOX_TEXT, null)
        set(text) {
            prefs.edit().putString(KEY_CONFIG_INFOBOX_TEXT, text).apply()
        }

    var forcedTraceShutdown: Boolean
        get() = prefs.getBoolean(KEY_CONFIG_FORCED_TRACE_SHUTDOWN, false)
        set(forcedTraceShutdown) {
            prefs.edit().putBoolean(
                KEY_CONFIG_FORCED_TRACE_SHUTDOWN,
                forcedTraceShutdown
            ).apply()
        }

    var infoboxLinkTitle: String?
        get() = prefs.getString(KEY_CONFIG_INFOBOX_LINK_TITLE, null)
        set(title) {
            prefs.edit().putString(KEY_CONFIG_INFOBOX_LINK_TITLE, title).apply()
        }

    var infoboxLinkUrl: String?
        get() = prefs.getString(KEY_CONFIG_INFOBOX_LINK_URL, null)
        set(url) {
            prefs.edit().putString(KEY_CONFIG_INFOBOX_LINK_URL, url).apply()
        }

}