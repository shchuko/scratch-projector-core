package com.github.shchuko.io3d

import com.github.shchuko.math.Point3D
import com.github.shchuko.math.Vector3D
import com.github.shchuko.math.eqNorm
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Scene3DFaceTest {

    @Test
    fun `test lazy initialization`() {
        val scene3D = object : Scene3D() {
            override fun getVertices() = vertices
            override fun getNormals() = goodNormals
            override fun getFaces() =
                (0 until facesNumber).map { Face(facesVerticesLists[it], normalIndexesFilled[it]) }
        }


        assertEquals(facesNumber, scene3D.getFaces().size)
        for (faceIndex in 0 until facesNumber) {
            val face = scene3D.getFaces()[faceIndex]

            // Checking normal initialization
            val expectedNormal = goodNormals[normalIndexesFilled[faceIndex]]
            val actualNormal = face.normal
            assertSame(expectedNormal, actualNormal)

            // Checking all the vertexes are copied
            assertEquals(facesVerticesLists[faceIndex].size, face.vertices.size)
            for (i in face.vertices.indices) {
                val vertexIndex = facesVerticesLists[faceIndex][i]
                assertSame(vertices[vertexIndex], face.vertices[i])
            }
        }
    }

    @Test
    fun `test normal calculation from vertices`() {
        val scene3D = object : Scene3D() {
            override fun getVertices() = vertices
            override fun getNormals() = goodNormals
            override fun getFaces() =
                (0 until facesNumber).map {
                    Face(
                        facesVerticesLists[it],
                        normalsIndexesUnknown[it]
                    )
                }
        }


        assertEquals(facesNumber, scene3D.getFaces().size)
        for (faceIndex in 0 until facesNumber) {
            val face = scene3D.getFaces()[faceIndex]

            // Checking normal initialization
            val expectedNormal = goodNormals[normalIndexesFilled[faceIndex]]
            val actualNormal = face.normal

            assertTrue(expectedNormal eqNorm actualNormal)
            assertNotSame(expectedNormal, actualNormal)
        }
    }

    @Test
    fun `test face detects bad normal`() {
        val sceneWithGoodNormals = object : Scene3D() {
            override fun getVertices() = vertices
            override fun getNormals() = goodNormals
            override fun getFaces() =
                (0 until facesNumber).map { Face(facesVerticesLists[it], normalIndexesFilled[it]) }
        }

        val sceneWithBadNormals = object : Scene3D() {
            override fun getVertices() = vertices
            override fun getNormals() = badNormals
            override fun getFaces() =
                (0 until facesNumber).map { Face(facesVerticesLists[it], normalIndexesFilled[it]) }
        }

        for (face in sceneWithGoodNormals.getFaces()) {
            assertTrue(face.isNormalCorrect())
        }

        for (face in sceneWithBadNormals.getFaces()) {
            assertFalse(face.isNormalCorrect())
        }
    }

    companion object {
        private val vertices = listOf(
            Point3D(0.767913, 1.533895, -0.239739),
            Point3D(1.473563, -0.223683, -0.882370),
            Point3D(0.350720, 0.716633, 1.537346),
            Point3D(1.056370, -1.040946, 0.894715),
            Point3D(-1.056370, 1.040946, -0.894715),
            Point3D(-0.350720, -0.716633, -1.537346),
            Point3D(-1.473563, 0.223683, 0.882370),
            Point3D(-0.767913, -1.533895, 0.239739)
        )

        private val goodNormals = listOf(
            Vector3D(-0.3528, 0.8788, 0.3213),
            Vector3D(-0.2086, -0.4086, 0.8885),
            Vector3D(-0.9121, -0.2465, -0.3275),
            Vector3D(0.3528, -0.8788, -0.3213),
            Vector3D(0.9121, 0.2465, 0.3275),
            Vector3D(0.2086, 0.4086, -0.8885),
        )

        private val badNormals = listOf(
            Vector3D(-0.4528, 0.8788, 0.3213),
            Vector3D(-0.2086, -0.6086, 0.8885),
            Vector3D(-0.1121, -0.2465, -0.3275),
            Vector3D(0.1528, -0.8788, -0.3213),
            Vector3D(0.3121, 0.0465, 0.3275),
            Vector3D(0.2086, 0.5086, -0.4885),
        )

        private val facesVerticesLists = listOf(
            listOf(0, 4, 6, 2),
            listOf(3, 2, 6, 7),
            listOf(7, 6, 4, 5),
            listOf(5, 1, 3, 7),
            listOf(1, 0, 2, 3),
            listOf(5, 4, 0, 1)
        )

        private val normalIndexesFilled = listOf(0, 1, 2, 3, 4, 5)
        private val normalsIndexesUnknown = listOf(-1, -1, -1, -1, -1, -1)

        private const val facesNumber = 6
    }
}