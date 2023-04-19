package com.quick.quickweather.fragment.viewModel

import android.content.Context
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.quick.quickweather.config.APIServices
import com.quick.quickweather.fragment.adapter.ForecastWeatherAdapter
import com.quick.quickweather.fragment.model.Example
import com.quick.quickweather.fragment.model.WeatherList
import com.quick.quickweather.util.convertIntoCelsius
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherViewModel : ViewModel() {

    val tempLiveData = MutableLiveData("")
    val cityLiveData = MutableLiveData("")
    val onError = ObservableBoolean(false)
    val onSuccess = ObservableBoolean(false)

    fun loadData(city: String, apiKey: String, context: Context, recyclerView: RecyclerView) {
        viewModelScope.launch {
            async {
                getCurrentWeather(city, apiKey)
                getForecastWeather(city, apiKey, context, recyclerView)
            }
        }
    }

    private fun getCurrentWeather(city: String, apiKey: String) {
        val call: Call<Example> = APIServices.apiServices.getWeather(city, apiKey)
        call.enqueue(object : Callback<Example> {
            override fun onResponse(call: Call<Example>, response: Response<Example>) {
                if (response.code() == 404) {
                    onFailureObserver()
                } else {
                    onSuccessObserver()
                    val data = response.body()
                    val main = data?.main
                    val temperature = main?.temp
                    if (temperature != null) {
                        tempLiveData.value = "${convertIntoCelsius(temperature)}°"
                    }
                    cityLiveData.value = data?.name
                }
            }

            override fun onFailure(call: Call<Example>, t: Throwable?) {
                onFailureObserver()
                Log.i("onFailure: ", t.toString())
            }

        })
    }

    private fun onSuccessObserver() {
        onError.set(false)
        onSuccess.set(true)
    }

    private fun onFailureObserver() {
        onError.set(true)
        onSuccess.set(false)
    }

    private fun getForecastWeather(
        city: String,
        apiKey: String,
        context: Context,
        recyclerView: RecyclerView
    ) {
        val weatherList = ArrayList<WeatherList>()

        val call: Call<Example> = APIServices.apiServices.getForecastWeather(city, apiKey)
        call.enqueue(object : Callback<Example> {
            override fun onResponse(call: Call<Example>, response: Response<Example>) {
                if (response.code() == 404) {
                    onFailureObserver()
                } else {
                    onSuccessObserver()
                    weatherList.clear()
                    val data = response.body()
                    val weatherL = data?.list

                    if (weatherL != null) {
                        for (i in weatherL.indices) {
                            weatherList.addAll(listOf(weatherL[i]))
                            weatherList.distinctBy { it.visibility }
                        }
                    }
                }
                weatherList.distinctBy { it.pop }
                val weatherAdapter = ForecastWeatherAdapter(context, weatherList)
                recyclerView.adapter = weatherAdapter
            }

            override fun onFailure(call: Call<Example>, t: Throwable?) {
                onFailureObserver()
                Log.i("onFailure: ", t.toString())
            }

        })
    }


}