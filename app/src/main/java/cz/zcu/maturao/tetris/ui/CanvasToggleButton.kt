package cz.zcu.maturao.tetris.ui

import android.graphics.Canvas
import cz.zcu.maturao.tetris.TouchInput
import cz.zcu.maturao.tetris.drawing.Drawable

class CanvasToggleButton(
    val toggledDrawable: Drawable,
    val untoggledDrawable: Drawable,
    val onToggle: (Boolean) -> Unit
) {
    var toggled = false

    private val canvasButton = CanvasButton(untoggledDrawable) {}

    init {
        canvasButton.onClick = {
            toggled = !toggled
            canvasButton.drawable = if (toggled) toggledDrawable else untoggledDrawable
            onToggle(toggled)
        }
    }

    fun update(touchInput: TouchInput?) {
        canvasButton.update(touchInput)
    }

    fun draw(canvas: Canvas, x: Float, y: Float, width: Float, height: Float) {
        canvasButton.draw(canvas,x, y, width, height)
    }
}