package com.github.shchuko.args.exception

class ArgsParseException : Exception {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}