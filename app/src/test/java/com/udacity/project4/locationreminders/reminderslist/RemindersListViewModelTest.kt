package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat

import com.udacity.project4.locationreminders.MainCoroutineRule

import com.udacity.project4.locationreminders.data.FakeRepository
import com.udacity.project4.domain.model.ReminderDTO
import com.udacity.project4.domain.useCase.GetRemindersUseCase
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.ui.reminderslist.RemindersListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var getRemindersUseCase: GetRemindersUseCase
    private lateinit var viewModel: RemindersListViewModel
    private lateinit var reminderListRepo: FakeRepository

    private val fakeReminders =
        mutableListOf(
            ReminderDTO("reminder1", "desc1", "location1", 100.00, 100.00, "1"),
            ReminderDTO("reminder2", "desc2", "location2", 200.00, 200.00, "2"),
            ReminderDTO("reminder3", "desc3", "location3", 300.00, 300.00, "3"),
            ReminderDTO("reminder4", "desc4", "location4", 400.00, 400.00, "4"),
            ReminderDTO("reminder5", "desc5", "location5", 500.00, 500.00, "5"),
        )

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun init() {
        reminderListRepo = FakeRepository(fakeReminders)
        getRemindersUseCase = GetRemindersUseCase(reminderListRepo)
        viewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), getRemindersUseCase)
    }

    @After
    fun cleanUp() {

    }


    @Test
    fun loadReminders_loadRemindersFromDataSource_ReturnRemindersSuccessful() {
        //GIVEN
        viewModel.loadReminders()

        //WHEN
        val expectedResult = fakeReminders.map {
            it.toReminderDataItem()
        }
        //THEN
        val value = viewModel.remindersList.getOrAwaitValue()
        assertThat(value).isEqualTo(expectedResult)


    }


    @Test
    fun loadTasks_loading() = runTest {
        // Main dispatcher will not run coroutines eagerly for this test
        Dispatchers.setMain(StandardTestDispatcher())

        // Load the task in the ViewModel
        viewModel.loadReminders()


        // Validate progress indicator is shown
        assertThat(viewModel.showLoading.getOrAwaitValue()).isTrue()

        // Execute pending coroutine actions
        // Wait until coroutine in  viewModel.loadReminders() complete
        advanceUntilIdle()

        // Validate progress indicator is hidden
        assertThat(viewModel.showLoading.getOrAwaitValue()).isFalse()

    }

    @Test
    fun loadReminders_showLoading() {
        //Given
        mainCoroutineRule.pauseDispatcher()
        //when
        viewModel.loadReminders()
        //then
        assertThat(viewModel.showLoading.getOrAwaitValue()).isTrue()
        mainCoroutineRule.resumeDispatcher()
        assertThat(viewModel.showLoading.getOrAwaitValue()).isFalse()
    }


    @Test
    fun loadReminders_returnError() {
        //Given
        reminderListRepo.setReturnError(true)
        //When
        viewModel.loadReminders()
        //Then
        val value = viewModel.showSnackBar.getOrAwaitValue()
        assertThat(value).isEqualTo("Error while getting reminders")

    }

}

