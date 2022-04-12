package com.yeletskyiv.biorec.ui.fragment

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.ScaleGestureDetector
import android.view.View
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.yeletskyiv.biorec.R
import com.yeletskyiv.biorec.ui.activity.MainActivity
import kotlinx.android.synthetic.main.fragment_camera.*
import java.text.SimpleDateFormat
import java.util.*

class CameraFragment : Fragment(R.layout.fragment_camera) {

    private var imageCapture: ImageCapture? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startCamera()

        take_image_button.setOnClickListener { takePhoto() }
    }

    private fun createZoomListener(cameraInfo: CameraInfo, cameraControl: CameraControl): ScaleGestureDetector {
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val currentZoomRatio = cameraInfo.zoomState.value?.zoomRatio ?: 0f

                val delta = detector.scaleFactor

                cameraControl.setZoomRatio(currentZoomRatio * delta)

                return true
            }
        }
        return ScaleGestureDetector(requireContext(), listener)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(camera_preview.surfaceProvider) }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            bindCamera(cameraProvider, cameraSelector, preview)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindCamera(
        cameraProvider: ProcessCameraProvider,
        cameraSelector: CameraSelector,
        preview: Preview
    ) {
        try {
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageCapture
            )

            val scaleGestureDetector = createZoomListener(camera.cameraInfo, camera.cameraControl)
            camera_preview.setOnTouchListener { _, motionEvent ->
                scaleGestureDetector.onTouchEvent(motionEvent)
                true
            }

        } catch (exception: Exception) {
            Log.e("TAG", "Binding failed", exception)
        }
    }

    private fun takePhoto() {
        activity?.let { activity ->
            if (imageCapture != null) {
                loader.show()

                // Create time stamped name and MediaStore entry.
                val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                    .format(System.currentTimeMillis())
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
                    }
                }

                // Create output options object which contains file + metadata
                val outputOptions = ImageCapture.OutputFileOptions
                    .Builder(
                        activity.contentResolver,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                    )
                    .build()

                imageCapture?.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(activity),
                    object : ImageCapture.OnImageSavedCallback {

                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            loader.hide()
                            (activity as? MainActivity)?.replaceFragment(ImageFragment.create(outputFileResults.savedUri))
                        }

                        override fun onError(exception: ImageCaptureException) {
                            loader.hide()
                            Log.e("TAG", "Photo capture failed: ${exception.message}", exception)
                        }
                    }
                )
            }
        }
    }

    companion object {

        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

        fun create() = CameraFragment()
    }
}