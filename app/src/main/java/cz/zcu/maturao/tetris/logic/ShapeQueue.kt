package cz.zcu.maturao.tetris.logic

import java.io.Serializable
import java.util.*

/**
 * Podává náhodné tvary do nekonečna
 */
class ShapeQueue : Serializable {
    companion object {
        private val allShapes = listOf(
            Shapes.I,
            Shapes.J,
            Shapes.L,
            Shapes.O,
            Shapes.S,
            Shapes.T,
            Shapes.Z,
        )

        private val rand = Random()
    }

    private fun randomShape() = allShapes[rand.nextInt(allShapes.size)]

    /**
     * Příští tvar
     */
    var nextShape = randomShape()
        private set

    /**
     * Vrátí nextShape a zároveň ho nastaví na nový náhodný tvar
     */
    fun getShape(): Shape {
        val shape = nextShape
        do {
            nextShape = randomShape()
        } while (nextShape === shape)
        return shape
    }
}