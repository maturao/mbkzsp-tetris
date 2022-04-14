package cz.zcu.maturao.tetris.logic

sealed interface Square {
    object Empty : Square
    sealed interface Full {
        class Colored(val color: Int) : Full
    }
}
