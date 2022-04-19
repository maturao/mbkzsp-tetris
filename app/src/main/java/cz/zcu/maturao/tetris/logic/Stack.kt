package cz.zcu.maturao.tetris.logic

import kotlin.math.roundToLong

class Stack {
    val squares = Matrix<Square>(20, 10, Square.Empty)
    var block = newRandomBlock()
        private set

    var nextFallTime: Long? = null
    var nextFallInterval: Double = 1000.0

    private fun newRandomBlock() = Shape.randomShape().let { shape ->
        Block(
            shape,
            -shape.squares.height / 2,
            squares.width / 2 - shape.squares.width / 2
        )
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

    fun setBlockCol(col: Int) {
        if (col == block.col) return

        val moved = block.moved(block.row, col)
        if (collidesWith(moved)) return

        block = moved
    }

    fun setBlockRow(row: Int) {
        if (row <= block.row) return

        val moved = block.moved(row, block.col)
        block = if (collidesWith(moved)) {
            add(block)
            newRandomBlock()
        } else moved

        resetFallTime()
    }

    fun checkFall() {
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
