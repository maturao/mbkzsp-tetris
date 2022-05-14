package cz.zcu.maturao.tetris.logic

sealed class Square(val color: Int) {
    abstract fun collidesWith(square: Square): Boolean

    object Empty : Square(0) {
        override fun collidesWith(square: Square) = false
    }

    sealed class Full(color: Int) : Square(color) {
        override fun collidesWith(square: Square) = square is Full

        object Wall : Full(0xFF_000000.toInt())
        object White : Full(0xFF_FFFFFF.toInt())

        object Cyan : Full(TetrisColors.CYAN)
        object Blue : Full(TetrisColors.BLUE)
        object Orange : Full(TetrisColors.ORANGE)
        object Yellow : Full(TetrisColors.YELLOW)
        object Green : Full(TetrisColors.GREEN)
        object Purple : Full(TetrisColors.PURPLE)
        object Red : Full(TetrisColors.RED)
    }
}
