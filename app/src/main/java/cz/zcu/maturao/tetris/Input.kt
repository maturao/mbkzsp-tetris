package cz.zcu.maturao.tetris

import android.util.Log
import android.view.MotionEvent
import java.util.concurrent.BlockingDeque
import java.util.concurrent.LinkedBlockingDeque

class Input {
    companion object {
        private const val tapMaxMove = 2.5
        private const val tapMaxMoveSqr = tapMaxMove * tapMaxMove

        private fun sqrDist(x1: Float, y1: Float, x2: Float, y2: Float): Float {
            val a = x2 - x1
            val b = y2 - y1
            return a * a + b * b
        }
    }

    private val touchInputDeque: BlockingDeque<TouchInput> = LinkedBlockingDeque(100)
    private var lastTouchInput: TouchInput? = null

    fun update(event: MotionEvent) {
        val oldTouchInput = lastTouchInput
        var firstX = oldTouchInput?.firstX
        var firstY = oldTouchInput?.firstY

        val x = event.x
        val y = event.y

        val action = when (event.action) {
            MotionEvent.ACTION_DOWN -> TouchAction.Down
            MotionEvent.ACTION_MOVE -> TouchAction.Move
            MotionEvent.ACTION_UP ->
                if (firstX == null || firstY == null ||
                    sqrDist(firstX, firstY, x, y) > tapMaxMoveSqr
                ) TouchAction.Up.Lift
                else TouchAction.Up.Click

            else -> {
                Log.e("Input", "Unexpected motion event action: ${event.action}")
                TouchAction.Up.Lift
            }
        }

        if (action == TouchAction.Move && oldTouchInput?.action == TouchAction.Move) return

        if (action == TouchAction.Down) {
            firstX = x
            firstY = y
        }

        val newTouchInput = TouchInput(action, x, y, firstX ?: x, firstY ?: y)
        lastTouchInput = newTouchInput
        if (!touchInputDeque.offer(newTouchInput)) {
            Log.e("INPUT", "New touch input does not fit into queue")
        }
    }

    fun popTouchInput(): TouchInput? = touchInputDeque.poll()
}

sealed interface TouchAction {
    object Down : TouchAction
    object Move : TouchAction

    sealed interface Up : TouchAction {
        object Click : Up
        object Lift : Up
    }
}

data class TouchInput(
    val action: TouchAction,
    val x: Float,
    val y: Float,
    val firstX: Float,
    val firstY: Float,
)
