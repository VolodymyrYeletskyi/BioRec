package com.yeletskyiv.biorec.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.yeletskyiv.biorec.R
import kotlinx.android.synthetic.main.fragment_image.*

class ImageFragment(private val imageUri: Uri?) : Fragment(R.layout.fragment_image) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        working_image.setImageURI(imageUri)
    }

    companion object {

        fun create(uri: Uri?) = ImageFragment(uri)
    }
}