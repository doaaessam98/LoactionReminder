package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import com.udacity.project4.ui.reminderslist.ReminderDataItem
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.ui.savereminder.SaveReminderViewModel
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import org.junit.*

import org.mockito.internal.matchers.Equals

@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

     lateinit var viewModel: SaveReminderViewModel
     lateinit var repository: FakeDataSource


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


     @Before
     fun setUpViewModel(){
         repository = FakeDataSource()
         viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext<Application?>(),repository)

     }

    @After
    fun onClear(){
        stopKoin()
    }

    @Test
    fun saveReminder_reminderSavedOk() {
      //Given
        val fakeReminder = ReminderDataItem("titel","descrption","location",300.00, 300.00, "1")

        //then
        viewModel.saveReminder(fakeReminder)

        //when
//         val savedReminder  =repository.reminders
//        assertEquals(savedReminder.size, 1)

        val savedReminder  =repository.reminders.first()
        assertEquals(savedReminder.id, "1")

    }

    @Test
    fun validateEnteredData_reminderTitleNull_returnFalse(){

        //Given
        val fakeReminder = ReminderDataItem("","descrption","location",300.00, 300.00, "1")

        //when
         val validateDate = viewModel.validateEnteredData(fakeReminder)

      //then
        assertThat(validateDate, `is`(false))
       // assertEquals(validateDate,false)





    }


    @Test
    fun validateEnteredData_reminderSaved_showSavedToast(){

        //Given
        val fakeReminder = ReminderDataItem("","descrption","location",300.00, 300.00, "1")

        //when
        viewModel.validateEnteredData(fakeReminder)
        val value = viewModel.showSnackBarInt.getOrAwaitValue()

        //then
        assertThat(value, `is`(R.string.err_enter_title))


    }


    @Test
    fun saveReminder_showLoading(){
         //Given
        val fakeReminder = ReminderDataItem("","descrption","location",300.00, 300.00, "1")
        mainCoroutineRule.pauseDispatcher()

        //when
        viewModel.saveReminder(fakeReminder)
        //then

        val loadingValue = viewModel.showLoading.getOrAwaitValue()
        Assert.assertEquals(loadingValue, true)

       mainCoroutineRule.resumeDispatcher()

        Assert.assertEquals(loadingValue, true)

    }

}