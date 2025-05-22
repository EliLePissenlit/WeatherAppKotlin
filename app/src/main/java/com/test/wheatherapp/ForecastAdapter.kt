package com.test.wheatherapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

// Classe de données pour chaque prévision
data class ForecastItem(
    val day: String,
    val temperature: Int,
    val weatherIconUrl: String
)

class ForecastAdapter(private var items: List<ForecastItem>) : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {
    class ForecastViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textDay: TextView = view.findViewById(R.id.text_day)
        val textTemp: TextView = view.findViewById(R.id.text_temp)
        val imgWeather: ImageView = view.findViewById(R.id.img_weather)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forecast, parent, false)
        return ForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val forecast = items[position]
        holder.textDay.text = forecast.day
        holder.textTemp.text = "${forecast.temperature}°"
        Glide.with(holder.itemView.context)
            .load(forecast.weatherIconUrl)
            .into(holder.imgWeather)
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<ForecastItem>) {
        items = newItems
        notifyDataSetChanged()
    }
} 