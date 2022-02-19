package com.github.shchuko.io2d

import com.github.shchuko.math.Circle2D
import com.github.shchuko.math.CircleArc2D
import com.github.shchuko.math.Line2D

interface MutableScene2D : Scene2D {
    fun addLine(line: Line2D)
    fun addCircle(circle: Circle2D)
    fun addCircleArc(arc: CircleArc2D)
    fun loadFrom(scene2D: Scene2D)
    fun reset()
}
