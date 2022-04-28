package cz.zcu.maturao.tetris

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.graphics.withSave
import cz.zcu.maturao.tetris.logic.Square
import cz.zcu.maturao.tetris.logic.Stack
import cz.zcu.maturao.tetris.utils.cleared
import kotlin.math.roundToInt

class StackController {
    private val stack = Stack()
    private val stackWidth = stack.squares.width.toFloat()
    private val stackHeight = stack.squares.height.toFloat()

    private var drawWidth = 1f
    private var drawHeight = 1f
    private var drawOffsetX = 0f
    private var drawOffsetY = 0f

    private var dragStartX: Float? = null
    private var dragStartY: Float? = null

    private val paint = Paint()

    fun update(touchInput: TouchInput?) {
        stack.checkFall()
        if (touchInput != null) {
            handleInput(touchInput)
        }
    }

    private fun handleInput(touchInput: TouchInput) {
        val stackX = (touchInput.x - drawOffsetX) / drawWidth * stackWidth
        val stackY = (touchInput.y - drawOffsetY) / drawHeight * stackHeight

        when (touchInput.action) {
            TouchAction.Down -> handleTouchDownEvent(stackX, stackY)
            TouchAction.Move -> handleDragEvent(stackX, stackY)
            TouchAction.Up.Click -> handleTouchTapEvent(stackY, stackX)
            TouchAction.Up.Lift -> {}
        }
    }

    private fun isInsideStack(stackX: Float, stackY: Float) =
        stackX in 0f..stackWidth && stackY in 0f..stackHeight

    private fun handleTouchDownEvent(stackX: Float, stackY: Float) {
        dragStartX = null
        dragStartY = null

        if (!isInsideStack(stackX, stackY)) return

        dragStartX = stackX - stack.block.col
        dragStartY = stackY - stack.block.row
    }

    private fun handleTouchTapEvent(stackY: Float, stackX: Float) {
        if (isInsideStack(stackX, stackY)) {
            stack.rotateBlock()

        }
    }

    private fun handleDragEvent(stackX: Float, stackY: Float) {
        handleHorizontalDrag(stackX)
        handleVerticalDrag(stackY)
    }

    private fun handleHorizontalDrag(stackX: Float) {
        val dragOffsetX = dragStartX ?: return
        val newBlockCol = (stackX - dragOffsetX).roundToInt()

        if (stack.setBlockCol(newBlockCol) == Stack.BlockMoveResult.MOVED) {
            this.dragStartY = null
        }
    }

    private fun handleVerticalDrag(stackY: Float) {
        val dragOffsetY = dragStartY ?: return
        val newBlockRow = (stackY - dragOffsetY).roundToInt()

        when (stack.setBlockRow(newBlockRow)) {
            Stack.BlockMoveResult.MOVED -> this.dragStartX = null
            Stack.BlockMoveResult.ADDED -> {
                this.dragStartX = null
                this.dragStartY = null
            }
            Stack.BlockMoveResult.NONE -> {}
        }
    }

    fun draw(canvas: Canvas) {
        drawHeight = canvas.height * 0.8f
        val stackSquareSize = drawHeight / stackHeight
        drawWidth = stackSquareSize * stackWidth

        drawOffsetX = canvas.width / 2f - drawWidth / 2f
        drawOffsetY = canvas.height / 2f - drawHeight / 2f

        canvas.withSave {
            translate(drawOffsetX, drawOffsetY)
            scale(stackSquareSize, stackSquareSize)

            paint.cleared {
                style = Paint.Style.STROKE
                strokeWidth = 0.05f
                color = Color.WHITE
            }

            for (row in 1 until stackHeight.toInt()) {
                val y = row.toFloat()
                drawLine(0f, y, stackWidth, y, paint)
            }
            for (col in 1 until stackWidth.toInt()) {
                val x = col.toFloat()
                drawLine(x, 0f, x, stackHeight, paint)
            }
            paint.strokeWidth *= 2
            drawRect(0f, 0f, stackWidth, stackHeight, paint)

            for ((i, j, square) in stack.squares.withIndices()) {
                val y = i.toFloat()
                val x = j.toFloat()

                paint.cleared().color = square.color
                drawRect(x, y, x + 1, y + 1, paint)
            }

            val block = stack.block
            for ((i, j, square) in block.shape.squares.withIndices()) {
                val y = (i + block.row).toFloat()
                val x = (j + block.col).toFloat()

                if (y < 0) continue

                paint.cleared().color = square.color
                drawRect(x, y, x + 1, y + 1, paint)
            }
        }
    }
}