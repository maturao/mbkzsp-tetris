package cz.zcu.maturao.tetris.game

import android.util.Log
import android.view.MotionEvent
import java.util.concurrent.BlockingDeque
import java.util.concurrent.LinkedBlockingDeque
import kotlin.math.hypot

/**
 * Třída, která zpracovává dotykový vstup od uživatele
 */
class Input {
    companion object {
        /**
         * Maximální počet pixelů, kolik může uživatel pohnout prstem, než se z akce Click stane jen Lift
         */
        private const val CLICK_MAX_MOVE = 3
    }

    /**
     * Fronta eventů
     */
    private val touchInputDeque: BlockingDeque<TouchInput> = LinkedBlockingDeque(100)

    /**
     * Naposledy zpracovaný TouchInput
     */
    private var lastTouchInput: TouchInput? = null

    /**
     * Zpracuje TouchInput
     */
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
                // zkontroluji, zda se jedná o Click
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

/**
 * Dotuková akce od uživatele
 */
sealed interface TouchAction {
    /**
     * Uživatel položil prst na obrazovku
     */
    object Down : TouchAction

    /**
     * Uživatel pohnul prstem
     */
    object Move : TouchAction

    /**
     * Uživatel zvednul prst
     */
    sealed interface Up : TouchAction {
        /**
         * Kliknutí - uživatel moc nehýbal prstem mezi položením a zvednutím
         */
        object Click : Up

        /**
         * Obyčejné zvednutí prstu
         */
        object Lift : Up
    }
}

/**
 * Dtokvý vsutp od uživatele
 */
data class TouchInput(
    /**
     * Dotkyová akce
     */
    val action: TouchAction,
    /**
     * Horizontální pozice prstu
     */
    val x: Float,
    /**
     * Vertikální pozie prstu
     */
    val y: Float,
    /**
     * Počáteční horizontální pozice prstu
     */
    val firstX: Float,
    /**
     * Počátečí vertikální pozice prstu
     */
    val firstY: Float,
)
