package com.github.shchuko.io2d.impl

import com.github.shchuko.io2d.CanvasProcessor
import com.github.shchuko.math.Vector2D

abstract class CanvasProcessorCommonGetImpl : CanvasProcessor {
    override fun getCanvasSize(padX: Double, padY: Double): Pair<Double, Double> {
        require(padX >= 0.0) { "padX must not be negative, got '$padX'" }
        require(padY >= 0.0) { "padY must not be negative, got '$padY'" }
        var xSize = 0.0
        var ySize = 0.0

        if (!isEmpty()) {
            xSize = maxX - minX
            ySize = maxY - minY
        }

        assert(xSize >= 0.0)
        assert(ySize >= 0.0)
        return Pair(xSize + padX * 2.0, ySize + padY * 2.0)
    }

    override fun getMoveVecToPositive(padX: Double, padY: Double): Vector2D {
        require(padX >= 0.0) { "padX must not be negative, got '$padX'" }
        require(padY >= 0.0) { "padY must not be negative, got '$padY'" }

        val dX = if (isEmpty() || minX >= 0.0) 0.0 else -minX
        val dY = if (isEmpty() || minY >= 0.0) 0.0 else -minY
        assert(dX >= 0.0)
        assert(dY >= 0.0)

        return Vector2D(dX + padX, dY + padY)
    }

    override fun toCanvasProperties(padX: Double, padY: Double): CanvasProcessor.CanvasProperties =
        CanvasProcessor.CanvasProperties(getMoveVecToPositive(padX, padY), getCanvasSize(padX, padY))
}
