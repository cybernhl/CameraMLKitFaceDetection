package com.app.newcameramlkitdemo

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.OrientationEventListener
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.app.newcameramlkitdemo.databinding.ActivityCameraBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity(), FaceContourDetectorProcessor.FaceContourDetectorListener {
    var mCameraManager: CameraManager? = null
    lateinit var mainBainding: ActivityCameraBinding
    var mBitmap:Bitmap?=null
    private var bitmapCompressFormat = Bitmap.CompressFormat.JPEG
    var orientationEventListener: OrientationEventListener? = null
    private var isPortraitMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBainding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_camera)
        setupPermissions()
        init()
    }


    private fun init(){
        try {
            mainBainding.buttonContinue.setOnClickListener {
                mainBainding.cardview.visibility = View.VISIBLE
                mainBainding.previewView.visibility =View.GONE
                mainBainding.faceOverlay.visibility = View.GONE
                mainBainding.buttonContinue.visibility =View.GONE
                val file = File(cacheDir, "Selfie.jpg")
                file.createNewFile()

                //Convert bitmap to byte array
                val bos = ByteArrayOutputStream()
                mBitmap!!.compress(bitmapCompressFormat, 100, bos)
                val bitmapData = bos.toByteArray()

                //write the bytes in file
                val fos = FileOutputStream(file)
                fos.write(bitmapData)
                fos.flush()
                fos.close()
                val bmImg = flip(BitmapFactory.decodeFile(file.absolutePath))
                Glide.with(this)
                    .asBitmap()
                    .load(bmImg)
                    .apply(RequestOptions().centerInside())
                    .into(mainBainding.image)
            }

            orientationEventListener = object : OrientationEventListener(this, SensorManager.SENSOR_DELAY_UI) {
                override fun onOrientationChanged(orientation: Int) {
                    try {
                        val thresoldValue = (orientation + 45) / 90 % 4
                        /*thresoldValue=1,2,3 for left & right lanscape & downside portrait*/
                        isPortraitMode = thresoldValue == 0
                    } catch (e: Exception) {
                    }
                }
            }
            if (orientationEventListener?.canDetectOrientation() == true) {
                orientationEventListener?.enable()
            }

        }catch (e:Exception){

        }
    }

    fun flip(src: Bitmap): Bitmap? {
        // create new matrix for transformation
        return try {
            val matrix = Matrix()
            matrix.preScale(-1.0f, 1.0f)
            // return transformed image
            Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
        } catch (e: Exception) {
            null
        }
    }

    private fun createCameraSource() {
        try {
            // If there's no existing cameraSource, create one.
            if (mCameraManager == null) {
                mCameraManager = CameraManager(
                    this@MainActivity,
                    mainBainding.previewView,
                    this,
                    mainBainding.faceOverlay,
                    this
                )
            }
            if (mCameraManager != null) {
                mCameraManager?.startCamera()
            }
        } catch (e: Exception) {

        }
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("TAG", "Permission to record denied")
            makeRequest()
        } else {
            createCameraSource()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.CAMERA),
            1
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i("TAG", "Permission has been denied by user")
                } else {
                    createCameraSource()
                    Log.i("TAG", "Permission has been granted by user")
                }
            }
        }
    }

    override fun onCapturedFace(originalCameraImage: Bitmap) {
        try {
            Log.e(" face detected", "face detected")
            if (isPortraitMode) {
                mainBainding.faceOverlay.border.color = Color.GREEN
                mainBainding.faceOverlay.invalidate()
                mainBainding.buttonContinue.isEnabled = true

                mBitmap = originalCameraImage
            }

        } catch (e: Exception) {
            e.stackTrace
        }
    }

    override fun onNoFaceDetected() {
        try {
            mainBainding.faceOverlay.border.color = Color.RED
            mainBainding.faceOverlay.invalidate()
            Log.e("no face detected", "no face detected")
            mainBainding.buttonContinue.isEnabled =false
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    override fun onDestroy() {
        super.onDestroy()
            try {
                if (orientationEventListener != null) {
                    orientationEventListener!!.disable()
                }
                if (mCameraManager != null && mCameraManager!!.imageProcessor != null) {
                    mCameraManager!!.imageProcessor!!.stop()
                }
            } catch (e: Exception) {
            }
    }
}