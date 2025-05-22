package com.test.wheatherapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import android.widget.TextView
import android.widget.ImageView
import com.bumptech.glide.Glide
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Color
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.LinearLayout
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.location.Geocoder
import androidx.core.app.ActivityCompat
import android.widget.Toast
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.abs
import android.util.Log

class MainActivity : AppCompatActivity() {
    private val viewModel: WeatherViewModel by viewModels()
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var forecastAdapter: ForecastAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val textCity = findViewById<TextView>(R.id.text_city)
        val textTemp = findViewById<TextView>(R.id.text_temp)
        val textDesc = findViewById<TextView>(R.id.text_desc)
        val textDay = findViewById<TextView>(R.id.text_day)
        val textDate = findViewById<TextView>(R.id.text_date)
        val imgWeather = findViewById<ImageView>(R.id.img_weather)
        val recyclerForecast = findViewById<RecyclerView>(R.id.recycler_forecast)
        recyclerForecast.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val defaultForecasts = listOf(
            ForecastItem("Lun", 23, "https://openweathermap.org/img/wn/01d@2x.png"),
            ForecastItem("Mar", 22, "https://openweathermap.org/img/wn/01d@2x.png"),
            ForecastItem("Mer", 20, "https://openweathermap.org/img/wn/02d@2x.png"),
            ForecastItem("Jeu", 19, "https://openweathermap.org/img/wn/02d@2x.png"),
            ForecastItem("Ven", 21, "https://openweathermap.org/img/wn/01d@2x.png"),
            ForecastItem("Sam", 24, "https://openweathermap.org/img/wn/01d@2x.png"),
            ForecastItem("Dim", 25, "https://openweathermap.org/img/wn/01d@2x.png")
        )
        forecastAdapter = ForecastAdapter(defaultForecasts)
        recyclerForecast.adapter = forecastAdapter
        val chevron = findViewById<ImageView>(R.id.ic_chevron_down)
        val locationIcon = findViewById<ImageView>(R.id.ic_location)

        locationIcon.setOnClickListener {
            if (checkLocationPermission()) {
                getCurrentLocation()
            } else {
                requestLocationPermission()
            }
        }

        textCity.setOnClickListener {
            CitySearchDialogFragment { city ->
                viewModel.setCity(city)
                // Propose d'ajouter aux favoris
                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setMessage("Ajouter $city aux favoris ?")
                    .setPositiveButton("Oui") { _, _ ->
                        FavoritesManager.addFavorite(this, city)
                    }
                    .setNegativeButton("Non", null)
                    .show()
            }.show(supportFragmentManager, "search")
        }

        fun updateDots() {
            val dotsContainer = findViewById<LinearLayout>(R.id.dots_container)
            val favorites = FavoritesManager.getFavorites(this).toList()
            val currentCity = viewModel.city.value
            dotsContainer.removeAllViews()
            favorites.forEachIndexed { index, favCity ->
                val dot = View(this)
                val size = (12 * resources.displayMetrics.density).toInt()
                val params = LinearLayout.LayoutParams(size, size)
                params.marginEnd = (6 * resources.displayMetrics.density).toInt()
                dot.layoutParams = params
                dot.background = ContextCompat.getDrawable(this,
                    if (favCity.equals(currentCity, ignoreCase = true)) R.drawable.dot_active else R.drawable.dot_inactive
                )
                dot.setOnClickListener {
                    viewModel.setCity(favCity)
                }
                dotsContainer.addView(dot)
            }
        }

