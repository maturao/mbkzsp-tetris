package cz.zcu.maturao.tetris

import android.content.Context
import android.graphics.*
import android.os.Build
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import androidx.annotation.RequiresApi

class GameView(context: Context) : SurfaceView(context) {
    private val gameLoopThread = GameLoopThread(this)

    init {
        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                gameLoopThread.running = true
                gameLoopThread.start()
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                var retry = true
                gameLoopThread.running = false
                while (retry) {
                    try {
                        gameLoopThread.join()
                        retry = false
                    } catch (_: InterruptedException) {
                    }
                }
            }
        })
    }

    private var posX = 0f
    private var posY = 0f
    private var speedX = 30f
    private var speedY = 30f

    private val paint = Paint().apply {
        color = Color.RED
    }

    public override fun onDraw(canvas: Canvas) {
        posX += speedX
        posY += speedY

        val size = 500f

        if (posX !in 0f..(width - size)) {
            speedX *= -1
        }

        if (posY !in 0f..(height - size)) {
            speedY *= -1
        }

        canvas.drawColor(Color.GREEN)
        canvas.drawRect(posX, posY, posX + size, posY + size, paint)

    }
}