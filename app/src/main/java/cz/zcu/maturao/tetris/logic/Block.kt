package cz.zcu.maturao.tetris.logic

import java.io.Serializable

/**
 * Tetrisový tvar, který má pozici
 */
class Block(val shape: Shape, val row: Int = 0, val col: Int = 0) : Serializable {
    /**
     * Nový, otočený blok
     */
    fun rotated() = Block(shape.rotated(), row, col)

    /**
     * Nový, posunutý blok
     */
    fun moved(row: Int, col: Int) = Block(shape, row, col)
}
