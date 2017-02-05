package ch.bildspur.skyge

import ch.bildspur.skyge.controller.SyphonController
import ch.bildspur.skyge.vision.ThermalAnalyser
import ch.bildspur.skyge.vision.ThermalImage
import org.opencv.core.Core
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PGraphics
import processing.opengl.PJOGL
import processing.video.Movie
import kotlin.properties.Delegates

/**
 * Created by cansik on 04.02.17.
 */
class Sketch : PApplet() {
    companion object {
        @JvmStatic val FRAME_RATE = 30f

        @JvmStatic val OUTPUT_WIDTH = 500
        @JvmStatic val OUTPUT_HEIGHT = 250

        @JvmStatic val WINDOW_WIDTH = 640
        @JvmStatic val WINDOW_HEIGHT = 500

        @JvmStatic val NAME = "SKYGE"

        @JvmStatic var instance = PApplet()
    }

    val syphon = SyphonController(this)

    var fpsOverTime = 0f

    var output: PGraphics by Delegates.notNull()

    var exampleMovie: Movie by Delegates.notNull()

    val analyser = ThermalAnalyser()

    init {

    }

    override fun settings() {
        size(WINDOW_WIDTH, WINDOW_HEIGHT, PConstants.P2D)
        PJOGL.profile = 1
    }

    override fun setup() {
        Sketch.instance = this

        smooth()
        frameRate(FRAME_RATE)

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

        surface.setTitle(NAME)
        syphon.setupSyphon(NAME)

        // setup output
        output = createGraphics(OUTPUT_WIDTH, OUTPUT_HEIGHT, PApplet.P2D)

        // load example
        exampleMovie = Movie(this, sketchPath("data/flir_footage.mp4"))
        exampleMovie.loop()

        analyser.start()
    }

    override fun draw() {
        background(55f)

        if (frameCount < 2)
            return

        syphon.sendImageToSyphon(output)

        // draw original image
        image(exampleMovie.copy(), width - 425f, 0f, 425f, 240f)

        // draw processed image
        analyser.input.put(ThermalImage(exampleMovie))

        if (analyser.output.size > 0) {
            val ti = analyser.output.poll()
            image(ti.output, width - 425f, 250f, 425f, 240f)

            g.removeCache(ti.input)
            g.removeCache(ti.output)
        }

        drawFPS()
    }

    fun movieEvent(m: Movie) {
        m.read()
    }

    fun drawFPS() {
        // draw fps
        fpsOverTime += frameRate
        val averageFPS = fpsOverTime / frameCount.toFloat()

        textAlign(PApplet.LEFT, PApplet.BOTTOM)
        fill(255)
        text("FPS: ${frameRate.format(2)}\nFOT: ${averageFPS.format(2)}", 10f, height - 5f)
    }
}