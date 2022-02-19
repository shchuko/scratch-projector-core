package com.github.shchuko.io2d.impl

import com.github.shchuko.math.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class MutableScene2DImplTest {

    private var blankScene = MutableScene2DImpl()
    private var filledSceneFirst = MutableScene2DImpl()
    private var filledSceneSecond = MutableScene2DImpl()

    @BeforeEach
    fun setUp() {
        blankScene = MutableScene2DImpl()

        filledSceneFirst = MutableScene2DImpl()
        TestData.lines.forEach { filledSceneFirst.addLine(it) }
        TestData.circles.forEach { filledSceneFirst.addCircle(it) }
        TestData.circleArcs.forEach { filledSceneFirst.addCircleArc(it) }

        filledSceneSecond = MutableScene2DImpl()
        TestData.linesSecond.forEach { filledSceneSecond.addLine(it) }
        TestData.circlesSecond.forEach { filledSceneSecond.addCircle(it) }
        TestData.circleArcsSecond.forEach { filledSceneSecond.addCircleArc(it) }
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun `test getLines`() {
        assertEquals(TestData.lines.size, filledSceneFirst.lines.size)
        for (line in TestData.lines) {
            assertEquals(1, filledSceneFirst.lines.filter { it === line }.size)
        }

        assertEquals(TestData.linesSecond.size, filledSceneSecond.lines.size)
        for (line in TestData.linesSecond) {
            assertEquals(1, filledSceneSecond.lines.filter { it === line }.size)
        }
    }

    @Test
    fun `test getCircles`() {
        assertEquals(TestData.circles.size, filledSceneFirst.circles.size)
        for (circle in TestData.circlesSecond) {
            assertEquals(1, filledSceneFirst.circles.filter { it === circle }.size)
        }

        assertEquals(TestData.circlesSecond.size, filledSceneSecond.circles.size)
        for (circle in TestData.circlesSecond) {
            assertEquals(1, filledSceneSecond.circles.filter { it === circle }.size)
        }
    }

    @Test
    fun `test getCircleArcs`() {
        assertEquals(TestData.circleArcs.size, filledSceneFirst.circleArcs.size)
        for (arc in TestData.circleArcs) {
            assertEquals(1, filledSceneFirst.circleArcs.filter { it === arc }.size)
        }

        assertEquals(TestData.circleArcsSecond.size, filledSceneSecond.circleArcs.size)
        for (arc in TestData.circleArcsSecond) {
            assertEquals(1, filledSceneSecond.circleArcs.filter { it === arc }.size)
        }
    }

    @Test
    fun `test getCanvasProcessor`() {
        assertEquals(TestData.emptyCanvasProcessor.maxX, blankScene.canvasProcessor.maxX)
        assertEquals(TestData.emptyCanvasProcessor.minX, blankScene.canvasProcessor.minX)
        assertEquals(TestData.emptyCanvasProcessor.maxY, blankScene.canvasProcessor.maxY)
        assertEquals(TestData.emptyCanvasProcessor.minY, blankScene.canvasProcessor.minY)
        assertEquals(TestData.emptyCanvasProcessor.isEmpty(), blankScene.canvasProcessor.isEmpty())

        assertEquals(TestData.canvasProcessor.maxX, filledSceneFirst.canvasProcessor.maxX)
        assertEquals(TestData.canvasProcessor.minX, filledSceneFirst.canvasProcessor.minX)
        assertEquals(TestData.canvasProcessor.maxY, filledSceneFirst.canvasProcessor.maxY)
        assertEquals(TestData.canvasProcessor.minY, filledSceneFirst.canvasProcessor.minY)
        assertEquals(TestData.canvasProcessor.isEmpty(), filledSceneFirst.canvasProcessor.isEmpty())

        assertEquals(TestData.canvasProcessorSecond.maxX, filledSceneSecond.canvasProcessor.maxX)
        assertEquals(TestData.canvasProcessorSecond.minX, filledSceneSecond.canvasProcessor.minX)
        assertEquals(TestData.canvasProcessorSecond.maxY, filledSceneSecond.canvasProcessor.maxY)
        assertEquals(TestData.canvasProcessorSecond.minY, filledSceneSecond.canvasProcessor.minY)
        assertEquals(TestData.canvasProcessorSecond.isEmpty(), filledSceneSecond.canvasProcessor.isEmpty())
    }

    @Test
    fun `test addLine throws on invalid line`() {
        val invalidLine = Line2D(Point2D(Double.NaN, 1.0), Point2D(1.0, 1.0))
        assertFalse(invalidLine.isValidLine())
        assertThrows<IllegalArgumentException> {
            blankScene.addLine(invalidLine)
        }
    }

    @Test
    fun `test addCircle throws on invalid circle`() {
        val invalidCircle = Circle2D(Point2D(Double.NaN, 1.0), Double.NaN)
        assertFalse(invalidCircle.isValidCircle())
        assertThrows<IllegalArgumentException> {
            blankScene.addCircle(invalidCircle)
        }
    }

    @Test
    fun `test addCircleArc throws on invalid circle arc`() {
        val invalidCircle = Circle2D(Point2D(1.5, 1.0), Double.NaN)
        val invalidArc = CircleArc2D(invalidCircle, Point2D(1.0, 1.0) to Point2D(1.5, 1.5), false)
        assertFalse(invalidArc.isValidCircleArc())
        assertThrows<IllegalArgumentException> {
            blankScene.addCircleArc(invalidArc)
        }
    }

    @Test
    fun loadFrom() {

    }


    @Test
    fun `test is empty false contains line`() {
        assertTrue(blankScene.isEmpty())
        blankScene.addLine(TestData.lines[0])
        assertFalse(blankScene.isEmpty())
    }

    @Test
    fun `test is empty false contains cicles`() {
        assertTrue(blankScene.isEmpty())
        blankScene.addCircle(TestData.circles[0])
        assertFalse(blankScene.isEmpty())
    }

    @Test
    fun `test is empty false contains circle arcs`() {
        assertTrue(blankScene.isEmpty())
        blankScene.addCircleArc(TestData.circleArcs[0])
        assertFalse(blankScene.isEmpty())
    }

    @Test
    fun `test is empty all`() {
        assertTrue(blankScene.isEmpty())
        assertFalse(filledSceneFirst.isEmpty())
        assertFalse(filledSceneSecond.isEmpty())
    }

    @Test
    fun `test reset`() {
        assertFalse(filledSceneFirst.isEmpty())
        filledSceneFirst.reset()
        assertTrue(filledSceneFirst.isEmpty())
    }

    object TestData {
        val emptyCanvasProcessor = MutableCanvasProcessorImpl()

        val lines = listOf(
            Line2D(Point2D(1.0, 1.5), Point2D(-1.4, 5.6)),
            Line2D(Point2D(10.0, -12.5), Point2D(1.4, -5.6))
        )
        val circles = listOf<Circle2D>(
            /* TODO fill */
        )
        val circleArcs = listOf<CircleArc2D>(
            /* TODO fill */
        )
        val canvasProcessor = MutableCanvasProcessorImpl()

        val linesSecond = listOf(
            Line2D(Point2D(-1.0, -1.5), Point2D(-1.4, -5.6)),
            Line2D(Point2D(-10.0, -12.5), Point2D(-1.4, -5.6)),
            Line2D(Point2D(15.0, -12.5), Point2D(10.4, -5.6))
        )
        val circlesSecond = listOf<Circle2D>(
            /* TODO fill */
        )
        val circleArcsSecond = listOf<CircleArc2D>(
            /* TODO fill */
        )
        val canvasProcessorSecond = MutableCanvasProcessorImpl()

        init {
            lines.forEach { canvasProcessor.processLine(it) }
            circles.forEach { canvasProcessor.processCircle(it) }
            circleArcs.forEach { canvasProcessor.processCircleArc(it) }

            linesSecond.forEach { canvasProcessorSecond.processLine(it) }
            circlesSecond.forEach { canvasProcessorSecond.processCircle(it) }
            circleArcsSecond.forEach { canvasProcessorSecond.processCircleArc(it) }
        }
    }
}
