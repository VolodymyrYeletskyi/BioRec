package com.yeletskyiv.biorec.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.yeletskyiv.biorec.R
import com.yeletskyiv.biorec.ui.activity.MainActivity
import kotlinx.android.synthetic.main.fragment_welcome.*

class WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        camera_button.setOnClickListener { (activity as? MainActivity)?.openCamera() }

        storage_button.setOnClickListener { (activity as? MainActivity)?.openGalleryForImage() }
    }

    companion object {

        fun create() = WelcomeFragment()
    }
}