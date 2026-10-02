package com.wojciechkula.deepskyapp.data.mapper

import com.wojciechkula.deepskyapp.core.common.decodeHtmlEntities

private const val SPACE = "[\\s\u00A0]"
private const val APOSTROPHE = "(?:['\u2019]|&#0?39;|&#8217;|&rsquo;)"

private val Tag = Regex("<[^>]*>")
private val BlockTag = Regex("<\\s*/?\\s*(?:br|p|div|li|hr|tr|td|h[1-6])\\b[^>]*>", RegexOption.IGNORE_CASE)
private val Whitespace = Regex("$SPACE+")
private val ExplanationLabel =
    Regex("^$SPACE*<(strong|b)>$SPACE*Explanation$SPACE*:?$SPACE*</\\1>$SPACE*:?$SPACE*", RegexOption.IGNORE_CASE)

// The site appends its own notices and a "Tomorrow's picture" teaser, which a favourite would keep for good.
private val SiteNotices = Regex("<(?:strong|b)>$SPACE*(?:APOD${APOSTROPHE}s |Tomorrow${APOSTROPHE}s picture)", RegexOption.IGNORE_CASE)
private val TrailingBreaks = Regex("(?:$SPACE|<br$SPACE*/?>|&nbsp;)+$", RegexOption.IGNORE_CASE)
private val CreditLabel = Regex("^[^:]{0,60}\\b(credit|copyright)\\b[^:]*:\\s*", RegexOption.IGNORE_CASE)
private val VideoSource = Regex("<(?:source|video|iframe)\\b[^>]*?\\bsrc\\s*=\\s*[\"']([^\"']+)[\"']", RegexOption.IGNORE_CASE)
private val WebUrl = Regex("^https?://[^/\\s]", RegexOption.IGNORE_CASE)
private val YouTubeEmbedId = Regex("youtube\\.com/embed/([A-Za-z0-9_-]+)")

// Inline tags go without a trace, so "NASA</a>, ESA" does not become "NASA , ESA".
internal fun String.htmlToPlainText(): String =
    replace(BlockTag, " ").replace(Tag, "").decodeHtmlEntities().replace(Whitespace, " ").trim()

internal fun String.withoutExplanationLabel(): String = replaceFirst(ExplanationLabel, "").trim()

internal fun String.withoutSiteNotices(): String {
    val notices = SiteNotices.find(this) ?: return this
    return substring(0, notices.range.first).replace(TrailingBreaks, "")
}

internal fun String.withoutCreditLabel(): String = replaceFirst(CreditLabel, "")

internal fun String.firstVideoSource(): String? =
    VideoSource.find(this)?.groupValues?.get(1)?.decodeHtmlEntities()
        ?.let { if (it.startsWith("//")) "https:$it" else it }
        ?.takeIf { WebUrl.containsMatchIn(it) }

internal fun String.youTubeThumbnailUrl(): String? =
    YouTubeEmbedId.find(this)?.groupValues?.get(1)?.let { "https://img.youtube.com/vi/$it/0.jpg" }
