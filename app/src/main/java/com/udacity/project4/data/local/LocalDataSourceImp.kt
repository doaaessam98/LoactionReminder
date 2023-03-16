package com.udacity.project4.data.local

import com.udacity.project4.data.local.db.RemindersDao
import com.udacity.project4.domain.model.ReminderDTO
import javax.inject.Inject

class LocalDataSourceImp @Inject constructor(
    private val remindersDao: RemindersDao
): LocalDataSource {
    override suspend fun getReminders(): List<ReminderDTO> {
      return remindersDao.getReminders()
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersDao.saveReminder(reminder)
    }

    override suspend fun getReminder(id: String): ReminderDTO? {
      return  remindersDao.getReminderById(id)
    }

    override suspend fun deleteAllReminders() {
      remindersDao.deleteAllReminders()
    }
}