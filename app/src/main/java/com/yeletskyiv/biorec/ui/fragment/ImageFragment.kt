package com.yeletskyiv.biorec.ui.fragment

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.yeletskyiv.biorec.R
import com.yeletskyiv.biorec.viewmodel.ImageViewModel
import kotlinx.android.synthetic.main.fragment_image.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.tensorflow.lite.task.vision.detector.ObjectDetector

class ImageFragment(private val imageUri: Uri?) : Fragment(R.layout.fragment_image) {

    private val imageViewModel: ImageViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageViewModel.resultsLiveData.observe(viewLifecycleOwner) { updateResults(it) }

        working_image.setImageURI(imageUri)
        detect_button.setOnClickListener { recognizeObject() }
    }

    private fun recognizeObject() {
        val bitmap = working_image.drawable.toBitmap()

        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(5)
            .setScoreThreshold(0.5f)
            .build()
        val detector = ObjectDetector.createFromFileAndOptions(
            requireContext(),
            MODEL_NAME,
            options
        )

        imageViewModel.detectImage(bitmap, detector)

        text_layout.visibility = View.VISIBLE
    }

    private fun updateResults(resultToDisplay: List<Pair<RectF, String>>) {
        if (resultToDisplay.isNotEmpty()) {
            val pen = Paint()
            pen.color = ContextCompat.getColor(requireContext(), R.color.light_green)
            pen.style = Paint.Style.STROKE
            pen.strokeWidth = 18.0f
            val bitmap = working_image.drawable.toBitmap()
            val copyBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            val canvas = Canvas(copyBitmap)
            canvas.drawRect(resultToDisplay.first().first, pen)
            working_image.setImageBitmap(copyBitmap)

            result_text.text = resultToDisplay.first().second
        } else {
            result_text.text = "Object not found"
        }
    }

    companion object {

        private const val MODEL_NAME = "model.tflite"

        fun create(uri: Uri?) = ImageFragment(uri)
    }
}