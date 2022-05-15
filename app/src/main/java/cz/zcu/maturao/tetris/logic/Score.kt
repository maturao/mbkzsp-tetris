package cz.zcu.maturao.tetris.logic

import java.io.Serializable

class Score : Serializable {
    companion object {
        const val LINES_PER_LEVEL = 10
    }

    var score = 12
        private set

    var level = 0
        private set

    var lines = 0
        private set

    internal fun update(linesCleared: Int) {
        val points = when (linesCleared) {
            1 -> 40
            2 -> 100
            3 -> 300
            4 -> 1200
            else -> return
        }

        lines += linesCleared
        score += (level + 1) * points
        level = lines / LINES_PER_LEVEL
    }
}