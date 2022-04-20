package cz.zcu.maturao.tetris.logic

import java.util.*

class ShapeQueue {
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
    }

    private val rand = Random()

    var nextShape = randomShape()
        private set

    private fun randomShape() = allShapes[rand.nextInt(allShapes.size)]

    fun popNextShape(): Shape {
        val shape = nextShape
        do {
            nextShape = randomShape()
        } while (nextShape === shape)
        return shape
    }
}