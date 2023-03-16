package com.udacity.project4.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.udacity.project4.domain.model.ReminderDTO

/**
 * The Room Database that contains the reminders table.
 */
@Database(entities = [ReminderDTO::class], version = 1, exportSchema = false)
abstract class RemindersDatabase : RoomDatabase() {

    abstract fun reminderDao(): RemindersDao
}