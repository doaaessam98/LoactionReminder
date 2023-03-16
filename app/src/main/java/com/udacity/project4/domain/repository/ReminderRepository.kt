package com.udacity.project4.domain.repository

import com.udacity.project4.base.Result
import com.udacity.project4.domain.model.ReminderDTO

interface ReminderRepository {
    suspend fun getReminders(): Result<List<ReminderDTO>>
    suspend fun saveReminder(reminder: ReminderDTO)
    suspend fun getReminder(id: String): Result<ReminderDTO>
    suspend fun deleteAllReminders()
}