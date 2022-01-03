package com.github.shchuko.io3d.impl.exception

import com.github.shchuko.io3d.exception.ParseException

class WavefrontParseException : ParseException {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}