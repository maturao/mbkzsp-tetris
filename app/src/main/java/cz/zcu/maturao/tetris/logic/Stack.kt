package cz.zcu.maturao.tetris.logic

class Stack {
    val squares = Matrix<Square>(20, 10, Square.Empty)
    var block = randomBlock()
        private set

    private fun randomBlock() = Block(Shape.randomShape(), 0, 0)

    fun getSquare(row: Int, col: Int): Square = when {
        col !in 0 until squares.width -> Square.Full.Void
        row >= squares.height -> Square.Full.Void
        row < 0 -> Square.Empty
        else -> squares[row, col]
    }

    fun collidesWith(block: Block): Boolean {
        val blockSquares = block.shape.squares
        for ((i, j) in blockSquares.indices) {
            if (blockSquares[i, j].collidesWith(getSquare(i, j))) return true
        }
        return false
    }

    private fun add(block: Block) {
        val blockSquares = block.shape.squares
        for ((i, j) in blockSquares.indices) {
            squares[block.row + i, block.col + j] = blockSquares[i, j]
        }
    }

    fun rotateBlock() {
        val rotated = block.rotated()
        if (collidesWith(rotated)) return

        block = rotated
    }

    fun setBlockCol(col: Int) {
        val moved = block.moved(block.row, col)
        if (collidesWith(moved)) return

        block = moved
    }

    fun setBlockRow(row: Int) {
        val moved = block.moved(block.row, block.col)
        block = if (collidesWith(moved)) {
            add(block)
            randomBlock()
        } else moved
    }

}
