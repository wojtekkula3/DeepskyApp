package com.wojciechkula.deepskyapp.feature.picture

import com.wojciechkula.deepskyapp.domain.Result
import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDayModel
import com.wojciechkula.deepskyapp.domain.repository.FavouriteRepository
import com.wojciechkula.deepskyapp.domain.repository.PictureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakePictureRepository(
    var result: Result<PictureOfTheDayModel> = Result.Success(sampleApod),
) : PictureRepository {
    override suspend fun getPictureOfTheDay(): Result<PictureOfTheDayModel> = result
}

class FakeFavouriteRepository : FavouriteRepository {
    val stored = MutableStateFlow<List<FavouritePictureModel>>(emptyList())
    var addResult: Long = 1L
    var deleteResult: Int = 1
    var throwOnAdd: Boolean = false
    var throwOnDelete: Boolean = false

    override fun getFavouritePictures(): Flow<List<FavouritePictureModel>> = stored

    // Reactive: re-emits whenever `stored` changes so the ViewModel's isFavourite state stays in sync.
    override fun isFavourite(date: String): Flow<Boolean> =
        stored.map { list -> list.any { it.date == date } }

    override suspend fun addFavouritePicture(picture: FavouritePictureModel): Long {
        if (throwOnAdd) throw RuntimeException("add failed")
        stored.value = stored.value + picture
        return addResult
    }

    override suspend fun deleteFavouritePicture(date: String): Int {
        if (throwOnDelete) throw RuntimeException("delete failed")
        stored.value = stored.value.filterNot { it.date == date }
        return deleteResult
    }
}

val sampleApod = PictureOfTheDayModel(
    copyright = "NASA",
    date = "2026-07-12",
    explanation = "A test nebula.",
    hdUrl = "https://example.com/hd.jpg",
    mediaType = "image",
    serviceVersion = "v1",
    title = "Test Nebula",
    url = "https://example.com/image.jpg",
)
