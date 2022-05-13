package cz.zcu.maturao.tetris.utils

import android.graphics.Paint

inline fun Paint.cleared(block: Paint.() -> Unit = {}) = apply(Paint::reset).apply(block)

fun fitAspectRatio(width: Float, height: Float, aspectRatio: Float) =
    (height * aspectRatio).let { newWidth ->
        if (newWidth <= width) Pair(newWidth, height) else Pair(width, width / aspectRatio)
    }
