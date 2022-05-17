package cz.zcu.maturao.tetris.game.ui

import android.graphics.Canvas
import cz.zcu.maturao.tetris.game.TouchInput

/**
 * Tlačítko, které má dva stavy
 */
class CanvasToggleButton(
    /**
     * Ikona při stavu toggled = true
     */
    val toggledDrawable: Drawable,
    /**
     * Ikona při stavu toggled = false
     */
    val untoggledDrawable: Drawable,
    /**
     * Callback při změně stavu toggled
     */
    val onToggle: (Boolean) -> Boolean
) {
    /**
     * Stav tlačítka
     */
    var toggled = false
        set(value) {
            if (value == field) return
            if (onToggle(value)) {
                field = value
                canvasButton.drawable = if (toggled) toggledDrawable else untoggledDrawable
            }
        }

    private val canvasButton = CanvasButton(untoggledDrawable) { toggled = !toggled }

    /**
     * Kontroluje, zda tlačítko nebylo stisknuté
     */
    fun update(touchInput: TouchInput?) {
        canvasButton.update(touchInput)
    }

    /**
     * Vykreslí tlačítko
     */
    fun draw(canvas: Canvas, x: Float, y: Float, width: Float, height: Float) {
        canvasButton.draw(canvas, x, y, width, height)
    }
}