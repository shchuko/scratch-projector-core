package com.github.shchuko.math

import kotlin.math.abs

/* Double */
// TODO check whether the delta suitable
infix fun Double.eq(other: Double): Boolean = abs(this - other) < 0.0001
infix fun Double.ne(other: Double): Boolean = !(this eq other)
infix fun Double.ge(other: Double): Boolean = this > other || eq(other)
fun Double.sqr() = this * this