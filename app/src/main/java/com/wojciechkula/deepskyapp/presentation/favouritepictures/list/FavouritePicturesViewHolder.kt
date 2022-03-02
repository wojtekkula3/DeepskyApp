package com.wojciechkula.deepskyapp.presentation.favouritepictures.list

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wojciechkula.deepskyapp.R
import java.text.SimpleDateFormat
import java.util.*

class FavouritePicturesViewHolder(
    private val view: View
) : RecyclerView.ViewHolder(view) {

    fun bind(picture: FavouritePicturesItem, onItemClicked: (picture: FavouritePicturesItem) -> Unit) {

        Glide.with(view.context)
            .load(picture.bitmap)
            .into(view.findViewById(R.id.favouritePicturesItemImageView) as ImageView)
        (view.findViewById(R.id.favouritePicturesItemTitle) as TextView).text = picture.title
        (view.findViewById(R.id.favouritePicturesDate) as TextView).text = formatDate(picture.date)
    }

    private fun formatDate(date: Date): String {
        val dateFormater = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormater.format(date)
    }

}