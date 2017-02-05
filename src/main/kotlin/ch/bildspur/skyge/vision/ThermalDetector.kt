package ch.bildspur.skyge.vision

import ch.bildspur.skyge.*
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import processing.core.PApplet
import processing.core.PConstants


/**
 * Created by cansik on 04.02.17.
 */
object ThermalDetector {

    val imageOutput = true

    val threshold = 200.0
    val elementSize = 1
    val minAreaSize = 500

    init {

    }

    fun detect(ti: ThermalImage) {
        val image = Mat(ti.input.height, ti.input.width, CvType.CV_8UC4)

        ti.input.toMat(image)
        val gray = image.copy()
        gray.gray()
        image.toBGRA(image)

        // threshold
        // todo: implement intelligent threshold
        gray.threshold(threshold)

        // remove small parts
        gray.erode(elementSize)
        gray.dilate(elementSize)

        // detect areas (connected-component analysis)
        val components = gray.connectedComponentsWithStats()


        if (imageOutput)
            createOutput(ti, image, gray, components)

        // free memory
        gray.release()
        image.release()
        components.release()
    }

    private fun createOutput(ti: ThermalImage, image: Mat, gray: Mat, components: ConnectedComponentsResult) {
        // create mask to draw filtered components
        val mask = gray.zeros()

        // create blob map
        componentCleanup@ for (label in 1..components.length - 1) {
            val areaSize = components.rectComponents[label, Imgproc.CC_STAT_AREA][0]

            if (areaSize < minAreaSize)
                continue@componentCleanup

            val labeledMask = components.labeled.getRegionMask(label)
            Core.add(mask, labeledMask, mask)
            labeledMask.release()
        }

        val output = Sketch.instance.createImage(ti.input.width, ti.input.height, PConstants.ARGB)
        val overlay = Sketch.instance.createImage(ti.input.width, ti.input.height, PConstants.ARGB)
        ti.createOutputImage()

        image.toPImage(output)
        mask.toPImage(overlay)

        ti.output!!.draw {
            it.blendMode(PApplet.BLEND)
            it.image(output, 0f, 0f)

            it.blendMode(PApplet.BLEND)
            it.image(overlay, 0f, 0f)

            it.strokeWeight(2f)
            it.stroke(255f, 0f, 0f)

            // draw cross on regions
            for (label in 1..components.length - 1) {
                val areaSize = components.rectComponents[label, Imgproc.CC_STAT_AREA][0]

                if (areaSize >= minAreaSize) {
                    val centroid = components.getCentroid(label)

                    val x = centroid.x.toFloat()
                    val y = centroid.y.toFloat()

                    val size = 15f

                    it.line(x, y - size, x, y + size)
                    it.line(x - size, y, x + size, y)
                }
            }

            // free memory
            it.removeCache(output)
            it.removeCache(overlay)
        }
    }
}