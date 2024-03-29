package com.udacity.project4.ui.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.data.local.LocalDataSource
import com.udacity.project4.domain.model.ReminderDTO
import com.udacity.project4.data.repository.RemindersRepositoryImp
import com.udacity.project4.ui.savereminder.SaveReminderViewModel
import com.udacity.project4.ui.reminderslist.ReminderListFragment
import com.udacity.project4.ui.reminderslist.RemindersListViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {
//
//    private lateinit var repository: FakeDataSource
//    private lateinit var appContext: Application
//
//    private val fakeReminder =
//        ReminderDTO(
//            "title", "description", " location",
//            1.0, 1.0
//        )
//    private val dataBindingIdlingResource = DataBindingIdlingResource()
//
//
//
//
//
//    @Before
//    fun setUp() {
//
//        stopKoin()
//        appContext = getApplicationContext()
//        repository = FakeDataSource(mutableListOf())
//        val module = module {
//
//            viewModel {
//                RemindersListViewModel( appContext, get() as LocalDataSource)
//            }
//
//            single {
//                SaveReminderViewModel( appContext, get() as LocalDataSource)
//            }
//
//            single { RemindersRepositoryImp(get()) as LocalDataSource }
//            single { LocalDB.createRemindersDao(appContext) }
//
//        }
//
//        startKoin {
//            modules(listOf(module))
//        }
//
//    }
//
//    @After
//    fun cleanUp() = runTest {
//        stopKoin()
//    }
//
//    @Before
//    fun registerIdlingResource(){
//        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
//    }
//
//    @After
//    fun unregisterIdlingResource(){
//        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
//    }
//
//    @Test
//    fun clickAddReminderButton_navigateToSaveReminderFragment() {
//
//
//        dataBinding// GIVEN
////        val fragmentScenario = launchFragmentInContainer<ReminderListFragment>(
////            Bundle(),
////            R.style.Theme_LocationReminder
////        )IdlingResource.monitorFragment(fragmentScenario)
//
//        val navController = mock(NavController::class.java)
//        fragmentScenario.onFragment {
//            Navigation.setViewNavController(it.view!!, navController)
//        }
//
//        // WHEN
//        onView(withId(R.id.addReminderFAB)).perform(ViewActions.click())
//
//        // THEN
//        Mockito.verify(navController).navigate(
//            ReminderListFragmentDirections.toSaveReminder()
//
//        )
//    }
//
//
//
//
//    @Test
//    fun reminderListFragment_NoReminders() = runTest {
//        // GIVEN
//        repository.deleteAllReminders()
//
//        // WHEN
//        val scenario  = launchFragmentInContainer<ReminderListFragment>(Bundle(),
//            R.style.Theme_LocationReminder)
//        val navController = mock(NavController::class.java)
//        scenario.onFragment {
//            Navigation.setViewNavController(it.view!!, navController)
//        }
//
//        // THEN -
//        onView(withText(fakeReminder.title)).check(ViewAssertions.doesNotExist())
//        onView(withText(R.string.no_data)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//        onView(withId(R.id.noDataTextView)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//    }



}