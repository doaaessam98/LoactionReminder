package com.udacity.project4.domain.useCase

import com.udacity.project4.data.local.db.RemindersDao
import com.udacity.project4.domain.model.ReminderDTO
import com.udacity.project4.domain.repository.ReminderRepository
import com.udacity.project4.ui.reminderslist.ReminderDataItem
import javax.inject.Inject

class AddNoteUseCase @Inject constructor(
    private val repository: ReminderRepository)
{
    suspend operator fun invoke(reminderDTO:ReminderDTO)=
        repository.saveReminder(reminder =reminderDTO )

}