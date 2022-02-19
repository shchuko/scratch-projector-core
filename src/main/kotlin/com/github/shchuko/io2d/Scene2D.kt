package com.github.shchuko.io2d

import com.github.shchuko.math.Circle2D
import com.github.shchuko.math.CircleArc2D
import com.github.shchuko.math.Line2D

interface Scene2D {
    val lines: List<Line2D>
    val circles: List<Circle2D>
    val circleArcs: List<CircleArc2D>
    val canvasProcessor: CanvasProcessor

    fun isEmpty(): Boolean
}
