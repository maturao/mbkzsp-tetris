package cz.zcu.maturao.tetris.drawing

import android.graphics.Canvas
import android.graphics.Path
import androidx.core.graphics.withSave
import cz.zcu.maturao.tetris.utils.cleared
import cz.zcu.maturao.tetris.utils.globalPaint
import kotlin.math.sqrt

class StopIcon(private val color: Int) : Drawable {
    override fun draw(canvas: Canvas, x: Float, y: Float, width: Float, height: Float) {
        val paint = globalPaint.cleared()
        paint.color = color

        canvas.drawRect(x, y, x + width / 3, y + height, paint)
        canvas.drawRect(x + width / 3 * 2, y, x + width, y + height, paint)
    }
}

class ResumeIcon(private val color: Int) : Drawable {
    companion object {
        private val shapePath = Path().apply {
//            fillType = Path.FillType.EVEN_ODD
            moveTo(0f, 0f)
            lineTo(sqrt(3f) / 2f, 0.5f)
            lineTo(0f, 1f)
            close()
        }
    }

    override fun draw(canvas: Canvas, x: Float, y: Float, width: Float, height: Float) {
        val paint = globalPaint.cleared()
        paint.color = color

        canvas.withSave {
            translate(x, y)
            scale(width, height)
            drawPath(shapePath, paint)
        }
    }
}

class HomeIcon(private val color: Int) : Drawable {
    companion object {
        private val shapePath = Path().apply {
            fillType = Path.FillType.EVEN_ODD
            moveTo(0.5f, 0f)
            lineTo(1f, 0.5f)
            lineTo(1f, 1f)
            lineTo(0f, 1f)
            lineTo(0f, 0.5f)
            close()
        }
    }

    override fun draw(canvas: Canvas, x: Float, y: Float, width: Float, height: Float) {
        val paint = globalPaint.cleared()
        paint.color = color

        canvas.withSave {
            translate(x, y)
            scale(width, height)
            drawPath(shapePath, paint)
        }
    }
}