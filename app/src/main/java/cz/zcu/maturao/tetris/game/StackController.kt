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

/**
 * Ovládá a vykresluje herní pole
 */
class StackController {
    /**
     * Zda je hra zastavená
     */
    var stopped = false

    /**
     * Herní pole
     */
    var stack = Stack()

    private val stackWidth get() = stack.squares.width.toFloat()
    private val stackHeight get() = stack.squares.height.toFloat()

    // vykreslovací výška/šířka/ofset herního pole
    private var drawWidth = 1f
    private var drawHeight = 1f
    private var drawOffsetX = 0f
    private var drawOffsetY = 0f

    // počáteční pozice tahu prstem
    private var dragStartX: Float? = null
    private var dragStartY: Float? = null

    private val paint = Paint()

    /**
     * Speciální tetrisový blok, který slouží jako "stop" ikona
     */
    private val stopIconBlock = Shapes.stop.let {
        Block(
            it,
            (stackHeight / 2f - it.squares.height / 2f).roundToInt(),
            (stackWidth / 2f - it.squares.width / 2f).roundToInt()
        )
    }

    /**
     * Aktualizuje stav herního pole
     */
    fun update(touchInput: TouchInput?) {
        if (stopped) return
        if (stack.gameOver) return

        stack.checkFall()
        if (touchInput != null) handleInput(touchInput)
    }

    /**
     * Zpracuje dotykový vstup
     */
    private fun handleInput(touchInput: TouchInput) {
        // relativní pozice v herním poli
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

    /**
     * Zda je pozice uvnitř herního pole
     */
    private fun isInsideStack(stackX: Float, stackY: Float) =
        isInside(stackX, stackY, 0f, 0f, stackWidth, stackHeight)

    /**
     * Zpracuje akci TouchAction.Down
     */
    private fun handleTouchDownEvent(stackX: Float, stackY: Float) {
        dragStartX = null
        dragStartY = null

        if (!isInsideStack(stackX, stackY)) return

        dragStartX = stackX - stack.block.col
        dragStartY = stackY - stack.block.row
    }

    /**
     * Zpracuje akci TouchAction.Click
     */
    private fun handleTouchClickEvent(stackX: Float, stackY: Float) {
        if (!isInsideStack(stackX, stackY)) return

        // zjistím, zda uživatel kliknul na ghostBlock
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

    /**
     * Zpracuje akci TouchAction.MOVE
     */
    private fun handleDragEvent(stackX: Float, stackY: Float) {
        handleHorizontalDrag(stackX)
        handleVerticalDrag(stackY)
    }

    /**
     * Zpracuje horizontální tah prstem
     */
    private fun handleHorizontalDrag(stackX: Float) {
        val dragStartX = dragStartX ?: return
        val newBlockCol = (stackX - dragStartX).roundToInt()

        if (stack.setBlockCol(newBlockCol) != Stack.BlockMoveResult.NONE) {
            // zamknu vertikální tah
            this.dragStartY = null
        }
    }

    /**
     * Zpracuje vertikální tah prstem
     */
    private fun handleVerticalDrag(stackY: Float) {
        val dragStartY = dragStartY ?: return
        val newBlockRow = (stackY - dragStartY).roundToInt()

        when (stack.setBlockRow(newBlockRow)) {
            // zamknu horizontální tah
            Stack.BlockMoveResult.MOVED -> this.dragStartX = null
            Stack.BlockMoveResult.COLLISION -> {
                // blok se stane součástí hracího pole - vyresetuji tahy prstem
                this.dragStartX = null
                this.dragStartY = null
            }
            Stack.BlockMoveResult.NONE -> {}
        }
    }

    /**
     * Vykreslí herní pole na Canvas
     */
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

            // hrana pole
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

            // bloky uvnitř pole
            drawSquares(stack.squares)
            if (!stack.gameOver) {
                // náhled, kam blok spadne
                drawBlock(stack.ghostBlock, alphaTransform(128))
                // padající blok
                drawBlock(stack.block)
                // "stop" ikona
                if (stopped) drawBlock(stopIconBlock, alphaTransform(225))

                // náhled přístího bloku
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

        // "GAME OVER" text
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