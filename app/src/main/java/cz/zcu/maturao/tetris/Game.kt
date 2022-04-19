package cz.zcu.maturao.tetris

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import cz.zcu.maturao.tetris.logic.Stack

class Game {
    val stack = Stack()
    private val paint = Paint()

    init {
        stack.setBlockRow(15)
    }

    fun update() {
        stack.checkFall()
    }

    fun draw(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)

        val stackDrawHeight = canvas.height * 0.9f
        val stackSquareSize = stackDrawHeight / stack.squares.height
        val stackDrawWidth = stackSquareSize * stack.squares.width

        canvas.save()
        canvas.translate(
            canvas.width / 2f - stackDrawWidth / 2f,
            canvas.height / 2f - stackDrawHeight / 2f
        )
        canvas.scale(stackSquareSize, stackSquareSize)

        for ((i, j) in stack.squares.indices) {
            val y = i.toFloat()
            val x = j.toFloat()

            paint.apply {
                reset()
                style = Paint.Style.STROKE
                strokeWidth = 0.1f
                color = Color.LTGRAY
            }

            canvas.drawRect(x, y, x + 1, y + 1, paint)
        }

        for ((i, j, square) in stack.squares.withIndices()) {
            val y = i.toFloat()
            val x = j.toFloat()

            paint.apply {
                reset()
                color = square.color
            }

            canvas.drawRect(x, y, x + 1, y + 1, paint)
        }


        val block = stack.block
        for ((i, j, square) in block.shape.squares.withIndices()) {
            val y = (i + block.row).toFloat()
            val x = (j + block.col).toFloat()

            if (y < 0) continue

            paint.apply {
                reset()
                color = square.color
            }

            canvas.drawRect(x, y, x + 1, y + 1, paint)
        }

        canvas.restore()
    }
}