package com.udacity.project4.di

import android.content.Context
import androidx.room.Room
import com.udacity.project4.data.local.db.RemindersDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)

object DatabaseTestModule {

    @Provides
    @Named("test_db")
    fun provideInMemoryDb(@ApplicationContext context: Context)= Room.inMemoryDatabaseBuilder(
         context,RemindersDatabase::class.java).allowMainThreadQueries().build()



}