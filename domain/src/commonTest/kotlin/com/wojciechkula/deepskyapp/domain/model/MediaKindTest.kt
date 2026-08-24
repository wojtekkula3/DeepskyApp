package com.wojciechkula.deepskyapp.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MediaKindTest {

    @Test
    fun mediaKind_returnsImageForAnImageMediaType() {
        assertEquals(MediaKind.IMAGE, mediaKind("image", "https://apod.nasa.gov/apod/image/2607/x.jpg"))
    }

    @Test
    fun mediaKind_returnsVideoFileForAnApodMp4Url() {
        assertEquals(
            MediaKind.VIDEO_FILE,
            mediaKind("video", "https://apod.nasa.gov/apod/image/2606/TritonPass_voyager2.mp4")
        )
    }

    // The set claims four extensions. Only mp4 has ever appeared in the feed, so the other three are
    // only ever exercised here — remove them from the set or keep this honest.
    @Test
    fun mediaKind_returnsVideoFileForEveryExtensionTheSetClaims() {
        listOf("clip.mp4", "clip.m4v", "clip.mov", "clip.webm").forEach { name ->
            assertEquals(MediaKind.VIDEO_FILE, mediaKind("video", "https://apod.nasa.gov/apod/image/2607/$name"), name)
        }
    }

    @Test
    fun mediaKind_returnsVideoEmbedForAYouTubeUrl() {
        assertEquals(MediaKind.VIDEO_EMBED, mediaKind("video", "https://www.youtube.com/embed/afHfMMC-MJE?rel=0"))
    }

    // A real entry (2026-08-23 as first published): the fragment comes *before* the query, which is
    // the reverse of the usual order and the shape most likely to trip up naive URL splitting.
    @Test
    fun mediaKind_returnsVideoEmbedForAVimeoUrlWithAFragmentBeforeTheQuery() {
        assertEquals(
            MediaKind.VIDEO_EMBED,
            mediaKind("video", "https://player.vimeo.com/video/11386048#t=0m58s?color=8BA0FF&portrait=0")
        )
    }

    // A real entry (2025-03-02): a video hosted on the same domain as every mp4, but an HTML page.
    // Classification has to go by the extension, not the host.
    @Test
    fun mediaKind_returnsVideoEmbedForAnApodHostedPageThatIsNotAFile() {
        assertEquals(
            MediaKind.VIDEO_EMBED,
            mediaKind("video", "https://apod.nasa.gov/apod/image/1803/AstroSoM/hudf.html")
        )
    }

    // A real entry (2024-08-11): a third-party interactive page, no extension at all.
    @Test
    fun mediaKind_returnsVideoEmbedForAPageWithNoExtension() {
        assertEquals(MediaKind.VIDEO_EMBED, mediaKind("video", "https://www.meteorshowers.org/view/perseids"))
    }

    @Test
    fun mediaKind_returnsUnsupportedForAnUnrecognisedMediaType() {
        assertEquals(MediaKind.UNSUPPORTED, mediaKind("other", "https://example.com/x"))
        assertEquals(MediaKind.UNSUPPORTED, mediaKind("", ""))
    }

    @Test
    fun isVideoFileUrl_ignoresCaseAndAnyTrailingQuery() {
        assertTrue(isVideoFileUrl("https://apod.nasa.gov/a.MP4"))
        assertTrue(isVideoFileUrl("https://apod.nasa.gov/a.mp4?v=2"))
        assertTrue(isVideoFileUrl("https://apod.nasa.gov/a.mov#t=1"))
        assertTrue(isVideoFileUrl("https://apod.nasa.gov/a.webm"))
    }

    @Test
    fun isVideoFileUrl_returnsFalseWithoutAVideoExtension() {
        assertFalse(isVideoFileUrl("https://www.youtube.com/embed/afHfMMC-MJE?rel=0"))
        assertFalse(isVideoFileUrl("https://apod.nasa.gov/apod/image/2607/x.jpg"))
        assertFalse(isVideoFileUrl(""))
    }

    // The extension is taken from the whole URL rather than its last path segment, so a host that
    // happens to end in one must not be read as a file. Pins the behaviour a tidier rewrite could lose.
    @Test
    fun isVideoFileUrl_doesNotTakeTheExtensionFromTheHost() {
        assertFalse(isVideoFileUrl("https://cdn.example.mov/watch/12345"))
    }
}
