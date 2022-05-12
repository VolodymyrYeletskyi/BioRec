package com.yeletskyiv.biorec.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeletskyiv.biorec.ml.AnimalModel
import com.yeletskyiv.biorec.utils.findMax
import com.yeletskyiv.biorec.utils.toGrayScale
import kotlinx.coroutines.*
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import kotlin.math.abs

class ImageViewModel : ViewModel() {

    private val _neuralLiveData by lazy { MutableLiveData<Pair<Int, Float>>() }
    val neuralLiveData: LiveData<Pair<Int, Float>> = _neuralLiveData

    private val _patternLiveData by lazy { MutableLiveData<String>() }
    val patternLiveData: LiveData<String> = _patternLiveData

    fun detectImage(bitmap: Bitmap, model: AnimalModel) = viewModelScope.launch(Dispatchers.Default) {
        var image = TensorImage(DataType.FLOAT32)
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(180, 180, ResizeOp.ResizeMethod.BILINEAR))
            .build()
        image.load(bitmap)
        image = imageProcessor.process(image)

        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 180, 180, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(image.buffer)

        val outputs = model.process(inputFeature0).outputFeature0AsTensorBuffer.floatArray
        model.close()

        val results = outputs.findMax()
        _neuralLiveData.postValue(results)
    }

    fun recognizeImageByPattern(
        workingImage: Bitmap,
        patterns: List<Bitmap>,
        patternsLabels: List<String>
    ) = viewModelScope.launch(Dispatchers.Default) {
        val imageList = mutableListOf<Pair<String, Bitmap>>()
        imageList.add(Pair(
            "Photo",
            workingImage.toGrayScale(180, 180)
        ))
        imageList.addAll(patterns.map {
            Pair(patternsLabels[patterns.indexOf(it)], it.toGrayScale(30, 30))
        })
        val results = mutableListOf<Pair<String, Float>>()
        for (index in 1 until imageList.size) {
            results.add(withContext(Dispatchers.Default) {
                templateMatching(
                    imageList.first().second,
                    imageList[index]
                )
            })
        }
        val patternsResults = results.map { it.second }.toFloatArray()
        val indexOfMax = patternsResults.findMax().first
        _patternLiveData.postValue(results[indexOfMax].first)
    }

    private fun templateMatching(
        searchImage: Bitmap,
        patternImage: Pair<String, Bitmap>
    ): Pair<String,Float> {
        var best = 0f
        for (y in 0 until (searchImage.width - patternImage.second.width)) {
            for (x in 0 until (searchImage.height - patternImage.second.height)) {
                var sumAbsDiff = 0f

                for (i in 0 until patternImage.second.width) {
                    for (j in 0 until patternImage.second.height) {
                        val searchPixel = searchImage.getPixel(y + i, x + j)
                        val patternPixel = patternImage.second.getPixel(i, j)
                        sumAbsDiff += abs(searchPixel * searchPixel - 2 * searchPixel * patternPixel + patternPixel * patternPixel)
                    }
                }
                if (sumAbsDiff >= best)
                    best = sumAbsDiff
            }
        }
        return Pair(patternImage.first, best)
    }
}