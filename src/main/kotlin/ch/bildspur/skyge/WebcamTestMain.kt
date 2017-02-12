package ch.bildspur.skyge

import processing.core.PApplet

/**
 * Created by cansik on 08.02.17.
 */
class WebcamTestMain {
    val sketch = WebcamTest()

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val main = WebcamTestMain()
            PApplet.runSketch(arrayOf("WebcamTest "), main.sketch)
        }
    }
}