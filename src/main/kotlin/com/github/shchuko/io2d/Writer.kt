package com.github.shchuko.io2d

interface Writer {
    fun write(scene: Scene2D, canvasProperties: CanvasProcessor.CanvasProperties)
}