package cz.zcu.maturao.tetris.logic

import java.io.Serializable

/**
 * Počítá skóre dle vyčistěných řádek
 */
class Score : Serializable {
    companion object {
        /**
         * Počet vyčistěných řádek na jeden level
         */
        const val LINES_PER_LEVEL = 10

        /**
         * Body dle počtu vyčistěných řádek
         */
        val pointsForLines = mapOf(
            1 to 40,
            2 to 100,
            3 to 300,
            4 to 1200,
        )
    }

    /**
     * Celkové skóre
     */
    var score = 0
        private set

    /**
     * Aktuální level
     */
    var level = 0
        private set

    /**
     * Počet vyčistěncých řádek
     */
    var lines = 0
        private set

    /**
     * Aktualizuje skóre
     */
    internal fun update(linesCleared: Int) {
        val points = pointsForLines[linesCleared] ?: return

        lines += linesCleared
        score += (level + 1) * points
        level = lines / LINES_PER_LEVEL
    }
}