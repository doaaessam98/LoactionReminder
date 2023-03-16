package com.udacity.project4

import android.app.Application
import com.udacity.project4.data.local.LocalDataSource
import com.udacity.project4.data.repository.RemindersRepositoryImp
import com.udacity.project4.ui.reminderslist.RemindersListViewModel
import com.udacity.project4.ui.savereminder.SaveReminderViewModel
import dagger.hilt.android.HiltAndroidApp
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
@HiltAndroidApp
class MyApp : Application() {

}