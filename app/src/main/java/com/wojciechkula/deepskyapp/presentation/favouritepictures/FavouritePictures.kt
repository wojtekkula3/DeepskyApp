package com.wojciechkula.deepskyapp.presentation.favouritepictures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.wojciechkula.deepskyapp.databinding.FragmentFavouritePicturesBinding
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDay
import com.wojciechkula.deepskyapp.presentation.favouritepictures.list.FavouritePicturesItem
import com.wojciechkula.deepskyapp.presentation.favouritepictures.list.FavouritePicturesListAdapter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class FavouritePictures : Fragment() {

    private var _binding: FragmentFavouritePicturesBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: FavouritePicturesViewModel by viewModels()

    private val adapter by lazy {
        FavouritePicturesListAdapter { picture ->
            Timber.d("Clicked")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        activity?.window?.statusBarColor= activity?.let { ContextCompat.getColor(it, R.color.red_500) }!!
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
        binding.picturesRecyclerView.layoutManager = GridLayoutManager(activity, 2)
    }

    private fun observeViewModel() {
        viewModel.picturesList.observe(viewLifecycleOwner, ::bindPictures)
    }

    private fun bindPictures(pictureList: List<PictureOfTheDay>?) {
        binding.picturesRecyclerView.adapter = adapter
        val list = pictureList?.map { picture ->
            FavouritePicturesItem(
                copyright = picture.copyright,
                date = picture.date,
                explanation = picture.explanation,
                title = picture.title,
                url = picture.url
            )
        }
        adapter.submitList(list)

    }


}