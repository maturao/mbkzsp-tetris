package cz.zcu.maturao.tetris.ui

import android.graphics.Canvas
import cz.zcu.maturao.tetris.TouchInput
import cz.zcu.maturao.tetris.drawing.Drawable
import cz.zcu.maturao.tetris.utils.debugLog
import kotlin.properties.Delegates

class CanvasToggleButton(
    val toggledDrawable: Drawable,
    val untoggledDrawable: Drawable,
    val onToggle: (Boolean) -> Unit
) {
    var toggled: Boolean by Delegates.observable(false) { _, oldValue, newValue ->
        if (oldValue == newValue) return@observable

        canvasButton.drawable = if (toggled) toggledDrawable else untoggledDrawable
        onToggle(toggled)
    }

    private val canvasButton = CanvasButton(untoggledDrawable) { toggled = !toggled }

    fun update(touchInput: TouchInput?) {
        canvasButton.update(touchInput)
    }

    fun draw(canvas: Canvas, x: Float, y: Float, width: Float, height: Float) {
        canvasButton.draw(canvas, x, y, width, height)
    }
}