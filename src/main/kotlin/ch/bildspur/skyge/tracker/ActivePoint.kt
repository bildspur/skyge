package ch.bildspur.skyge.tracker

import org.opencv.core.Point

/**
 * Created by cansik on 12.02.17.
 */
class ActivePoint(val point: Point) {
    var used = false
}