        viewModel.city.observe(this, Observer { city ->
            textCity.text = city.replaceFirstChar { it.uppercase() }
            updateDots()
        })
        viewModel.weather.observe(this, Observer { weather ->
            if (weather != null) {
                textTemp.text = "${weather.main.temp.toInt()}°C"
                // Applique le gradient sur la température
                val paint = textTemp.paint
                val width = paint.measureText(textTemp.text.toString())
                val textShader = LinearGradient(
                    0f, 0f, width, textTemp.textSize,
                    intArrayOf(Color.WHITE, Color.parseColor("#b3e5fc")),
                    null, Shader.TileMode.CLAMP
                )
                textTemp.paint.shader = textShader
                textTemp.setShadowLayer(8f, 0f, 4f, Color.parseColor("#33000000"))
                textTemp.invalidate()
                // Met la première lettre en majuscule
                val desc = weather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: ""
                textDesc.text = desc
                val icon = weather.weather.firstOrNull()?.icon ?: "01d"
                val iconUrl = "https://openweathermap.org/img/wn/${icon}@4x.png"
                com.bumptech.glide.Glide.with(this).load(iconUrl).into(imgWeather)
                // Affiche le vrai jour de la semaine et la date formatée sur deux lignes
                val date = java.util.Date(weather.dt * 1000)
                val dayFormat = java.text.SimpleDateFormat("EEEE", java.util.Locale.FRENCH)
                val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.FRENCH)
                textDay.text = dayFormat.format(date).replaceFirstChar { it.uppercase() }
                textDate.text = dateFormat.format(date)
            }
        })
        viewModel.forecast.observe(this, Observer { forecast ->
            if (forecast != null) {
                val newItems = forecast.list.take(7).mapIndexed { index, day ->
                    val dayName = java.text.SimpleDateFormat("EEE", java.util.Locale.FRENCH).format(java.util.Date(day.dt * 1000)).replaceFirstChar { it.uppercase() }
                    val temp = day.temp.day.toInt()
                    val icon = day.weather.firstOrNull()?.icon ?: "01d"
                    val iconUrl = "https://openweathermap.org/img/wn/${icon}@2x.png"
                    ForecastItem(dayName, temp, iconUrl)
                }
                forecastAdapter.updateData(newItems)
            }
        })
        viewModel.fetchWeather()

        chevron.setOnClickListener {
            val favorites = FavoritesManager.getFavorites(this).toList()
            val villes = favorites.map { it.replaceFirstChar { c -> c.uppercase() } } + "Ajouter une ville"
            val checkedItem = favorites.indexOf(viewModel.city.value)
            val builder = AlertDialog.Builder(this, R.style.RoundedDialog)
                .setTitle(null)
                .setSingleChoiceItems(villes.toTypedArray(), checkedItem) { dialog, which ->
                    if (which < favorites.size) {
                        viewModel.setCity(favorites[which])
                    } else {
                        // Ouvre le DialogFragment de recherche de ville
                        CitySearchDialogFragment { city ->
                            val cityFormatted = city.replaceFirstChar { it.uppercase() }
                            viewModel.setCity(cityFormatted)
                            FavoritesManager.addFavorite(this, cityFormatted)
                            updateDots()
                        }.show(supportFragmentManager, "search")
                    }
                    dialog.dismiss()
                }
            val alertDialog = builder.create()
            alertDialog.listView?.setOnItemLongClickListener { _, _, position, _ ->
                if (position < favorites.size) {
                    AlertDialog.Builder(this, R.style.RoundedDialog)
                        .setMessage("Supprimer ${favorites[position].replaceFirstChar { it.uppercase() }} des favoris ?")
                        .setPositiveButton("Oui") { _, _ ->
                            FavoritesManager.removeFavorite(this, favorites[position])
                            if (favorites[position] == viewModel.city.value) {
                                // Si on supprime la ville courante, passer à la première restante
                                val newFavs = FavoritesManager.getFavorites(this).toList()
                                if (newFavs.isNotEmpty()) {
                                    viewModel.setCity(newFavs[0])
                                }
                            }
                            alertDialog.dismiss()
                            updateDots()
                        }
                        .setNegativeButton("Non", null)
                        .show()
                    true
                } else false
            }
            alertDialog.show()
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Permission de localisation refusée", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @android.annotation.SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        getCityFromLocation(location)
                    } else {
                        Toast.makeText(this, "Impossible d'obtenir la position", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erreur de localisation", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            Toast.makeText(this, "Erreur de localisation", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCityFromLocation(location: Location) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val geocoder = Geocoder(this@MainActivity, Locale.FRENCH)
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val cityName = addresses[0].locality
                    if (cityName != null) {
                        val cityFormatted = cityName.replaceFirstChar { it.uppercase() }
                        withContext(Dispatchers.Main) {
                            viewModel.setCity(cityFormatted)
                            FavoritesManager.addFavorite(this@MainActivity, cityFormatted)
                            Toast.makeText(this@MainActivity, "Ville ajoutée aux favoris", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Erreur lors de la récupération de la ville", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}