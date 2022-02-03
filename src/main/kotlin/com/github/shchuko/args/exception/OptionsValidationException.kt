package com.github.shchuko.args.exception

class OptionsValidationException : Exception {
    constructor(message: String?) : super(message)
    constructor(message: String?, errors: List<String>) : super("$message: [${errors.joinToString(", ")}]")
}