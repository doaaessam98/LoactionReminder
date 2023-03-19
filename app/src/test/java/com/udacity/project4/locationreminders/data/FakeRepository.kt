package com.udacity.project4.locationreminders.data

import com.udacity.project4.domain.model.ReminderDTO
import com.udacity.project4.base.Result
import com.udacity.project4.domain.repository.ReminderRepository

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeRepository (var reminders: MutableList<ReminderDTO> = mutableListOf()):
    ReminderRepository {


    private var shouldReturnError = false

        fun setReturnError(shouldReturnError: Boolean) {
            this.shouldReturnError = shouldReturnError
        }
    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Result.Error("Error while getting reminders")
        }
        return Result.Success(reminders)

    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError) {
            return Result.Error("Error while getting reminder with id: $id")
        }
        return try {
            Result.Success(reminders.first { it.id == id })
        } catch (ex: Exception) {
            Result.Error("Reminder not found")
        }
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }


}