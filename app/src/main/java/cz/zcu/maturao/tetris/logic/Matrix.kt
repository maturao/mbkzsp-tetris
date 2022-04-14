package cz.zcu.maturao.tetris.logic

class Matrix<T>(val height: Int, val width: Int, defaultValue: T) {
    private val values = MutableList(height * width) { defaultValue }

    private fun getIndex(row: Int, col: Int): Int = row * width + col

    operator fun get(row: Int, col: Int): T = values[getIndex(row, col)]
    operator fun set(row: Int, col: Int, value: T) {
        values[getIndex(row, col)] = value

    }
}