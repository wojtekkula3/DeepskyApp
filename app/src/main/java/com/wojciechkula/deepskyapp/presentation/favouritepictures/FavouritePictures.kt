package com.wojciechkula.deepskyapp.presentation.favouritepictures

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourite_pictures, container, false)
    }

}