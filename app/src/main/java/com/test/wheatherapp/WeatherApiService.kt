package com.test.wheatherapp

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String = "c57b3b6188e2e24926a62a3d3e616905",
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "fr"
    ): WeatherResponse

    @GET("forecast/daily")
    suspend fun getDailyForecast(
        @Query("q") city: String,
        @Query("cnt") days: Int = 7,
        @Query("appid") apiKey: String = "c57b3b6188e2e24926a62a3d3e616905",
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "fr"
    ): ForecastResponse
}

fun getWeatherIconName(iconCode: String): String {
    return when (iconCode) {
        // Ciel dégagé
        "01d" -> "ic_sunny"
        "01n" -> "ic_nighty"
        // Peu nuageux
        "02d" -> "ic_cloudy_sunny"
        "02n" -> "ic_cloudy_nighty"
        // Nuageux
        "03d", "03n" -> "ic_cloudy"
        "04d", "04n" -> "ic_cloudy"
        // Pluie
        "09d", "09n" -> "ic_rainy"
        "10d", "10n" -> "ic_cloudy_rainy"
        // Orage
        "11d", "11n" -> "ic_stormy"
        // Neige
        "13d", "13n" -> "ic_snowy"
        // Brouillard
        "50d", "50n" -> "ic_foggy"
        // Cas spéciaux (exemples, adapte selon tes besoins et tes icônes)
        // Vent
        // "windy" -> "ic_windy"
        // Combinés
        // Ajoute ici d'autres cas selon tes icônes complexes
        else -> "ic_sunny" // Par défaut
    }
} 