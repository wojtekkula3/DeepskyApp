package com.wojciechkula.deepskyapp.core.media

import coil3.ColorImage
import coil3.ComponentRegistry
import coil3.Image
import coil3.Uri
import coil3.decode.DataSource
import coil3.fetch.Fetcher
import coil3.fetch.ImageFetchResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

private const val VIDEO_URL = "https://apod.nasa.gov/apod/image/2607/Auroras_Esa.mp4"
private const val IMAGE_URL = "https://apod.nasa.gov/apod/image/2607/Auroras_Esa.jpg"

private class FakeVideoFrameLoader(private val frame: Image?) : VideoFrameLoader {

    val requestedUrls = mutableListOf<String>()
    val requestedBounds = mutableListOf<Pair<Int, Int>>()

    override suspend fun loadFrame(
        url: String,
        maxWidth: Int,
        maxHeight: Int
    ): Image? {
        requestedUrls += url
        requestedBounds += maxWidth to maxHeight
        return frame
    }
}

class VideoFrameFetcherTest {

    @Test
    fun addVideoFrameFetcher_registersTheFactoryForUriData() {
        val registry = ComponentRegistry.Builder()
            .addVideoFrameFetcher({ true }, FakeVideoFrameLoader(ColorImage()))
            .build()

        val (factory, type) = registry.fetcherFactories.single()
        assertIs<VideoFrameFetcher.Factory>(factory)
        assertEquals(Uri::class, type)
    }

    @Test
    fun addVideoFrameFetcher_keepsTheVideoFactoryAheadOfOneAddedAfterIt() {
        // Coil takes the first factory that claims the URI, and the network fetcher claims every https
        // URL, so anything registered after it is unreachable.
        val registry = ComponentRegistry.Builder()
            .addVideoFrameFetcher({ true }, FakeVideoFrameLoader(ColorImage()))
            .add(Fetcher.Factory<Uri> { _, _, _ -> null }, Uri::class)
            .build()

        assertEquals(2, registry.fetcherFactories.size)
        assertIs<VideoFrameFetcher.Factory>(registry.fetcherFactories.first().first)
    }

    @Test
    fun fetch_returnsTheFrameReadFromTheRequestedUrl() = runTest {
        val frame = ColorImage()
        val frameLoader = FakeVideoFrameLoader(frame)

        val result = VideoFrameFetcher(VIDEO_URL, frameLoader, maxWidth = 0, maxHeight = 0).fetch()

        assertIs<ImageFetchResult>(result)
        assertSame(frame, result.image)
        assertEquals(DataSource.NETWORK, result.dataSource)
        assertEquals(listOf(VIDEO_URL), frameLoader.requestedUrls)
    }

    @Test
    fun fetch_throwsInsteadOfReturningNull_whenNoFrameCanBeRead() = runTest {
        val fetcher = VideoFrameFetcher(VIDEO_URL, FakeVideoFrameLoader(null), maxWidth = 0, maxHeight = 0)

        assertFailsWith<IllegalStateException> { fetcher.fetch() }
    }

    @Test
    fun fetch_passesTheRequestedBoundsToTheLoaderAndReportsSampling() = runTest {
        val frameLoader = FakeVideoFrameLoader(ColorImage())

        val result = VideoFrameFetcher(VIDEO_URL, frameLoader, maxWidth = 320, maxHeight = 180).fetch()

        assertIs<ImageFetchResult>(result)
        assertEquals(listOf(320 to 180), frameLoader.requestedBounds)
        assertTrue(result.isSampled)
    }

    @Test
    fun fetch_reportsNoSampling_whenTheBoundsAreUnconstrained() = runTest {
        val frameLoader = FakeVideoFrameLoader(ColorImage())

        val result = VideoFrameFetcher(VIDEO_URL, frameLoader, maxWidth = 0, maxHeight = 0).fetch()

        assertIs<ImageFetchResult>(result)
        assertFalse(result.isSampled)
    }

    @Test
    fun fetcherOrNull_appliesTheIsVideoUrlPredicate() {
        val factory = VideoFrameFetcher.Factory({ url -> url.endsWith(".mp4") }, FakeVideoFrameLoader(ColorImage()))

        assertIs<VideoFrameFetcher>(factory.fetcherOrNull(VIDEO_URL))
        assertNull(factory.fetcherOrNull(IMAGE_URL))
    }
}
