package cz.zcu.maturao.tetris.logic

import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.math.sign

class Stack {
    private val shapeQueue = ShapeQueue()

    val squares = Matrix<Square>(20, 10, Square.Empty)

    var block = newRandomBlock()
        private set

    var nextFallTime: Long? = null
        private set
    var nextFallInterval: Double = 1000.0
        private set

    private fun newRandomBlock() = shapeQueue.popNextShape().let { shape ->
        Block(shape, -2, (squares.width / 2.0 - shape.squares.width / 2.0).roundToInt())
    }

    fun getSquare(row: Int, col: Int): Square = when {
        col !in 0 until squares.width -> Square.Full.Wall
        row >= squares.height -> Square.Full.Wall
        row < 0 -> Square.Empty
        else -> squares[row, col]
    }

    fun collidesWith(block: Block): Boolean {
        for ((i, j, square) in block.shape.squares.withIndices()) {
            if (square.collidesWith(
                    getSquare(
                        block.row + i,
                        block.col + j
                    )
                )
            ) return true
        }
        return false
    }

    private fun add(block: Block) {
        for ((i, j, square) in block.shape.squares.withIndices()) {
            if (square is Square.Empty || i < 0) continue
            squares[block.row + i, block.col + j] = square
        }
        checkFullLines()
    }

    fun checkFullLines() {
        val removedLines = mutableListOf<Int>()

        rowLoop@
        for (row in 0 until squares.height) {
            for (col in 0 until squares.width) {
                if (squares[row, col] is Square.Empty) {
                    continue@rowLoop
                }
            }

            removedLines.add(row)
            for (col in 0 until squares.width) {
                squares[row, col] = Square.Empty
            }
        }

        for (removedLine in removedLines) {
            for (row in removedLine downTo 1) {
                for (col in 0 until squares.width) {
                    squares[row, col] = squares[row - 1, col]
                    squares[row - 1, col] = Square.Empty
                }
            }
        }
    }

    fun rotateBlock() {
        val rotated = block.rotated()
        if (collidesWith(rotated)) return

        block = rotated
    }

    enum class BlockMoveResult { NONE, MOVED, ADDED }

    fun setBlockCol(col: Int): BlockMoveResult {
        if (col == block.col) return BlockMoveResult.NONE

        val range =
            if (col > block.col) (block.col + 1)..col
            else (block.col - 1) downTo col

        var result = BlockMoveResult.NONE

        for (subCol in range) {
            val moved = block.moved(block.row, subCol)
            if (collidesWith(moved)) return result

            block = moved
            result = BlockMoveResult.MOVED
        }

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
                return BlockMoveResult.ADDED
            } else {
                block = moved
            }
        }

        return BlockMoveResult.MOVED
    }

    fun checkFall() {
//        return
        val nextFallTime = nextFallTime

        if (nextFallTime == null) {
            resetFallTime()
        } else if (System.currentTimeMillis() > nextFallTime) {
            setBlockRow(block.row + 1)
        }
    }

    fun resetFallTime() {
        nextFallTime = System.currentTimeMillis() + nextFallInterval.roundToLong()
    }
}
