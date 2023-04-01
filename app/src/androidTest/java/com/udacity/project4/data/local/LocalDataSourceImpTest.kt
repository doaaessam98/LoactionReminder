package com.udacity.project4.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.data.local.db.RemindersDatabase
import com.udacity.project4.domain.model.ReminderDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith
@OptIn(ExperimentalCoroutinesApi::class)

@RunWith(AndroidJUnit4::class)
class LocalDataSourceImpTest {

     lateinit var  database: RemindersDatabase
     lateinit var  localDataSourceImp: LocalDataSourceImp
    val fakeReminder = ReminderDTO("titel","descrption","location",300.00, 300.00, "1")
    val fakeReminder2 = ReminderDTO("titel","descrption","location",300.00, 300.00, "2")


    @get:Rule
   var instantExecutorRule = InstantTaskExecutorRule()

    @Before
     fun init(){
         database = Room.inMemoryDatabaseBuilder(
             getApplicationContext(),
             RemindersDatabase::class.java
         ).allowMainThreadQueries().build()

         localDataSourceImp = LocalDataSourceImp(database.reminderDao(),Dispatchers.Main)
     }

    @After
     fun tearDown(){
        database.close()
    }
    @Test
    fun getReminders() = runTest{
        //GIVEN
        localDataSourceImp.saveReminder(fakeReminder)
        localDataSourceImp.saveReminder(fakeReminder2)
        //WHEN
        val expectedResult = localDataSourceImp.getReminders()

        //THEN
        MatcherAssert.assertThat(expectedResult.size, CoreMatchers.`is`(2))

    }

    @Test
    fun saveReminder_validReminder_savedOk() = runTest{
        val fakeReminder = ReminderDTO("titel","descrption","location",300.00, 300.00, "1")
        localDataSourceImp.saveReminder(fakeReminder)

        val expectedResult = localDataSourceImp.getReminder(fakeReminder.id) as com.udacity.project4.base.Result.Success
        MatcherAssert.assertThat(expectedResult.data, CoreMatchers.`is`(fakeReminder))
       ///assertThat(expectedResult).isEqualTo(fakeReminder.id)


    }

    @Test
    fun getReminder() {


    }

    @Test
    fun deleteAllReminders() {
    }
}