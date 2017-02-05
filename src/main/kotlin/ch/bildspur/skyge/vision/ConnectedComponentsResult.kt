package ch.bildspur.skyge.vision

import org.opencv.core.Mat
import org.opencv.core.Point

/**
 * Created by cansik on 05.02.17.
 */
data class ConnectedComponentsResult(val labeled: Mat, val rectComponents: Mat, val centComponents: Mat) {
    fun release() {
        labeled.release()
        rectComponents.release()
        centComponents.release()
    }

    val length: Int
        get() = centComponents.size().height.toInt()

    fun getCentroid(componentId: Int): Point {
        val centroidInfo = DoubleArray(2)
        centComponents.row(componentId).get(0, 0, centroidInfo)
        return Point(centroidInfo[0], centroidInfo[1])
    }

    fun getConnectedComponents(): List<ConnectedComponent> {
        return (0..length - 1).map { ConnectedComponent(it, rectComponents.row(it), centComponents.row(it)) }
    }
}