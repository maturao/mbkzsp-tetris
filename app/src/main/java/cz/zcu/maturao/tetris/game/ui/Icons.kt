package cz.zcu.maturao.tetris.game.ui

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import androidx.core.graphics.withSave
import cz.zcu.maturao.tetris.utils.cleared
import cz.zcu.maturao.tetris.utils.globalPaint
import kotlin.math.sqrt

class StopIcon(private val color: Int) : Drawable {
    override fun draw(canvas: Canvas, x: Float, y: Float, width: Float, height: Float) {
        val paint = globalPaint.cleared()
        paint.color = color
        val round = 0.1f * width
        canvas.drawRoundRect(x, y, x + width / 3, y + height, round, round, paint)
        canvas.drawRoundRect(x + width / 3 * 2, y, x + width, y + height, round, round, paint)
    }
}

private fun drawRoundedShape(
    canvas: Canvas,
    color: Int,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    shape: Path,
) {
    val paint = globalPaint.cleared {
        this.color = color
        style = Paint.Style.FILL_AND_STROKE
        strokeJoin = Paint.Join.ROUND
        strokeWidth = 0.3f
    }
    canvas.withSave {
        translate(x, y)
        scale(width, height)
        scale(1 / (1 + paint.strokeWidth), 1 / (1 + paint.strokeWidth))
        translate(paint.strokeWidth / 2f, paint.strokeWidth / 2f)
        drawPath(shape, paint)
    }
}

class ResumeIcon(private val color: Int) : Drawable {
    companion object {
        private val shapePath = Path().apply {
            moveTo(0f, 0f)
            lineTo(sqrt(3f) / 2f, 0.5f)
            lineTo(0f, 1f)
            close()
        }
    }

    override fun draw(canvas: Canvas, x: Float, y: Float, width: Float, height: Float) {
        drawRoundedShape(canvas, color, x, y, width, height, shapePath)
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
        drawRoundedShape(canvas, color, x, y, width, height, shapePath)
    }
}