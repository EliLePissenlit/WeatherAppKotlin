package com.test.wheatherapp

class WeatherRepository(private val api: WeatherApiService) {
    suspend fun getCurrentWeather(city: String) = api.getCurrentWeather(city)
    suspend fun getDailyForecast(city: String) = api.getDailyForecast(city)
} 