package ch.fhnw.afpars.util.opencv

import ch.bildspur.skyge.Sketch
import org.opencv.core.Point

/**
 * Created by cansik on 06.01.17.
 */
fun MutableList<Point>.sparsePoints(maxDistance: Double): MutableList<MutableList<Point>> {
    val sparseCloud = mutableListOf<MutableList<Point>>()
    val points = this.toMutableList()

    while (!points.isEmpty()) {
        val firstPoint = points.removeAt(0)
        val group = mutableListOf<Point>()
        val nearPoints = mutableListOf(firstPoint)

        while (!nearPoints.isEmpty()) {
            val point = nearPoints.removeAt(0)
            group.add(point)

            for (p in points.toList()) {
                // calculate distance
                val dist = point.distance(p)

                if (dist < maxDistance) {
                    points.remove(p)
                    nearPoints.add(p)
                }
            }
        }

        sparseCloud.add(group)
    }

    return sparseCloud
}

fun MutableList<MutableList<Point>>.combinePoints(): MutableList<Point> {
    val sparsePoints = mutableListOf<Point>()
    this.mapTo(sparsePoints) { pointList -> Point(pointList.map { it.x }.average(), pointList.map { it.y }.average()) }
    return sparsePoints
}

fun Point.distance(p2: Point): Double {
    return Math.sqrt(Math.pow(p2.x - this.x, 2.0) + Math.pow(p2.y - this.y, 2.0))
}

fun Point.map(x1: Double, y1: Double, x2: Double, y2: Double): Point {
    return Point(Sketch.map(this.x, 0.0, x1, 0.0, x2), Sketch.map(this.y, 0.0, y1, 0.0, y2))
}