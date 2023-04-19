package com.quick.quickweather.fragment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quick.quickweather.R
import com.quick.quickweather.databinding.LayoutForecastItemBinding
import com.quick.quickweather.fragment.model.WeatherList
import com.quick.quickweather.util.convertIntoCelsius
import com.quick.quickweather.util.getDayOfWeek

class ForecastWeatherAdapter(var context: Context, users: ArrayList<WeatherList>) :
    RecyclerView.Adapter<ForecastWeatherAdapter.UsersViewHolder>() {

    var weatherList: ArrayList<WeatherList> = users

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.layout_forecast_item, parent, false)
        return UsersViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val list = weatherList[position]
        holder.binding.dayTv.text = list.dt?.let { getDayOfWeek(it) }
        var temp = list.main?.temp?.let { convertIntoCelsius(it) }
        holder.binding.tempTv.text = "$temp C"

    }


    override fun getItemCount(): Int {
        return weatherList.size
    }

    inner class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: LayoutForecastItemBinding = LayoutForecastItemBinding.bind(itemView)

    }

    init {
        this.weatherList = users
    }
}
