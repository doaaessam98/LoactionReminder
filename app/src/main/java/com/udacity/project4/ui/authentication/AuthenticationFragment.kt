package com.udacity.project4.ui.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.navigateTo
import com.udacity.project4.databinding.FragmentAuthenticationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class AuthenticationFragment : BaseFragment<FragmentAuthenticationBinding,AuthenticationViewModel>() {


    override val _viewModel: AuthenticationViewModel  by viewModels()

    override fun getViewDataBinding(): FragmentAuthenticationBinding {
      return  FragmentAuthenticationBinding.inflate(layoutInflater)
    }

    private val signInLauncher:ActivityResultLauncher<Intent> = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result: FirebaseAuthUIAuthenticationResult? ->
        handelAuthResponse(result)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
           launchSignInFlow()
          //this.findNavController().popBackStack()


    }
    private fun handelAuthResponse(result: FirebaseAuthUIAuthenticationResult?) {
         val response  = result?.idpResponse
        if (result?.resultCode == Activity.RESULT_OK) {
                 navigationToHome()

        } else{
            if(response!=null) {
                if(response.isRecoverableErrorResponse){

                }
            }else{
               // findNavController().popBackStack()
            }
        }

    }







    private fun launchSignInFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.Theme_LocationReminder)
            .setLogo(R.drawable.login_img)
            .build()

        signInLauncher.launch(signInIntent)
    }

    private fun navigationToHome() {
      navigateTo(destination = R.id.to_reminders)
    }

}



