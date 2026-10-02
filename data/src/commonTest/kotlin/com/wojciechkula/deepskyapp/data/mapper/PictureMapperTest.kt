package com.wojciechkula.deepskyapp.data.mapper

import com.wojciechkula.deepskyapp.data.api.dto.ApodBasicDto
import com.wojciechkula.deepskyapp.domain.model.MediaKind
import com.wojciechkula.deepskyapp.domain.model.mediaKind
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

// Trimmed from real apod-basic responses: 260927 (image), 260913 (video file), 241204 (YouTube),
// 241207 (an archive entry migrated without a media type).
private const val HD_URL =
    "https://assets.science.nasa.gov/dynamicimage/assets/science/cds/apod/apod/2026/september/M31Before_Scherer_4298.jpg" +
        "?w=4298&h=3394&fit=clip&crop=faces%2Cfocalpoint"
private const val FULL_SCREEN_URL =
    "https://assets.science.nasa.gov/dynamicimage/assets/science/cds/apod/apod/2026/september/M31Before_Scherer_4298.jpg" +
        "?w=4096&h=3394&fit=clip&crop=faces%2Cfocalpoint"
private const val CARD_URL =
    "https://assets.science.nasa.gov/dynamicimage/assets/science/cds/apod/apod/2026/september/M31Before_Scherer_4298.jpg" +
        "?w=1280&h=3394&fit=clip&crop=faces%2Cfocalpoint"

private fun dto(
    mediaType: String? = "image",
    explanation: String = "<strong>Explanation:</strong> What does the Andromeda galaxy really look like?",
    copyright: String = "<a href=\"https://www.flickr.com/people/kees-scherer/\">Kees Scherer</a>",
    title: String = "Andromeda Before Photoshop",
    hdUrl: String = HD_URL,
    basicHtml: String = "<html><body><img src=\"$HD_URL\"></body></html>"
) = ApodBasicDto(
    date = "2026-09-27",
    title = title,
    mediaType = mediaType,
    explanation = explanation,
    copyright = copyright,
    hdUrl = hdUrl,
    basicHtml = basicHtml
)

class PictureMapperTest {

    @Test
    fun `an image is shown from hdurl scaled down for the card and never from the article url`() {
        val picture = dto().toDomain()

        assertEquals(CARD_URL, picture.url)
        assertEquals(FULL_SCREEN_URL, picture.hdUrl)
        assertEquals(MediaKind.IMAGE, mediaKind(picture.mediaType, picture.url))
    }

    @Test
    fun `the full-screen copy is capped to the 4096 px box Coil decodes into`() {
        val original = "https://assets.science.nasa.gov/dynamicimage/a/sharpless_catalog.png?w=4455&h=5592&fit=clip"

        assertEquals(
            "https://assets.science.nasa.gov/dynamicimage/a/sharpless_catalog.png?w=4096&h=4096&fit=clip",
            dto(hdUrl = original).toDomain().hdUrl
        )
    }

    @Test
    fun `an hdurl already narrower than the card or without a width is used as is`() {
        val narrow = "https://assets.science.nasa.gov/dynamicimage/a/CosmicLatte_960.jpg?w=960&h=640&fit=clip"
        val plain = "https://apod.nasa.gov/apod/image/2609/CosmicLatte.jpg"

        assertEquals(narrow, dto(hdUrl = narrow).toDomain().url)
        assertEquals(plain, dto(hdUrl = plain).toDomain().url)
    }

    @Test
    fun `a video file is taken from the source tag of basic_html`() {
        val mp4 = "https://assets.science.nasa.gov/content/dam/science/cds/apod/apod/2026/september/NoctilucentNeowise_Girotti.mp4"
        val picture = dto(
            mediaType = "video",
            hdUrl = "https://assets.science.nasa.gov/dynamicimage/assets/science/cds/apod/apod/2026/september/Comet_snapshot.png",
            basicHtml = "<center><video controls><source src=\"$mp4\" type=\"video/mp4\"></video></center>"
        ).toDomain()

        assertEquals(mp4, picture.url)
        assertEquals("video", picture.mediaType)
        assertNull(picture.thumbnailUrl)
        assertEquals(MediaKind.VIDEO_FILE, mediaKind(picture.mediaType, picture.url))
    }

    @Test
    fun `a protocol-relative YouTube iframe becomes an https embed with a YouTube thumbnail`() {
        val picture = dto(
            mediaType = "video",
            basicHtml = "<iframe width=\"960\" height=\"540\" src=\"//www.youtube.com/embed/7QB_MOemCqs?rel=0\" frameborder=\"0\"></iframe>"
        ).toDomain()

        assertEquals("https://www.youtube.com/embed/7QB_MOemCqs?rel=0", picture.url)
        assertEquals("https://img.youtube.com/vi/7QB_MOemCqs/0.jpg", picture.thumbnailUrl)
        assertEquals(MediaKind.VIDEO_EMBED, mediaKind(picture.mediaType, picture.url))
    }

    @Test
    fun `the iframe media type is treated as a video`() {
        val picture = dto(mediaType = "iframe", basicHtml = "<iframe src=\"https://player.vimeo.com/video/1\"></iframe>").toDomain()

        assertEquals("video", picture.mediaType)
        assertEquals("https://player.vimeo.com/video/1", picture.url)
        assertNull(picture.thumbnailUrl)
    }

