package com.udacity.project4.data.local

import com.udacity.project4.base.Result
import com.udacity.project4.data.local.db.RemindersDao
import com.udacity.project4.di.IoDispatcher
import com.udacity.project4.domain.model.ReminderDTO
import com.udacity.project4.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalDataSourceImp @Inject constructor(
    private val remindersDao: RemindersDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher

): LocalDataSource {
    override suspend fun getReminders(): List<ReminderDTO> {
      return remindersDao.getReminders()
    }

    override suspend fun saveReminder(reminder: ReminderDTO) =
       /// wrapEspressoIdlingResource {
        remindersDao.saveReminder(reminder)
   /// }

    override suspend fun getReminder(id: String): Result<ReminderDTO> =
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                try {
                    val reminder = remindersDao.getReminderById(id)
                    if(reminder!=null) {
                        return@withContext Result.Success(reminder)
                    } else {
                        return@withContext Result.Error("Reminder not found!")
                    }
                } catch (e: Exception) {
                    return@withContext Result.Error(e.localizedMessage)

                }
            }
        }
    override suspend fun deleteAllReminders() {
      remindersDao.deleteAllReminders()
    }
}