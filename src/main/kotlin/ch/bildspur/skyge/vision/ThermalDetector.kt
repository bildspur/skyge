package ch.bildspur.skyge.vision

import ch.bildspur.skyge.Sketch
import ch.bildspur.skyge.draw
import ch.bildspur.skyge.toMat
import ch.bildspur.skyge.toPImage
import org.opencv.core.CvType
import org.opencv.core.Mat
import processing.core.PConstants


/**
 * Created by cansik on 04.02.17.
 */
object ThermalDetector {

    fun detect(ti: ThermalImage) {
        val image = Mat(ti.input.height, ti.input.width, CvType.CV_8UC4)
        val output = Sketch.instance.createImage(ti.input.width, ti.input.height, PConstants.ARGB)

        ti.input.toMat(image)

        image.toPImage(output)

        ti.output.draw {
            it.image(output, 0f, 0f)
        }
    }
}