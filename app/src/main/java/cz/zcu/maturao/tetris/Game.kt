package cz.zcu.maturao.tetris

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import cz.zcu.maturao.tetris.logic.Stack

class Game {
    val stack = Stack()
    private val stackWidth = stack.squares.width.toFloat()
    private val stackHeight = stack.squares.height.toFloat()

    private val paint = Paint()

    fun update() {
        stack.checkFall()
    }

    fun draw(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)

        val stackDrawHeight = canvas.height * 0.8f
        val stackSquareSize = stackDrawHeight / stackHeight
        val stackDrawWidth = stackSquareSize * stackWidth

        canvas.save()
        canvas.translate(
            canvas.width / 2f - stackDrawWidth / 2f,
            canvas.height / 2f - stackDrawHeight / 2f
        )
        canvas.scale(stackSquareSize, stackSquareSize)

        paint.apply {
            reset()
            style = Paint.Style.STROKE
            strokeWidth = 0.075f
            color = Color.WHITE
        }

        for (row in 1 until stackHeight.toInt()) {
            val y = row.toFloat()
            canvas.drawLine(0f, y, stackWidth, y, paint)
        }
        for (col in 1 until stackWidth.toInt()) {
            val x = col.toFloat()
            canvas.drawLine(x, 0f, x, stackHeight, paint)
        }
        paint.strokeWidth *= 2
        canvas.drawRect(0f, 0f, stackWidth, stackHeight, paint)

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