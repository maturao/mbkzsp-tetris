package cz.zcu.maturao.tetris.logic

import java.io.Serializable

class Score : Serializable {
    companion object {
        const val LINES_PER_LEVEL = 10
        val pointsForLines = mapOf(
            1 to 40,
            2 to 100,
            3 to 300,
            4 to 1200,
        )
    }

    var score = 0
        private set

    var level = 0
        private set

    var lines = 0
        private set

    internal fun update(linesCleared: Int) {
        val points = pointsForLines[linesCleared] ?: return

        lines += linesCleared
        score += (level + 1) * points
        level = lines / LINES_PER_LEVEL
    }
}