package cz.zcu.maturao.tetris.utils

import android.graphics.Paint

inline fun Paint.cleared(block: Paint.() -> Unit = {}) = apply(Paint::reset).apply(block)
