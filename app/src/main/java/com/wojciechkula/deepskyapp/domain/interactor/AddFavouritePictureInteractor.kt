package com.wojciechkula.deepskyapp.domain.interactor

import android.graphics.Bitmap
import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDayModel
import com.wojciechkula.deepskyapp.domain.repository.PictureRepository
import javax.inject.Inject

class AddFavouritePictureInteractor @Inject constructor(private val repository: PictureRepository) {

    suspend operator fun invoke(
        picture: PictureOfTheDayModel,
        pictureBitmap: Bitmap
    ): Long {
        val favouritePicture = FavouritePictureModel(
            copyright = picture.copyright,
            date = picture.date,
            explanation = picture.explanation,
            hdurl = picture.hdurl,
            media_type = picture.media_type,
            service_version = picture.service_version,
            title = picture.title,
            url = picture.url,
            bitmap = pictureBitmap
        )
        return repository.addFavouritePicture(favouritePicture)
    }
}