package com.ufpr.equilibrium.feature_professional

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.ufpr.equilibrium.R

class SemicircleProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }

    private var strokeWidth = resources.getDimensionPixelSize(R.dimen.semicircle_stroke).toFloat()
    private var sweep = 0f // degrees (0..180)

    private val arcRect = RectF()

    init {
        val tc = ContextCompat.getColor(context, R.color.blue)
        val pc = ContextCompat.getColor(context, R.color.blue)
        val bgc = ContextCompat.getColor(context, R.color.gray_light)

        trackPaint.color = bgc
        progressPaint.color = pc
        dotPaint.color = tc

        trackPaint.strokeWidth = strokeWidth
        progressPaint.strokeWidth = strokeWidth
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val left = paddingLeft + strokeWidth / 2
        val right = width - paddingRight - strokeWidth / 2
        val top = paddingTop + strokeWidth / 2
        val bottom = height - paddingBottom - strokeWidth / 2

        val size = Math.min((right - left), (bottom - top) * 2)
        arcRect.set(left, top, left + size, top + size)

        canvas.drawArc(arcRect, 180f, 180f, false, trackPaint)

        if (sweep > 0f) {
            canvas.drawArc(arcRect, 180f, sweep, false, progressPaint)
        }

            // draw small dot at start of arc (align precisely with arc left edge)
            val angleDeg = 180.0
            val angleRad = Math.toRadians(angleDeg)
            val dotRadius = resources.getDimension(R.dimen.semicircle_dot_radius)
            // Arc is drawn centered on the arcRect border; to put the dot on the outer edge
            // compute radius to the center of the stroke and offset by half stroke to sit on edge.
            // Move the dot slightly inward so it lies within the gray stroke area
            // Use a fraction of the stroke width to offset inward from the outer edge.
            val inwardOffset = strokeWidth * 0.6
            val radius = (arcRect.width() / 2.0) + (strokeWidth / 2.0) - inwardOffset
            val cx = arcRect.centerX() + (radius * Math.cos(angleRad)).toFloat()
            val cy = arcRect.centerY() + (radius * Math.sin(angleRad)).toFloat()
            canvas.drawCircle(cx, cy, dotRadius, dotPaint)
    }

    fun setProgress(percent: Int, animate: Boolean = true) {
        val target = (percent.coerceIn(0, 100) / 100f) * 180f
        if (animate) {
            val anim = ValueAnimator.ofFloat(sweep, target)
            anim.duration = 700
            anim.addUpdateListener { v ->
                sweep = v.animatedValue as Float
                invalidate()
            }
            anim.start()
        } else {
            sweep = target
            invalidate()
        }
    }
}
