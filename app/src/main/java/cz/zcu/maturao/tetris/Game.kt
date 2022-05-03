package cz.zcu.maturao.tetris

import android.graphics.Canvas
import android.graphics.Color

class Game(private val input: Input) {
    private val stackController = StackController()

    fun update() {
        val touchInput = input.popTouchInput()
        stackController.update(touchInput)
    }

    fun draw(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)

        val stackOffsetY = 400f
        stackController.draw(
            canvas,
            0f,
            stackOffsetY,
            canvas.width.toFloat(),
            canvas.height - stackOffsetY,
        )
    }
}