package cz.zcu.maturao.tetris.logic

class Matrix<T>(val height: Int, val width: Int, private val defaultValue: T) {
    private val values = MutableList(height * width) { defaultValue }

    private fun getIndex(row: Int, col: Int) = row * width + col


    operator fun get(row: Int, col: Int) = values[getIndex(row, col)]
    operator fun set(row: Int, col: Int, value: T) {
        values[getIndex(row, col)] = value
    }

    fun getOrNull(row: Int, col: Int) = values.getOrNull(getIndex(row, col))

    val indices: Sequence<Pair<Int, Int>>
        get() = (0 until height)
            .asSequence()
            .flatMap { i ->
                (0 until width)
                    .asSequence()
                    .map { j -> i to j }
            }

    fun rotated(): Matrix<T> =
        Matrix(width, height, defaultValue).also { result ->
            for ((i, j) in indices) {
                result[j, height - i - 1] = this[i, j]
            }
        }

    override fun toString() = values.asSequence()
        .chunked(width) { it.joinToString(" ") }
        .joinToString("\n")
}
