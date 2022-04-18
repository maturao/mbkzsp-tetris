package cz.zcu.maturao.tetris

import android.content.Context
import android.graphics.Canvas
import android.util.Log
import android.view.WindowManager
import java.lang.Exception
import kotlin.math.roundToLong

class GameLoopThread(private val view: GameView) : Thread() {
    var running: Boolean = false

    override fun run() {
        val windowService = view.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val fps = windowService.defaultDisplay.refreshRate
        val ticksPS: Long = 1000 / fps.roundToLong()
        var startTime: Long
        var sleepTime: Long

        while (running) {
            var c: Canvas? = null
            startTime = System.currentTimeMillis()

            try {
                c = view.holder.lockCanvas()

                synchronized(view.holder) {
                    // Pridana kontrola, aby nehazelo chybu pri tlacitku BACK
                    if (c != null) {
                        view.onDraw(c)
                    }
                }
            } finally {
                if (c != null) {
                    view.holder.unlockCanvasAndPost(c)
                }
            }

            sleepTime = ticksPS - (System.currentTimeMillis() - startTime)

            try {
                if (sleepTime > 0) {
                    sleep(sleepTime)
                } else {
                    sleep(10)
                }
            } catch (_: Exception) {
            }
        }
    }
}