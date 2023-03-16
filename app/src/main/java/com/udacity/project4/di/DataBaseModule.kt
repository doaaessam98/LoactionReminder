package com.udacity.project4.di

import android.content.Context
import androidx.room.Room
import com.udacity.project4.data.local.db.RemindersDao
import com.udacity.project4.data.local.db.RemindersDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {


    @Provides
    @Singleton
    fun reminderDataBase(@ApplicationContext context: Context): RemindersDatabase =
        Room.databaseBuilder(context, RemindersDatabase::class.java,"reminder_DB").build()

    @Provides
    @Singleton
    fun provideReminderDataBase(db: RemindersDatabase): RemindersDao =db.reminderDao()
}