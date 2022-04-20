package cz.zcu.maturao.tetris

import android.view.MotionEvent

class Input {
    var touchInput: TouchInput? = null

    fun update(event: MotionEvent) {
        synchronized(this) {
            val oldTouchInput = touchInput
            val actionDown = event.action == MotionEvent.ACTION_DOWN
            val firstX = if (actionDown) event.x else oldTouchInput?.firstX ?: event.x
            val firstY = if (actionDown) event.y else oldTouchInput?.firstY ?: event.y

            touchInput = TouchInput(event.action, firstX, firstY, event.x, event.y)
        }
    }

    fun popTouchInput(): TouchInput? {
        return synchronized(this) {
            val tmp = touchInput
            touchInput = null
            tmp
        }
    }
}

data class TouchInput(
    val action: Int,
    val firstX: Float,
    val firstY: Float,
    val x: Float,
    val y: Float,
)
