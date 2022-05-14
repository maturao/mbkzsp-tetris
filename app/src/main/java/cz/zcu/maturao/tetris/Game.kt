package cz.zcu.maturao.tetris

import android.graphics.Canvas
import android.graphics.Color
import cz.zcu.maturao.tetris.drawing.StartIcon
import cz.zcu.maturao.tetris.drawing.StopIcon
import cz.zcu.maturao.tetris.ui.CanvasToggleButton

class Game(private val input: Input) {
    private val stackController = StackController()
    private val stopToggleButton =
        CanvasToggleButton(
            StartIcon(Color.WHITE),
            StopIcon(Color.WHITE)
        ) { stackController.stopped = it }

    fun update() {
        val touchInput = input.popTouchInput()

        stopToggleButton.update(touchInput)
        stackController.update(touchInput)
    }

    fun draw(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)

        val stopButtonMargin = 80f
        val stopButtonSize = 140f
        stopToggleButton.draw(
            canvas,
            stopButtonMargin,
            stopButtonMargin,
            stopButtonSize,
            stopButtonSize
        )

        val stackMarginTop = 400f
        val stackMarginBottom = 100f
        val stackMarginX = 100f

        stackController.draw(
            canvas,
            stackMarginX,
            stackMarginTop,
            canvas.width.toFloat() - stackMarginX * 2,
            canvas.height - stackMarginTop - stackMarginBottom,
        )
    }
}