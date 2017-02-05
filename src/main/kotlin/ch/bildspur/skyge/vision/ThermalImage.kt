package ch.bildspur.skyge.vision

import ch.bildspur.skyge.Sketch
import processing.core.PConstants
import processing.core.PGraphics
import processing.core.PImage

/**
 * Created by cansik on 04.02.17.
 */
class ThermalImage {
    val input: PImage
    var output: PGraphics? = null

    val components = mutableListOf<ConnectedComponent>()

    constructor(input: PImage) {
        this.input = input
    }

    fun createOutputImage() {
        output = Sketch.instance.createGraphics(input.width, input.height, PConstants.JAVA2D)
    }
}