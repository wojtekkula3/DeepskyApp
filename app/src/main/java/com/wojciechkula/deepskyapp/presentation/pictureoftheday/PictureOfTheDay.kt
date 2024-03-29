package com.wojciechkula.deepskyapp.presentation.pictureoftheday

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.wojciechkula.deepskyapp.R
import com.wojciechkula.deepskyapp.databinding.FragmentPictureOfTheDayBinding
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDayModel
import com.wojciechkula.utils.NetworkConnection
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PictureOfTheDay : Fragment() {

    private var _binding: FragmentPictureOfTheDayBinding? = null
    private val binding
        get() = _binding!!

    @Inject
    lateinit var hasNetworkConnection: NetworkConnection
    private val viewModel: PictureOfTheDayViewModel by activityViewModels()

    private var isPictureSet = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPictureOfTheDayBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.statusBarColor = activity?.let { ContextCompat.getColor(it, R.color.blue_700) }!!
        observeViewModel()
        observeNetworkConnection()
        initViews()
    }

    private fun observeNetworkConnection() {
        hasNetworkConnection.observe(viewLifecycleOwner) {
            viewModel.changeNetworkConnectionStatus(it)
        }
    }

    private fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner, ::bindState)
        viewModel.viewEvent.observe(viewLifecycleOwner, ::handleEvents)
        viewModel.isFavouriteState.observe(viewLifecycleOwner, ::bindIsFavouriteState)
    }

    private fun bindIsFavouriteState(isAlreadyFavourite: Boolean) {
        if (isAlreadyFavourite) {
            binding.addToFavouriteButton.setColorFilter(
                ActivityCompat.getColor(
                    this@PictureOfTheDay.requireContext(),
                    R.color.red_500
                )
            )
        } else {
            binding.addToFavouriteButton.setColorFilter(
                ActivityCompat.getColor(
                    this@PictureOfTheDay.requireContext(),
                    androidx.appcompat.R.color.material_grey_600
                )
            )
        }
    }

    private fun initViews() {
        binding.addToFavouriteButton.setOnClickListener { viewModel.onFavouriteButtonClick(getBitmap()) }
    }

    private fun getBitmap(): Bitmap {
        val bitmapDrawable = binding.imageOutput.drawable as BitmapDrawable
        return bitmapDrawable.bitmap
    }

    private fun bindState(state: PictureOfTheDayViewState) {
        with(state) {
            if (pictureOfTheDay != null) {
                if (!isPictureSet) {
                    setPictureInfo(pictureOfTheDay)
                    isPictureSet = true
                }
            } else {
                binding.imageCardView.visibility = View.GONE
                binding.infoCardView.visibility = View.GONE
            }

            if (hasInternetConnection) {
                binding.noInternetLayout.visibility = View.GONE
            } else if (!hasInternetConnection) {
                binding.noInternetLayout.visibility = View.VISIBLE
            }

            if (isLoading) {
                binding.loadingProgressBar.visibility = View.VISIBLE
            } else {
                binding.loadingProgressBar.visibility = View.GONE
            }

            if (timeToNewPicture.isNotEmpty()) {
                binding.newPictureInLabel.text = getString(R.string.apod_new_picture_in_time, timeToNewPicture)
                binding.newPictureInLabel.visibility = View.VISIBLE
            } else {
                binding.newPictureInLabel.visibility = View.GONE

            }
        }
    }

    private fun setPictureInfo(pictureOfTheDay: PictureOfTheDayModel) {
        Glide.with(this@PictureOfTheDay)
            .load(pictureOfTheDay.url)
            .into(binding.imageOutput)

        binding.titleOutput.text = pictureOfTheDay.title
        if (pictureOfTheDay.copyright != null) {
            // replace unnecessary new line signs
            binding.copyrightOutput.text = pictureOfTheDay.copyright.replace("\n", "")
        } else {
            binding.copyrightLabel.visibility = View.GONE
            binding.copyrightOutput.visibility = View.GONE
        }
        binding.dateOutput.text = pictureOfTheDay.date
        binding.explanationOutput.text = pictureOfTheDay.explanation
        binding.imageCardView.visibility = View.VISIBLE
        binding.infoCardView.visibility = View.VISIBLE
    }

    private fun handleEvents(event: PictureOfTheDayViewEvent) {
        when (event) {
            is PictureOfTheDayViewEvent.ShowError -> onShowError(event.message)
        }
    }

    private fun onShowError(message: String) {
        Toast.makeText(context, "APOD API Error: $message", Toast.LENGTH_SHORT).show()
    }

}