package com.wojciechkula.deepskyapp.data.di

import com.wojciechkula.deepskyapp.core.common.DateFormatter
import com.wojciechkula.deepskyapp.data.database.dao.FavouritePictureDao
import com.wojciechkula.deepskyapp.data.database.entity.FavouritePictureEntity
import com.wojciechkula.deepskyapp.domain.repository.FavouriteRepository
import com.wojciechkula.deepskyapp.domain.repository.PictureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertNotNull

private class FakeFavouritePictureDao : FavouritePictureDao {
    private val rows = MutableStateFlow<List<FavouritePictureEntity>>(emptyList())
    override fun getFavouritePictures(): Flow<List<FavouritePictureEntity>> = rows
    override fun getByDate(date: String): Flow<List<FavouritePictureEntity>> = rows
    override suspend fun add(entity: FavouritePictureEntity): Long = 0L
    override suspend fun delete(date: String): Int = 0
}

class DataModuleTest {

    @Test
    fun dataModule_resolves_both_repositories() {
        val app = koinApplication {
            modules(
                dataModule(apiKey = "test-key"),
                module {
                    single<FavouritePictureDao> { FakeFavouritePictureDao() }
                    single { DateFormatter() }
                },
            )
        }
        val koin = app.koin

        assertNotNull(koin.get<PictureRepository>())
        assertNotNull(koin.get<FavouriteRepository>())

        koin.close()
    }
}
