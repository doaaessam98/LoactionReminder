package com.udacity.project4.domain.useCase

import com.udacity.project4.domain.repository.ReminderRepository
import javax.inject.Inject

class GetReminderById @Inject constructor(
    private val repository: ReminderRepository
) {
    suspend operator  fun invoke(reminderId:String)= repository.getReminder(reminderId)
}