package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat

import com.google.common.truth.Truth.assertThat
import com.udacity.project4.locationreminders.MainCoroutineRule

import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var viewModel: RemindersListViewModel
    private lateinit var reminderListRepo: FakeDataSource

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
    fun initializeViewModel() {
        stopKoin()
        reminderListRepo = FakeDataSource(fakeReminders)
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), reminderListRepo)
    }

    @After
    fun cleanUp() {
        stopKoin()
    }


    @Test
    fun loadReminders_loadRemindersFromDataSource() {
        //GIVEN
        viewModel.loadReminders()

        //WHEN
        val expectedResult = fakeReminders.map {
            it.toReminderDataItem()
        }
        //THEN
        val value = viewModel.remindersList.getOrAwaitValue()
        assertThat(value, `is`(expectedResult))

    }


    @Test
    fun loadReminders_showLoading() {
       //Given
        mainCoroutineRule.pauseDispatcher()
       //when
        viewModel.loadReminders()
        //then
        assertThat(viewModel.showLoading.getOrAwaitValue(), Is.`is`(true))
        mainCoroutineRule.resumeDispatcher()
        assertThat(viewModel.showLoading.getOrAwaitValue(), Is.`is`(false))
    }



        @Test
        fun loadReminders_returnError() {
            //Given
            reminderListRepo.setReturnError(true)
            //When
            viewModel.loadReminders()
            //Then
            val value = viewModel.showSnackBar.getOrAwaitValue()
            assertThat(value, `is`("Error while getting reminders"))

        }




}