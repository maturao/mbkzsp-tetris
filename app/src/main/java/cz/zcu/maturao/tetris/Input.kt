package cz.zcu.maturao.tetris

import android.util.Log
import android.view.MotionEvent

class Input {
    companion object {
        private const val tapTimeoutLength: Long = 300
        private const val tapMaxMove = 2.5
        private const val tapMaxMoveSqr = tapMaxMove * tapMaxMove

        private fun sqrDist(x1: Float, y1: Float, x2: Float, y2: Float): Float {
            val a = x2 - x1
            val b = y2 - y1
            return a * a + b * b
        }
    }

    private var tapTimeout: Long = 0
    private var oldTouchInput: TouchInput? = null
    private var touchInput: TouchInput? = null

    fun update(event: MotionEvent) {
        synchronized(this) {
            val oldTouchInput = oldTouchInput
            var firstX = oldTouchInput?.firstX
            var firstY = oldTouchInput?.firstY

            val x = event.x
            val y = event.y

            val action = when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    tapTimeout = System.currentTimeMillis() + tapTimeoutLength
                    TouchAction.Down
                }
                MotionEvent.ACTION_MOVE -> TouchAction.Move
                MotionEvent.ACTION_UP -> {
                    val tapTimeout = tapTimeout
                    this.tapTimeout = 0

                    if (System.currentTimeMillis() > tapTimeout
                        || firstX == null || firstY == null
                        || Companion.sqrDist(firstX, firstY, x, y) > tapMaxMoveSqr
                    ) TouchAction.Up.Lift
                    else TouchAction.Up.Click
                }

                else -> {
                    Log.e("INPUT", "unexpected motion event action: ${event.action}")
                    return
                }
            }

            if (action == TouchAction.Down) {
                firstX = x
                firstY = y
            }

            touchInput = TouchInput(action, x, y, firstX ?: x, firstY ?: y)
        }
    }

    fun popTouchInput(): TouchInput? {
        return synchronized(this) {
            val tmp = touchInput
            touchInput = null
            if (tmp != null) {
                oldTouchInput = tmp
            }
            tmp
        }
    }
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
