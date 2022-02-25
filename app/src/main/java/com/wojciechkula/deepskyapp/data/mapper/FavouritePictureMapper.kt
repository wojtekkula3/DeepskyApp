package com.wojciechkula.deepskyapp.data.mapper

import com.wojciechkula.deepskyapp.data.entity.FavouritePictureEntity
import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import javax.inject.Inject

class FavouritePictureMapper @Inject constructor() {

    fun mapToEntity(pictureModel: FavouritePictureModel) =
        FavouritePictureEntity(
            id = pictureModel.id,
            copyright = pictureModel.copyright,
            date = pictureModel.date,
            explanation = pictureModel.explanation,
            hdurl = pictureModel.hdurl,
            media_type = pictureModel.media_type,
            service_version = pictureModel.service_version,
            title = pictureModel.title,
            url = pictureModel.url,
            bitmap = pictureModel.bitmap
        )

    fun mapToDomain(pictureEntity: FavouritePictureEntity) =
        FavouritePictureModel(
            id = pictureEntity.id,
            copyright = pictureEntity.copyright,
            date = pictureEntity.date,
            explanation = pictureEntity.explanation,
            hdurl = pictureEntity.hdurl,
            media_type = pictureEntity.media_type,
            service_version = pictureEntity.service_version,
            title = pictureEntity.title,
            url = pictureEntity.url,
            bitmap = pictureEntity.bitmap
        )
}