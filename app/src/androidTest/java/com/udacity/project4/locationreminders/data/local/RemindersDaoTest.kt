package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.domain.model.ReminderDTO
import com.udacity.project4.data.local.db.RemindersDatabase

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

   lateinit var database: RemindersDatabase
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    val fakeReminder = ReminderDTO("titel","descrption","location",300.00, 300.00, "1")

    @Before
   fun initDatabase(){
       database = Room.inMemoryDatabaseBuilder(
           getApplicationContext(),
           RemindersDatabase::class.java
       ).build()
   }
    @After
    fun closeDb() = database.close()


    @Test
    fun  insertReminder_returnListOFReminder()= runBlockingTest {
        //Given

        database.reminderDao().saveReminder(fakeReminder)

        //when
         val reminders = database.reminderDao().getReminders()

        // then
        assertThat(reminders.size, `is`(1))


    }

    @Test
    fun insetReminder_getById() = runTest {
        // GIVEN
        database.reminderDao().saveReminder(fakeReminder)

        // WHEN
        val reminder = database.reminderDao().getReminderById(fakeReminder.id)

        // THEN
        assertThat(reminder as ReminderDTO, notNullValue())
        assertThat(reminder.title, `is`(fakeReminder.title))
        assertThat(reminder.description, `is`(fakeReminder.description))
        assertThat(reminder.location, `is`(fakeReminder.location))
        assertThat(reminder.latitude, `is`(fakeReminder.latitude))
        assertThat(reminder.longitude, `is`(fakeReminder.longitude))
        assertThat(reminder.id, `is`(fakeReminder.id))
    }



    @Test
    fun deleteAllRemindersTest()= runTest {
        // GIVEN
        database.reminderDao().saveReminder(fakeReminder)
        database.reminderDao().deleteAllReminders()

        // WHEN
        val reminders = database.reminderDao().getReminders()

        // THEN
        ViewMatchers.assertThat(reminders.size, `is`(0))
    }


}