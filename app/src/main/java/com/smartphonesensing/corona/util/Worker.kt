package com.smartphonesensing.corona.util

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ContactsCreationWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    override fun doWork(): Result {

        // Do the work here--in this case, generate each epochs contacts.
        DP3THelper.generateContactsForHandshakes()

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "CreateContactsWorker"
    }
}