package cz.zcu.maturao.tetris.game

import android.app.Activity
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import cz.zcu.maturao.tetris.android.GameView
import cz.zcu.maturao.tetris.game.ui.HomeIcon
import cz.zcu.maturao.tetris.game.ui.ResumeIcon
import cz.zcu.maturao.tetris.game.ui.StopIcon
import cz.zcu.maturao.tetris.game.ui.CanvasButton
import cz.zcu.maturao.tetris.game.ui.CanvasToggleButton
import cz.zcu.maturao.tetris.utils.cleared
import cz.zcu.maturao.tetris.utils.drawAnchoredText
import kotlin.math.sqrt

/**
 * Řídí a vykresluje hru
 */
class Game(private val gameView: GameView, private val input: Input) {
    private val foregroundColor = Color.WHITE
    private val paint = Paint()
    private var scoreCache = 0

    val stackController = StackController()

    /**
     * Tlačítko pro zastavení/spuštení hry
     */
    private val stopToggleButton =
        CanvasToggleButton(
            ResumeIcon(foregroundColor),
            StopIcon(foregroundColor)
        ) {
            if (stackController.stack.gameOver) false
            else {
                stackController.stopped = it
                true
            }
        }

    /**
     * Zda je hra zastavená
     */
    var stopped: Boolean
        get() = stopToggleButton.toggled
        set(value) {
            if (!stackController.stack.gameOver) {
                stopToggleButton.toggled = value
            }
        }

    /**
     * Tlačítko pro návrat do menu
     */
    private val homeButton = CanvasButton(HomeIcon(foregroundColor)) {
        gameView.gameActivity.exit()
    }

    /**
     * Aktualizuje stav hry
     */
    fun update() {
        val touchInput = input.popTouchInput()

        stopToggleButton.update(touchInput)
        homeButton.update(touchInput)
        stackController.update(touchInput)
        updateHighScore()
    }

    /**
     * Aktualizuje nevyšší skóre
     */
    private fun updateHighScore() {
        val score = stackController.stack.score.score
        if (scoreCache != score) {
            scoreCache = score
            val sharedPreferences = gameView.context.getSharedPreferences(
                "cz.zcu.maturao.tetris",
                Activity.MODE_PRIVATE
            )
            val highScore = sharedPreferences.getInt("highScore", 0)
            if (score > highScore) {
                val editor = sharedPreferences.edit()
                editor.putInt("highScore", score)
                editor.apply()
            }
        }
    }

    /**
     * Vykreslí herní pole
     */
    fun draw(canvas: Canvas) {
        // pozadí
        canvas.drawColor(Color.BLACK)

        val size = sqrt(canvas.width * canvas.height.toFloat())

        // velikost mezery
        val margin = size * 0.03f
        // velikost tlačítek a textu
        val elementSize = size * 0.07f

        //vykreslím tlačítka
        stopToggleButton.draw(
            canvas,
            margin,
            margin,
            elementSize,
            elementSize
        )
        homeButton.draw(
            canvas,
            canvas.width - elementSize - margin,
            margin,
            elementSize,
            elementSize
        )

        // vykreslím skóre
        val score = gameView.game.stackController.stack.score
        paint.cleared {
            color = foregroundColor
            textSize = elementSize * 0.6f
            isFakeBoldText = true
        }
        canvas.drawAnchoredText(
            "SCORE ${score.score}",
            canvas.width / 2f,
            margin + elementSize / 2,
            paint
        )

        // aktuální level
        canvas.drawAnchoredText(
            "LEVEL ${score.level}",
            margin,
            margin + elementSize + margin + elementSize / 2,
            paint,
            0f,
            0.5f
        )

        // počet vyčistěných řádek
        canvas.drawAnchoredText(
            "LINE ${score.lines}",
            canvas.width - margin,
            margin + elementSize + margin + elementSize / 2,
            paint,
            1f,
            0.5f
        )

        // mezera mezi tlačítky a hracím polem
        val middleMargin = margin * 4
        val stackMarginTop = margin + elementSize + margin + elementSize + middleMargin

        //vykreslím hrací pole
        stackController.draw(
            canvas,
            margin,
            stackMarginTop,
            canvas.width.toFloat() - margin * 2,
            canvas.height - stackMarginTop - margin,
        )
    }
}