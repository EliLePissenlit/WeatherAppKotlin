package com.test.wheatherapp

data class ForecastResponse(
    val city: City,
    val list: List<ForecastDay>
)

data class City(
    val name: String
)

data class ForecastDay(
    val dt: Long,
    val temp: Temp,
    val weather: List<Weather>
)

data class Temp(
    val day: Float
) 