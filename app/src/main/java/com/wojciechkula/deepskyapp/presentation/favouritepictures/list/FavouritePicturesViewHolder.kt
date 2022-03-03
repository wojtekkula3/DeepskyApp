package com.wojciechkula.deepskyapp.presentation.favouritepictures.list

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.wojciechkula.deepskyapp.R
import com.wojciechkula.utils.DateFormatter

class FavouritePicturesViewHolder(
    private val view: View
) : RecyclerView.ViewHolder(view) {

    fun bind(picture: FavouritePicturesItem, onItemClicked: (picture: FavouritePicturesItem) -> Unit) {

        Glide.with(view.context)
            .load(picture.bitmap)
            .into(view.findViewById(R.id.favouritePicturesItemImageView) as ImageView)
        (view.findViewById(R.id.favouritePicturesItemTitle) as TextView).text = picture.title
        (view.findViewById(R.id.favouritePicturesDate) as TextView).text = DateFormatter.formatDate(picture.date)
        (view.findViewById(R.id.favouritePictureItem) as MaterialCardView).setOnClickListener { onItemClicked(picture) }
    }

}