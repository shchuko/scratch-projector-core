package com.github.shchuko.io3d.impl

import com.github.shchuko.io3d.Parser
import com.github.shchuko.io3d.Scene3D
import com.github.shchuko.io3d.impl.exception.WavefrontParseException
import com.github.shchuko.math.Point3D
import com.github.shchuko.math.Vector3D
import java.io.BufferedReader
import java.io.InputStream
import java.util.stream.Stream

class MinimalWavefrontParser(
    private val stream: InputStream, private val validateNormals: Boolean = false
) : Parser {
    private val scene: ParseContext by lazy { parse() }
    private var lineCounter = 0

    override fun getResult(): Scene3D = scene

    private fun parse(): ParseContext {
        val parsed: ParseContext
        try {
            BufferedReader(stream.reader()).use { parsed = parseLines(it.lines()) }
        } catch (e: WavefrontParseException) {
            throw e
        } catch (e: Throwable) {
            throw WavefrontParseException(e)
        }

        // Finalize faces data
        parsed.getFaces()

        // Validate normals if needed
        if (validateNormals) { //  && parsed.getFaces().any { !it.isNormalCorrect() }
            for (it in parsed.getFaces()) {
                if (!it.isNormalCorrect()) {
                    throw WavefrontParseException("File contains invalid faces normals")
                }
            }
        }
        return parsed
    }

    private fun parseLines(lines: Stream<String>): ParseContext {
        val context = ParseContext()
        lineCounter = 0
        try {
            lines.forEach { parseLine(it, context) }
        } catch (e: WavefrontParseException) {
            // Rethrow appending line number
            throw WavefrontParseException("Line $lineCounter: ${e.message}", e.cause)
        } catch (e: Throwable) {
            throw WavefrontParseException("Line $lineCounter: parse error ${e.message}", e)
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
        lineCounter++
    }

    private fun parseVertex(tokens: List<String>, context: ParseContext) =
        context.verticesStorage.add(parseCoordinates(tokens))


    private fun parseNormal(tokens: List<String>, context: ParseContext) {
        context.normalsStorage.add(parseCoordinates(tokens))
    }

    private fun parseFace(tokens: List<String>, context: ParseContext) {
        if (tokens.size <= 2) {
            throw WavefrontParseException("strange face with only ${tokens.size} vertexes")
        }
        val verticesIndexes = getFaceVerticesIndexes(tokens, context)
        val normalIndex = getFaceNormalIndex(tokens, context)
        context.facesMetadata.add(ParseContext.FaceMetadata(verticesIndexes, normalIndex))
    }

    private fun getFaceNormalIndex(tokens: List<String>, context: ParseContext): Int {
        var normalIndex = Int.MIN_VALUE

        if (context.normalsStorage.isEmpty()) {
            return normalIndex
        }

        val faceTokenSplit = tokens[0].split("/")
        if (faceTokenSplit.size == 3) {
            try {
                normalIndex = faceTokenSplit[2].toInt() - 1
            } catch (e: NumberFormatException) {
                throw WavefrontParseException("malformed face normal index $faceTokenSplit[2]", e)
            }

            if (normalIndex < 0 || normalIndex > context.normalsStorage.size) {
                throw WavefrontParseException("normal index ${normalIndex + 1} is out of range [1..${context.normalsStorage.size}]")
            }
        } else {
            context.facesMetadata.forEach { it.normalIndex = Int.MIN_VALUE }
            // Drop parsed normals data if there is no association with faces
            context.normalsStorage.clear()
        }

        return normalIndex
    }

    private fun getFaceVerticesIndexes(tokens: List<String>, context: ParseContext): List<Int> {
        val verticesIndexes: List<Int>
        try {
            verticesIndexes = tokens.map { it.split("/")[0].toInt() - 1 }.toList()
        } catch (e: NumberFormatException) {
            throw WavefrontParseException("unable context parse vertex index: ${e.message}", e)
        }

        verticesIndexes.stream().forEach {
            if (it < 0 || it >= context.verticesStorage.size) {
                throw WavefrontParseException("vertex index ${it + 1} is out of range [1..${context.normalsStorage.size}]")
            }
        }
        return verticesIndexes
    }

    private fun parseCoordinates(tokens: List<String>): Triple<Double, Double, Double> {
        if (tokens.size != 3) {
            throw WavefrontParseException("expected 3 coordinates, but got ${tokens.size}")
        }
        try {
            // Wavefront mappings:
            // X = X
            // Y = -Z
            // Z = Y
            return Triple(tokens[0].toDouble(), -tokens[2].toDouble(), tokens[1].toDouble())
        } catch (e: NumberFormatException) {
            throw WavefrontParseException("unable context parse coordinate: ${e.message}", e)
        }
    }

    private class ParseContext : Scene3D() {
        val verticesStorage: MutableList<Point3D> = ArrayList()
        val normalsStorage: MutableList<Vector3D> = ArrayList()
        val facesMetadata: MutableList<FaceMetadata> = ArrayList()
        val facesStorage: List<Face> by lazy { faceMetadataToFaces() }

        override fun getVertices(): List<Point3D> = verticesStorage
        override fun getNormals(): List<Vector3D> = normalsStorage
        override fun getFaces(): List<Face> = facesStorage

        private fun faceMetadataToFaces(): List<Face> =
            facesMetadata.map { Face(it.verticesIndexes, it.normalIndex) }.toList()


        data class FaceMetadata(val verticesIndexes: List<Int>, var normalIndex: Int)
    }
}