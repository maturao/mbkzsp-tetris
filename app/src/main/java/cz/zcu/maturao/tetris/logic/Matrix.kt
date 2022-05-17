package cz.zcu.maturao.tetris.logic

import java.io.Serializable

/**
 * Matice
 */
class Matrix<T>(
    /**
     * Výška
     */
    val height: Int,
    /**
     * Šířka
     */
    val width: Int,
    /**
     * Výchozí hodnota
     */
    private val defaultValue: T
) : Serializable {
    private val values = MutableList(height * width) { defaultValue }

    /**
     * Index do pole dle řádku a sloupce
     */
    private fun getIndex(row: Int, col: Int) = row * width + col

    operator fun get(row: Int, col: Int) = values[getIndex(row, col)]
    operator fun set(row: Int, col: Int, value: T) {
        values[getIndex(row, col)] = value
    }

    /**
     * Sekvence indexu do matice ve tvaru (řádka, sloupec)
     */
    val indices: Sequence<Pair<Int, Int>>
        get() = (0 until height)
            .asSequence()
            .flatMap { i ->
                (0 until width)
                    .asSequence()
                    .map { j -> i to j }
            }

    /**
     * Sekvence prvků matice včetně indexu ve tvaru (řádka, sloupec, prvek matice)
     */
    fun withIndices(): Sequence<Triple<Int, Int, T>> = indices
        .zip(values.asSequence())
        .map { (ij, value) -> Triple(ij.first, ij.second, value) }

    /**
     * Nová matice, otočená o 90 stupňu po směru hodinových ručiček
     */
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
