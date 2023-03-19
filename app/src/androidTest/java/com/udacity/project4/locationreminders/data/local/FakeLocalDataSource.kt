package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.base.Result
import com.udacity.project4.data.local.LocalDataSource
import com.udacity.project4.domain.model.ReminderDTO

class FakeLocalDataSource(var reminders: MutableList<ReminderDTO> = mutableListOf()

) :LocalDataSource{
    override suspend fun getReminders(): List<ReminderDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        TODO("Not yet implemented")
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllReminders() {
        TODO("Not yet implemented")
    }
}