package cz.zcu.maturao.tetris.logic

import java.io.Serializable

class Matrix<T>(val height: Int, val width: Int, private val defaultValue: T) : Serializable {
    private val values = MutableList(height * width) { defaultValue }

    private fun getIndex(row: Int, col: Int) = row * width + col

    operator fun get(row: Int, col: Int) = values[getIndex(row, col)]
    operator fun set(row: Int, col: Int, value: T) {
        values[getIndex(row, col)] = value
    }

    val indices: Sequence<Pair<Int, Int>>
        get() = (0 until height)
            .asSequence()
            .flatMap { i ->
                (0 until width)
                    .asSequence()
                    .map { j -> i to j }
            }

    fun withIndices(): Sequence<Triple<Int, Int, T>> = indices
        .zip(values.asSequence())
        .map { (ij, value) -> Triple(ij.first, ij.second, value) }

    fun rotated(): Matrix<T> =
        Matrix(width, height, defaultValue).also { result ->
            for ((i, j, value) in withIndices()) {
                result[j, height - i - 1] = value
            }
        }

    override fun toString() = values.asSequence()
        .chunked(width) { it.joinToString(" ") }
        .joinToString("\n")
}
