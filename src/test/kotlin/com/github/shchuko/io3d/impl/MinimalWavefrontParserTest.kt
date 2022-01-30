package com.github.shchuko.io3d.impl

import com.github.shchuko.io3d.Scene3D
import com.github.shchuko.io3d.impl.exception.WavefrontParseException
import com.github.shchuko.math.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class MinimalWavefrontParserTest {

    @Test
    fun `test valid wavefront full`() {
        val parser = MinimalWavefrontParser(GOOD_WAVEFRONT.byteInputStream(), true)
        parser.parse()

        val actual = parser.parsedScene
        validateScene(ValidWavefrontScene, actual, false)
    }

    @Test
    fun `test valid wavefront no vt`() {
        val parser = MinimalWavefrontParser(GOOD_WAVEFRONT_NO_VT.byteInputStream(), true)
        parser.parse()

        val actual = parser.parsedScene
        validateScene(ValidWavefrontScene, actual, false)
    }

    @Test
    fun `test valid wavefront no vn`() {
        val parser = MinimalWavefrontParser(GOOD_WAVEFRONT_NO_VN.byteInputStream(), true)
        parser.parse()

        val actual = parser.parsedScene
        validateScene(ValidWavefrontScene, actual, true)
    }

    @Test
    fun `test valid wavefront no vt no vn`() {
        val parser = MinimalWavefrontParser(GOOD_WAVEFRONT_NO_VT_NO_VN.byteInputStream(), true)
        parser.parse()
        val actual = parser.parsedScene
        validateScene(ValidWavefrontScene, actual, true)
    }

    @Test
    fun `test wavefront invalid normals throws`() {
        // Ignore invalid normals
        MinimalWavefrontParser(GOOD_WAVEFRONT_INVALID_NORMALS.byteInputStream(), false).parse()

        // Validate normals
        assertThrows<WavefrontParseException> {
            MinimalWavefrontParser(GOOD_WAVEFRONT_INVALID_NORMALS.byteInputStream(), true).parse()
        }
    }

    @Test
    fun `test malformed wavefront bad v coord`() {
        assertThrows<WavefrontParseException> {
            MinimalWavefrontParser(MALFORMED_WAVEFRONT_BAD_V_COORDINATE.byteInputStream(), false).parse()
        }
    }

    @Test
    fun `test malformed wavefront bad n coord`() {
        assertThrows<WavefrontParseException> {
            MinimalWavefrontParser(MALFORMED_WAVEFRONT_BAD_N_COORDINATE.byteInputStream(), false).parse()
        }
    }

    @Test
    fun `test malformed wavefront bad v coord number`() {
        assertThrows<WavefrontParseException> {
            MinimalWavefrontParser(MALFORMED_WAVEFRONT_WRONG_V_COORDINATES_NUMBER.byteInputStream(), false).parse()
        }
    }

    @Test
    fun `test malformed wavefront bad v coord number 2`() {
        assertThrows<WavefrontParseException> {
            MinimalWavefrontParser(
                MALFORMED_WAVEFRONT_WRONG_V_COORDINATES_NUMBER_2.byteInputStream(),
                false
            ).parse()
        }
    }

    @Test
    fun `test malformed wavefront bad n coord number`() {
        assertThrows<WavefrontParseException> {
            MinimalWavefrontParser(MALFORMED_WAVEFRONT_WRONG_N_COORDINATES_NUMBER.byteInputStream(), false).parse()
        }
    }

    @Test
    fun `test malformed wavefront bad n coord number 2`() {
        assertThrows<WavefrontParseException> {
            MinimalWavefrontParser(
                MALFORMED_WAVEFRONT_WRONG_N_COORDINATES_NUMBER_2.byteInputStream(),
                false
            ).parse()
        }
    }

    @Test
    fun `test malformed wavefront bad face normal index`() {
        assertThrows<WavefrontParseException> {
            MinimalWavefrontParser(MALFORMED_WAVEFRONT_BAD_FACE_NORMAL_INDEX.byteInputStream(), false).parse()
        }
    }

    @Test
    fun `test malformed wavefront face normal index out of range`() {
        assertThrows<WavefrontParseException> {
            MinimalWavefrontParser(
                MALFORMED_WAVEFRONT_FACE_NORMAL_INDEX_OUT_OF_RANGE.byteInputStream(),
                false
            ).parse()
        }
    }

    @Test
    fun `test malformed wavefront face vertex index out of range`() {
        assertThrows<WavefrontParseException> {
            MinimalWavefrontParser(
                MALFORMED_WAVEFRONT_FACE_VERTEX_INDEX_OUT_OF_RANGE.byteInputStream(),
                false
            ).parse()
        }
    }

    @Test
    fun `test malformed wavefront bad face vertex index`() {
        assertThrows<WavefrontParseException> {
            MinimalWavefrontParser(MALFORMED_WAVEFRONT_BAD_FACE_VERTEX_INDEX.byteInputStream(), false).parse()
        }
    }

    @Test
    fun `test malformed wavefront bad face vertex number`() {
        assertThrows<WavefrontParseException> {
            MinimalWavefrontParser(MALFORMED_WAVEFRONT_WRONG_FACE_VERTEX_NUMBER.byteInputStream(), false).parse()
        }
    }

    private fun validateScene(
        expectedScene: Scene3D,
        actualScene: Scene3D,
        normalsCalculated: Boolean
    ) {
        assertEquals(expectedScene.vertices.size, actualScene.vertices.size)
        for (i in expectedScene.vertices.indices) {
            val expected = expectedScene.vertices[i]
            val actual = actualScene.vertices[i]
            assertTrue(expected eq actual)
        }

        if (!normalsCalculated) {
            assertEquals(expectedScene.normals.size, actualScene.normals.size)
            for (i in expectedScene.normals.indices) {
                val expected = expectedScene.normals[i]
                val actual = actualScene.normals[i]
                assertTrue(expected eq actual)
            }
        }

        assertEquals(expectedScene.faces.size, actualScene.faces.size)
        for (i in expectedScene.faces.indices) {
            val expectedFace = expectedScene.faces[i]
            val actualFace = actualScene.faces[i]

            val expectedFaceVertices = expectedFace.vertices
            val actualFaceVertices = actualFace.vertices

            assertEquals(expectedFaceVertices.size, actualFaceVertices.size)
            for (j in expectedFaceVertices.indices) {
                assertTrue(expectedFaceVertices[j] eq actualFaceVertices[j])
            }

            val expectedFaceVerticesIndices = expectedFace.verticesIndexes.toIntArray()
            val actualFaceVerticesIndices = actualFace.verticesIndexes.toIntArray()
            assertArrayEquals(expectedFaceVerticesIndices, actualFaceVerticesIndices)

            if (normalsCalculated) {
                assertTrue(actualFace.normalIndex < 0)
            }

            val expectedFaceNormal = expectedFace.normal
            val actualFaceNormal = actualFace.normal
            assertTrue(expectedFaceNormal eq actualFaceNormal)
        }
    }

    companion object {
        object ValidWavefrontScene : Scene3D() {
            override val vertices: List<Point3D> = listOf(
                Point3D(1.232927, 1.361101, 2.117226),
                Point3D(1.51486, 2.128918, -1.014031),
                Point3D(1.678257, -1.76125, 1.391691),
                Point3D(1.960191, -0.993434, -1.739567),
                Point3D(-1.960191, 0.993434, 1.739567),
                Point3D(-1.678257, 1.76125, -1.391691),
                Point3D(-1.51486, -2.128918, 1.014031),
                Point3D(-1.232927, -1.361101, -2.117226),
            )

            override val normals: List<Vector3D> = listOf(
                Vector3D(-0.0871, -0.2372, 0.9675),
                Vector3D(0.1376, -0.9648, -0.2242),
                Vector3D(-0.9866, -0.1136, -0.1167),
                Vector3D(0.0871, 0.2372, -0.9675),
                Vector3D(0.9866, 0.1136, 0.1167),
                Vector3D(-0.1376, 0.9648, 0.2242),
            )

            override val faces: List<Face> = listOf(
                listOf(0, 0, 4, 6, 2), // [normal_index] [v1_index] [v2_index] ...
                listOf(1, 3, 2, 6, 7),
                listOf(2, 7, 6, 4, 5),
                listOf(3, 5, 1, 3, 7),
                listOf(4, 1, 0, 2, 3),
                listOf(5, 5, 4, 0, 1),
            ).map { Face(it.drop(1), it[0]) }.toList()
        }

        val GOOD_WAVEFRONT = """
            # Some comments here
            o Cube
            # Vertices
            v 1.232927 2.117226 -1.361101
            v 1.514860 -1.014031 -2.128918
            v 1.678257 1.391691 1.761250
            v 1.960191 -1.739567 0.993434
            v -1.960191 1.739567 -0.993434
            v -1.678257 -1.391691 -1.761250
            v -1.514860 1.014031 2.128918
            v -1.232927 -2.117226 1.361101
            
            # Unused in parser
            vt 0.625000 0.500000
            vt 0.875000 0.500000
            vt 0.875000 0.750000
            vt 0.625000 0.750000
            vt 0.375000 0.750000
            vt 0.625000 1.000000
            vt 0.375000 1.000000
            vt 0.375000 0.000000
            vt 0.625000 0.000000
            vt 0.625000 0.250000
            vt 0.375000 0.250000
            vt 0.125000 0.500000
            vt 0.375000 0.500000
            vt 0.125000 0.750000
            
            # Normals
            vn -0.0871 0.9675 0.2372
            vn 0.1376 -0.2242 0.9648
            vn -0.9866 -0.1167 0.1136
            vn 0.0871 -0.9675 -0.2372
            vn 0.9866 0.1167 -0.1136
            vn -0.1376 0.2242 -0.9648
            
            # Unused in parser
            usemtl Material
            s off
            
            # Faces
            f 1/1/1 5/2/1 7/3/1 3/4/1
            f 4/5/2 3/4/2 7/6/2 8/7/2
            f 8/8/3 7/9/3 5/10/3 6/11/3
            f 6/12/4 2/13/4 4/5/4 8/14/4
            f 2/13/5 1/1/5 3/4/5 4/5/5
            f 6/11/6 5/10/6 1/1/6 2/13/6
        """.trimIndent()

        val GOOD_WAVEFRONT_NO_VT = """
            v 1.232927 2.117226 -1.361101
            v 1.514860 -1.014031 -2.128918
            v 1.678257 1.391691 1.761250
            v 1.960191 -1.739567 0.993434
            v -1.960191 1.739567 -0.993434
            v -1.678257 -1.391691 -1.761250
            v -1.514860 1.014031 2.128918
            v -1.232927 -2.117226 1.361101
            vn -0.0871 0.9675 0.2372
            vn 0.1376 -0.2242 0.9648
            vn -0.9866 -0.1167 0.1136
            vn 0.0871 -0.9675 -0.2372
            vn 0.9866 0.1167 -0.1136
            vn -0.1376 0.2242 -0.9648
            f 1//1 5//1 7//1 3//1
            f 4//2 3//2 7//2 8//2
            f 8//3 7//3 5//3 6//3
            f 6//4 2//4 4//4 8//4
            f 2//5 1//5 3//5 4//5
            f 6//6 5//6 1//6 2//6
        """.trimIndent()

        val GOOD_WAVEFRONT_NO_VN = """
            v 1.232927 2.117226 -1.361101
            v 1.514860 -1.014031 -2.128918
            v 1.678257 1.391691 1.761250
            v 1.960191 -1.739567 0.993434
            v -1.960191 1.739567 -0.993434
            v -1.678257 -1.391691 -1.761250
            v -1.514860 1.014031 2.128918
            v -1.232927 -2.117226 1.361101
            vt 0.625000 0.500000
            vt 0.875000 0.500000
            vt 0.875000 0.750000
            vt 0.625000 0.750000
            vt 0.375000 0.750000
            vt 0.625000 1.000000
            vt 0.375000 1.000000
            vt 0.375000 0.000000
            vt 0.625000 0.000000
            vt 0.625000 0.250000
            vt 0.375000 0.250000
            vt 0.125000 0.500000
            vt 0.375000 0.500000
            vt 0.125000 0.750000
            f 1/1 5/2 7/3 3/4
            f 4/5 3/4 7/6 8/7
            f 8/8 7/9 5/10 6/11
            f 6/12 2/13 4/5 8/14
            f 2/13 1/1 3/4 4/5
            f 6/11 5/10 1/1 2/13
        """.trimIndent()

        val GOOD_WAVEFRONT_NO_VT_NO_VN = """
            v 1.232927 2.117226 -1.361101
            v 1.514860 -1.014031 -2.128918
            v 1.678257 1.391691 1.761250
            v 1.960191 -1.739567 0.993434
            v -1.960191 1.739567 -0.993434
            v -1.678257 -1.391691 -1.761250
            v -1.514860 1.014031 2.128918
            v -1.232927 -2.117226 1.361101
            f 1 5 7 3
            f 4 3 7 8
            f 8 7 5 6
            f 6 2 4 8
            f 2 1 3 4
            f 6 5 1 2
        """.trimIndent()

        val GOOD_WAVEFRONT_INVALID_NORMALS = """
            v 1.232927 2.117226 -1.361101
            v 1.514860 -1.014031 -2.128918
            v 1.678257 1.391691 1.761250
            v 1.960191 -1.739567 0.993434
            v -1.960191 1.739567 -0.993434
            v -1.678257 -1.391691 -1.761250
            v -1.514860 1.014031 2.128918
            v -1.232927 -2.117226 1.361101
            
            # Problem is here: (expected to be -0.0871 0.9675 0.2372)
            vn -2.0871 0.9675 0.2372
            
            vn 0.1376 -0.2242 0.9648
            vn -0.9866 -0.1167 0.1136
            vn 0.0871 -0.9675 -0.2372
            vn 0.9866 0.1167 -0.1136
            vn -0.1376 0.2242 -0.9648
            
            f 1//1 5//1 7//1 3//1
            f 4//2 3//2 7//2 8//2
            f 8//3 7//3 5//3 6//3
            f 6//4 2//4 4//4 8//4
            f 2//5 1//5 3//5 4//5
            f 6//6 5//6 1//6 2//6
        """.trimIndent()

        val MALFORMED_WAVEFRONT_BAD_V_COORDINATE = """
            # Problem is here:
            v foo 2.117226 -1.361101
            v 1.514860 -1.014031 -2.128918
            v 1.678257 1.391691 1.761250
            v 1.960191 -1.739567 0.993434
            v -1.960191 1.739567 -0.993434
            v -1.678257 -1.391691 -1.761250
            v -1.514860 1.014031 2.128918
            v -1.232927 -2.117226 1.361101
            vn -0.0871 0.9675 0.2372
            vn 0.1376 -0.2242 0.9648
            vn -0.9866 -0.1167 0.1136
            vn 0.0871 -0.9675 -0.2372
            vn 0.9866 0.1167 -0.1136
            vn -0.1376 0.2242 -0.9648
            f 1//1 5//1 7//1 3//1
            f 4//2 3//2 7//2 8//2
            f 8//3 7//3 5//3 6//3
            f 6//4 2//4 4//4 8//4
            f 2//5 1//5 3//5 4//5
            f 6//6 5//6 1//6 2//6
        """.trimIndent()

        val MALFORMED_WAVEFRONT_BAD_N_COORDINATE = """
            v 1.232927 2.117226 -1.361101
            v 1.514860 -1.014031 -2.128918
            v 1.678257 1.391691 1.761250
            v 1.960191 -1.739567 0.993434
            v -1.960191 1.739567 -0.993434
            v -1.678257 -1.391691 -1.761250
            v -1.514860 1.014031 2.128918
            v -1.232927 -2.117226 1.361101
            # Problem is here:
            vn -0.0871 foo 0.2372
            vn 0.1376 -0.2242 0.9648
            vn -0.9866 -0.1167 0.1136
            vn 0.0871 -0.9675 -0.2372
            vn 0.9866 0.1167 -0.1136
            vn -0.1376 0.2242 -0.9648
            f 1//1 5//1 7//1 3//1
            f 4//2 3//2 7//2 8//2
            f 8//3 7//3 5//3 6//3
            f 6//4 2//4 4//4 8//4
            f 2//5 1//5 3//5 4//5
            f 6//6 5//6 1//6 2//6
        """.trimIndent()

        val MALFORMED_WAVEFRONT_WRONG_V_COORDINATES_NUMBER = """
            # Problem is here:
            v 1.232927 1.232927 2.117226 -1.361101
            v 1.514860 -1.014031 -2.128918
            v 1.678257 1.391691 1.761250
            v 1.960191 -1.739567 0.993434
            v -1.960191 1.739567 -0.993434
            v -1.678257 -1.391691 -1.761250
            v -1.514860 1.014031 2.128918
            v -1.232927 -2.117226 1.361101
            vn -0.0871 0.9675 0.2372
            vn 0.1376 -0.2242 0.9648
            vn -0.9866 -0.1167 0.1136
            vn 0.0871 -0.9675 -0.2372
            vn 0.9866 0.1167 -0.1136
            vn -0.1376 0.2242 -0.9648
            f 1//1 5//1 7//1 3//1
            f 4//2 3//2 7//2 8//2
            f 8//3 7//3 5//3 6//3
            f 6//4 2//4 4//4 8//4
            f 2//5 1//5 3//5 4//5
            f 6//6 5//6 1//6 2//6
        """.trimIndent()

        val MALFORMED_WAVEFRONT_WRONG_V_COORDINATES_NUMBER_2 = """
            # Problem is here:
            v 2.117226 -1.361101
            v 1.514860 -1.014031 -2.128918
            v 1.678257 1.391691 1.761250
            v 1.960191 -1.739567 0.993434
            v -1.960191 1.739567 -0.993434
            v -1.678257 -1.391691 -1.761250
            v -1.514860 1.014031 2.128918
            v -1.232927 -2.117226 1.361101
            vn -0.0871 0.9675 0.2372
            vn 0.1376 -0.2242 0.9648
            vn -0.9866 -0.1167 0.1136
            vn 0.0871 -0.9675 -0.2372
            vn 0.9866 0.1167 -0.1136
            vn -0.1376 0.2242 -0.9648
            f 1//1 5//1 7//1 3//1
            f 4//2 3//2 7//2 8//2
            f 8//3 7//3 5//3 6//3
            f 6//4 2//4 4//4 8//4
            f 2//5 1//5 3//5 4//5
            f 6//6 5//6 1//6 2//6
        """.trimIndent()

        val MALFORMED_WAVEFRONT_WRONG_N_COORDINATES_NUMBER = """
            v 1.232927 2.117226 -1.361101
            v 1.514860 -1.014031 -2.128918
            v 1.678257 1.391691 1.761250
            v 1.960191 -1.739567 0.993434
            v -1.960191 1.739567 -0.993434
            v -1.678257 -1.391691 -1.761250
            v -1.514860 1.014031 2.128918
            v -1.232927 -2.117226 1.361101
            # Problem is here:
            vn 0.9675 0.2372
            vn 0.1376 -0.2242 0.9648
            vn -0.9866 -0.1167 0.1136
            vn 0.0871 -0.9675 -0.2372
            vn 0.9866 0.1167 -0.1136
            vn -0.1376 0.2242 -0.9648
            f 1//1 5//1 7//1 3//1
            f 4//2 3//2 7//2 8//2
            f 8//3 7//3 5//3 6//3
            f 6//4 2//4 4//4 8//4
            f 2//5 1//5 3//5 4//5
            f 6//6 5//6 1//6 2//6
        """.trimIndent()

        val MALFORMED_WAVEFRONT_WRONG_N_COORDINATES_NUMBER_2 = """
            v 1.232927 2.117226 -1.361101
            v 1.514860 -1.014031 -2.128918
            v 1.678257 1.391691 1.761250
            v 1.960191 -1.739567 0.993434
            v -1.960191 1.739567 -0.993434
            v -1.678257 -1.391691 -1.761250
            v -1.514860 1.014031 2.128918
            v -1.232927 -2.117226 1.361101
            # Problem is here:
            vn -0.0871 -0.0871 0.9675 0.2372
            vn 0.1376 -0.2242 0.9648
            vn -0.9866 -0.1167 0.1136
            vn 0.0871 -0.9675 -0.2372
            vn 0.9866 0.1167 -0.1136
            vn -0.1376 0.2242 -0.9648
            f 1//1 5//1 7//1 3//1
            f 4//2 3//2 7//2 8//2
            f 8//3 7//3 5//3 6//3
            f 6//4 2//4 4//4 8//4
            f 2//5 1//5 3//5 4//5
            f 6//6 5//6 1//6 2//6
        """.trimIndent()

        val MALFORMED_WAVEFRONT_BAD_FACE_NORMAL_INDEX = """ 
            v 1.232927 2.117226 -1.361101
            v 1.514860 -1.014031 -2.128918
            v 1.678257 1.391691 1.761250
            v 1.960191 -1.739567 0.993434
            v -1.960191 1.739567 -0.993434
            v -1.678257 -1.391691 -1.761250
            v -1.514860 1.014031 2.128918
            v -1.232927 -2.117226 1.361101
            # Problem is here:
            vn -0.0871 0.9675 0.2372
            vn 0.1376 -0.2242 0.9648
            vn -0.9866 -0.1167 0.1136
            vn 0.0871 -0.9675 -0.2372
            vn 0.9866 0.1167 -0.1136
            vn -0.1376 0.2242 -0.9648
            # Problem is here:
            f 1//1.4423423 5//1 7//1 3//1
            f 4//2 3//2 7//2 8//2
            f 8//3 7//3 5//3 6//3
            f 6//4 2//4 4//4 8//4
            f 2//5 1//5 3//5 4//5
            f 6//6 5//6 1//6 2//6
        """.trimIndent()

        val MALFORMED_WAVEFRONT_FACE_NORMAL_INDEX_OUT_OF_RANGE = """ 
            v 1.232927 2.117226 -1.361101
            v 1.514860 -1.014031 -2.128918
            v 1.678257 1.391691 1.761250
            v 1.960191 -1.739567 0.993434
            v -1.960191 1.739567 -0.993434
            v -1.678257 -1.391691 -1.761250
            v -1.514860 1.014031 2.128918
            v -1.232927 -2.117226 1.361101
            # Just drop some normals
            vn -0.0871 0.9675 0.2372
            vn 0.1376 -0.2242 0.9648
            f 1//1 5//1 7//1 3//1
            f 4//2 3//2 7//2 8//2
            f 8//3 7//3 5//3 6//3
            f 6//4 2//4 4//4 8//4
            f 2//5 1//5 3//5 4//5
            f 6//6 5//6 1//6 2//6
        """.trimIndent()

        val MALFORMED_WAVEFRONT_FACE_VERTEX_INDEX_OUT_OF_RANGE = """ 
            # Just drop some vertices
            v 1.232927 2.117226 -1.361101
            v 1.514860 -1.014031 -2.128918
            v 1.678257 1.391691 1.761250
            # Problem is here:
            vn -0.0871 0.9675 0.2372
            vn 0.1376 -0.2242 0.9648
            vn -0.9866 -0.1167 0.1136
            vn 0.0871 -0.9675 -0.2372
            vn 0.9866 0.1167 -0.1136
            vn -0.1376 0.2242 -0.9648
            f 1//1 5//1 7//1 3//1
            f 4//2 3//2 7//2 8//2
            f 8//3 7//3 5//3 6//3
            f 6//4 2//4 4//4 8//4
            f 2//5 1//5 3//5 4//5
            f 6//6 5//6 1//6 2//6
        """.trimIndent()

        val MALFORMED_WAVEFRONT_BAD_FACE_VERTEX_INDEX = """ 
            v 1.232927 2.117226 -1.361101
            v 1.514860 -1.014031 -2.128918
            v 1.678257 1.391691 1.761250
            v 1.960191 -1.739567 0.993434
            v -1.960191 1.739567 -0.993434
            v -1.678257 -1.391691 -1.761250
            v -1.514860 1.014031 2.128918
            v -1.232927 -2.117226 1.361101
            # Problem is here:
            vn -0.0871 0.9675 0.2372
            vn 0.1376 -0.2242 0.9648
            vn -0.9866 -0.1167 0.1136
            vn 0.0871 -0.9675 -0.2372
            vn 0.9866 0.1167 -0.1136
            vn -0.1376 0.2242 -0.9648
            # Problem is here:
            f 1.4343//1 5//1 7//1 3//1
            f 4//2 3//2 7//2 8//2
            f 8//3 7//3 5//3 6//3
            f 6//4 2//4 4//4 8//4
            f 2//5 1//5 3//5 4//5
            f 6//6 5//6 1//6 2//6
        """.trimIndent()

        val MALFORMED_WAVEFRONT_WRONG_FACE_VERTEX_NUMBER = """ 
            v 1.232927 2.117226 -1.361101
            v 1.514860 -1.014031 -2.128918
            v 1.678257 1.391691 1.761250
            v 1.960191 -1.739567 0.993434
            v -1.960191 1.739567 -0.993434
            v -1.678257 -1.391691 -1.761250
            v -1.514860 1.014031 2.128918
            v -1.232927 -2.117226 1.361101
            # Problem is here:
            vn -0.0871 0.9675 0.2372
            vn 0.1376 -0.2242 0.9648
            vn -0.9866 -0.1167 0.1136
            vn 0.0871 -0.9675 -0.2372
            vn 0.9866 0.1167 -0.1136
            vn -0.1376 0.2242 -0.9648
            # Problem is here:
            f 5//1 7//1
            f 4//2 3//2 7//2 8//2
            f 8//3 7//3 5//3 6//3
            f 6//4 2//4 4//4 8//4
            f 2//5 1//5 3//5 4//5
            f 6//6 5//6 1//6 2//6
        """.trimIndent()

    }
}