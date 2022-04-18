package com.yeletskyiv.biorec.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.yeletskyiv.biorec.R
import com.yeletskyiv.biorec.ui.base.BaseActivity
import com.yeletskyiv.biorec.ui.fragment.CameraFragment
import com.yeletskyiv.biorec.ui.fragment.ImageFragment
import com.yeletskyiv.biorec.ui.fragment.WelcomeFragment

class MainActivity : BaseActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupFragment(WelcomeFragment.create())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (allPermissionsGranted())
                replaceFragment(CameraFragment.create())
            else {
                Toast.makeText(this, "No permissions", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == STORAGE_REQUEST_CODE)
            replaceFragment(ImageFragment.create(data?.data))
    }

    fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = INTENT_TYPE
        startActivityForResult(intent, STORAGE_REQUEST_CODE)
    }

    fun openCamera() {
        if (allPermissionsGranted())
            replaceFragment(CameraFragment.create())
        else
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, CAMERA_REQUEST_CODE)
    }

    private fun setupFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {

        private const val INTENT_TYPE = "image/"

        private const val STORAGE_REQUEST_CODE = 18

        private const val CAMERA_REQUEST_CODE = 69

        private val REQUIRED_PERMISSIONS =
            mutableListOf (Manifest.permission.CAMERA).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}