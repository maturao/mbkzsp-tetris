package cz.zcu.maturao.tetris.logic

import java.io.Serializable
import kotlin.math.min
import kotlin.math.max

class Shape(val squares: Matrix<Square>) : Serializable {
    fun rotated() = Shape(squares.rotated())

    val centerRow: Float
    val centerCol: Float

    val minRow: Int
    val minCol: Int
    val maxRow: Int
    val maxCol: Int

    init {
        var minRow = squares.height - 1
        var minCol = squares.width - 1
        var maxRow = 0
        var maxCol = 0

        for ((row, col, square) in squares.withIndices()) {
            if (square is Square.Empty) continue

            minRow = min(minRow, row)
            minCol = min(minCol, col)

            maxRow = max(maxRow, row)
            maxCol = max(maxCol, col)
        }

        centerRow = (minRow + maxRow + 1).toFloat() / 2f
        centerCol = (minCol + maxCol + 1).toFloat() / 2f
        this.minRow = minRow
        this.minCol = minCol
        this.maxRow = maxRow
        this.maxCol = maxCol
    }

    companion object {
        private const val SHAPE_FULL_CHAR = '#'

        fun fromString(fullSquare: Square.Full, string: String): Shape {
            val lines = string.lines()

            val shapeHeight = lines.size
            val shapeWidth = lines.firstOrNull()?.length ?: 0

            if (lines.any { it.length != shapeWidth })
                throw IllegalArgumentException("All lines must have the same length")

            val matrix = Matrix<Square>(shapeHeight, shapeWidth, Square.Empty)

            for ((i, j) in matrix.indices) {
                if (lines[i][j] == SHAPE_FULL_CHAR)
                    matrix[i, j] = fullSquare
            }

            return Shape(matrix)
        }
    }
}