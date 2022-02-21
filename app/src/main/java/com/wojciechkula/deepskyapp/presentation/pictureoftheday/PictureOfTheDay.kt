package com.wojciechkula.deepskyapp.presentation.pictureoftheday

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.wojciechkula.deepskyapp.databinding.FragmentPictureOfTheDayBinding
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDay
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPictureOfTheDayBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeNetworkConnection()
        observeViewModel()
    }

    private fun observeNetworkConnection() {
        hasNetworkConnection.observe(viewLifecycleOwner) {
            viewModel.changeNetworkConnection(it)
        }
    }

    private fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner, ::bindState)
        viewModel.viewEvent.observe(viewLifecycleOwner, ::handleEvents)
    }

    private fun bindState(state: PictureOfTheDayViewState) {
        with(state) {
            if (pictureOfTheDay != null) {
                setPictureInfo(pictureOfTheDay)
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
        }
    }

    private fun setPictureInfo(pictureOfTheDay: PictureOfTheDay) {
        Glide.with(this@PictureOfTheDay)
            .load(pictureOfTheDay.url)
            .into(binding.imageOutput)

        binding.titleOutput.text = pictureOfTheDay.title
        if (pictureOfTheDay.copyright != null) {
            binding.copyrightOutput.text = pictureOfTheDay.copyright
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