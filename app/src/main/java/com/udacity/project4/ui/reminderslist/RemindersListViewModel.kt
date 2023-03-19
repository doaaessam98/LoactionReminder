package com.udacity.project4.ui.reminderslist

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.data.local.LocalDataSource
import com.udacity.project4.domain.model.ReminderDTO
import com.udacity.project4.base.Result
import com.udacity.project4.domain.useCase.GetRemindersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemindersListViewModel @Inject constructor(
    app: Application,
    private val getRemindersUseCase: GetRemindersUseCase
) : BaseViewModel(app) {

     private  var  _remindersList  = MutableLiveData<List<ReminderDataItem>>()
     val remindersList :LiveData<List<ReminderDataItem>> = _remindersList

    /**
     * Get all the reminders from the DataSource and add them to the remindersList to be shown on the UI,
     * or show error if any
     */
    fun loadReminders() {
        showLoading.value = true
        viewModelScope.launch {
            getRemindersUseCase.invoke().let {result->
                showLoading.postValue(false)
                when (result) {
                    is Result.Success<*> -> {
                        val dataList = result.data as List<ReminderDTO>
                        val remindersList =  dataList.map {
                              it.toReminderDataItem()
                        }
                        _remindersList.value = remindersList
                    }
                    is Result.Error ->
                        showSnackBar.value = result.message
                }
            }
            //check if no data has to be shown
            invalidateShowNoData()
        }
    }

    /**
     * Inform the user that there's not any data if the remindersList is empty
     */
    private fun invalidateShowNoData() {
        showNoData.value = remindersList.value == null || remindersList.value!!.isEmpty()
    }
}

