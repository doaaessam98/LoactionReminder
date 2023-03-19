package com.udacity.project4.data.remote

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.udacity.project4.R
import javax.inject.Inject

class RemoteDataSourceImp @Inject constructor(

) :RemoteDataSource{


    override fun launchSignInFlow(signInLauncher:ActivityResultLauncher<Intent>) {
        val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build()
            )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.Theme_LocationReminder)
            .setLogo(R.drawable.map)
            .build()

        signInLauncher.launch(signInIntent)



    }

    override fun logOut() {

    }
}