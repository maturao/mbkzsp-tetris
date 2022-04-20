package cz.zcu.maturao.tetris

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import cz.zcu.maturao.tetris.logic.Square
import cz.zcu.maturao.tetris.logic.Stack
import kotlin.math.roundToInt

class Game(private val input: Input) {
    private val stack = Stack()

    private val stackWidth = stack.squares.width.toFloat()
    private val stackHeight = stack.squares.height.toFloat()

    private var stackDrawWidth = 1f
    private var stackDrawHeight = 1f
    private var stackDrawOffsetX = 0f
    private var stackDrawOffsetY = 0f

    private var dragOffsetX: Float? = null
    private var dragOffsetY: Float? = null

    private val paint = Paint()

    private var clickTimeout: Long = 0
    private val clickTimeoutLength: Long = 300

//    init {
//        for (i in 10..19) {
//            stack.squares[i, 4] = Square.Full.Purple
//            stack.squares[i, 5] = Square.Full.Purple
//        }
//        stack.setBlockCol(0)
//        stack.setBlockRow(13)
//    }

    fun update() {
        stack.checkFall()
        handleInput()
    }

    private fun handleInput() {
        val touchInput = input.popTouchInput() ?: return

        val stackX = (touchInput.x - stackDrawOffsetX) / stackDrawWidth * stackWidth
        val stackY = (touchInput.y - stackDrawOffsetY) / stackDrawHeight * stackHeight

        when (touchInput.action) {
            MotionEvent.ACTION_DOWN -> {
                handleTouchDownEvent(stackX, stackY)
            }
            MotionEvent.ACTION_MOVE -> {
                handleDragEvent(stackX, stackY)
            }
            MotionEvent.ACTION_UP -> {
                handleTouchUpEvent(stackY, stackX)
            }
        }
    }

    private fun handleTouchDownEvent(stackX: Float, stackY: Float) {
        clickTimeout = System.currentTimeMillis() + clickTimeoutLength
        dragOffsetX = stackX - stack.block.col
        dragOffsetY = stackY - stack.block.row
    }

    private fun handleTouchUpEvent(stackY: Float, stackX: Float) {
        if (System.currentTimeMillis() > clickTimeout) return
        // click
        var blockTouch = false
        for ((i, j, square) in stack.block.shape.squares.withIndices()) {
            val row = i + stack.block.row
            val col = j + stack.block.col

            if (square !is Square.Empty
                && stackY in row.toFloat()..(row + 1f)
                && stackX in col.toFloat()..(col + 1f)
            ) {
                blockTouch = true
                break
            }
        }
        if (blockTouch) {
            stack.rotateBlock()
        }
    }

    private fun handleDragEvent(stackX: Float, stackY: Float) {
        handleHorizontalDrag(stackX)
        handleVerticalDrag(stackY)
    }

    private fun handleVerticalDrag(stackY: Float) {
        val dragOffsetY = dragOffsetY ?: return
        val newBlockRow = (stackY - dragOffsetY).roundToInt()

        when (stack.setBlockRow(newBlockRow)) {
            Stack.BlockMoveResult.MOVED -> this.dragOffsetX = null
            Stack.BlockMoveResult.ADDED -> this.dragOffsetY = null
            Stack.BlockMoveResult.NONE -> {}
        }
    }

    private fun handleHorizontalDrag(stackX: Float) {
        val dragOffsetX = dragOffsetX ?: return
        val newBlockCol = (stackX - dragOffsetX).roundToInt()

        if (stack.setBlockCol(newBlockCol) == Stack.BlockMoveResult.MOVED) {
            this.dragOffsetY = null
        }
    }

    fun draw(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)

        stackDrawHeight = canvas.height * 0.8f
        val stackSquareSize = stackDrawHeight / stackHeight
        stackDrawWidth = stackSquareSize * stackWidth

        stackDrawOffsetX = canvas.width / 2f - stackDrawWidth / 2f
        stackDrawOffsetY = canvas.height / 2f - stackDrawHeight / 2f

        canvas.save()
        canvas.translate(stackDrawOffsetX, stackDrawOffsetY)
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