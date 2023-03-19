package com.udacity.project4.data.local

import com.udacity.project4.domain.model.ReminderDTO
import com.udacity.project4.base.Result

/**
 * Main entry point for accessing reminders data.
 */
interface LocalDataSource {
    suspend fun getReminders(): List<ReminderDTO>
    suspend fun saveReminder(reminder: ReminderDTO)
    suspend fun getReminder(id: String): Result<ReminderDTO>
    suspend fun deleteAllReminders()
}