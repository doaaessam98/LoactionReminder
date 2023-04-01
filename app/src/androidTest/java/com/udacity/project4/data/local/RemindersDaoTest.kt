package com.udacity.project4.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.matcher.ViewMatchers
import com.udacity.project4.data.local.db.RemindersDao
import com.udacity.project4.data.local.db.RemindersDatabase
import com.udacity.project4.domain.model.ReminderDTO
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named



class RemindersDaoTest {

   // @get:Rule
   /// var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

//      @Inject
//      @Named("test_db")
      lateinit var database:RemindersDatabase

       lateinit var remindersDao: RemindersDao
        val fakeReminder = ReminderDTO("titel","descrption","location",300.00, 300.00, "1")
        val fakeReminder1 = ReminderDTO("titel","descrption","location",300.00, 300.00, "2")



    @Before
 fun setUp(){
    // hiltRule.inject()


     remindersDao = database.reminderDao()
 }

    @After
    fun tearDown(){
        database.close()
    }





    @Test
    fun  insertReminder_returnListOFReminder()= runTest {
        //Given
        database.reminderDao().saveReminder(fakeReminder1)

        database.reminderDao().saveReminder(fakeReminder)

        //when
        val reminders = database.reminderDao().getReminders()

        // then
        MatcherAssert.assertThat(reminders.size, CoreMatchers.`is`(2))


    }

    @Test
    fun insetReminder_getById() = runTest {
        // GIVEN
        database.reminderDao().saveReminder(fakeReminder)

        // WHEN
        val reminder = database.reminderDao().getReminderById(fakeReminder.id)

        // THEN
        MatcherAssert.assertThat(reminder as ReminderDTO, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(reminder.title, CoreMatchers.`is`(fakeReminder.title))
        MatcherAssert.assertThat(reminder.description, CoreMatchers.`is`(fakeReminder.description))
        MatcherAssert.assertThat(reminder.location, CoreMatchers.`is`(fakeReminder.location))
        MatcherAssert.assertThat(reminder.latitude, CoreMatchers.`is`(fakeReminder.latitude))
        MatcherAssert.assertThat(reminder.longitude, CoreMatchers.`is`(fakeReminder.longitude))
        MatcherAssert.assertThat(reminder.id, CoreMatchers.`is`(fakeReminder.id))
    }



    @Test
    fun deleteAllRemindersTest()= runTest {
        // GIVEN
        database.reminderDao().saveReminder(fakeReminder)
        database.reminderDao().deleteAllReminders()

        // WHEN
        val reminders = database.reminderDao().getReminders()

        // THEN
        ViewMatchers.assertThat(reminders.size, CoreMatchers.`is`(0))
    }


}