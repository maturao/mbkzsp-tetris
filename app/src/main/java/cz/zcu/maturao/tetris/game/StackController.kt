package cz.zcu.maturao.tetris.game

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.graphics.withSave
import cz.zcu.maturao.tetris.logic.Block
import cz.zcu.maturao.tetris.logic.Shapes
import cz.zcu.maturao.tetris.logic.Square
import cz.zcu.maturao.tetris.logic.Stack
import cz.zcu.maturao.tetris.utils.*
import kotlin.math.roundToInt

class StackController {
    var stopped = false

    var stack = Stack()

    private val stackWidth get() = stack.squares.width.toFloat()
    private val stackHeight get() = stack.squares.height.toFloat()

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
        if (stack.gameOver) return

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
        if (!isInsideStack(stackX, stackY)) return

        var ghostBlockCollision = false
        val ghostBlock = stack.ghostBlock
        for ((i, j, square) in ghostBlock.shape.squares.withIndices()) {
            if (square is Square.Empty) continue

            val y = i + ghostBlock.row
            val x = j + ghostBlock.col

            if (isInside(stackX, stackY, x.toFloat(), y.toFloat(), 1f, 1f)) {
                ghostBlockCollision = true
                break
            }
        }

        if (ghostBlockCollision) {
            stack.setBlockRow(stack.squares.height)
        } else {
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

        if (stack.setBlockCol(newBlockCol) != Stack.BlockMoveResult.NONE) {
            this.dragStartY = null
        }
    }

    private fun handleVerticalDrag(stackY: Float) {
        val dragStartY = dragStartY ?: return
        val newBlockRow = (stackY - dragStartY).roundToInt()

        when (stack.setBlockRow(newBlockRow)) {
            Stack.BlockMoveResult.MOVED -> this.dragStartX = null
            Stack.BlockMoveResult.COLLISION -> {
                this.dragStartX = null
                this.dragStartY = null
            }
            Stack.BlockMoveResult.NONE -> {}
        }
    }

    fun draw(canvas: Canvas, x: Float, y: Float, width: Float, height: Float) {
        val (drawWidth, drawHeight) = fitAspectRatio(width, height, stackWidth / stackHeight)
        this.drawWidth = drawWidth
        this.drawHeight = drawHeight

        drawOffsetX = x + width / 2 - drawWidth / 2
        drawOffsetY = y + height / 2 - drawHeight / 2

        val stackSquareSize = drawHeight / stackHeight

        canvas.withSave {
            translate(drawOffsetX, drawOffsetY)
            scale(stackSquareSize, stackSquareSize)

            paint.cleared {
                style = Paint.Style.STROKE
                strokeWidth = 0.1f
                color = Color.WHITE
            }

            val round = 0.3f
            val padding = round / 2f
            drawRoundRect(
                -padding,
                -padding,
                stackWidth + padding,
                stackHeight + padding,
                round,
                round,
                paint
            )

            drawSquares(stack.squares)
            if (!stack.gameOver) {
                drawBlock(stack.ghostBlock, alphaTransform(128))
                drawBlock(stack.block)
                if (stopped) drawBlock(stopIconBlock, alphaTransform(225))

                val nextShape = stack.shapeQueue.nextShape
                val nextShapePreviewOffset = 0.5f
                drawSquares(
                    nextShape.squares,
                    -nextShape.maxRow.toFloat() - 1 - nextShapePreviewOffset,
                    stack.squares.width / 2f - nextShape.centerCol,
                    outOfBounds = true
                )
            }
        }

        if (stack.gameOver) {
            val gameOverText = "GAME OVER"
            val desiredTextWidth = drawWidth * 0.90f

            paint.cleared {
                color = Color.WHITE
                textSize = 100f
                isFakeBoldText = true
            }
            paint.textSize *= desiredTextWidth / paint.measureText(gameOverText)

            canvas.drawAnchoredText(
                gameOverText,
                drawOffsetX + drawWidth / 2,
                drawOffsetY + drawHeight / 2,
                paint
            )
        }
    }
}