package com.wojciechkula.deepskyapp.domain.interactor

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.wojciechkula.deepskyapp.domain.repository.PictureRepository
import com.wojciechkula.utils.DateFormatter
import javax.inject.Inject

class CheckIfPictureIsFavouriteInteractor @Inject constructor(private val repository: PictureRepository) {

    operator fun invoke(): LiveData<Boolean> =
        repository.getPictureByDate(DateFormatter.getCurrentDate()).map { list -> list.isNotEmpty() }
}