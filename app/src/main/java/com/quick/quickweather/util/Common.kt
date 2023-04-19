package com.quick.quickweather.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val REQUESTCODE = 100

fun animateImage(image: ImageView) {
    val rotate = RotateAnimation(
        0f, 360f,
        Animation.RELATIVE_TO_SELF, 0.5f,
        Animation.RELATIVE_TO_SELF, 0.5f
    )

    rotate.duration = 900
    rotate.repeatCount = Animation.INFINITE
    image.startAnimation(rotate)
}

fun convertIntoCelsius(temp: Double): String {
    return (temp.minus(273.15)).toInt().toString()
}

fun getDayOfWeek(timestamp: Long): String {
    return SimpleDateFormat("EEEE", Locale.ENGLISH).format(timestamp * 1000)
}

fun askPermission(activity: Activity) {
    ActivityCompat.requestPermissions(
        activity,
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
        REQUESTCODE
    )
}

fun getCity(latitude: Double, longitude: Double, context: Context): String {
    val result = StringBuilder()
    val mainAddress: Address
    var city: String? = null

    try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(latitude,longitude,1) as List<Address>
        if (addresses.isNotEmpty()) {
            mainAddress = addresses[0]
            result.append(mainAddress.locality).append("\n")
            result.append(mainAddress.countryName)
            city = addresses[0].locality
        }
    } catch (e: IOException) {
        Log.e("tag", e.message.toString())
    }
    return city.toString()
}

fun String.toDate(): Date{
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(this)
}

