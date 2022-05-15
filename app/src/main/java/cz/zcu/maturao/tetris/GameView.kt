package cz.zcu.maturao.tetris

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.media.MediaPlayer
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(val gameActivity: GameActivity) : SurfaceView(gameActivity) {
//    private val player: MediaPlayer = MediaPlayer.create(context, R.raw.tetris)

    private val gameLoopThread = GameLoopThread(this)
    private val input = Input()

    val game = Game(this, input)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        input.update(event)
        return true
    }

    init {
//        player.isLooping = true
//        player.start()

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

    public override fun onDraw(canvas: Canvas) {
        game.update()
        game.draw(canvas)
    }
}