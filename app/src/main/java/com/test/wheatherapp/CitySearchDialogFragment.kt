package com.test.wheatherapp

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class CitySearchDialogFragment(
    private val onCitySelected: (String) -> Unit
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val input = EditText(requireContext())
        input.hint = "Nom de la ville"
        val padding = (24 * resources.displayMetrics.density).toInt()
        input.setPadding(padding, padding, padding, padding)

        return AlertDialog.Builder(requireContext(), R.style.RoundedDialog)
            .setTitle("Rechercher une ville")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val city = input.text.toString().trim()
                if (city.isNotEmpty()) {
                    onCitySelected(city)
                }
            }
            .setNegativeButton("Annuler", null)
            .create()
    }
} 