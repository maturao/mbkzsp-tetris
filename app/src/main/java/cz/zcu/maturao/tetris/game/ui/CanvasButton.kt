package cz.zcu.maturao.tetris.game.ui

import android.graphics.Canvas
import cz.zcu.maturao.tetris.game.TouchAction
import cz.zcu.maturao.tetris.game.TouchInput
import cz.zcu.maturao.tetris.utils.isInside

/**
 * Tlačítko vykreslené na Canvas
 */
class CanvasButton(
    /**
     * Ikona tlačítka
     */
    var drawable: Drawable,

    /**
     * Callback při stisknutí tlačítka
     */
    var onClick: () -> Unit
) {
    var x: Float = 0f
    var y: Float = 0f
    var width: Float = 0f
    var height: Float = 0f

    /**
     * Kontroluje, zda tlačítko nebylo stisknuté
     */
    fun update(touchInput: TouchInput?) {
        touchInput ?: return
        if (touchInput.action != TouchAction.Up.Click) return
        if (isInside(touchInput.x, touchInput.y, x, y, width, height)) onClick()
    }

    /**
     * Vykreslí tlačítko
     */
    fun draw(canvas: Canvas, x: Float, y: Float, width: Float, height: Float) {
        this.x = x
        this.y = y
        this.width = width
        this.height = height

        drawable.draw(canvas, x, y, width, height)
    }
}
