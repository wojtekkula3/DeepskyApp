package com.wojciechkula.deepskyapp.di

import com.wojciechkula.deepskyapp.core.common.DateFormatter
import com.wojciechkula.deepskyapp.domain.Result
import com.wojciechkula.deepskyapp.domain.interactor.AddFavouritePictureInteractor
import com.wojciechkula.deepskyapp.domain.interactor.CheckIfPictureIsFavouriteInteractor
import com.wojciechkula.deepskyapp.domain.interactor.DeleteFavouritePictureInteractor
import com.wojciechkula.deepskyapp.domain.interactor.GetFavouritePicturesInteractor
import com.wojciechkula.deepskyapp.domain.interactor.GetPictureOfTheDayInteractor
import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDayModel
import com.wojciechkula.deepskyapp.domain.repository.FavouriteRepository
import com.wojciechkula.deepskyapp.domain.repository.PictureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertNotNull

private class FakePictureRepository : PictureRepository {
    override suspend fun getPictureOfTheDay(): Result<PictureOfTheDayModel> =
        Result.Success(
            PictureOfTheDayModel(
                date = "2026-07-10",
                explanation = "",
                hdUrl = "",
                mediaType = "image",
                serviceVersion = "v1",
                title = "",
                url = "",
            )
        )
}

private class FakeFavouriteRepository : FavouriteRepository {
    override fun getFavouritePictures(): Flow<List<FavouritePictureModel>> = flowOf(emptyList())
    override fun isFavourite(date: String): Flow<Boolean> = flowOf(false)
    override suspend fun addFavouritePicture(picture: FavouritePictureModel): Long = 0L
    override suspend fun deleteFavouritePicture(date: String): Int = 0
}

class DomainModuleTest {

    @Test
    fun domainModule_resolves_all_interactors() {
        val app = koinApplication {
            modules(
                module {
                    single<PictureRepository> { FakePictureRepository() }
                    single<FavouriteRepository> { FakeFavouriteRepository() }
                    single { DateFormatter() }
                },
                domainModule,
            )
        }
        val koin = app.koin

        assertNotNull(koin.get<GetPictureOfTheDayInteractor>())
        assertNotNull(koin.get<GetFavouritePicturesInteractor>())
        assertNotNull(koin.get<AddFavouritePictureInteractor>())
        assertNotNull(koin.get<DeleteFavouritePictureInteractor>())
        assertNotNull(koin.get<CheckIfPictureIsFavouriteInteractor>())

        koin.close()
    }
}
