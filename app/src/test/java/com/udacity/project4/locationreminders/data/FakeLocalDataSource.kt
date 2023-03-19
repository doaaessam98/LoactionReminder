package com.udacity.project4.locationreminders.data

import com.udacity.project4.base.Result
import com.udacity.project4.data.local.LocalDataSource
import com.udacity.project4.domain.model.ReminderDTO

class FakeLocalDataSource(var reminders: MutableList<ReminderDTO> = mutableListOf())
    :LocalDataSource{


    override suspend fun getReminders(): List<ReminderDTO> {

        reminders?.let { return ArrayList(it) }

        return emptyList()

    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        return try {
            Result.Success(reminders!!.first { it.id == id })
        } catch (ex: Exception) {
            Result.Error("Reminder not found!")
        }

    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }
}