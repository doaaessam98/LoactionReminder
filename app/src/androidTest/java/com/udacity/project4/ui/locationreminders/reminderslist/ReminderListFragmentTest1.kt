package com.udacity.project4.ui.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.PerformException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.R
import com.udacity.project4.launchFragmentInHiltContainer
import com.udacity.project4.ui.reminderslist.ReminderListFragment
import com.udacity.project4.ui.reminderslist.ReminderListFragmentDirections
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorFragment
import com.udacity.project4.util.monitorFragmentHilt
import com.udacity.project4.utils.EspressoIdlingResource.countingIdlingResource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify


@RunWith(AndroidJUnit4::class)
@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class ReminderListFragmentTest1 {


    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)



    @Before
    fun registerIdlingResources() {
        hiltRule.inject()
        IdlingRegistry.getInstance().register(countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }




    @After
    fun unregisterIdlingResources() {
        IdlingRegistry.getInstance().unregister(countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
   fun clickAddReminderButton_navigateToSaveReminderFragment() {

        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())

        launchFragmentInHiltContainer<ReminderListFragment>{
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            dataBindingIdlingResource.monitorFragmentHilt(this)

        }
        onView(ViewMatchers.withId(R.id.addReminderFAB)).perform(ViewActions.click())
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.saveReminderFragment)
    }


    // using mock


    @Test
    fun clickAddReminderButton_navigateToSaveReminderFragment_mock() {

        val navController = mock(NavController::class.java)

        launchFragmentInHiltContainer<ReminderListFragment> {
            Navigation.setViewNavController(requireView(), navController)
            dataBindingIdlingResource.monitorFragmentHilt(this)
        }

        onView(withId(R.id.addReminderFAB)).perform(click())

        val action = ReminderListFragmentDirections.toSaveReminder()

        verify(navController).navigate(action)


    }

    @Test
   fun reminderListFragment_NoReminders() = runTest {

        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())

        launchFragmentInHiltContainer<ReminderListFragment>{
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            dataBindingIdlingResource.monitorFragmentHilt(this)

        }

        // THEN -
        onView(withText(R.string.no_data)).check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.noDataTextView)).check(matches(ViewMatchers.isDisplayed()))

    }



//    @Test(expected = PerformException::class)
//    fun itemWithText_doesNotExist() {
//        onView(withId(R.id.reminderssRecyclerView))
//            .perform(
//                RecyclerViewActions.scrollTo(hasDescendant(withText("not in the list")))
//            )
//    }
//
//
//    @Test fun scrollToItemBelowFold_checkItsText() {
//        // First, scroll to the position that needs to be matched and click on it.
//        onView(ViewMatchers.withId(R.id.reminderssRecyclerView))
//            .perform(
//                RecyclerViewActions.actionOnItemAtPosition(
//                    1,
//                    click()
//                )
//            )
//    }







}