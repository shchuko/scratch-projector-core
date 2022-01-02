package com.github.shchuko.io3d

import com.github.shchuko.math.*
import java.util.stream.Collectors

abstract class Scene3D {
    abstract fun getVertices(): List<Point3D>
    abstract fun getNormals(): List<Vector3D>
    abstract fun getFaces(): List<Face>

    inner class Face(val verticesIndexes: List<Int>, val normalIndex: Int) {
        val vertices: List<Point3D> by lazy { lazyVertexes() }
        val normal: Vector3D by lazy { lazyNormal() }

        fun isNormalCorrect(): Boolean {
            return when {
                normalIndex < 0 -> true
                else -> buildNormalFromVertices().eqNorm(normal)
            }
        }

        private fun lazyVertexes(): List<Point3D> {
            return verticesIndexes.stream().map { getVertices()[it] }.collect(Collectors.toList())
        }

        private fun lazyNormal(): Vector3D {
            return when {
                normalIndex < 0 -> buildNormalFromVertices()
                else -> getNormals()[normalIndex]
            }
        }

        private fun buildNormalFromVertices(): Vector3D {
            val firstVec = vertices[0] vectorTo vertices[1]
            val secondVec = vertices[1] vectorTo vertices[2]
            return (firstVec dot secondVec).norm()
        }
    }
}