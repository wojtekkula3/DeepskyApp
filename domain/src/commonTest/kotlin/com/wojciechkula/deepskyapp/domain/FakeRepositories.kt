package com.wojciechkula.deepskyapp.domain

import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDayModel
import com.wojciechkula.deepskyapp.domain.repository.FavouriteRepository
import com.wojciechkula.deepskyapp.domain.repository.PictureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakePictureRepository(
    var result: Result<PictureOfTheDayModel> =
        Result.Success(samplePotd()),
) : PictureRepository {
    override suspend fun getPictureOfTheDay(): Result<PictureOfTheDayModel> = result
}

class FakeFavouriteRepository : FavouriteRepository {
    val favourites = MutableStateFlow<List<FavouritePictureModel>>(emptyList())
    val added = mutableListOf<FavouritePictureModel>()
    val deletedDates = mutableListOf<String>()

    override fun getFavouritePictures(): Flow<List<FavouritePictureModel>> = favourites

    override fun isFavourite(date: String): Flow<Boolean> =
        MutableStateFlow(favourites.value.any { it.date == date })

    override suspend fun addFavouritePicture(picture: FavouritePictureModel): Long {
        added += picture
        favourites.value = favourites.value + picture
        return added.size.toLong()
    }

    override suspend fun deleteFavouritePicture(date: String): Int {
        deletedDates += date
        val before = favourites.value.size
        favourites.value = favourites.value.filterNot { it.date == date }
        return before - favourites.value.size
    }
}

fun samplePotd(date: String = "2026-07-07") = PictureOfTheDayModel(
    copyright = "NASA",
    date = date,
    explanation = "A galaxy.",
    hdUrl = "https://example.com/hd.jpg",
    mediaType = "image",
    serviceVersion = "v1",
    title = "Galaxy",
    url = "https://example.com/sd.jpg",
)
