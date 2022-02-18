package com.wojciechkula.deepskyapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        val optionsMyFavourite = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.anim.enter_right_to_left)
            .setExitAnim(R.anim.exit_right_to_left)
            .build()

        val optionsAPOD = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.anim.enter_left_to_right)
            .setExitAnim(R.anim.exit_left_to_right)
            .build()

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.pictureOfTheDay -> {
                    navController.navigate(R.id.pictureOfTheDay, null, optionsAPOD)
                }
                R.id.favouritePictures -> {
                    navController.navigate(R.id.favouritePictures, null, optionsMyFavourite)
                }
            }
            true
        }
    }
}