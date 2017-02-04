package ch.bildspur.skyge.vision

import ch.bildspur.skyge.draw

/**
 * Created by cansik on 04.02.17.
 */
object ThermalDetector {

    fun detect(ti: ThermalImage) {
        ti.output.draw {
            it.image(ti.input, 0f, 0f)
            it.noFill()
            it.stroke(0f, 255f, 0f)
            it.strokeWeight(5f)
            val size = (Math.random() * 100f).toFloat()
            it.ellipse(it.width / 2f, it.height / 2f, 50f + size, 50f + size)
        }
    }
}