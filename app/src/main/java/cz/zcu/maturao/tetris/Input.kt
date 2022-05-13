package cz.zcu.maturao.tetris

import android.util.Log
import android.view.MotionEvent
import java.util.concurrent.BlockingDeque
import java.util.concurrent.LinkedBlockingDeque
import kotlin.math.hypot

class Input {
    companion object {
        private const val CLICK_MAX_MOVE = 3
    }

    private val touchInputDeque: BlockingDeque<TouchInput> = LinkedBlockingDeque()
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
                if (firstX != null && firstY != null &&
                    hypot(firstX - x, firstY - y) <= CLICK_MAX_MOVE
                ) TouchAction.Up.Click
                else TouchAction.Up.Lift

            else -> {
                Log.e("Input", "Unexpected motion event action: ${event.action}")
                TouchAction.Up.Lift
            }
        }

        val previousTouchInput = touchInputDeque.pollLast()
        // pokud již ve frontě čeká 'Move' akce, tak nebudu přidávat další,
        // protože jsou nejčastější a mohly by zahltit frontu
        if (action == previousTouchInput?.action && action == TouchAction.Move) return

        if (action == TouchAction.Down) {
            firstX = x
            firstY = y
        }

        val newTouchInput = TouchInput(action, x, y, firstX ?: x, firstY ?: y)
        lastTouchInput = newTouchInput
        touchInputDeque.offer(newTouchInput)
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
