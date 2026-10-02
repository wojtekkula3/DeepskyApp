package com.wojciechkula.deepskyapp.core.common

private val Entity = Regex("&(#[xX][0-9a-fA-F]+|#[0-9]+|[a-zA-Z]+);")
private val NamedEntities = mapOf("amp" to "&", "lt" to "<", "gt" to ">", "quot" to "\"", "apos" to "'", "nbsp" to "\u00A0")

/**
 * Decodes numeric (`&#8211;`, `&#x2013;`) and the common named (`&amp;`, `&lt;`, `&gt;`, `&quot;`, `&apos;`,
 * `&nbsp;`) HTML entities. Anything it does not recognise, including code points outside the BMP, is left as written.
 */
fun String.decodeHtmlEntities(): String = replace(Entity) { match ->
    val name = match.groupValues[1]
    val codePoint = when {
        name.startsWith("#x", ignoreCase = true) -> name.drop(2).toIntOrNull(radix = 16)
        name.startsWith("#") -> name.drop(1).toIntOrNull()
        else -> return@replace NamedEntities[name] ?: match.value
    }
    if (codePoint != null && codePoint <= Char.MAX_VALUE.code) Char(codePoint).toString() else match.value
}
