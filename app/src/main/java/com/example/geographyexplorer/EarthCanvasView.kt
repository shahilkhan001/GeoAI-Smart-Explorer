package com.example.geographyexplorer

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class EarthCanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val earthBitmap: Bitmap =
        BitmapFactory.decodeResource(resources, R.drawable.earth_satellite)

    private var rotationAngle = 0f

    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f

        val size = width.coerceAtMost(height) * 0.9f
        val radius = size / 2

        // --- Draw atmospheric glow ---
        val gradient = RadialGradient(
            centerX,
            centerY,
            radius * 1.2f,
            intArrayOf(
                Color.argb(80, 30, 144, 255),   // blue glow
                Color.TRANSPARENT
            ),
            floatArrayOf(0.6f, 1f),
            Shader.TileMode.CLAMP
        )

        glowPaint.shader = gradient
        canvas.drawCircle(centerX, centerY, radius * 1.2f, glowPaint)

        // --- Prepare scaled Earth bitmap ---
        val scaledBitmap = Bitmap.createScaledBitmap(
            earthBitmap,
            size.toInt(),
            size.toInt(),
            true
        )

        val left = centerX - size / 2
        val top = centerY - size / 2

        canvas.save()

        // Rotate Earth
        canvas.rotate(rotationAngle, centerX, centerY)

        canvas.drawBitmap(scaledBitmap, left, top, paint)

        canvas.restore()

        rotationAngle += 0.4f

        // Continuous animation
        postInvalidateOnAnimation()
    }
}