    @Test
    fun `entities in a video source are decoded`() {
        val picture = dto(
            mediaType = "video",
            basicHtml = "<iframe src=\"https://player.vimeo.com/video/32095756?title=0&#038;byline=0&amp;portrait=0\"></iframe>"
        ).toDomain()

        assertEquals("https://player.vimeo.com/video/32095756?title=0&byline=0&portrait=0", picture.url)
    }

    @Test
    fun `a missing media type or a video without a source is unsupported`() {
        assertEquals(MediaKind.UNSUPPORTED, dto(mediaType = null).toDomain().let { mediaKind(it.mediaType, it.url) })
        assertEquals(MediaKind.UNSUPPORTED, dto(mediaType = "video").toDomain().let { mediaKind(it.mediaType, it.url) })
    }

    @Test
    fun `the explanation keeps its HTML but loses the Explanation label`() {
        val strong = dto(explanation = "<strong>Explanation:</strong> See <a href=\"https://x\">M31</a>.").toDomain()
        val bold = dto(explanation = "<b>Explanation</b>: The Southern Crown dazzles.").toDomain()
        val bare = dto(explanation = "There was something behind the clouds.").toDomain()

        assertEquals("See <a href=\"https://x\">M31</a>.", strong.explanation)
        assertEquals("The Southern Crown dazzles.", bold.explanation)
        assertEquals("There was something behind the clouds.", bare.explanation)
    }

    @Test
    fun `the copyright becomes plain text without its credit label`() {
        val linked = dto().toDomain()
        val labelled = dto(
            copyright = "<b>Image Credit &amp; <a href=\"https://science.nasa.gov/apod/about-apod/\">Copyright</a>:</b> " +
                "<a href=\"http://www.astrobin.com/users/SkyHunter/\">Federico Pelliccia</a>"
        ).toDomain()
        val video = dto(copyright = "<b> Video Credit &amp; Copyright: </b> <a>Nick Wright</a>").toDomain()

        assertEquals("Kees Scherer", linked.copyright)
        assertEquals("Federico Pelliccia", labelled.copyright)
        assertEquals("Nick Wright", video.copyright)
    }

    @Test
    fun `an empty copyright is null so the row is hidden`() {
        assertNull(dto(copyright = "").toDomain().copyright)
    }

    @Test
    fun `entities in the title are decoded`() {
        assertEquals(
            "APOD: 2024 December 7 – Rocket Engine Fireplace",
            dto(title = "APOD: 2024 December 7 &#8211; Rocket Engine Fireplace").toDomain().title
        )
        assertEquals("Stars & Dust", dto(title = "Stars &amp; Dust").toDomain().title)
    }

    @Test
    fun `the site notices and the tomorrow teaser are cut from the explanation`() {
        val explanation = "<strong>Explanation:</strong> Mars at <a href=\"https://mars.nasa.gov\">Gale crater</a>." +
            "<br><br><strong>APOD's email for image submissions has changed.</strong> Please see: " +
            "<a href=\"https://science.nasa.gov/apod/submit/\">APOD Submissions</a>.<br>" +
            "<strong>Tomorrow's picture: </strong><a href=\"///Users/jtbonnel/Desktop/apodT/ap261004.html\">Sunday's Childe</a>"

        assertEquals("Mars at <a href=\"https://mars.nasa.gov\">Gale crater</a>.", dto(explanation = explanation).toDomain().explanation)
    }

    @Test
    fun `an explanation label followed by a non-breaking space is removed`() {
        assertEquals(
            "This sight was worth getting up for.",
            dto(explanation = "<strong>Explanation:\u00A0</strong>This sight was worth getting up for.").toDomain().explanation
        )
    }

    @Test
    fun `inline tags leave no space before punctuation in the copyright`() {
        val copyright = "<strong>Image Credit:</strong> <a href=\"https://x\">NASA</a>, <a href=\"https://y\">JPL-Caltech</a>, " +
            "<a href=\"https://z\">MSSS</a> - Panorama: <a href=\"https://w\">Andrew Bodrov</a>"

        assertEquals("NASA, JPL-Caltech, MSSS - Panorama: Andrew Bodrov", dto(copyright = copyright).toDomain().copyright)
    }

    @Test
    fun `a video source without a host is unsupported`() {
        val picture = dto(mediaType = "video", basicHtml = "<iframe src=\"///Users/jtbonnel/video.html\"></iframe>").toDomain()

        assertEquals(MediaKind.UNSUPPORTED, mediaKind(picture.mediaType, picture.url))
    }

    @Test
    fun `explicit nulls in the response map to empty values`() {
        val picture = ApodBasicDto(
            date = "2024-12-07",
            title = null,
            mediaType = null,
            explanation = null,
            copyright = null,
            hdUrl = null,
            basicHtml = null
        ).toDomain()

        assertEquals("", picture.title)
        assertEquals("", picture.explanation)
        assertNull(picture.copyright)
        assertEquals(MediaKind.UNSUPPORTED, mediaKind(picture.mediaType, picture.url))
    }
}
