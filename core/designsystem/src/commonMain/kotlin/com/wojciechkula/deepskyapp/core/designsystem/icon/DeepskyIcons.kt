package com.wojciechkula.deepskyapp.core.designsystem.icon

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.wojciechkula.deepskyapp.core.designsystem.resources.Res
import com.wojciechkula.deepskyapp.core.designsystem.resources.content_description_back
import com.wojciechkula.deepskyapp.core.designsystem.resources.content_description_dismiss
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_back
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_close
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_favourite
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_image
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_info
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * The app's icons, ported from the original app's vector drawables. Compose Multiplatform's
 * material3 does not bundle material-icons, and text glyphs are not a substitute: Android renders
 * `♥` through the colour-emoji font, which ignores the requested colour, so a glyph cannot show
 * state. Keeping the generated [Res] accessors internal means callers see painters, not resources.
 */
object DeepskyIcons {

    @Composable
    fun back(): Painter = painterResource(Res.drawable.ic_back)

    @Composable
    fun close(): Painter = painterResource(Res.drawable.ic_close)

    @Composable
    fun favourite(): Painter = painterResource(Res.drawable.ic_favourite)

    @Composable
    fun info(): Painter = painterResource(Res.drawable.ic_info)

    @Composable
    fun picture(): Painter = painterResource(Res.drawable.ic_image)
}

/**
 * Content descriptions for the icons that more than one feature renders. They live next to the icon
 * they describe because the generated [Res] accessors are internal to this module, so a feature
 * cannot read this module's strings directly.
 */
object DeepskyContentDescriptions {

    val back: String
        @Composable get() = stringResource(Res.string.content_description_back)
}
