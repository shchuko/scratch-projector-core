package com.github.shchuko.io2d

import com.github.shchuko.math.Vector2D

interface CanvasProcessor {
    val maxX: Double
    val minX: Double
    val maxY: Double
    val minY: Double

    fun isEmpty(): Boolean
    fun getCanvasSize(padX: Double = 0.0, padY: Double = padX): Pair<Double, Double>
    fun getMoveVecToPositive(padX: Double = 0.0, padY: Double = padX): Vector2D
    fun toCanvasProperties(padX: Double = 0.0, padY: Double = padX): CanvasProperties

    data class CanvasProperties(val globalMoveVec: Vector2D, val canvasSize: Pair<Double, Double>)
}
