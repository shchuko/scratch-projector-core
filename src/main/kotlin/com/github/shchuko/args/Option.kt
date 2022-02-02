package com.github.shchuko.args

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Option(
    val alias: String = "",
    val description: String = "",
    val required: Boolean = false
)
