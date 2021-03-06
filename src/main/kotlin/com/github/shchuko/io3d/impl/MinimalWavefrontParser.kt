package com.github.shchuko.io3d.impl

import com.github.shchuko.io3d.Parser
import com.github.shchuko.io3d.Scene3D
import com.github.shchuko.io3d.impl.exception.WavefrontParseException
import com.github.shchuko.math.Point3D
import com.github.shchuko.math.Vector3D
import java.io.BufferedReader
import java.io.InputStream

class MinimalWavefrontParser(
    private val stream: InputStream,
    private val validateNormals: Boolean = false
) : Parser {
    override val parsedScene: Scene3D by lazy { parseInternal() }

    override fun parse() {
        this::parsedScene.get()
    }

    private fun parseInternal(): Scene3D {
        val context = ParseContext()

        try {
            BufferedReader(stream.reader()).use { reader ->
                reader.lines().forEach { line ->
                    parseLine(line, context)
                }
            }
        } catch (e: Throwable) {
            throw WavefrontParseException("parse error ${e.message}", e, context.lineCounter)
        }

        // Validate normals if needed
        if (validateNormals && context.faces.any { !it.isNormalCorrect() }) {
            throw WavefrontParseException("File contains invalid faces normals")
        }
        return context
    }

    private fun parseLine(line: String, context: ParseContext) {
        val tokens = line.split(" ")
        when (tokens[0]) {
            "v" -> parseVertex(tokens.drop(1), context)
            "vn" -> parseNormal(tokens.drop(1), context)
            "f" -> parseFace(tokens.drop(1), context)
        }
        context.lineCounter++
    }

    private fun parseVertex(tokens: List<String>, context: ParseContext) =
        context.vertices.add(parseCoordinates(tokens, context))


    private fun parseNormal(tokens: List<String>, context: ParseContext) {
        context.normals.add(parseCoordinates(tokens, context))
    }

    private fun parseFace(tokens: List<String>, context: ParseContext) {
        if (tokens.size <= 2) {
            throw WavefrontParseException("strange face with only ${tokens.size} vertexes", context.lineCounter)
        }
        val verticesIndexes = parseFaceVerticesIndexes(tokens, context)
        val normalIndex = parseFaceNormalIndex(tokens, context)
        context.facesMetadata.add(ParseContext.FaceMetadata(verticesIndexes, normalIndex))
    }

    private fun parseFaceNormalIndex(tokens: List<String>, context: ParseContext): Int {
        var normalIndex = Int.MIN_VALUE

        if (context.normals.isEmpty()) {
            return normalIndex
        }

        val faceTokenSplit = tokens[0].split("/")
        if (faceTokenSplit.size == 3) {
            try {
                normalIndex = faceTokenSplit[2].toInt() - 1
            } catch (e: NumberFormatException) {
                throw WavefrontParseException(
                    "malformed face normal index $faceTokenSplit[2]",
                    e,
                    context.lineCounter
                )
            }

            if (normalIndex < 0 || normalIndex > context.normals.size) {
                throw WavefrontParseException(
                    "normal index ${normalIndex + 1} is out of range [1..${context.normals.size}]",
                    context.lineCounter
                )
            }
        } else {
            context.facesMetadata.forEach { it.normalIndex = Int.MIN_VALUE }
            // Drop parsed normals data if there is no association with faces
            context.normals.clear()
        }

        return normalIndex
    }

    private fun parseFaceVerticesIndexes(tokens: List<String>, context: ParseContext): List<Int> {
        val verticesIndexes: List<Int>
        try {
            verticesIndexes = tokens.map { it.split("/")[0].toInt() - 1 }.toList()
        } catch (e: NumberFormatException) {
            throw WavefrontParseException(
                "unable context parse vertex index: ${e.message}",
                e,
                context.lineCounter
            )
        }

        verticesIndexes.forEach {
            if (it < 0 || it >= context.vertices.size) {
                throw WavefrontParseException(
                    "vertex index ${it + 1} is out of range [1..${context.normals.size}]",
                    context.lineCounter
                )
            }
        }
        return verticesIndexes
    }

    private fun parseCoordinates(tokens: List<String>, context: ParseContext): Triple<Double, Double, Double> {
        if (tokens.size != 3) {
            throw WavefrontParseException("expected 3 coordinates, but got ${tokens.size}", context.lineCounter)
        }
        try {
            // Wavefront mappings:
            // X = X
            // Y = -Z
            // Z = Y
            return Triple(tokens[0].toDouble(), -tokens[2].toDouble(), tokens[1].toDouble())
        } catch (e: NumberFormatException) {
            throw WavefrontParseException("unable context parse coordinate: ${e.message}", e, context.lineCounter)
        }
    }

    private class ParseContext : Scene3D() {
        override val vertices: MutableList<Point3D> = ArrayList()
        override val normals: MutableList<Vector3D> = ArrayList()
        override val faces: List<Face> by lazy { facesMetadata.map { Face(it.verticesIndexes, it.normalIndex) } }

        val facesMetadata: MutableList<FaceMetadata> = ArrayList()
        var lineCounter = 0

        data class FaceMetadata(val verticesIndexes: List<Int>, var normalIndex: Int)
    }
}
