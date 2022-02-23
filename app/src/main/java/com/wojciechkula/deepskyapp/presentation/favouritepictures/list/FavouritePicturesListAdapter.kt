package com.wojciechkula.deepskyapp.presentation.favouritepictures.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.wojciechkula.deepskyapp.R

class FavouritePicturesListAdapter(
    private val onItemClicked: (picture: FavouritePicturesItem) -> Unit
) : ListAdapter<FavouritePicturesItem, FavouritePicturesViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritePicturesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_favourite_pictures, parent, false)
        return FavouritePicturesViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouritePicturesViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClicked)
    }
}

class DiffCallback : DiffUtil.ItemCallback<FavouritePicturesItem>() {
    override fun areItemsTheSame(oldItem: FavouritePicturesItem, newItem: FavouritePicturesItem): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: FavouritePicturesItem, newItem: FavouritePicturesItem): Boolean {
        return oldItem == newItem
    }

}