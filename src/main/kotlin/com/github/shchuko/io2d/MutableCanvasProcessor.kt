package com.github.shchuko.io2d

import com.github.shchuko.math.Circle2D
import com.github.shchuko.math.CircleArc2D
import com.github.shchuko.math.Line2D

interface MutableCanvasProcessor : CanvasProcessor {
    fun processLine(line: Line2D)
    fun processCircle(circle: Circle2D)
    fun processCircleArc(arc: CircleArc2D)
    fun loadFrom(other: CanvasProcessor)
    fun reset()
}