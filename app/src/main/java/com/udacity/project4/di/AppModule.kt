package com.udacity.project4.di

import com.udacity.project4.data.local.LocalDataSource
import com.udacity.project4.data.local.LocalDataSourceImp
import com.udacity.project4.data.repository.RemindersRepositoryImp
import com.udacity.project4.domain.repository.ReminderRepository
import com.udacity.project4.domain.useCase.AddNoteUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
interface AppModule {
    @Binds
    fun provideLocalDataSource(localDataSource: LocalDataSourceImp): LocalDataSource


    @Binds
    fun provideRepository(repository: RemindersRepositoryImp): ReminderRepository


}