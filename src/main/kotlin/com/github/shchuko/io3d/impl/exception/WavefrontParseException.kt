package com.github.shchuko.io3d.impl.exception

import com.github.shchuko.io3d.exception.ParseException

class WavefrontParseException : ParseException {
    constructor(message: String, line: Int) : this("Line $line: $message")
    constructor(message: String, cause: Throwable?, line: Int) : this("Line $line: $message", cause)
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable?) : super(message, cause)
}