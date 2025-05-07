package com.owais.milktracker.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owais.milktracker.data.SettingsDataStore
import com.owais.milktracker.utils.SettingsPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val context: Context) : ViewModel() {

    // Expose reminder settings and milk price
    private val _reminderSettings = SettingsPreferences.getReminderSettings(context)
        .stateIn(viewModelScope, SharingStarted.Eagerly, Triple(true, 20, 0))
    val reminderEnabled = _reminderSettings.map { it.first }
    val reminderHour = _reminderSettings.map { it.second }
    val reminderMinute = _reminderSettings.map { it.third }

    val milkPrice: StateFlow<Float> = SettingsPreferences.getMilkPrice(context)
        .stateIn(viewModelScope, SharingStarted.Eagerly, 35.0f)

    // Expose dark mode state
    val isDarkMode: StateFlow<Boolean> = SettingsDataStore.getDarkMode(context)
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun updateReminder(enabled: Boolean, hour: Int, minute: Int) {
        viewModelScope.launch {
            SettingsPreferences.saveReminder(context, enabled, hour, minute)
        }
    }

    fun updateMilkPrice(price: Float) {
        viewModelScope.launch {
            SettingsPreferences.saveMilkPrice(context, price)
        }
    }

    fun updateDarkMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            SettingsDataStore.setDarkMode(context, isDarkMode)
        }
    }
}