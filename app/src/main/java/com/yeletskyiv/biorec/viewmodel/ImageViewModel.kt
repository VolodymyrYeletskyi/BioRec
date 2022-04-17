package com.yeletskyiv.biorec.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.ObjectDetector

class ImageViewModel : ViewModel() {

    private val _resultsLiveData by lazy { MutableLiveData<List<List<Any>>>() }
    val resultsLiveData: LiveData<List<List<Any>>> = _resultsLiveData

    fun detectImage(bitmap: Bitmap, detector: ObjectDetector) = viewModelScope.launch(Dispatchers.Default) {
        val image = TensorImage.fromBitmap(bitmap)
        delay(2000)
        val results = viewModelScope.async { detector.detect(image) }
        val resultsPairs = results.await().map {
            // Get the top-1 category and craft the display text
            val category = it.categories.first()
            val name = category.label
            val score = "${category.score.times(100).toInt()}%"

            // Create a data object to display the detection result
            listOf(it.boundingBox, name, score)
        }
        _resultsLiveData.postValue(resultsPairs)
    }
}