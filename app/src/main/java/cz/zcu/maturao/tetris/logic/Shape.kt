package cz.zcu.maturao.tetris.logic

import kotlin.random.Random

class Shape(val squares: Matrix<Square>) {
    fun rotated() = Shape(squares.rotated())

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