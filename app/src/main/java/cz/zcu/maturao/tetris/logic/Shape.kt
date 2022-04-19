package cz.zcu.maturao.tetris.logic

import kotlin.random.Random

class Shape private constructor(val squares: Matrix<Square>) {
    fun rotated() = Shape(squares.rotated())

    companion object {
        const val SHAPE_SIZE = 4
        const val SHAPE_FULL_CHAR = '#'

        private fun fromString(fullSquare: Square.Full, string: String): Shape {
            val lines = string.lines()

            if (lines.size != SHAPE_SIZE) throw IllegalArgumentException("Shape string must have $SHAPE_SIZE lines")
            if (lines.any { it.length != SHAPE_SIZE }) throw IllegalArgumentException("Each line in shape string must contain $SHAPE_SIZE characters")

            val matrix = Matrix<Square>(SHAPE_SIZE, SHAPE_SIZE, Square.Empty)

            for ((i, j) in matrix.indices) {
                if (lines[i][j] == SHAPE_FULL_CHAR) matrix[i, j] = fullSquare
            }

            return Shape(matrix)
        }

        fun randomShape() = shapes[Random.nextInt(shapes.size)]

        val shapes = listOf(
            fromString(
                Square.Full.Cyan, """
                ,,,,
                ,,,,
                ####
                ,,,,
            """.trimIndent()
            ),
            fromString(
                Square.Full.Blue, """
                ,,,,
                #,,,
                ###,
                ,,,,
            """.trimIndent()
            ),
            fromString(
                Square.Full.Orange, """
                ,,,,
                ,,#,
                ###,
                ,,,,
            """.trimIndent()
            ),
            fromString(
                Square.Full.Yellow, """
                ,,,,
                ,##,
                ,##,
                ,,,,
            """.trimIndent()
            ),
            fromString(
                Square.Full.Green, """
                ,,,,
                ,##,
                ##,,
                ,,,,
            """.trimIndent()
            ),
            fromString(
                Square.Full.Purple, """
                ,,,,
                ,#,,
                ###,
                ,,,,
            """.trimIndent()
            ),
            fromString(
                Square.Full.Red, """
                ,,,,
                ##,,
                ,##,
                ,,,,
            """.trimIndent()
            ),
        )
    }
}