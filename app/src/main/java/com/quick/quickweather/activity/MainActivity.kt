package com.quick.quickweather.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.quick.quickweather.R

class MainActivity : AppCompatActivity() {

    private var isCourseLocationPermission = false
    private var isFineLocationPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermission()
    }

    private fun requestPermission() {
        var permissionLauncher: ActivityResultLauncher<Array<String>> =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                isCourseLocationPermission = permissions[Manifest.permission.ACCESS_COARSE_LOCATION]
                    ?: isCourseLocationPermission
                isFineLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION]
                    ?: isFineLocationPermission
            }

        allowMultiplePermission(permissionLauncher)
    }

    private fun allowMultiplePermission(permissionLauncher: ActivityResultLauncher<Array<String>>) {

        isCourseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        isFineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val permissionRequest: MutableList<String> = ArrayList()

        if (!isFineLocationPermission) {
            permissionRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (!isCourseLocationPermission) {
            permissionRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (permissionRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }

}