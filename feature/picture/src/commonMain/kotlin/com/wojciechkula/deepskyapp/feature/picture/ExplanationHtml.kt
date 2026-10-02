package com.wojciechkula.deepskyapp.feature.picture

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.wojciechkula.deepskyapp.core.common.decodeHtmlEntities

private val Tag = Regex("<\\s*(/?)\\s*([a-zA-Z][a-zA-Z0-9]*)([^>]*)>")
private val Comment = Regex("<!--[\\s\\S]*?-->")
private val WebUrl = Regex("^https?://[^/\\s]", RegexOption.IGNORE_CASE)
private val Href = Regex("\\bhref\\s*=\\s*[\"']([^\"']*)[\"']", RegexOption.IGNORE_CASE)
private val Whitespace = Regex("\\s+")
private val Bold = SpanStyle(fontWeight = FontWeight.SemiBold)
private val Italic = SpanStyle(fontStyle = FontStyle.Italic)

// AnnotatedString.fromHtml is Android-only in Compose Multiplatform, so the subset APOD uses is parsed here.
internal fun explanationToAnnotatedString(html: String, linkStyles: TextLinkStyles): AnnotatedString {
    val builder = AnnotatedString.Builder()
    val open = mutableListOf<Pair<String, Int>>()
    var atLineStart = true
    var trailingBreaks = 0

    fun appendText(raw: String) {
        val text = raw.decodeHtmlEntities().replace(Whitespace, " ").let { if (atLineStart) it.trimStart() else it }
        if (text.isEmpty()) return
        builder.append(text)
        atLineStart = text.last() == ' '
        trailingBreaks = 0
    }

    fun lineBreak() {
        if (builder.length == 0) return
        builder.append('\n')
        trailingBreaks++
        atLineStart = true
    }

    fun paragraphBreak() {
        while (builder.length > 0 && trailingBreaks < 2) lineBreak()
    }

    fun close(name: String) {
        val index = open.indexOfLast { it.first == name }
        if (index < 0) return
        builder.pop(open[index].second)
        open.subList(index, open.size).clear()
    }

    // A relative or junk href would be styled as a link that does nothing when tapped.
    fun openLink(attributes: String) {
        val href = Href.find(attributes)?.groupValues?.get(1)?.decodeHtmlEntities()?.trim() ?: return
        val url = if (href.startsWith("//")) "https:$href" else href
        if (WebUrl.containsMatchIn(url)) open += "a" to builder.pushLink(LinkAnnotation.Url(url, linkStyles))
    }

    val source = html.replace(Comment, "")
    var cursor = 0
    Tag.findAll(source).forEach { tag ->
        appendText(source.substring(cursor, tag.range.first))
        cursor = tag.range.last + 1
        val (closing, rawName, attributes) = tag.destructured
        val name = rawName.lowercase()
        when {
            closing.isNotEmpty() -> close(name).also { if (name == "p") paragraphBreak() }
            name == "a" -> openLink(attributes)
            name == "b" || name == "strong" -> open += name to builder.pushStyle(Bold)
            name == "i" || name == "em" -> open += name to builder.pushStyle(Italic)
            name == "br" || name == "hr" -> lineBreak()
            name == "p" -> paragraphBreak()
        }
    }
    appendText(source.substring(cursor))

    val text = builder.toAnnotatedString()
    return text.subSequence(0, text.text.trimEnd().length)
}
