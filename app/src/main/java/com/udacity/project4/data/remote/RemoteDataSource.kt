package com.udacity.project4.data.remote

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

interface RemoteDataSource {

    fun launchSignInFlow(signInLauncher: ActivityResultLauncher<Intent>)
    fun logOut()
}