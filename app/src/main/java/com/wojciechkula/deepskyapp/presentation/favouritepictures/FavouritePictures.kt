package com.wojciechkula.deepskyapp.presentation.favouritepictures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wojciechkula.deepskyapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouritePictures : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        activity?.window?.statusBarColor= activity?.let { ContextCompat.getColor(it, R.color.red_500) }!!
//        activity?.setTheme(R.style.Theme_DeepskyApp_FavouritePictures)
        return inflater.inflate(R.layout.fragment_favourite_pictures, container, false)
    }

}