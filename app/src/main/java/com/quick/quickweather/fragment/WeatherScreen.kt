package com.quick.quickweather.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import com.quick.quickweather.R
import com.quick.quickweather.databinding.FragmentWeatherScreenBinding
import com.quick.quickweather.fragment.viewModel.WeatherViewModel
import com.quick.quickweather.util.animateImage
import com.quick.quickweather.util.getCity
import kotlinx.coroutines.launch

class WeatherScreen : Fragment() {

    private var searchBinding: FragmentWeatherScreenBinding? = null
    private val binding get() = searchBinding!!
    private val weatherViewModel: WeatherViewModel by viewModels()
    var mFusedLocationClient: FusedLocationProviderClient? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        searchBinding = FragmentWeatherScreenBinding.inflate(layoutInflater, container, false)
        binding.viewModel = weatherViewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        animateImage(binding.imageView1)
        lifecycleScope.launch {
            context?.let { getLastLocation(it) }
        }
        binding.retryBtn.setOnClickListener {
            context?.let { getLastLocation(it) }
        }
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation(context: Context) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        if (isLocationEnabled(context)) {
            mFusedLocationClient?.lastLocation?.addOnCompleteListener { task ->
                val location = task.result
                if (location == null) {
                    requestNewLocationData(context)
                } else {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    var city = getCity(latitude, longitude, context)
                    weatherViewModel.loadData(
                        city,
                        getString(R.string.apiKey),
                        requireContext(),
                        binding.forecastRvlist
                    )
                }
            }
        } else {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }

    }


    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    fun requestNewLocationData(context: Context) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 5
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location? = locationResult.lastLocation
            if (mLastLocation != null) {
                val latitude = mLastLocation.latitude
                val longitude = mLastLocation.longitude
                var city = getCity(latitude, longitude, requireContext())
                weatherViewModel.loadData(
                    city,
                    getString(R.string.apiKey),
                    requireContext(),
                    binding.forecastRvlist
                )
            }
        }
    }


}