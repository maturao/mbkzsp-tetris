package cz.zcu.maturao.tetris.utils

import android.graphics.Canvas
import android.graphics.Paint
import cz.zcu.maturao.tetris.logic.Block
import cz.zcu.maturao.tetris.logic.Matrix
import cz.zcu.maturao.tetris.logic.Square
import cz.zcu.maturao.tetris.logic.Stack

val globalPaint = Paint()

inline fun Paint.cleared(block: Paint.() -> Unit = {}) = apply(Paint::reset).apply(block)

fun fitAspectRatio(width: Float, height: Float, aspectRatio: Float) =
    (height * aspectRatio).let { newWidth ->
        if (newWidth <= width) Pair(newWidth, height) else Pair(width, width / aspectRatio)
    }

fun isInside(x: Float, y: Float, offsetX: Float, offsetY: Float, width: Float, height: Float) =
    (offsetX <= x && x < offsetX + width) && (offsetY <= y && y < offsetY + height)

typealias ColorTransform = (Int) -> Int

fun alphaTransform(alpha: Int): ColorTransform = { it and 0x00_FFFFFF or (alpha shl (8 * 3)) }

inline fun Canvas.drawSquare(
    square: Square,
    row: Int,
    col: Int,
    colorTransform: ColorTransform = { it }
) {
    if (square is Square.Empty) return
    if (row !in 0 until Stack.HEIGHT || col !in 0 until Stack.WIDTH) return

    val squareColor = colorTransform(square.color)
    val x = col.toFloat()
    val y = row.toFloat()

    drawRect(x, y, x + 1f, y + 1f, globalPaint.cleared { color = squareColor })
}

inline fun Canvas.drawSquares(
    squares: Matrix<Square>,
    row: Int,
    col: Int,
    colorTransform: ColorTransform = { it }
) {
    for ((i, j, square) in squares.withIndices()) {
        drawSquare(square, row + i, col + j, colorTransform)
    }
}

inline fun Canvas.drawBlock(block: Block, colorTransform: ColorTransform = { it }) =
    drawSquares(block.shape.squares, block.row, block.col, colorTransform)
