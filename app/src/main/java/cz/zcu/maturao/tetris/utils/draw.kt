package cz.zcu.maturao.tetris.utils

import android.graphics.Paint

inline fun Paint.cleared(block: Paint.() -> Unit = {}) = apply(Paint::reset).apply(block)

fun fitAspectRatio(width: Float, height: Float, aspectRatio: Float) =
    (height * aspectRatio).let {
        if (it <= width) Pair(it, height) else Pair(width, width / aspectRatio)
    }
