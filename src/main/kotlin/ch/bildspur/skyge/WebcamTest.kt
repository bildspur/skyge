package ch.bildspur.skyge

import processing.core.PApplet
import processing.core.PConstants
import processing.opengl.PJOGL
import processing.video.Capture


/**
 * Created by cansik on 08.02.17.
 */
class WebcamTest : PApplet() {
    var camera: Capture? = null

    override fun settings() {
        size(Sketch.WINDOW_WIDTH, Sketch.WINDOW_HEIGHT, PConstants.P2D)
        PJOGL.profile = 1
    }

    override fun setup() {
        camera = Capture(this)
        camera!!.start()
    }

    fun captureEvent(c: Capture) {
        c.read()
    }

    override fun draw() {
        background(55f)

        if (camera!!.available())
            camera!!.read()

        image(camera!!, 0f, 0f)

        println("Size: ${camera!!.width} / ${camera!!.height}")
    }
}