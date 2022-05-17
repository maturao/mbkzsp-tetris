package cz.zcu.maturao.tetris.logic

import java.io.Serializable
import kotlin.math.min
import kotlin.math.max

/**
 * Tetrisový tvar
 */
class Shape(
    /**
     * Matice tetrisových čtverců
     */
    val squares: Matrix<Square>
) : Serializable {
    /**
     * Nový, otočený tvar
     */
    fun rotated() = Shape(squares.rotated())

    /**
     * Střed tvaru
     */
    val centerRow: Float

    /**
     * Střed tvaru
     */
    val centerCol: Float

    /**
     * Minimální index vyplněného řádku
     */
    val minRow: Int

    /**
     * Minimální index vyplněného sloupce
     */
    val minCol: Int

    /**
     * Maximální index vyplněného řádku
     */
    val maxRow: Int

    /**
     * Maximální index vyplněného sloupce
     */
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

        /**
         * Načte tvar z rětezce, kde '#' reprezentuje plný čtverec a ostatní znaky reprezentují prázdný čtverec
         * Řádky jsou oddělené '\n'
         */
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