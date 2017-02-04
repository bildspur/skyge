package ch.bildspur.skyge

import processing.core.PGraphics

/**
 * Created by cansik on 04.02.17.
 */
fun Float.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

fun PGraphics.draw(block: (g: PGraphics) -> Unit) {
    this.beginDraw()
    block(this)
    this.endDraw()
}