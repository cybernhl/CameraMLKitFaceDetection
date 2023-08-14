package com.app.newcameramlkitdemo

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceLandmark

/** Graphic instance for rendering face contours graphic overlay view.  */
class FaceContourGraphic(
    overlay: OverlayView,
    private val firebaseVisionFace: Face?
) : OverlayView.Graphic(overlay) {

    private val facePositionPaint: Paint
    private val idPaint: Paint
    private val boxPaint: Paint

    init {
        val selectedColor = Color.WHITE

        facePositionPaint = Paint()
        facePositionPaint.color = selectedColor

        idPaint = Paint()
        idPaint.color = selectedColor
        idPaint.textSize = ID_TEXT_SIZE

        boxPaint = Paint()
        boxPaint.color = selectedColor
        boxPaint.style = Paint.Style.STROKE
        boxPaint.strokeWidth = BOX_STROKE_WIDTH
    }

    /** Draws the face annotations for position on the supplied canvas.  */
    override fun draw(canvas: Canvas?) {
        try {
            val face = firebaseVisionFace ?: return

            // Draws a circle at the position of the detected face, with the face's track id below.
            val x = translateX(face.boundingBox.centerX().toFloat())
            val y = translateY(face.boundingBox.centerY().toFloat())
//        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, facePositionPaint)
//        canvas.drawText("id: ${face.trackingId}", x + ID_X_OFFSET, y + ID_Y_OFFSET, idPaint)

            // Draws a bounding box around the face.
            val left = x - scale(face.boundingBox.width() / 2.0f)
            val top = y - scale(face.boundingBox.height() / 2.0f)
            val right = x + scale(face.boundingBox.width() / 2.0f)
            val bottom = y + scale(face.boundingBox.height() / 2.0f)

            canvas?.drawRect(left, top, right, bottom, boxPaint)

            val contour = face.allContours
            for (faceContour in contour) {
                for (point in faceContour.points) {
                    val px = translateX(point.x)
                    val py = translateY(point.y)
                    canvas?.drawCircle(px, py, FACE_POSITION_RADIUS, facePositionPaint)
                }
            }
            val leftEye = face.getLandmark(FaceLandmark.LEFT_EYE)
            leftEye?.position?.let {
                canvas?.drawCircle(
                        translateX(it.x),
                        translateY(it.y),
                        FACE_POSITION_RADIUS,
                        facePositionPaint
                )
            }
            val rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE)
            rightEye?.position?.let {
                canvas?.drawCircle(
                        translateX(it.x),
                        translateY(it.y),
                        FACE_POSITION_RADIUS,
                        facePositionPaint
                )
            }
            val leftCheek = face.getLandmark(FaceLandmark.LEFT_CHEEK)
            leftCheek?.position?.let {
                canvas?.drawCircle(
                        translateX(it.x),
                        translateY(it.y),
                        FACE_POSITION_RADIUS,
                        facePositionPaint
                )
            }

            val rightCheek = face.getLandmark(FaceLandmark.RIGHT_CHEEK)
            rightCheek?.position?.let {
                canvas?.drawCircle(
                        translateX(it.x),
                        translateY(it.y),
                        FACE_POSITION_RADIUS,
                        facePositionPaint
                )
            }
        } catch (e: Exception) {
        }
    }

    companion object {
        private const val FACE_POSITION_RADIUS = 4.0f
        private const val ID_TEXT_SIZE = 30.0f
        private const val BOX_STROKE_WIDTH = 5.0f
    }
}
