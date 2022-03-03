package com.wojciechkula.deepskyapp.presentation.picturedetails

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.wojciechkula.deepskyapp.R
import com.wojciechkula.deepskyapp.databinding.FragmentPictureDetailsBinding
import com.wojciechkula.deepskyapp.presentation.favouritepictures.list.FavouritePicturesItem
import com.wojciechkula.utils.DateFormatter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
        viewModel.viewEvent.observe(viewLifecycleOwner, ::handleEvents)
    }

    private fun handleEvents(event: PictureDetailsViewEvent) {
        when (event) {
            is PictureDetailsViewEvent.Error -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            PictureDetailsViewEvent.ShowFavouritePictures -> findNavController().popBackStack()
        }
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

        val dateString = DateFormatter.formatDate(picture.date)
        binding.dateOutput.text = dateString
        binding.explanationOutput.text = picture.explanation
        binding.deleteButton.setOnClickListener { showDeleteDialog(dateString) }
    }

    private fun showDeleteDialog(date: String) {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setMessage(getString(R.string.picture_details_dialog_message))
            .setCancelable(true)
            .setPositiveButton(getString(R.string.picture_details_dialog_confirm)) { dialog, id ->
                viewModel.deleteFavouritePicture(date)
            }
            .setNegativeButton(getString(R.string.profile_details_dialog_cancel)) { dialog, id ->
                dialog.cancel()
            }

        val alert = dialog.create()
        alert.setTitle(getString(R.string.picture_details_dialog_title))
        alert.show()
    }
}