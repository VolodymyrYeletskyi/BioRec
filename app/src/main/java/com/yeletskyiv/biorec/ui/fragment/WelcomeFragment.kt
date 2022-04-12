package com.yeletskyiv.biorec.ui.fragment

import android.app.Activity
import android.content.Intent
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

        storage_button.setOnClickListener { openGalleryForImage() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == STORAGE_REQUEST_CODE)
            (activity as? MainActivity)?.replaceFragment(ImageFragment.create(data?.data))
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = INTENT_TYPE
        activity?.startActivityFromFragment(this, intent, STORAGE_REQUEST_CODE)
    }

    companion object {

        private const val INTENT_TYPE = "image/"

        private const val STORAGE_REQUEST_CODE = 69

        fun create() = WelcomeFragment()
    }
}