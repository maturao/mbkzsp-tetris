package cz.zcu.maturao.tetris.game.ui

import android.graphics.Canvas
import cz.zcu.maturao.tetris.game.TouchInput

class CanvasToggleButton(
    val toggledDrawable: Drawable,
    val untoggledDrawable: Drawable,
    val onToggle: (Boolean) -> Boolean
) {
    var toggled = false
        set(value) {
            if (value == field) return
            if (onToggle(value)) {
                field = value
                canvasButton.drawable = if (toggled) toggledDrawable else untoggledDrawable
            }
        }

    private val canvasButton = CanvasButton(untoggledDrawable) { toggled = !toggled }

    fun update(touchInput: TouchInput?) {
        canvasButton.update(touchInput)
    }

    fun draw(canvas: Canvas, x: Float, y: Float, width: Float, height: Float) {
        canvasButton.draw(canvas, x, y, width, height)
    }
}