package com.github.shchuko.io3d

import com.github.shchuko.math.*

abstract class Scene3D {
    abstract val vertices: List<Point3D>
    abstract val normals: List<Vector3D>
    abstract val faces: List<Face>

    /**
     * Face representation.
     * @param verticesIndexes Indices of vertices of the scene the face contains.
     * @param normalIndex Face normal index. If normal absent any negative value must be provided.
     */
    inner class Face(val verticesIndexes: List<Int>, val normalIndex: Int) {
        val vertices: List<Point3D> by lazy { verticesIndexes.map { this@Scene3D.vertices[it] } }
        val normal: Vector3D by lazy {
            when {
                normalIndex < 0 -> buildNormalFromVertices()
                else -> this@Scene3D.normals[normalIndex]
            }
        }

        fun isNormalCorrect(): Boolean = when {
            (normalIndex < 0) -> true
            else -> buildNormalFromVertices().eqNorm(normal)
        }

        private fun buildNormalFromVertices(): Vector3D {
            val firstVec = this@Face.vertices[0] vectorTo this@Face.vertices[1]
            val secondVec = this@Face.vertices[1] vectorTo this@Face.vertices[2]
            return (firstVec dot secondVec).norm()
        }
    }
}