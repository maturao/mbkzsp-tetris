package cz.zcu.maturao.tetris.logic

import kotlin.random.Random

class Shape private constructor(val squares: Matrix<Square>) {
    fun rotated() = Shape(squares.rotated())

    companion object {
        const val SHAPE_SIZE = 4
        const val SHAPE_FULL_CHAR = '#'

        private fun fromString(color: Int, string: String): Shape {
            val lines = string.lines()

            if (lines.size != SHAPE_SIZE) throw IllegalArgumentException("Shape string must have $SHAPE_SIZE lines")
            if (lines.any { it.length != SHAPE_SIZE }) throw IllegalArgumentException("Each line in shape string must contain $SHAPE_SIZE characters")

            val matrix = Matrix<Square>(SHAPE_SIZE, SHAPE_SIZE, Square.Empty)
            val fullSquare = Square.Full.Colored(color)

            for (i in 0 until SHAPE_SIZE) {
                for (j in 0 until SHAPE_SIZE) {
                    if (lines[i][j] == SHAPE_FULL_CHAR) matrix[i, j] = fullSquare
                }
            }

            return Shape(matrix)
        }

        fun randomShape() = shapes[Random.nextInt(shapes.size)]

        val shapes = listOf(
            fromString(
                TetrisColors.cyan, """
                ,,,,
                ,,,,
                ####
                ,,,,
            """.trimIndent()
            ),
            fromString(
                TetrisColors.blue, """
                ,,,,
                #,,,
                ###,
                ,,,,
            """.trimIndent()
            ),
            fromString(
                TetrisColors.orange, """
                ,,,,
                ,,#,
                ###,
                ,,,,
            """.trimIndent()
            ),
            fromString(
                TetrisColors.yellow, """
                ,,,,
                ,##,
                ,##,
                ,,,,
            """.trimIndent()
            ),
            fromString(
                TetrisColors.green, """
                ,,,,
                ,##,
                ##,,
                ,,,,
            """.trimIndent()
            ),
            fromString(
                TetrisColors.purple, """
                ,,,,
                ,#,,
                ###,
                ,,,,
            """.trimIndent()
            ),
            fromString(
                TetrisColors.red, """
                ,,,,
                ##,,
                ,##,
                ,,,,
            """.trimIndent()
            ),
        )
    }
}