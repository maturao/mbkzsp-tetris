package cz.zcu.maturao.tetris.game.ui

import android.graphics.Canvas
import cz.zcu.maturao.tetris.game.TouchAction
import cz.zcu.maturao.tetris.game.TouchInput
import cz.zcu.maturao.tetris.utils.isInside

class CanvasButton(
    var drawable: Drawable,
    var onClick: () -> Unit
) {
    var x: Float = 0f
    var y: Float = 0f
    var width: Float = 0f
    var height: Float = 0f

    fun update(touchInput: TouchInput?) {
        touchInput ?: return
        if (touchInput.action != TouchAction.Up.Click) return
        if (isInside(touchInput.x, touchInput.y, x, y, width, height)) onClick()
    }

    fun draw(canvas: Canvas, x: Float, y: Float, width: Float, height: Float) {
        this.x = x
        this.y = y
        this.width = width
        this.height = height

        drawable.draw(canvas, x, y, width, height)
    }
}
