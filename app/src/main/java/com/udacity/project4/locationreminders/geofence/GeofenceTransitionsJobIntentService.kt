package com.udacity.project4.locationreminders.geofence

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver.Companion.ACTION_GEOFENCE_EVENT
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.sendNotification
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class GeofenceTransitionsJobIntentService : JobIntentService(), CoroutineScope {

    private var coroutineJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob

    companion object {
        private const val JOB_ID = 573
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(
                context,
                GeofenceTransitionsJobIntentService::class.java, JOB_ID,
                intent
            )
        }
    }

    override fun onHandleWork(intent: Intent) {
        if (intent.action == ACTION_GEOFENCE_EVENT) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent)

            if (geofencingEvent != null) {
                if (geofencingEvent.hasError()) {
                    return
                }

                if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                    sendNotification(geofencingEvent.triggeringGeofences!!)
                }
            }

        }





    }


    private fun sendNotification(triggeringGeofences: List<Geofence>) {

        for(triggeringGeofence in triggeringGeofences) {
            val requestId = triggeringGeofence.requestId

            val remindersLocalRepository: ReminderDataSource by inject()
            CoroutineScope(coroutineContext).launch(SupervisorJob()) {
                val result = remindersLocalRepository.getReminder(requestId)
                if(result is Result.Success<ReminderDTO>) {
                    val reminderDTO = result.data
                    sendNotification(
                        this@GeofenceTransitionsJobIntentService, ReminderDataItem(
                            reminderDTO.title,
                            reminderDTO.description,
                            reminderDTO.location,
                            reminderDTO.latitude,
                            reminderDTO.longitude,
                            reminderDTO.id
                        )
                    )
                }
            }
        }
    }
}
