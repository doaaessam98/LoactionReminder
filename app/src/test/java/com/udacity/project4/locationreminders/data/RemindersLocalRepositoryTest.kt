package com.udacity.project4.locationreminders.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.base.Result
import com.udacity.project4.data.repository.RemindersRepositoryImp
import com.udacity.project4.domain.model.ReminderDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var localDataSource: FakeLocalDataSource
    lateinit var repository: RemindersRepositoryImp
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    val fakeReminder = ReminderDTO("titel","descrption","location",300.00, 300.00, "1")
    val fakeReminder2 = ReminderDTO("titel2","descrption","location",300.00, 300.00, "1")

    @Before
    fun initDatabase(){

        localDataSource = FakeLocalDataSource()

         repository= RemindersRepositoryImp(localDataSource, Dispatchers.Unconfined)
    }
    @Test
    fun saveReminder_returnReminderById_savedOk()= runTest{
        //Given
        repository.saveReminder(fakeReminder)
        // when
        val expectValue = localDataSource.reminders?.first()?.id
        //then
        assertThat(fakeReminder.id).isEqualTo(expectValue)

    }
    @Test
    fun saveReminder_returnListOfReminder()= runTest{
        //Given
        repository.saveReminder(fakeReminder)
        repository.saveReminder(fakeReminder2)

        // when
        val expectedResult  = localDataSource.reminders?.size

        //then
       assertThat(expectedResult).isEqualTo(3)

    }
    @Test
    fun deleteAllReminders_returnNull()= runTest{
        //Given
        repository.saveReminder(fakeReminder)
        repository.saveReminder(fakeReminder2)

//        val expectedResult  = localDataSource.reminders?.size
//        assertThat(expectedResult).isEqualTo(2)

        repository.deleteAllReminders()
        // when
        val result  =  localDataSource.reminders?.size

        //then
        assertThat(result).isEqualTo(0)

    }
    @Test
    fun getReminderById_notExistId_returnNotFoundError() = runTest {
        // GIVEN
        val reminderId = "0"
        // WHEN
        val result = repository.getReminder(reminderId) as Result.Error

        // THEN
        assertThat(result.message).isEqualTo("Reminder not found!")
    }


}