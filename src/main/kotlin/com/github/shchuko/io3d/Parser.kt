package com.github.shchuko.io3d

import com.github.shchuko.io3d.exception.ParseException

/**
 * Simple 3D parser interface
 *
 * @author Vladislav Yaroshchuk
 */
interface Parser {
    /**
     * Parse result. If [parse] is not invoked before, the behaviour is undefined.
     */
    val parsedScene: Scene3D


    /**
     * Start parse process.
     *
     * @throws [ParseException] on any parse error
     */
    fun parse()
}