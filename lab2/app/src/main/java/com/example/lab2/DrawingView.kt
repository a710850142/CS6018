package com.example.lab2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class DrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }
    private val path = Path()
    private val points = mutableListOf<Point>()

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(event.x, event.y)
                points.add(Point(event.x, event.y))
                Log.d("DrawingView", "Touch DOWN: ${event.x}, ${event.y}")
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(event.x, event.y)
                points.add(Point(event.x, event.y))
                invalidate()
                Log.d("DrawingView", "Touch MOVE: ${event.x}, ${event.y}")
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
    }

    fun getPoints(): List<Point> = points.toList()

    fun setPoints(savedPoints: List<Point>) {
        points.clear()
        points.addAll(savedPoints)
        path.reset()
        for (point in points) {
            if (points.indexOf(point) == 0) {
                path.moveTo(point.x, point.y)
            } else {
                path.lineTo(point.x, point.y)
            }
        }
        invalidate()
        Log.d("DrawingView", "Points set: ${points.size}")
    }

    data class Point(val x: Float, val y: Float)
}