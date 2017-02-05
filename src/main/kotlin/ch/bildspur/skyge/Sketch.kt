package ch.bildspur.skyge

import ch.bildspur.skyge.controller.SyphonController
import ch.bildspur.skyge.vision.ThermalDetector
import ch.bildspur.skyge.vision.ThermalImage
import controlP5.ControlP5
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

    var video: PGraphics by Delegates.notNull()

    var exampleMovie: Movie by Delegates.notNull()

    var cp5: ControlP5 by Delegates.notNull()

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

        cp5 = ControlP5(this)
        setupUI()

        // load example
        exampleMovie = Movie(this, sketchPath("data/flir_footage.mp4"))
        exampleMovie.loop()

        // setup output
        output = createGraphics(OUTPUT_WIDTH, OUTPUT_HEIGHT, PApplet.P2D)
        video = createGraphics(854, 480, PApplet.P2D)
    }

    override fun draw() {
        background(55f)

        if (frameCount < 2)
            return

        // analyse image
        val ti = ThermalImage(exampleMovie)
        ThermalDetector.detect(ti)

        // draw debug image
        video.draw {
            it.image(exampleMovie.copy(), 0f, 0f)

            // draw cross for component
            it.strokeWeight(3f)
            it.stroke(0f, 0f, 255f)
            it.fill(0f, 0f, 255f)

            for (component in ti.components) {
                val x = component.centroid.x.toFloat()
                val y = component.centroid.y.toFloat()

                val size = 15f

                it.line(x, y - size, x, y + size)
                it.line(x - size, y, x + size, y)

                it.text("${component.label}", x + (size / 2f), y + size)
            }
        }

        image(video, 0f, 0f, 640f, 380f)

        // draw output
        output.draw { it.background(255) }

        // send output
        syphon.sendImageToSyphon(output)

        cp5.draw()
        drawFPS()

        // cleanup
        g.removeCache(ti.input)
    }

    fun movieEvent(m: Movie) {
        if (m.available())
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

    fun setupUI() {
        val h = 400f
        val w = 20f

        cp5.addSlider("threshold")
                .setPosition(w, h)
                .setSize(120, 20)
                .setValue(ThermalDetector.threshold.toFloat())
                .setRange(0f, 255f)
                .onChange { e ->
                    ThermalDetector.threshold = e.controller.value.toDouble()
                }

        cp5.addSlider("element")
                .setPosition(w + 220, h)
                .setSize(120, 20)
                .setValue(ThermalDetector.elementSize.toFloat())
                .setRange(1f, 20f)
                .onChange { e ->
                    ThermalDetector.elementSize = e.controller.value.toInt()
                }

        cp5.addSlider("min area")
                .setPosition(w + 440, h)
                .setSize(120, 20)
                .setValue(ThermalDetector.minAreaSize.toFloat())
                .setRange(0f, 5000f)
                .onChange { e ->
                    ThermalDetector.minAreaSize = e.controller.value.toInt()
                }
    }
}