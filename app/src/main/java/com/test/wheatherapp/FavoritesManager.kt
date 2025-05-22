package com.test.wheatherapp

import android.content.Context

object FavoritesManager {
    private const val PREFS_NAME = "weather_prefs"
    private const val KEY_FAVORITES = "favorites"

    fun getFavorites(context: Context): Set<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }

    fun addFavorite(context: Context, city: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val favorites = getFavorites(context).toMutableSet()
        val cityFormatted = city.replaceFirstChar { it.uppercase() }
        favorites.add(cityFormatted)
        prefs.edit().putStringSet(KEY_FAVORITES, favorites).apply()
    }

    fun removeFavorite(context: Context, city: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val favorites = getFavorites(context).toMutableSet()
        favorites.remove(city)
        prefs.edit().putStringSet(KEY_FAVORITES, favorites).apply()
    }
} 