package com.app.newcameramlkitdemo

import android.graphics.Bitmap
import android.graphics.Canvas

class CameraImageGraphic(overlay: OverlayView?, private val bitmap: Bitmap) : OverlayView.Graphic(overlay) {
    override fun draw(canvas: Canvas?) {
        try {
            getTransformationMatrix()?.let { canvas?.drawBitmap(bitmap, it, null) }
        } catch (e: Exception) {
        }
    }
}