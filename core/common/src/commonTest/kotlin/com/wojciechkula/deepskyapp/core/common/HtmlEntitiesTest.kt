package com.wojciechkula.deepskyapp.core.common

import kotlin.test.Test
import kotlin.test.assertEquals

class HtmlEntitiesTest {

    @Test
    fun `decodes named entities`() {
        assertEquals("Credit & Copyright <b> \"x\" 'y'", "Credit &amp; Copyright &lt;b&gt; &quot;x&quot; &apos;y&apos;".decodeHtmlEntities())
    }

    @Test
    fun `decodes decimal and hex code points`() {
        assertEquals("2024 December 7 \u2013 \u2013", "2024 December 7 &#8211; &#x2013;".decodeHtmlEntities())
    }

    @Test
    fun `leaves unknown entities and a bare ampersand as written`() {
        assertEquals("M&M &bogus; &#128640;", "M&M &bogus; &#128640;".decodeHtmlEntities())
    }

    @Test
    fun `decodes an escaped entity only once`() {
        assertEquals("&lt;", "&amp;lt;".decodeHtmlEntities())
    }
}
