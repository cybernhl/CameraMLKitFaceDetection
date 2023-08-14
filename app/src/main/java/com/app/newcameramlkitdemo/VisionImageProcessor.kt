package com.app.newcameramlkitdemo

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.app.newcameramlkitdemo.FrameMetadata
import com.app.newcameramlkitdemo.GraphicOverlay
import com.google.mlkit.common.MlKitException
import java.nio.ByteBuffer

/** An inferface to process the images with different ML Kit detectors and custom image models.  */
interface VisionImageProcessor {
    /** Processes the images with the underlying machine learning models.  */
    @Throws(MlKitException::class)
    fun process(data: ByteBuffer?, frameMetadata: FrameMetadata?, graphicOverlay: OverlayView?)

    @Throws(MlKitException::class)
    fun processByteBuffer(
        data: ByteBuffer?, frameMetadata: FrameMetadata?, graphicOverlay: OverlayView?)

    /** Processes the bitmap images.  */
    fun process(bitmap: Bitmap?, graphicOverlay: OverlayView?)

    @Throws(MlKitException::class)
    fun processImageProxy(image: ImageProxy?, graphicOverlay: OverlayView?)

    /** Stops the underlying machine learning model and release resources.  */
    fun stop()
}