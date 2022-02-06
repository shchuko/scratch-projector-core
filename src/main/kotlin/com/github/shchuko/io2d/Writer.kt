package com.github.shchuko.io2d

interface Writer {
    fun write(scene: Scene2D, canvasProperties: Scene2D.CanvasProperties)
}