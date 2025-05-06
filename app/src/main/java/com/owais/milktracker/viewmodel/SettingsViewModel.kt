package com.owais.milktracker.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owais.milktracker.utils.SettingsPreferences
import kotlinx.coroutines.flow.*
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
}
