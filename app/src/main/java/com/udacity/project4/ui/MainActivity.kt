package com.udacity.project4.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_main)
        navController = this.findNavController(R.id.nav_host_fragment)


        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.authenticationFragment


                )
        )
        NavigationUI.setupActionBarWithNavController(this, navController,appBarConfiguration)

        navController.addOnDestinationChangedListener{ _,destination,_ ->
            when(destination.id){
                R.id.authenticationFragment -> {
                    supportActionBar?.hide()


                }
                else -> supportActionBar?.show()
            }
        }

    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp()
    }
}

