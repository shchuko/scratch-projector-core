package com.github.shchuko.io2d.impl

import com.github.shchuko.io2d.CanvasProcessor
import com.github.shchuko.io2d.MutableCanvasProcessor
import com.github.shchuko.math.*

class MutableCanvasProcessorImpl : CanvasProcessorCommonGetImpl(), MutableCanvasProcessor {
    private var maxXBacking: Double = 0.0
    private var minXBacking: Double = 0.0
    private var maxYBacking: Double = 0.0
    private var minYBacking: Double = 0.0
    private var emptyFlag = true

    init {
        reset()
    }

    override val maxX: Double
        get() = maxXBacking
    override val minX: Double
        get() = minXBacking
    override val maxY: Double
        get() = maxYBacking
    override val minY: Double
        get() = minYBacking

    override fun isEmpty(): Boolean = emptyFlag

    override fun processLine(line: Line2D) {
        require(line.isValidLine()) { "Invalid line: $line" }

        maxXBacking = maxOf(maxX, line.lineStart().x(), line.lineEnd().x())
        minXBacking = minOf(minX, line.lineStart().x(), line.lineEnd().x())

        maxYBacking = maxOf(maxY, line.lineStart().y(), line.lineEnd().y())
        minYBacking = minOf(minY, line.lineStart().y(), line.lineEnd().y())
        emptyFlag = false
    }

    override fun processCircle(circle: Circle2D) {
        require(circle.isValidCircle()) { "Invalid circle: $circle" }

        val center = circle.circleCenter()
        val radius = circle.circleRadius()
        maxXBacking = maxOf(maxX, center.x() + radius, center.x() - radius)
        minXBacking = minOf(minX, center.x() + radius, center.x() - radius)

        maxYBacking = maxOf(maxY, center.y() + radius, center.y() - radius)
        minYBacking = minOf(minY, center.y() + radius, center.y() - radius)
        emptyFlag = false
    }

    override fun processCircleArc(arc: CircleArc2D) {
        require(arc.isValidCircleArc()) { "Invalid circle arc: $arc" }
        // TODO implement proper arc processing
        processCircle(arc.arcCircle())
        emptyFlag = false
    }

    override fun loadFrom(other: CanvasProcessor) {
        if (other.isEmpty()) {
            return
        }

        maxXBacking = maxOf(maxX, other.maxX)
        minXBacking = minOf(minX, other.minX)
        maxYBacking = maxOf(maxY, other.maxY)
        minYBacking = minOf(minY, other.minY)
        emptyFlag = false
    }

    override fun reset() {
        maxXBacking = Double.MIN_VALUE
        minXBacking = Double.MAX_VALUE
        maxYBacking = Double.MIN_VALUE
        minYBacking = Double.MAX_VALUE
        emptyFlag = true
    }
}
