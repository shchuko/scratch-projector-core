package com.github.shchuko.io2d

import com.github.shchuko.math.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class CanvasProcessorTest {

    @Test
    fun `test getCanvasSize throws on illegal args`() {
        val processor = CanvasProcessor()
        processor.getCanvasSize(0.0, 0.0)

        assertThrows<IllegalArgumentException> {
            processor.getCanvasSize(-1.0, 0.0)
        }

        assertThrows<IllegalArgumentException> {
            processor.getCanvasSize(0.0, -1.0)
        }
    }

    @Test
    fun `test getCanvasSize on no objects returns zero`() {
        val processor = CanvasProcessor()
        val expected = Pair(0.0, 0.0)
        val actual = processor.getCanvasSize()
        assertPairEq(expected, actual)
    }

    @Test
    fun `test getCanvasSize on no objects padding append`() {
        val processor = CanvasProcessor()

        val padX = 5.5
        val padY = 7.5

        val expected = Pair(padX * 2, padY * 2)
        val actual = processor.getCanvasSize(padX, padY)
        assertPairEq(expected, actual)
    }

    @Test
    fun `test getCanvasSize with objects`() {
        val processor = CanvasProcessor()

        val a = Point2D(1.1, -5.7)
        val b = Point2D(2.5, 8.1)
        val line = Line2D(a, b)

        val maxX = 2.5
        val minX = 1.1
        val maxY = 8.1
        val minY = -5.7

        processor.processLine(line)
        validate(processor, maxX, minX, maxY, minY)
    }

    @Test
    fun `test getCanvasSize with objects padding append`() {
        val processor = CanvasProcessor()

        val a = Point2D(1.1, -5.7)
        val b = Point2D(2.5, 8.1)
        val line = Line2D(a, b)

        val maxX = 2.5
        val minX = 1.1
        val maxY = 8.1
        val minY = -5.7

        val padX = 0.7
        val padY = 6.7

        processor.processLine(line)
        validate(processor, maxX, minX, maxY, minY, padX, padY)
    }

    @Test
    fun `test getMoveVecToPositive throws on illegal args`() {
        val processor = CanvasProcessor()
        processor.getMoveVecToPositive(0.0, 0.0)

        assertThrows<IllegalArgumentException> {
            processor.getMoveVecToPositive(-1.0, 0.0)
        }

        assertThrows<IllegalArgumentException> {
            processor.getMoveVecToPositive(0.0, -1.0)
        }
    }

    @Test
    fun `test getMoveVecToPositive no objects returns zero`() {
        val processor = CanvasProcessor()
        assertPairEq(Vector2D(0.0, 0.0), processor.getMoveVecToPositive())
    }

    @Test
    fun `test getMoveVecToPositive on no objects padding append`() {
        val processor = CanvasProcessor()

        val padX = 5.5
        val padY = 7.5

        val actual = processor.getMoveVecToPositive(padX, padY)
        assertPairEq(Vector2D(padX, padY), actual)
    }

    @Test
    fun `test getMoveVecToPositive with objects`() {
        val processor = CanvasProcessor()

        val a = Point2D(1.1, -5.7)
        val b = Point2D(2.5, 8.1)
        val line = Line2D(a, b)

        val maxX = 2.5
        val minX = 1.1
        val maxY = 8.1
        val minY = -5.7

        processor.processLine(line)
        validate(processor, maxX, minX, maxY, minY)
    }

    @Test
    fun `test getMoveVecToPositive with objects and padding`() {
        val processor = CanvasProcessor()

        val a = Point2D(1.1, -5.7)
        val b = Point2D(2.5, 8.1)
        val line = Line2D(a, b)

        val maxX = 2.5
        val minX = 1.1
        val maxY = 8.1
        val minY = -5.7

        val padX = 0.7
        val padY = 6.7

        processor.processLine(line)
        validate(processor, maxX, minX, maxY, minY, padX, padY)
    }

    @Test
    fun `test processes line correctly`() {
        val processor = CanvasProcessor()

        val a = Point2D(1.1, -5.7)
        val b = Point2D(2.5, 8.1)
        val line = Line2D(a, b)

        val maxX = 2.5
        val minX = 1.1
        val maxY = 8.1
        val minY = -5.7

        processor.processLine(line)
        validate(processor, maxX, minX, maxY, minY)
    }

    @Test
    fun `test reset`() {
        val processor = CanvasProcessor()

        val a1 = Point2D(1.1, -5.7)
        val b1 = Point2D(2.5, 8.1)
        val line1 = Line2D(a1, b1)

        val maxX1 = 2.5
        val minX1 = 1.1
        val maxY1 = 8.1
        val minY1 = -5.7

        assertFalse(processor.isUsed)
        processor.processLine(line1)
        assertTrue(processor.isUsed)
        validate(processor, maxX1, minX1, maxY1, minY1)

        processor.reset()

        val a2 = Point2D(2.0, 7.05)
        val b2 = Point2D(1.5, 1.2)
        val line2 = Line2D(a2, b2)

        val maxX2 = 2.0
        val minX2 = 1.5
        val maxY2 = 7.05
        val minY2 = 1.2

        assertFalse(processor.isUsed)
        processor.processLine(line2)
        assertTrue(processor.isUsed)
        validate(processor, maxX2, minX2, maxY2, minY2)
    }

    @Test
    fun `test processes two lines correctly`() {
        val processor = CanvasProcessor()

        val a1 = Point2D(1.1, -5.7)
        val b1 = Point2D(2.5, 8.1)
        val line = Line2D(a1, b1)

        val a2 = Point2D(2.0, 7.05)
        val b2 = Point2D(1.5, 1.2)
        val line2 = Line2D(a2, b2)

        val maxX = 2.5
        val minX = 1.1
        val maxY = 8.1
        val minY = -5.7

        processor.processLine(line)
        processor.processLine(line2)
        validate(processor, maxX, minX, maxY, minY)

        processor.reset()

        processor.processLine(line2)
        processor.processLine(line)
        validate(processor, maxX, minX, maxY, minY)
    }

    @Test
    fun `test processes circle correctly`() {
        val processor = CanvasProcessor()

        val center = Point2D(1.5, -2.2)
        val radius = 2.5
        val circle = Circle2D(center, radius)

        val maxX = 4.0
        val minX = -1.0
        val maxY = 0.3
        val minY = -4.7

        processor.processCircle(circle)
        validate(processor, maxX, minX, maxY, minY)
    }

    @Test
    fun `test processes two circles correctly`() {
        val processor = CanvasProcessor()

        val center1 = Point2D(1.5, -2.2)
        val radius1 = 2.5
        val circle1 = Circle2D(center1, radius1)

        val center2 = Point2D(-1.5, 2.2)
        val radius2 = 0.5
        val circle2 = Circle2D(center2, radius2)

        val maxX = 4.0
        val minX = -2.0
        val maxY = 2.7
        val minY = -4.7

        processor.processCircle(circle1)
        processor.processCircle(circle2)
        validate(processor, maxX, minX, maxY, minY)
        processor.reset()

        processor.processCircle(circle2)
        processor.processCircle(circle1)
        validate(processor, maxX, minX, maxY, minY)
    }

    @Test
    fun `test processes circle arc correctly`() {
        val processor = CanvasProcessor()
        // TODO rewrite the test when circle arc processing is implemented property
        val unusedA = Point2D(0.0, 0.0)
        val unusedB = Point2D(0.0, 0.0)

        val center = Point2D(1.5, -2.2)
        val radius = 2.5
        val circle = Circle2D(center, radius)

        val arc = CircleArc2D(circle, Pair(unusedA, unusedB), false)

        val maxX = 4.0
        val minX = -1.0
        val maxY = 0.3
        val minY = -4.7

        processor.processCircleArc(arc)
        validate(processor, maxX, minX, maxY, minY)
    }

    @Test
    fun `test processes two circle arcs correctly`() {
        val processor = CanvasProcessor()

        // TODO rewrite the test when circle arc processing is implemented property
        val unusedA = Point2D(0.0, 0.0)
        val unusedB = Point2D(0.0, 0.0)

        val center1 = Point2D(1.5, -2.2)
        val radius1 = 2.5
        val circle1 = Circle2D(center1, radius1)
        val arc1 = CircleArc2D(circle1, Pair(unusedA, unusedB), false)

        val center2 = Point2D(-1.5, 2.2)
        val radius2 = 0.5
        val circle2 = Circle2D(center2, radius2)
        val arc2 = CircleArc2D(circle2, Pair(unusedA, unusedB), false)

        val maxX = 4.0
        val minX = -2.0
        val maxY = 2.7
        val minY = -4.7

        processor.processCircleArc(arc1)
        processor.processCircleArc(arc2)
        validate(processor, maxX, minX, maxY, minY)
        processor.reset()

        processor.processCircleArc(arc2)
        processor.processCircleArc(arc1)
        validate(processor, maxX, minX, maxY, minY)
    }

    @ParameterizedTest
    @CsvSource(
        "LINE, CIRCLE, ARC",
        "LINE, ARC, CIRCLE",
        "CIRCLE, LINE, ARC",
        "CIRCLE, ARC, LINE",
        "ARC, CIRCLE, LINE",
        "ARC, LINE, CIRCLE",
    )
    fun `test processes line circle and arc correctly`(first: String, second: String, third: String) {
        val processor = CanvasProcessor()

        val a = Point2D(1.1, -5.7)
        val b = Point2D(2.5, 0.1)
        val line = Line2D(a, b)

        val circleCenter = Point2D(1.5, -2.2)
        val circleRadius = 2.5
        val circle = Circle2D(circleCenter, circleRadius)

        // TODO rewrite the test when circle arc processing is implemented property
        val unusedA = Point2D(0.0, 0.0)
        val unusedB = Point2D(0.0, 0.0)

        val arcCenter = Point2D(-1.5, 2.2)
        val arcRadius = 0.5
        val arcCircle = Circle2D(arcCenter, arcRadius)
        val arc = CircleArc2D(arcCircle, Pair(unusedA, unusedB), false)

        val maxX = 4.0
        val minX = -2.0
        val maxY = 2.7
        val minY = -5.7

        when (first) {
            "LINE" -> processor.processLine(line)
            "CIRCLE" -> processor.processCircle(circle)
            "ARC" -> processor.processCircleArc(arc)
        }
        when (second) {
            "LINE" -> processor.processLine(line)
            "CIRCLE" -> processor.processCircle(circle)
            "ARC" -> processor.processCircleArc(arc)
        }
        when (third) {
            "LINE" -> processor.processLine(line)
            "CIRCLE" -> processor.processCircle(circle)
            "ARC" -> processor.processCircleArc(arc)
        }
        validate(processor, maxX, minX, maxY, minY)
        processor.reset()

    }

    private fun validate(
        processor: CanvasProcessor,
        maxX: Double,
        minX: Double,
        maxY: Double,
        minY: Double,
        padX: Double? = null,
        padY: Double? = null
    ) {
        require(padX == null && padY == null || padX != null && padY != null)
        require(padX == null || padX >= 0.0)
        require(padY == null || padY >= 0.0)
        require(maxX >= minX)
        require(maxY >= minY)

        assertDoubleEq(maxX, processor.maxX) { "maxX: Expected $maxX but was: ${processor.maxX}" }
        assertDoubleEq(minX, processor.minX) { "minX: Expected $minX but was: ${processor.minX}" }
        assertDoubleEq(maxY, processor.maxY) { "maxY: Expected $maxY but was: ${processor.maxY}" }
        assertDoubleEq(minY, processor.minY) { "minY: Expected $minY but was: ${processor.minY}" }

        val expectedCanvas = Pair(
            maxX - minX + (padX ?: 0.0) * 2,
            maxY - minY + (padY ?: 0.0) * 2
        )
        val actualCanvas = if (padX == null || padY == null) {
            processor.getCanvasSize()
        } else {
            processor.getCanvasSize(padX, padY)
        }

        assertPairEq(expectedCanvas, actualCanvas) { "getCanvasSize: Expected $expectedCanvas but was: $actualCanvas" }

        val dX = if (minX < 0.0) -minX else 0.0
        val dY = if (minY < 0.0) -minY else 0.0
        val expectedVec = Vector2D(
            dX + (padX ?: 0.0),
            dY + (padY ?: 0.0)
        )
        val actualVec = if (padX == null || padY == null) {
            processor.getMoveVecToPositive()
        } else {
            processor.getMoveVecToPositive(padX, padY)
        }
        assertPairEq(expectedVec, actualVec) { "getMoveVecToPositive: Expected $expectedVec but was: $actualVec" }
    }

    private inline fun assertDoubleEq(
        expected: Double,
        actual: Double,
        lazyMessage: () -> String = { "Expected $expected but was: $actual" }
    ) = assertTrue(expected eq actual, lazyMessage())


    private inline fun assertPairEq(
        expected: Pair<Double, Double>,
        actual: Pair<Double, Double>,
        lazyMessage: () -> String = { "Expected $expected but was: $actual" }
    ) = assertTrue(expected eq actual, lazyMessage())

}
