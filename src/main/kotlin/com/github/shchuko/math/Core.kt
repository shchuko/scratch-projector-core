package com.github.shchuko.math

import kotlin.math.abs
import kotlin.math.sqrt

typealias Vector3D = Triple<Double, Double, Double>
typealias Point3D = Triple<Double, Double, Double>

typealias Vector2D = Pair<Double, Double>
typealias Point2D = Pair<Double, Double>

/* Double */
// TODO check whether the delta suitable
private infix fun Double.eq(other: Double): Boolean = abs(this - other) < 0.0001
private infix fun Double.ge(other: Double): Boolean = this > other || eq(other)
private fun Double.sqr() = this * this

/* Vector 3D and Point 3D commons */

enum class Coordinate {
    X, Y, Z
}

fun Triple<Double, Double, Double>.x(): Double = first
fun Triple<Double, Double, Double>.y(): Double = second
fun Triple<Double, Double, Double>.z(): Double = third

infix fun Triple<Double, Double, Double>.eq(other: Triple<Double, Double, Double>): Boolean =
    first eq other.first && second eq other.second && third eq other.third

operator fun Triple<Double, Double, Double>.get(i: Int): Double = when (i) {
    0 -> first
    1 -> second
    2 -> third
    else -> throw IllegalArgumentException()
}

fun Triple<Double, Double, Double>.to2d(ignore: Coordinate): Pair<Double, Double> = when (ignore) {
    Coordinate.X -> Pair(second, third)
    Coordinate.Y -> Pair(first, third)
    Coordinate.Z -> Pair(first, second)
}

operator fun Triple<Double, Double, Double>.unaryPlus(): Triple<Double, Double, Double> = this

operator fun Triple<Double, Double, Double>.unaryMinus(): Triple<Double, Double, Double> =
    Triple(-first, -second, -third)

operator fun Triple<Double, Double, Double>.plus(other: Triple<Double, Double, Double>): Triple<Double, Double, Double> =
    Triple(first + other.first, second + other.second, third + other.third)

operator fun Triple<Double, Double, Double>.minus(other: Triple<Double, Double, Double>): Triple<Double, Double, Double> =
    Triple(first - other.first, second - other.second, third - other.third)


/* Vector 3D */
val VEC_3D_0 = Vector3D(0.0, 0.0, 0.0)
val VEC_3D_I = Vector3D(1.0, 0.0, 0.0)
val VEC_3D_J = Vector3D(0.0, 1.0, 0.0)
val VEC_3D_K = Vector3D(0.0, 0.0, 1.0)

fun Vector3D.size(): Double = sqrt(first.sqr() + second.sqr() + third.sqr())

fun Vector3D.norm(): Vector3D {
    val size = size()
    return when {
        size eq 0.0 -> VEC_3D_0
        else -> Triple(first / size, second / size, third / size)
    }
}

infix fun Vector3D.eqNorm(other: Vector3D): Boolean = norm() eq other.norm()


infix fun Vector3D.dot(other: Vector3D): Vector3D = Triple(
    second * other.third - third * other.second,
    third * other.first - first * other.third,
    first * other.second - second * other.first,
)


/* Point 3D */
infix fun Point3D.vectorTo(other: Point3D): Vector3D = other - this


/* Vector 2D and Point 2D commons */
fun Pair<Double, Double>.x(): Double = first
fun Pair<Double, Double>.y(): Double = second

infix fun Pair<Double, Double>.eq(other: Pair<Double, Double>): Boolean =
    first eq other.first && second eq other.second

operator fun Pair<Double, Double>.get(i: Int): Double = when (i) {
    0 -> first
    1 -> second
    else -> throw IllegalArgumentException()
}

fun Pair<Double, Double>.to3d(zeroed: Coordinate): Triple<Double, Double, Double> = when (zeroed) {
    Coordinate.X -> Triple(0.0, first, second)
    Coordinate.Y -> Triple(first, 0.0, second)
    Coordinate.Z -> Triple(first, second, 0.0)
}

operator fun Pair<Double, Double>.unaryPlus(): Pair<Double, Double> = this

operator fun Pair<Double, Double>.unaryMinus(): Pair<Double, Double> = Pair(-first, -second)

operator fun Pair<Double, Double>.plus(other: Pair<Double, Double>): Pair<Double, Double> =
    Pair(first + other.first, second + other.second)

operator fun Pair<Double, Double>.minus(other: Pair<Double, Double>): Pair<Double, Double> =
    Pair(first - other.first, second - other.second)


/* Vector 2D */
val VEC_2D_0 = Vector2D(0.0, 0.0)
val VEC_2D_I = Vector2D(1.0, 0.0)
val VEC_2D_J = Vector2D(0.0, 1.0)

fun Vector2D.size(): Double = sqrt(first.sqr() + second.sqr())

fun Vector2D.norm(): Vector2D {
    val size = size()
    return when {
        size eq 0.0 -> VEC_2D_0
        else -> Pair(first / size, second / size)
    }
}

infix fun Vector2D.eqNorm(other: Vector2D): Boolean = norm() eq other.norm()

/* Point 2D */
infix fun Point2D.vectorTo(other: Point2D): Vector2D = other - this

