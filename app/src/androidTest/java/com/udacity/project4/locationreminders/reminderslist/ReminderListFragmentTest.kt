package com.udacity.project4.locationreminders.reminderslist

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

    private lateinit var repository: FakeDataSource
    private val fakeReminder =
        ReminderDTO(
            "title", "description", " location",
            1.0, 1.0
        )

    private val testModule = module {
        repository = FakeDataSource(mutableListOf())
        viewModel {
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), repository)
        }
        single {
            FakeDataSource() as ReminderDataSource
        }

    }

    @Before
    fun setUp() {
        loadKoinModules(testModule)
    }

    @After
    fun cleanUp() = runTest {
        unloadKoinModules(testModule)
    }

    @Test
    fun clickAddReminderButton_navigateToSaveReminderFragment() {

        // GIVEN
        val scenario = launchFragmentInContainer<ReminderListFragment>(
            Bundle(),
            R.style.Theme_LocationReminder
        )
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // WHEN
        onView(withId(R.id.addReminderFAB)).perform(ViewActions.click())

        // THEN
        Mockito.verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()

        )
    }


    @Test
    fun reminderListFragment_ReminderDisplayedInUi() = kotlinx.coroutines.test.runTest {
        //GIVEN
        repository.saveReminder(fakeReminder)

        // WHEN
        val scenario = launchFragmentInContainer<ReminderListFragment>(
            Bundle(),
            R.style.Theme_LocationReminder
        )
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // THEN
        onView(withText(fakeReminder.title)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun reminderListFragment_NoReminders() = kotlinx.coroutines.test.runTest {
        // GIVEN
        repository.deleteAllReminders()

        // WHEN
        val scenario = launchFragmentInContainer<ReminderListFragment>(
            Bundle(),
            R.style.Theme_LocationReminder
        )
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // THEN
        onView(withText(fakeReminder.title)).check(ViewAssertions.doesNotExist())
        onView(withText(R.string.no_data)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.noDataTextView)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

}