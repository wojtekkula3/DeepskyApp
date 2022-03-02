package com.wojciechkula.deepskyapp.presentation.picturedetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.wojciechkula.deepskyapp.databinding.FragmentPictureDetailsBinding
import com.wojciechkula.deepskyapp.presentation.favouritepictures.list.FavouritePicturesItem
import com.wojciechkula.utils.DateFormatter

class PictureDetails : Fragment() {

    private var _binding: FragmentPictureDetailsBinding? = null
    private val binding
        get() = _binding!!

    private val args: PictureDetailsArgs by navArgs()

    private val viewModel: PictureDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPictureDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeViewModel()
        viewModel.initViews(args.pictureData)
    }

    private fun initViews() {
        binding.backButton.setOnClickListener { findNavController().popBackStack() }
    }

    private fun observeViewModel() {
        viewModel.picture.observe(viewLifecycleOwner, ::bindPicture)
    }

    private fun bindPicture(picture: FavouritePicturesItem) {
        Glide.with(this)
            .load(picture.bitmap)
            .into(binding.imageOutput)

        binding.titleOutput.text = picture.title
        if (args.pictureData.copyright != null) {
            binding.copyrightOutput.text = picture.copyright
        } else {
            binding.copyrightLabel.visibility = View.GONE
            binding.copyrightOutput.visibility = View.GONE
        }
        binding.dateOutput.text = DateFormatter.formatDate(picture.date)
        binding.explanationOutput.text = picture.explanation
    }
}