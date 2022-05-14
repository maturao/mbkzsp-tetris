package cz.zcu.maturao.tetris

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.graphics.withSave
import cz.zcu.maturao.tetris.logic.Block
import cz.zcu.maturao.tetris.logic.Shapes
import cz.zcu.maturao.tetris.logic.Stack
import cz.zcu.maturao.tetris.utils.*
import kotlin.math.roundToInt

class StackController {
    var stopped = false

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
    private val stopIconBlock = Shapes.stop.let {
        Block(
            it,
            (stackHeight / 2f - it.squares.height / 2f).roundToInt(),
            (stackWidth / 2f - it.squares.width / 2f).roundToInt()
        )
    }

    fun update(touchInput: TouchInput?) {
        if (stopped) return

        stack.checkFall()
        if (touchInput != null) handleInput(touchInput)
    }

    private fun handleInput(touchInput: TouchInput) {
        val stackX = (touchInput.x - drawOffsetX) / drawWidth * stackWidth
        val stackY = (touchInput.y - drawOffsetY) / drawHeight * stackHeight


        when (touchInput.action) {
            TouchAction.Down -> handleTouchDownEvent(stackX, stackY)
            TouchAction.Move -> handleDragEvent(stackX, stackY)
            is TouchAction.Up -> {
                dragStartX = null
                dragStartY = null

                if (touchInput.action == TouchAction.Up.Click) {
                    handleTouchClickEvent(stackX, stackY)
                }
            }
        }
    }

    private fun isInsideStack(stackX: Float, stackY: Float) =
        isInside(stackX, stackY, 0f, 0f, stackWidth, stackHeight)

    private fun handleTouchDownEvent(stackX: Float, stackY: Float) {
        dragStartX = null
        dragStartY = null

        if (!isInsideStack(stackX, stackY)) return

        dragStartX = stackX - stack.block.col
        dragStartY = stackY - stack.block.row
    }

    private fun handleTouchClickEvent(stackX: Float, stackY: Float) {
        if (isInsideStack(stackX, stackY)) {
            stack.rotateBlock()
        }
    }

    private fun handleDragEvent(stackX: Float, stackY: Float) {
        handleHorizontalDrag(stackX)
        handleVerticalDrag(stackY)
    }

    private fun handleHorizontalDrag(stackX: Float) {
        val dragStartX = dragStartX ?: return
        val newBlockCol = (stackX - dragStartX).roundToInt()

        if (stack.setBlockCol(newBlockCol) == Stack.BlockMoveResult.MOVED) {
            this.dragStartY = null
        }
    }

    private fun handleVerticalDrag(stackY: Float) {
        val dragStartY = dragStartY ?: return
        val newBlockRow = (stackY - dragStartY).roundToInt()

        when (stack.setBlockRow(newBlockRow)) {
            Stack.BlockMoveResult.MOVED -> this.dragStartX = null
            Stack.BlockMoveResult.ADDED -> {
                this.dragStartX = null
                this.dragStartY = null
            }
            Stack.BlockMoveResult.NONE -> {}
        }
    }

    fun draw(canvas: Canvas, offsetX: Float, offsetY: Float, width: Float, height: Float) {
        val (drawWidth, drawHeight) = fitAspectRatio(width, height, stackWidth / stackHeight)
        this.drawWidth = drawWidth
        this.drawHeight = drawHeight

        drawOffsetX = offsetX + width / 2 - drawWidth / 2
        drawOffsetY = offsetY + height / 2 - drawHeight / 2

        val stackSquareSize = drawHeight / stackHeight

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

            drawSquares(stack.squares, 0, 0)
            drawBlock(stack.ghostBlock, alphaTransform(128))
            drawBlock(stack.block)
            if (stopped) drawBlock(stopIconBlock, alphaTransform(225))
        }
    }
}