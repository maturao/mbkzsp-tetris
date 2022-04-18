package cz.zcu.maturao.tetris.logic

sealed interface Square {
    val color: Int
    infix fun collidesWith(square: Square): Boolean

    object Empty : Square {
        override val color = 0
        override fun collidesWith(square: Square) = false
    }

    sealed interface Full : Square {
        override fun collidesWith(square: Square) = square is Full

        object Void : Full {
            override val color: Int = 0x000000FF
        }

        class Colored(override val color: Int) : Full
    }
}
