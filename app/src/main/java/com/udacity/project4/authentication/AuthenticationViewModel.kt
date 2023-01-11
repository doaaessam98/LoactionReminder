package com.udacity.project4.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.locationreminder.login.FirebaseUserLiveData


class AuthenticationViewModel() :ViewModel() {

    val authenticationState = FirebaseUserLiveData().map { user->

        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }
}

enum class AuthenticationState {
    AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
}