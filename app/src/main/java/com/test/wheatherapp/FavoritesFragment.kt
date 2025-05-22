package com.test.wheatherapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class FavoritesFragment(
    private val onCitySelected: (String) -> Unit
) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)
        val listView = view.findViewById<ListView>(R.id.list_favorites)
        val favorites = FavoritesManager.getFavorites(requireContext()).toList()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, favorites.map { it.replaceFirstChar { c -> c.uppercase() } })
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            onCitySelected(favorites[position])
        }
        return view
    }
} 