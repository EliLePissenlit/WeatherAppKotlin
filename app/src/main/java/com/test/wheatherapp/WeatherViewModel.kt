package com.test.wheatherapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import kotlinx.coroutines.launch
import android.util.Log

class WeatherViewModel : ViewModel() {
    private val repository = WeatherRepository(RetrofitInstance.api)

    private val _city = MutableLiveData<String>("Paris")
    val city: LiveData<String> = _city

    private val _weather = MutableLiveData<WeatherResponse?>()
    val weather: LiveData<WeatherResponse?> = _weather

    private val _forecast = MutableLiveData<ForecastResponse?>()
    val forecast: LiveData<ForecastResponse?> = _forecast

    fun setCity(newCity: String) {
        _city.value = newCity
        fetchWeather()
    }

    fun fetchWeather() {
        val currentCity = _city.value ?: return
        viewModelScope.launch {
            try {
                _weather.value = repository.getCurrentWeather(currentCity)
                _forecast.value = repository.getDailyForecast(currentCity)
            } catch (e: Exception) {
                _weather.value = null
                _forecast.value = null
            }
        }
    }
} 