package cz.zcu.maturao.tetris.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import cz.zcu.maturao.tetris.logic.Block
import cz.zcu.maturao.tetris.logic.Matrix
import cz.zcu.maturao.tetris.logic.Square
import cz.zcu.maturao.tetris.logic.Stack

val globalPaint = Paint()
val globalRect = Rect()

/**
 * Vyčistí Paint a aplikuje na něj předanou funkci
 */
inline fun Paint.cleared(block: Paint.() -> Unit = {}) = apply(Paint::reset).apply(block)

/**
 * Vrátí výšku a šířku obdélníka s daným poměrem stran tak, aby se vešel do zadaného obdelníka
 */
fun fitAspectRatio(width: Float, height: Float, aspectRatio: Float) =
    (height * aspectRatio).let { newWidth ->
        if (newWidth <= width) Pair(newWidth, height) else Pair(width, width / aspectRatio)
    }

/**
 * Zda se bod nachází uvnitř obdélníka
 */
fun isInside(x: Float, y: Float, offsetX: Float, offsetY: Float, width: Float, height: Float) =
    (offsetX <= x && x < offsetX + width) && (offsetY <= y && y < offsetY + height)

/**
 * Vykreslí text, který je relativně ukotvený
 */
fun Canvas.drawAnchoredText(
    text: String,
    x: Float,
    y: Float,
    paint: Paint,
    anchorX: Float = 0.5f,
    anchorY: Float = 0.5f,
) {
    val bounds = globalRect
    paint.getTextBounds(text, 0, text.length, bounds)
    paint.textAlign = Paint.Align.LEFT
    drawText(
        text,
        x - bounds.width() * anchorX - bounds.left,
        y + bounds.height() * (1f - anchorY) - bounds.bottom,
        paint
    )
}

/**
 * Typ funkce pro transformaci barvy
 */
typealias ColorTransform = (Int) -> Int

/**
 * Změní průhlednost barvy dle zadané hodnoty
 */
fun alphaTransform(alpha: Int): ColorTransform = { it and 0x00_FFFFFF or (alpha shl (8 * 3)) }

/**
 * Vykreslí tetrisový čtverec na Canvas
 */
inline fun Canvas.drawSquare(
    square: Square,
    row: Float,
    col: Float,
    colorTransform: ColorTransform = { it },
    outOfBounds: Boolean = false
) {
    if (square is Square.Empty) return
    if (!outOfBounds && !isInside(
            col,
            row,
            0f,
            0f,
            Stack.WIDTH.toFloat(),
            Stack.HEIGHT.toFloat()
        )
    ) return

    val squareColor = colorTransform(square.color)

    val margin = 0.05f

    drawRoundRect(
        col + margin,
        row + margin,
        col + 1f - margin,
        row + 1f - margin,
        0.1f, 0.1f,
        globalPaint.cleared { color = squareColor })
}

/**
 * Vykreslí matici tetrisových čtverců na Canvas
 */
inline fun Canvas.drawSquares(
    squares: Matrix<Square>,
    row: Float = 0f,
    col: Float = 0f,
    colorTransform: ColorTransform = { it },
    outOfBounds: Boolean = false
) {
    for ((i, j, square) in squares.withIndices()) {
        drawSquare(square, row + i, col + j, colorTransform, outOfBounds)
    }
}

/**
 * Vykreslí tetrisový blok na Canvas
 */
inline fun Canvas.drawBlock(block: Block, colorTransform: ColorTransform = { it }) =
    drawSquares(block.shape.squares, block.row.toFloat(), block.col.toFloat(), colorTransform)
