package cz.zcu.maturao.tetris

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi

class GameView(context: Context) : SurfaceView(context) {
    private val gameLoopThread = GameLoopThread(this)
    private val game = Game()

    init {
        setOnTouchListener(object : OnSwipeTouchListener(context) {
            override fun onSwipeTop() {
                game.stack.rotateBlock()
            }

            override fun onSwipeLeft() {
                game.stack.setBlockCol(game.stack.block.col - 1)
            }

            override fun onSwipeRight() {
                game.stack.setBlockCol(game.stack.block.col + 1)
            }

            override fun onSwipeBottom() {
                game.stack.setBlockRow(game.stack.block.row + 1)
            }
        })

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