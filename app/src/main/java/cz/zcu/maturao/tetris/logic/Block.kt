package cz.zcu.maturao.tetris.logic

class Block(val shape: Shape, val row: Int = 0, val col: Int = 0) {
    fun rotated() = Block(shape.rotated(), row, col)
    fun moved(row: Int, col: Int) = Block(shape, row, col)
}
