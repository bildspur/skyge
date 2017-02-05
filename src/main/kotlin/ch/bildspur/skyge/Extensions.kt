package ch.bildspur.skyge

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import processing.core.PGraphics
import processing.core.PImage
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*


/**
 * Created by cansik on 04.02.17.
 */
fun Float.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

fun PGraphics.draw(block: (g: PGraphics) -> Unit) {
    this.beginDraw()
    block(this)
    this.endDraw()
}

fun PImage.toMat(m: Mat) {
    val matPixels = ((this.native as BufferedImage).raster.dataBuffer as DataBufferInt).data

    val bb = ByteBuffer.allocate(matPixels.size * 4)
    val ib = bb.asIntBuffer()
    ib.put(matPixels)

    val bvals = bb.array()

    m.put(0, 0, bvals)
}

fun Mat.toPImage(img: PImage) {
    img.loadPixels()

    if (this.channels() === 3) {
        val m2 = Mat()
        Imgproc.cvtColor(this, m2, Imgproc.COLOR_RGB2RGBA)
        img.pixels = m2.toARGBPixels()
    } else if (this.channels() === 1) {
        val m2 = Mat()
        Imgproc.cvtColor(this, m2, Imgproc.COLOR_GRAY2RGBA)
        img.pixels = m2.toARGBPixels()
    } else if (this.channels() === 4) {
        img.pixels = this.toARGBPixels()
    }

    img.updatePixels()
}

fun Mat.toARGBPixels(): IntArray {
    val pImageChannels = 4
    val numPixels = this.width() * this.height()
    val intPixels = IntArray(numPixels)
    val matPixels = ByteArray(numPixels * pImageChannels)

    this.get(0, 0, matPixels)
    ByteBuffer.wrap(matPixels).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(intPixels)
    return intPixels
}

fun Mat.toBGRA(bgra: Mat) {
    val channels = ArrayList<Mat>()
    Core.split(this, channels)

    val reordered = ArrayList<Mat>()
    // Starts as ARGB.
    // Make into BGRA.

    reordered.add(channels[3])
    reordered.add(channels[2])
    reordered.add(channels[1])
    reordered.add(channels[0])

    Core.merge(reordered, bgra)
}