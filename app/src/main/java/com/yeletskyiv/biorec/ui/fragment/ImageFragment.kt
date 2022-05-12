package com.yeletskyiv.biorec.ui.fragment

import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.yeletskyiv.biorec.R
import com.yeletskyiv.biorec.ml.AnimalModel
import com.yeletskyiv.biorec.ui.activity.MainActivity
import com.yeletskyiv.biorec.viewmodel.ImageViewModel
import kotlinx.android.synthetic.main.fragment_image.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ImageFragment(private val imageUri: Uri?) : Fragment(R.layout.fragment_image) {

    private val imageViewModel: ImageViewModel by viewModel()

    private val labels = listOf(
        "Butterfly",
        "Cat",
        "Chicken",
        "Cow",
        "Dog",
        "Elephant",
        "Horse",
        "Sheep",
        "Spider",
        "Squirrel"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageViewModel.neuralLiveData.observe(viewLifecycleOwner) { showNeuralResults(it) }
        imageViewModel.patternLiveData.observe(viewLifecycleOwner) { showPatternResults((it)) }

        working_image.setImageURI(imageUri)

        detect_button.setOnClickListener { getNeuralRecognition() }
        pattern_button.setOnClickListener { getPatternRecognition() }

        photo_button.setOnClickListener { (activity as MainActivity).replaceFragment(CameraFragment.create()) }
        storage_button.setOnClickListener { (activity as? MainActivity)?.openGalleryForImage() }
    }

    private fun getNeuralRecognition() {
        loader.show()

        val model = AnimalModel.newInstance(requireContext())
        val bitmap = working_image.drawable.toBitmap()

        recognition_layout.visibility = View.INVISIBLE
        imageViewModel.detectImage(bitmap, model)
    }

    private fun getPatternRecognition() {
        loader.show()

        val bitmap = working_image.drawable.toBitmap()

        recognition_layout.visibility = View.INVISIBLE
        imageViewModel.recognizeImageByPattern(
            bitmap,
            initPatterns(),
            labels
        )
    }

    private fun showNeuralResults(resultToDisplay: Pair<Int, Float>) {
        name_layout.visibility = View.VISIBLE
        bottom_buttons_layout.visibility = View.VISIBLE

        name_text.text = labels[resultToDisplay.first]

        loader.hide()
    }

    private fun showPatternResults(result: String) {
        name_layout.isVisible = true
        bottom_buttons_layout.visibility = View.VISIBLE

        name_text.text = result

        loader.hide()
    }

    private fun initPatterns() = listOf(
        BitmapFactory.decodeResource(resources, R.drawable.butterfly_template),
        BitmapFactory.decodeResource(resources, R.drawable.cat_template),
        BitmapFactory.decodeResource(resources, R.drawable.chicken_template),
        BitmapFactory.decodeResource(resources, R.drawable.cow_template),
        BitmapFactory.decodeResource(resources, R.drawable.dog_template),
        BitmapFactory.decodeResource(resources, R.drawable.elephant_template),
        BitmapFactory.decodeResource(resources, R.drawable.horse_template),
        BitmapFactory.decodeResource(resources, R.drawable.sheep_template),
        BitmapFactory.decodeResource(resources, R.drawable.spider_template),
        BitmapFactory.decodeResource(resources, R.drawable.squirrel_template)
    )

    companion object {

        fun create(uri: Uri?) = ImageFragment(uri)
    }
}