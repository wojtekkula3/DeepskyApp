package com.wojciechkula.deepskyapp.feature.picture

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private val Styles = TextLinkStyles()

private fun parse(html: String) = explanationToAnnotatedString(html, Styles)

class ExplanationHtmlTest {

    @Test
    fun plainLegacyText_isKeptAsIs() {
        val text = parse("A distant galaxy, about 2 million light-years away.")

        assertEquals("A distant galaxy, about 2 million light-years away.", text.text)
        assertTrue(text.spanStyles.isEmpty())
    }

    @Test
    fun anchor_becomesAClickableUrlOverItsText() {
        val text = parse("What does the <a href=\"https://example.com/m31\" target=\"_blank\">Andromeda galaxy</a> look like?")

        assertEquals("What does the Andromeda galaxy look like?", text.text)
        val link = text.getLinkAnnotations(0, text.length).single()
        assertEquals("https://example.com/m31", (link.item as LinkAnnotation.Url).url)
        assertEquals("Andromeda galaxy", text.text.substring(link.start, link.end))
    }

    @Test
    fun anchorWithoutHref_rendersAsPlainText() {
        val text = parse("Video by <a>Nick Wright</a>.")

        assertEquals("Video by Nick Wright.", text.text)
        assertTrue(text.getLinkAnnotations(0, text.length).isEmpty())
    }

    @Test
    fun boldAndItalic_areStyled() {
        val text = parse("<b>Bold</b> and <em>italic</em>")

        assertEquals("Bold and italic", text.text)
        val bold = text.spanStyles.single { it.item.fontWeight == FontWeight.SemiBold }
        val italic = text.spanStyles.single { it.item.fontStyle == FontStyle.Italic }
        assertEquals("Bold", text.text.substring(bold.start, bold.end))
        assertEquals("italic", text.text.substring(italic.start, italic.end))
    }

    @Test
    fun lineBreaks_areKeptAndTrailingOnesTrimmed() {
        val text = parse("light emissions.<br /><br /><b> APOD Year in Review: </b> <a href=\"https://youtu.be/x\">Talk</a> <br />")

        assertEquals("light emissions.\n\nAPOD Year in Review: Talk", text.text)
    }

    @Test
    fun paragraphs_areSeparatedByOneBlankLine() {
        assertEquals("One.\n\nTwo.", parse("<p>One.</p><p>Two.</p>").text)
    }

    @Test
    fun whitespaceAndEntities_areNormalised() {
        assertEquals("Stars & dust < 1 – 2", parse("  Stars &amp;\n   dust &lt; 1 &#8211; 2  ").text)
    }

    @Test
    fun unbalancedTags_doNotBreakParsing() {
        val text = parse("<b>one <i>two</b> three</i> <sub>4</sub>")

        assertEquals("one two three 4", text.text)
    }

    @Test
    fun linkHref_hasItsEntitiesDecoded() {
        val text = parse("<a href=\"http://eol.jsc.nasa.gov/photo.pl?mission=ISS002&amp;roll=E&#038;frame=7377\">photo</a>")

        assertEquals("http://eol.jsc.nasa.gov/photo.pl?mission=ISS002&roll=E&frame=7377", text.linkUrls().single())
    }

    @Test
    fun protocolRelativeHref_getsHttps() {
        assertEquals("https://en.wikipedia.org/wiki/", parse("<a href=\"//en.wikipedia.org/wiki/\">wiki</a>").linkUrls().single())
    }

    @Test
    fun relativeOrJunkHref_isPlainText() {
        val text = parse("See <a href=\"ap240101.html\">yesterday</a> and <a href=\"\u201c\">this</a>.")

        assertEquals("See yesterday and this.", text.text)
        assertTrue(text.linkUrls().isEmpty())
    }

    @Test
    fun htmlComments_areDropped() {
        assertEquals("Stars and dust.", parse("<!-- wp:paragraph -->Stars <!--more-->and dust.<!-- /wp:paragraph -->").text)
    }

    @Test
    fun plainTextComparisons_areNotTakenForTags() {
        assertEquals("redshift z<1 and masses > 3 suns", parse("redshift z<1 and masses > 3 suns").text)
    }

    @Test
    fun hrefWithoutAHost_isPlainText() {
        val text = parse("<a href=\"///Users/jtbonnel/Desktop/apodT/ap261004.html\">Sunday's Childe</a>")

        assertEquals("Sunday's Childe", text.text)
        assertTrue(text.linkUrls().isEmpty())
    }
}

private fun AnnotatedString.linkUrls(): List<String> =
    getLinkAnnotations(0, length).map { (it.item as LinkAnnotation.Url).url }
