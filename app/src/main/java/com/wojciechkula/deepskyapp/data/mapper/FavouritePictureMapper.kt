package com.wojciechkula.deepskyapp.data.mapper

import com.wojciechkula.deepskyapp.data.entity.FavouritePictureEntity
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDay
import javax.inject.Inject

class FavouritePictureMapper @Inject constructor() {

    fun mapToEntity(pictureModel : PictureOfTheDay) =
        FavouritePictureEntity(
            copyright = pictureModel.copyright,
            date = pictureModel.date,
            explanation = pictureModel.explanation,
            hdurl = pictureModel.hdurl,
            media_type = pictureModel.media_type,
            service_version = pictureModel.service_version,
            title = pictureModel.title,
            url = pictureModel.url
        )

    fun mapToDomain(favouritePictureEntity: FavouritePictureEntity) =
        PictureOfTheDay(
            copyright = favouritePictureEntity.copyright,
            date = favouritePictureEntity.date,
            explanation = favouritePictureEntity.explanation,
            hdurl = favouritePictureEntity.hdurl,
            media_type = favouritePictureEntity.media_type,
            service_version = favouritePictureEntity.service_version,
            title = favouritePictureEntity.title,
            url = favouritePictureEntity.url
        )
}