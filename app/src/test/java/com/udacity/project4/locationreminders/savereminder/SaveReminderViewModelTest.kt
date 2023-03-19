package com.udacity.project4.locationreminders.savereminder

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.R
import com.udacity.project4.domain.model.ReminderDTO
import com.udacity.project4.domain.useCase.AddNoteUseCase
import com.udacity.project4.locationreminders.data.FakeRepository
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.ui.reminderslist.ReminderDataItem
import com.udacity.project4.ui.savereminder.SaveReminderViewModel

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

      lateinit var repository: FakeRepository
      lateinit var addNoteUseCase: AddNoteUseCase
      lateinit var saveReminderViewModel: SaveReminderViewModel
    val fakReminder = ReminderDTO("reminder1", "desc1", "location1", 100.00, 100.00, "1")



    @Before
    fun setUp() {
          repository = FakeRepository()
          addNoteUseCase = AddNoteUseCase(repository)
          saveReminderViewModel =
              SaveReminderViewModel(ApplicationProvider.getApplicationContext(), addNoteUseCase)

    }

    @After
    fun tearDown() {
    }


    @Test
    fun validateAndSaveReminder() {
    }

    @Test
    fun saveReminder_validReminder_savedOk() {
         saveReminderViewModel.saveReminder(fakReminder.toReminderDataItem())
          val expectedValue  = repository.reminders.first().id
          assertThat(fakReminder.id).isEqualTo(expectedValue)

    }

    @Test
    fun validateEnteredData_emptyTitle_returnFalse() {
        //GIVEN
        val fakReminder = ReminderDTO("", "desc1", "location1", 100.00, 100.00, "1")
        //THEN
        val expectedValue  = saveReminderViewModel.validateEnteredData(fakReminder.toReminderDataItem())
        //WHEN
        assertThat(expectedValue).isFalse()

    }

    @Test
    fun validateEnteredData_emptyTitle_showSnackBar(){

        //Given
        val fakeReminder = ReminderDataItem("","descrption","location",300.00, 300.00, "1")
        saveReminderViewModel.validateEnteredData(fakeReminder)

        //when
        val expectedValue = saveReminderViewModel.showSnackBarInt.getOrAwaitValue()

        //then
        assertThat(expectedValue).isEqualTo(R.string.err_enter_title)


    }




}