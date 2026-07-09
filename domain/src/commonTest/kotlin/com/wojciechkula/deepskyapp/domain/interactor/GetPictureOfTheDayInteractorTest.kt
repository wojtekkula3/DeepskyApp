package com.wojciechkula.deepskyapp.domain.interactor

import com.wojciechkula.deepskyapp.domain.FakePictureRepository
import com.wojciechkula.deepskyapp.domain.Result
import com.wojciechkula.deepskyapp.domain.samplePotd
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetPictureOfTheDayInteractorTest {

    @Test
    fun invoke_returnsRepositoryResult() = runTest {
        val repo = FakePictureRepository(result = Result.Success(samplePotd()))
        val interactor = GetPictureOfTheDayInteractor(repo)

        val result = interactor()

        assertEquals(Result.Success(samplePotd()), result)
    }
}
