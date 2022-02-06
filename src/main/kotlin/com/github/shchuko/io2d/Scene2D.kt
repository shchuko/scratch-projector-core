package com.github.shchuko.io2d

import com.github.shchuko.math.*

class Scene2D {
    val lines: List<Line2D> = mutableListOf()
    val circles: List<Circle2D> = mutableListOf()
    val circleArcs: List<CircleArc2D> = mutableListOf()
    private val canvasProcessor: CanvasProcessor = CanvasProcessor()

    fun addLine(line: Line2D) {
        require(line.isValidLine()) { "Invalid line: $line" }
        (lines as MutableList).add(line)
        canvasProcessor.processLine(line)
    }

    fun addCircle(circle: Circle2D) {
        require(circle.isValidCircle()) { "Invalid circle: $circle" }
        (circles as MutableList).add(circle)
        canvasProcessor.processCircle(circle)
    }

    fun addCircleArc(arc: CircleArc2D) {
        require(arc.isValidCircleArc()) { "Invalid circle arc: $arc" }
        (circleArcs as MutableList).add(arc)
        canvasProcessor.processCircleArc(arc)
    }

    fun getCanvasProperties(padSize: Double = 0.0): CanvasProperties {
        require(padSize.isFinite() && padSize >= 0.0)
        return CanvasProperties(
            canvasProcessor.getMoveVecToPositive(padSize),
            canvasProcessor.getCanvasSize(padSize)
        )
    }

    data class CanvasProperties(val globalMoveVec: Vector2D, val canvasSize: Pair<Double, Double>)
}

