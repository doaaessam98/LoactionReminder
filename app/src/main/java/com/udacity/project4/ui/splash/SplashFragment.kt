package com.udacity.project4.ui.splash

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.base.navigateTo
import kotlinx.coroutines.delay


class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        navigateTo(destination = R.id.action_splashFragment_to_reminderListFragment)

//        lifecycleScope.launchWhenCreated {
//            delay(3000)
//             if(FirebaseAuth.getInstance().currentUser!=null){
//                  navigateTo(destination = R.id.action_splashFragment_to_reminderListFragment)
//             }else{
//                 navigateTo(destination = R.id.action_splashFragment_to_authenticationFragment)
//        }
//    }
}}