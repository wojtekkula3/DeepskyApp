package com.wojciechkula.deepskyapp.presentation.favouritepictures.list

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wojciechkula.deepskyapp.R

class FavouritePicturesViewHolder(
    private val view: View
) : RecyclerView.ViewHolder(view){

    fun bind(picture: FavouritePicturesItem, onItemClicked: (picture: FavouritePicturesItem) -> Unit){

        Glide.with(view.context)
            .load(picture.url)
            .into(view.findViewById(R.id.favouritePicturesItemImageView) as ImageView)
        (view.findViewById(R.id.favouritePicturesItemTitle) as TextView).text = picture.title

    }
}