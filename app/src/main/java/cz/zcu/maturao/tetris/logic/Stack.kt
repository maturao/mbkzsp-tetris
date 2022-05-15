package cz.zcu.maturao.tetris.logic

import java.io.Serializable
import kotlin.math.roundToInt
import kotlin.math.roundToLong


class Stack : Serializable {
    companion object {
        const val WIDTH = 10
        const val HEIGHT = 20
    }
    private val shapeQueue = ShapeQueue()

    val squares = Matrix<Square>(HEIGHT, WIDTH, Square.Empty)

    var gameOver = false
        private set

    var block = newRandomBlock()
        private set

    private var _ghostBlock: Block? = null
    val ghostBlock: Block get() = _ghostBlock ?: createGhostBlock().also { _ghostBlock = it }

    private var nextFallTime: Long? = null
    private var nextFallInterval: Double = 1000.0

    private fun newRandomBlock() = shapeQueue.getShape().let { shape ->
        Block(shape, -2, (squares.width / 2.0 - shape.squares.width / 2.0).roundToInt())
    }

    private fun getSquare(row: Int, col: Int): Square = when {
        col !in 0 until squares.width -> Square.Full.Wall
        row >= squares.height -> Square.Full.Wall
        row < 0 -> Square.Empty
        else -> squares[row, col]
    }

    private fun collidesWith(block: Block) =
        block.shape.squares.withIndices().any { (i, j, shapeSquare) ->
            shapeSquare.collidesWith(getSquare(block.row + i, block.col + j))
        }

    private fun add(block: Block) {
        for ((i, j, square) in block.shape.squares.withIndices()) {
            if (square is Square.Empty) continue

            val row = block.row + i
            val col = block.col + j

            if (row in 0 until squares.height && col in 0 until squares.width) {
                squares[row, col] = square
            } else {
                gameOver = true
            }
        }
        clearFullRows()
    }

    private fun moveRowDown(row: Int, numRows: Int) {
        for (col in 0 until squares.width) {
            squares[row + numRows, col] = squares[row, col]
            squares[row, col] = Square.Empty
        }
    }

    private fun isRowFull(row: Int) = (0 until squares.width)
        .asSequence()
        .map { col -> squares[row, col] }
        .all { it is Square.Full }

    private fun clearRow(row: Int) = (0 until squares.width)
        .forEach { col -> squares[row, col] = Square.Empty }

    private fun clearFullRows(): Int {
        var cleared = 0
        for (row in squares.height - 1 downTo 0) {
            if (isRowFull(row)) {
                clearRow(row)
                cleared++
            } else if (cleared > 0) {
                moveRowDown(row, cleared)
            }
        }
        return cleared
    }

    private fun resetFallTime() {
        nextFallTime = System.currentTimeMillis() + nextFallInterval.roundToLong()
    }

    fun rotateBlock() {
        val rotated = block.rotated()
        if (collidesWith(rotated)) return

        block = rotated
        resetGhostBlock()
    }

    enum class BlockMoveResult { NONE, COLLISION, MOVED }

    fun setBlockCol(col: Int): BlockMoveResult {
        if (col == block.col) return BlockMoveResult.NONE

        val range =
            if (col > block.col) (block.col + 1)..col
            else (block.col - 1) downTo col

        var result = BlockMoveResult.MOVED
        var isMoved = false

        for (subCol in range) {
            val moved = block.moved(block.row, subCol)

            if (collidesWith(moved)) {
                result = BlockMoveResult.COLLISION
                break
            }

            block = moved
            isMoved = true
        }

        if (isMoved) resetGhostBlock()

        return result
    }

    fun setBlockRow(row: Int): BlockMoveResult {
        if (row <= block.row) return BlockMoveResult.NONE
        resetFallTime()

        for (subRow in (block.row + 1)..row) {
            val moved = block.moved(subRow, block.col)

            if (collidesWith(moved)) {
                add(block)
                block = newRandomBlock()
                resetGhostBlock()
                return BlockMoveResult.COLLISION
            } else {
                block = moved
            }
        }

        return BlockMoveResult.MOVED
    }

    fun checkFall() {
        val nextFallTime = nextFallTime

        if (nextFallTime == null) {
            resetFallTime()
        } else if (System.currentTimeMillis() > nextFallTime) {
            setBlockRow(block.row + 1)
        }
    }

    private fun resetGhostBlock() {
        _ghostBlock = null
    }

    private fun createGhostBlock(): Block {
        var ghostBlock = block

        while (true) {
            val moved = ghostBlock.moved(ghostBlock.row + 1, ghostBlock.col)
            if (collidesWith(moved)) break
            ghostBlock = moved
        }

        return ghostBlock
    }
}
