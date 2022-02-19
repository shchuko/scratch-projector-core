package com.github.shchuko.io2d.impl

import com.github.shchuko.io2d.CanvasProcessor
import com.github.shchuko.io2d.MutableCanvasProcessor
import com.github.shchuko.io2d.MutableScene2D
import com.github.shchuko.io2d.Scene2D
import com.github.shchuko.math.Circle2D
import com.github.shchuko.math.CircleArc2D
import com.github.shchuko.math.Line2D

open class MutableScene2DImpl : MutableScene2D {
    final override val lines: List<Line2D> = mutableListOf()
    final override val circles: List<Circle2D> = mutableListOf()
    final override val circleArcs: List<CircleArc2D> = mutableListOf()
    final override val canvasProcessor: CanvasProcessor = MutableCanvasProcessorImpl()

    private val linesProxy = lines as MutableList<Line2D>
    private val circlesProxy = circles as MutableList<Circle2D>
    private val circleArcsProxy = circleArcs as MutableList<CircleArc2D>
    private val canvasProcessorProxy = canvasProcessor as MutableCanvasProcessor

    override fun addLine(line: Line2D) {
        canvasProcessorProxy.processLine(line)
        linesProxy.add(line)
    }

    override fun addCircle(circle: Circle2D) {
        canvasProcessorProxy.processCircle(circle)
        circlesProxy.add(circle)
    }

    override fun addCircleArc(arc: CircleArc2D) {
        canvasProcessorProxy.processCircleArc(arc)
        circleArcsProxy.add(arc)
    }

    override fun loadFrom(scene2D: Scene2D) {
        linesProxy.addAll(scene2D.lines)
        circlesProxy.addAll(scene2D.circles)
        circleArcsProxy.addAll(scene2D.circleArcs)
        canvasProcessorProxy.loadFrom(scene2D.canvasProcessor)
    }

    override fun reset() {
        linesProxy.clear()
        circlesProxy.clear()
        circleArcsProxy.clear()
        canvasProcessorProxy.reset()
    }

    override fun isEmpty(): Boolean =
        lines.isEmpty() && circles.isEmpty() && circleArcs.isEmpty() && canvasProcessorProxy.isEmpty()
}
