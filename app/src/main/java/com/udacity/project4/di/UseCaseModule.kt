package com.udacity.project4.di

import com.udacity.project4.domain.repository.ReminderRepository
import com.udacity.project4.domain.useCase.AddNoteUseCase
import com.udacity.project4.domain.useCase.GetReminderById
import com.udacity.project4.domain.useCase.GetRemindersUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideAddNoteUseCase(repository: ReminderRepository):AddNoteUseCase = AddNoteUseCase(repository)

    @Provides
    fun provideGetAllReminderUseCase(repository: ReminderRepository):GetRemindersUseCase = GetRemindersUseCase(repository)

    @Provides
    fun provideReminderByIUseCase(repository: ReminderRepository):GetReminderById = GetReminderById(repository)
}