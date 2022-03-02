package com.wojciechkula.deepskyapp.presentation.favouritepictures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.wojciechkula.deepskyapp.R
import com.wojciechkula.deepskyapp.databinding.FragmentFavouritePicturesBinding
import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import com.wojciechkula.deepskyapp.presentation.favouritepictures.list.FavouritePicturesItem
import com.wojciechkula.deepskyapp.presentation.favouritepictures.list.FavouritePicturesListAdapter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class FavouritePictures : Fragment() {

    private var _binding: FragmentFavouritePicturesBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: FavouritePicturesViewModel by activityViewModels()

    private val adapter by lazy {
        FavouritePicturesListAdapter { picture ->
            Timber.d("Clicked")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.window?.statusBarColor = activity?.let { ContextCompat.getColor(it, R.color.red_700) }!!
//        activity?.setTheme(R.style.Theme_DeepskyApp_FavouritePictures)
        _binding = FragmentFavouritePicturesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeViewModel()
    }

    private fun initViews() {
        binding.picturesRecyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.picturesRecyclerView.adapter = adapter

    }

    private fun observeViewModel() {
        viewModel.picturesList.observe(viewLifecycleOwner, ::bindPictures)
    }

    private fun bindPictures(pictureList: List<FavouritePictureModel>) {
        inflateRecyclerView(pictureList)
        if (pictureList.isEmpty()) {
            binding.noPicturesYetLabel.visibility = View.VISIBLE
        } else {
            binding.noPicturesYetLabel.visibility = View.GONE
        }
    }

    private fun inflateRecyclerView(pictureList: List<FavouritePictureModel>) {
        val list = pictureList.map { picture ->
            picture.id?.let {
                id
                FavouritePicturesItem(
                    id = id,
                    copyright = picture.copyright,
                    date = dateStringToDate(picture.date),
                    explanation = picture.explanation,
                    title = picture.title,
                    url = picture.url,
                    bitmap = picture.bitmap,
                )
            }
        }
        adapter.submitList(list.sortedByDescending { it?.date })
    }

    private fun dateStringToDate(date: String): Date {
        return SimpleDateFormat("yyyy-MM-dd").parse(date)
    }
}