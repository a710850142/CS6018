package com.example.lab2

import android.util.Log

object DrawingStateManager {
    private var points: List<DrawingView.Point> = listOf()

    fun saveState(newPoints: List<DrawingView.Point>) {
        points = newPoints
        Log.d("DrawingStateManager", "State saved: ${points.size} points")
    }

    fun loadState(): List<DrawingView.Point> {
        Log.d("DrawingStateManager", "State loaded: ${points.size} points")
        return points
    }

    fun hasState(): Boolean = points.isNotEmpty()
}