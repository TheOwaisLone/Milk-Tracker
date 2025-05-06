package com.owais.milktracker.utils

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Define the DataStore instance at the top level for your application context.
val Context.dataStore by preferencesDataStore(name = "settings")

object SettingsPreferences {
    private val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
    private val REMINDER_HOUR = intPreferencesKey("reminder_hour")
    private val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
    private val MILK_PRICE = floatPreferencesKey("milk_price")

    // Get Milk Price as Float
    fun getMilkPrice(context: Context): Flow<Float> {
        return context.dataStore.data.map { prefs ->
            prefs[MILK_PRICE] ?: 35.0f // Default value if not set
        }
    }

    suspend fun cleanCorruptedMilkPrice(context: Context) {
        context.dataStore.edit { prefs ->
            if (prefs.asMap()[MILK_PRICE] !is Float) {
                prefs.remove(MILK_PRICE)
            }
        }
    }

    // Save Milk Price as Float
    suspend fun saveMilkPrice(context: Context, price: Float) {
        context.dataStore.edit { prefs ->
            prefs[MILK_PRICE] = price // Directly store the Float value
        }
    }

    // Get Reminder Settings (enabled, hour, minute)
    fun getReminderSettings(context: Context): Flow<Triple<Boolean, Int, Int>> {
        return context.dataStore.data.map { prefs ->
            Triple(
                prefs[REMINDER_ENABLED] ?: true,
                prefs[REMINDER_HOUR] ?: 20,
                prefs[REMINDER_MINUTE] ?: 0
            )
        }
    }

    // Save Reminder Settings
    suspend fun saveReminder(context: Context, enabled: Boolean, hour: Int, minute: Int) {
        context.dataStore.edit { prefs ->
            prefs[REMINDER_ENABLED] = enabled
            prefs[REMINDER_HOUR] = hour
            prefs[REMINDER_MINUTE] = minute
        }
    }
}


