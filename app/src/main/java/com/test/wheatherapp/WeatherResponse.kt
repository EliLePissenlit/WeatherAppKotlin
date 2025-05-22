package com.test.wheatherapp

data class WeatherResponse(
    val name: String,
    val main: Main,
    val weather: List<Weather>,
    val dt: Long,
    val coord: Coord
)

data class Main(
    val temp: Float
)

data class Weather(
    val main: String,
    val description: String,
    val icon: String
)

data class Coord(
    val lon: Double,
    val lat: Double
) 