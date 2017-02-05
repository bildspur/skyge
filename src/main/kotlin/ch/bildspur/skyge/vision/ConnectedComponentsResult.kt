package ch.bildspur.skyge.vision

import org.opencv.core.Mat

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
}