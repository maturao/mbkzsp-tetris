package cz.zcu.maturao.tetris.game.ui

import android.graphics.Canvas

/**
 * Označuje třídu, kterou lze vykreslit na Canvas
 */
interface Drawable {
    fun draw(canvas: Canvas, x: Float, y: Float, width: Float, height: Float)
}