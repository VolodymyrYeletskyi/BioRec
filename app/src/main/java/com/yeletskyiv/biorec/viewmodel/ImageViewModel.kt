package com.yeletskyiv.biorec.viewmodel

import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.RectF
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector

class ImageViewModel : ViewModel() {

    private val _resultsLiveData by lazy { MutableLiveData<List<Pair<RectF, String>>>() }
    val resultsLiveData: LiveData<List<Pair<RectF, String>>> = _resultsLiveData

    fun detectImage(bitmap: Bitmap, detector: ObjectDetector) = viewModelScope.launch(Dispatchers.Main) {
        val image = TensorImage.fromBitmap(bitmap)
        val results = viewModelScope.async { detector.detect(image) }
        val resultsPairs = results.await().map {
            // Get the top-1 category and craft the display text
            val category = it.categories.first()
            val text = "${category.label}, ${category.score.times(100).toInt()}%"

            // Create a data object to display the detection result
            Pair(it.boundingBox, text)
        }
        _resultsLiveData.postValue(resultsPairs)
    }
}