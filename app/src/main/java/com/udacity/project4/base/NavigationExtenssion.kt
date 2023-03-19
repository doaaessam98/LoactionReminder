package com.udacity.project4.base

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController

fun Fragment.navigateTo(
    action: Int? = null,
    destination: Int,
    bundle: Bundle? = null,
    navOptions: NavOptions? = null
) {
    try {
        findNavController().navigate(destination)
       // findNavController().safeNavigation(action, destination, bundle, navOptions)
    } catch (e: Throwable) {
        Log.e(TAG, "navigateTo: ${e.stackTrace}", )
    }
}