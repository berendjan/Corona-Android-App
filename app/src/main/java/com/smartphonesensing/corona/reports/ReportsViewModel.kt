package com.smartphonesensing.corona.reports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import nl.tudelft.ipv8.util.toHex
import org.dpppt.android.sdk.internal.database.models.Contact
import org.dpppt.android.sdk.internal.util.DayDate

class ReportsViewModel : ViewModel() {

    private val _reportItems = MutableLiveData<List<ReportItem>>()
    val reportItem: LiveData<List<ReportItem>>
        get() = _reportItems

    fun updateReports(contacts: List<Contact>) {
        val newReports = mutableListOf<ReportItem>()
        for (contact: Contact in contacts) {
            newReports.add(ReportItem(contact))
        }
        _reportItems.value = newReports
    }

}

class ReportItem(
    val contact: Contact
) {
    val identifier = contact.ephId.data.toHex()
    val dateString = DayDate(contact.date).formatAsString()
    val count = contact.windowCount.toString() + " windows"
}