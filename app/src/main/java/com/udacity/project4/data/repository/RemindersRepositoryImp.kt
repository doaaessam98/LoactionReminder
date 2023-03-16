package com.udacity.project4.data.repository

import com.udacity.project4.domain.model.ReminderDTO
import com.udacity.project4.base.Result
import com.udacity.project4.data.local.LocalDataSource
import com.udacity.project4.data.local.db.RemindersDao
import com.udacity.project4.di.IoDispatcher
import com.udacity.project4.domain.repository.ReminderRepository
import com.udacity.project4.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Concrete implementation of a data source as a db.
 *
 * The repository is implemented so that you can focus on only testing it.
 *
 * @param remindersDao the dao that does the Room db operations
 * @param ioDispatcher a coroutine dispatcher to offload the blocking IO tasks
 */
class RemindersRepositoryImp @Inject constructor (
    private val localDataSource: LocalDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher


) : ReminderRepository {
    //private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    /**
     * Get the reminders list from the local db
     * @return Result the holds a Success with all the reminders or an Error object with the error message
     */
    override suspend fun getReminders(): Result<List<ReminderDTO>> =
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
            return@withContext try {
                Result.Success(localDataSource.getReminders())
            } catch (ex: Exception) {
                Result.Error(ex.localizedMessage)
            }
        }
    }

    /**
     * Insert a reminder in the db.
     * @param reminder the reminder to be inserted
     */
    override suspend fun saveReminder(reminder: ReminderDTO) =
        withContext(ioDispatcher) {
            localDataSource.saveReminder(reminder)
        }

    /**
     * Get a reminder by its id
     * @param id to be used to get the reminder
     * @return Result the holds a Success object with the Reminder or an Error object with the error message
     */
    override suspend fun getReminder(id: String): Result<ReminderDTO> = withContext(ioDispatcher) {
        try {
            val reminder = localDataSource.getReminder(id)
            if (reminder != null) {
                return@withContext Result.Success(reminder)
            } else {
                return@withContext Result.Error("Reminder not found!")
            }
        } catch (e: Exception) {
            return@withContext Result.Error(e.localizedMessage)
        }
    }

    /**
     * Deletes all the reminders in the db
     */
    override suspend fun deleteAllReminders() {
        withContext(ioDispatcher) {
            localDataSource.deleteAllReminders()
        }
    }
}
