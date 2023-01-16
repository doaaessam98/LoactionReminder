package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.`is`

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    lateinit var database:RemindersDatabase
    lateinit var repository: RemindersLocalRepository
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    val fakeReminder = ReminderDTO("titel","descrption","location",300.00, 300.00, "1")
    val fakeReminder2 = ReminderDTO("titel2","descrption","location",300.00, 300.00, "1")

    @Before
    fun initDatabase(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()

         repository= RemindersLocalRepository(
            database.reminderDao(),
            Dispatchers.Main
        )
    }
    @After
    fun closeDb() = database.close()

    @Test
    fun saveReminder_returnReminderById()= runBlockingTest{
        //Given
        repository.saveReminder(fakeReminder)
        // when
        val reminder = repository.getReminder(fakeReminder.id) as Result.Success

        //then
        MatcherAssert.assertThat(reminder.data.title, Is.`is`("titel"))
        MatcherAssert.assertThat(reminder.data.description, Is.`is`("descrption"))
        MatcherAssert.assertThat(reminder.data.location, Is.`is`(fakeReminder.location))
        MatcherAssert.assertThat(reminder.data.longitude, Is.`is`(fakeReminder.longitude))
        MatcherAssert.assertThat(reminder.data.latitude, Is.`is`(fakeReminder.latitude))

    }



    @Test
    fun saveReminder_returnListOfReminder()= runBlockingTest{
        //Given
        repository.saveReminder(fakeReminder)
        repository.saveReminder(fakeReminder2)
        // when
        val reminder = repository.getReminders() as com.udacity.project4.locationreminders.data.dto.Result.Success

        //then
        assertThat(reminder.data.size, Is.`is`(2))

    }




    @Test
    fun deleteAllReminders_returnNull()= runBlockingTest{
        //Given
        repository.deleteAllReminders()
        // when
        val reminder = repository.getReminders() as com.udacity.project4.locationreminders.data.dto.Result.Success

        //then
        assertThat(reminder.data.size, `is`(0))

    }


    @Test
    fun getReminderById_notExistId_returnNotFoundError() = runTest {
        // GIVEN
        val reminderId = "0"

        // WHEN
        val result = repository.getReminder(reminderId) as Result.Error

        // THEN
       assertThat(result.message, CoreMatchers.`is`("Reminder not found!"))
    }


}