package cz.zcu.maturao.tetris.drawing

import android.graphics.Canvas

interface Drawable {
    fun draw(canvas: Canvas, x: Float, y: Float, width: Float, height: Float)
}