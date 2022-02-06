package com.github.shchuko.io2d

import com.github.shchuko.math.*

class CanvasProcessor {
    var maxX: Double = 0.0
        private set
    var minX: Double = 0.0
        private set
    var maxY: Double = 0.0
        private set
    var minY: Double = 0.0
        private set
    var isUsed = false
        private set

    init {
        reset()
    }

    fun processLine(line: Line2D) {
        maxX = maxOf(maxX, line.lineStart().x(), line.lineEnd().x())
        minX = minOf(minX, line.lineStart().x(), line.lineEnd().x())

        maxY = maxOf(maxY, line.lineStart().y(), line.lineEnd().y())
        minY = minOf(minY, line.lineStart().y(), line.lineEnd().y())
        isUsed = true
    }

    fun processCircle(circle: Circle2D) {
        assert(circle.isValidCircle())

        val center = circle.circleCenter()
        val radius = circle.circleRadius()
        maxX = maxOf(maxX, center.x() + radius, center.x() - radius)
        minX = minOf(minX, center.x() + radius, center.x() - radius)

        maxY = maxOf(maxY, center.y() + radius, center.y() - radius)
        minY = minOf(minY, center.y() + radius, center.y() - radius)
        isUsed = true
    }

    fun processCircleArc(arc: CircleArc2D) {
        assert(arc.arcCircle().isValidCircle())
        // TODO implement proper arc processing
        processCircle(arc.arcCircle())
        isUsed = true
    }

    fun getCanvasSize(padX: Double = 0.0, padY: Double = padX): Pair<Double, Double> {
        require(padX >= 0.0) { "padX must not be negative, got '$padX'" }
        require(padY >= 0.0) { "padY must not be negative, got '$padY'" }
        var xSize = 0.0
        var ySize = 0.0

        if (isUsed) {
            xSize = maxX - minX
            ySize = maxY - minY
        }

        assert(xSize >= 0.0)
        assert(ySize >= 0.0)
        return Pair(xSize + padX * 2.0, ySize + padY * 2.0)
    }

    fun getMoveVecToPositive(padX: Double = 0.0, padY: Double = padX): Vector2D {
        require(padX >= 0.0) { "padX must not be negative, got '$padX'" }
        require(padY >= 0.0) { "padY must not be negative, got '$padY'" }

        val dX = if (!isUsed || minX >= 0.0) 0.0 else -minX
        val dY = if (!isUsed || minY >= 0.0) 0.0 else -minY
        assert(dX >= 0.0)
        assert(dY >= 0.0)

        return Vector2D(dX + padX, dY + padY)
    }

    fun reset() {
        maxX = Double.MIN_VALUE
        minX = Double.MAX_VALUE
        maxY = Double.MIN_VALUE
        minY = Double.MAX_VALUE
        isUsed = false
    }
}
