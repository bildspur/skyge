package ch.bildspur.skyge

/**
 * Created by cansik on 04.02.17.
 */
fun Float.format(digits: Int) = java.lang.String.format("%.${digits}f", this)