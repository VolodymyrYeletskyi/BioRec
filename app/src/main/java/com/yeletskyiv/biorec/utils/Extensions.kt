package com.yeletskyiv.biorec.utils

import android.graphics.*

fun Bitmap.toGrayScale(width: Int, height: Int): Bitmap {
    val grayScaleBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(grayScaleBitmap)
    val paint = Paint()
    val colorMatrix = ColorMatrix()
    colorMatrix.setSaturation(0f)
    val filter = ColorMatrixColorFilter(colorMatrix)
    paint.colorFilter = filter
    canvas.drawBitmap(
        this,
        0f,
        0f,
        paint
    )
    return grayScaleBitmap
}

fun FloatArray.findMax(): Pair<Int, Float> {
    var max = this[0]
    for (index in 1 until size) {
        if (this[index] >= max)
            max = this[index]
    }
    return Pair(indexOfFirst { it == max }, max)
}