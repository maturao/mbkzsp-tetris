package cz.zcu.maturao.tetris

import android.graphics.Canvas
import android.graphics.Color
import cz.zcu.maturao.tetris.drawing.HomeIcon
import cz.zcu.maturao.tetris.drawing.ResumeIcon
import cz.zcu.maturao.tetris.drawing.StopIcon
import cz.zcu.maturao.tetris.ui.CanvasButton
import cz.zcu.maturao.tetris.ui.CanvasToggleButton

class Game(private val gameView: GameView, private val input: Input) {
    private val buttonColor = Color.WHITE

    val stackController = StackController()
    private val stopToggleButton =
        CanvasToggleButton(
            ResumeIcon(buttonColor),
            StopIcon(buttonColor)
        ) {
            if (!stackController.stack.gameOver) {
                stackController.stopped = it
                true
            } else false
        }

    var stopped: Boolean
        get() = stopToggleButton.toggled
        set(value) {
            if (!stackController.stack.gameOver) {
                stopToggleButton.toggled = value
            }
        }

    private val homeButton = CanvasButton(HomeIcon(buttonColor)) {
        gameView.gameActivity.exit()
    }

    fun update() {
        val touchInput = input.popTouchInput()

        stopToggleButton.update(touchInput)
        homeButton.update(touchInput)
        stackController.update(touchInput)
    }

    fun draw(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)

        val buttonMargin = 80f
        val buttonSize = 140f
        stopToggleButton.draw(
            canvas,
            buttonMargin,
            buttonMargin,
            buttonSize,
            buttonSize
        )
        homeButton.draw(
            canvas,
            canvas.width - buttonSize - buttonMargin,
            buttonMargin,
            buttonSize,
            buttonSize
        )


        val stackMarginTop = 400f
        val stackMargin = 100f

        stackController.draw(
            canvas,
            stackMargin,
            stackMarginTop,
            canvas.width.toFloat() - stackMargin * 2,
            canvas.height - stackMarginTop - stackMargin,
        )
    }
}