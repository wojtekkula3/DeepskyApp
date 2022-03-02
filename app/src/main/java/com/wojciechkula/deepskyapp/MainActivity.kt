package com.wojciechkula.deepskyapp

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @SuppressLint("ResourceType")
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
            .setPopEnterAnim(R.anim.enter_left_to_right)
            .setPopExitAnim(R.anim.exit_left_to_right)
            .build()

        val optionsAPOD = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.anim.enter_left_to_right)
            .setExitAnim(R.anim.exit_left_to_right)
            .setPopEnterAnim(R.anim.enter_right_to_left)
            .setPopExitAnim(R.anim.exit_right_to_left)
            .build()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.pictureOfTheDay -> {
                    val xrp = resources.getXml(R.drawable.bottom_nav_selector)
                    val colorStateList = ColorStateList.createFromXml(resources, xrp, theme)
                    bottomNavigationView.itemIconTintList = colorStateList
                    bottomNavigationView.itemTextColor = colorStateList
                    bottomNavigationView.visibility = View.VISIBLE
                }
                R.id.favouritePictures -> {
                    val xrp = resources.getXml(R.drawable.bottom_nav_selector_favourite)
                    val colorStateList = ColorStateList.createFromXml(resources, xrp, theme)
                    bottomNavigationView.itemIconTintList = colorStateList
                    bottomNavigationView.itemTextColor = colorStateList
                    bottomNavigationView.visibility = View.VISIBLE
                }
                R.id.pictureDetails -> bottomNavigationView.visibility = View.GONE
            }
        }
        bottomNavigationView.setupWithNavController(navController)

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