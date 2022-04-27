package cz.zcu.maturao.tetris

import android.util.Log
import android.view.MotionEvent

class Input {
    companion object {
        const val tapTimeoutLength: Long = 300
    }

    private var tapTimeout: Long = 0
    private var touchInput: TouchInput? = null

    fun update(event: MotionEvent) {
        synchronized(this) {
            val action = when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    tapTimeout = System.currentTimeMillis() + tapTimeoutLength
                    TouchAction.Down
                }
                MotionEvent.ACTION_MOVE -> TouchAction.Move
                MotionEvent.ACTION_UP -> {
                    val tapTimeout = tapTimeout
                    this.tapTimeout = 0

                    if (System.currentTimeMillis() <= tapTimeout) TouchAction.Up.Tap
                    else TouchAction.Up.Lift
                }
                else -> {
                    Log.w("INPUT", "unexpected motion event action: ${event.action}")
                    return
                }
            }

            touchInput = TouchInput(action, event.x, event.y)
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

sealed interface TouchAction {
    object Down : TouchAction
    object Move : TouchAction

    sealed interface Up : TouchAction {
        object Tap : Up
        object Lift : Up
    }
}

data class TouchInput(
    val action: TouchAction,
    val x: Float,
    val y: Float,
)
