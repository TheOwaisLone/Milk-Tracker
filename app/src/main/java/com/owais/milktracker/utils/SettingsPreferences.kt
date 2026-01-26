package com.owais.milktracker.utils

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// SINGLE DataStore for the entire app (GOOD)
val Context.dataStore by preferencesDataStore(name = "settings")

object SettingsPreferences {

    // ───────── Existing Keys ─────────
    private val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
    private val REMINDER_HOUR = intPreferencesKey("reminder_hour")
    private val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
    private val MILK_PRICE = floatPreferencesKey("milk_price")

    // ───────── NEW: Onboarding Key ─────────
    private val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")

    // ───────── Onboarding ─────────
    suspend fun isOnboardingDone(context: Context): Boolean {
        val prefs = context.dataStore.data.first()
        return prefs[ONBOARDING_DONE] ?: false
    }

    suspend fun setOnboardingDone(context: Context) {
        context.dataStore.edit { prefs ->
            prefs[ONBOARDING_DONE] = true
        }
    }

    // ───────── Milk Price ─────────
    fun getMilkPrice(context: Context): Flow<Float> {
        return context.dataStore.data.map { prefs ->
            val raw = prefs.asMap()[MILK_PRICE]

            when (raw) {
                is Float -> raw
                is String -> raw.toFloatOrNull() ?: 35.0f
                else -> 35.0f
            }
        }
    }


    suspend fun cleanCorruptedMilkPrice(context: Context) {
        context.dataStore.edit { prefs ->
            val raw = prefs.asMap()[MILK_PRICE]

            if (raw is String) {
                val fixed = raw.toFloatOrNull()
                prefs.remove(MILK_PRICE)
                if (fixed != null) {
                    prefs[MILK_PRICE] = fixed
                }
            }
        }
    }


    suspend fun saveMilkPrice(context: Context, price: Float) {
        context.dataStore.edit { prefs ->
            prefs[MILK_PRICE] = price
        }
    }

    // ───────── Reminder ─────────
    fun getReminderSettings(context: Context): Flow<Triple<Boolean, Int, Int>> {
        return context.dataStore.data.map { prefs ->
            Triple(
                prefs[REMINDER_ENABLED] ?: true,
                prefs[REMINDER_HOUR] ?: 20,
                prefs[REMINDER_MINUTE] ?: 0
            )
        }
    }

    suspend fun saveReminder(context: Context, enabled: Boolean, hour: Int, minute: Int) {
        context.dataStore.edit { prefs ->
            prefs[REMINDER_ENABLED] = enabled
            prefs[REMINDER_HOUR] = hour
            prefs[REMINDER_MINUTE] = minute
        }
    }

    suspend fun getReminderSettingsOnce(
        context: Context
    ): Triple<Boolean, Int, Int> {
        return getReminderSettings(context).first()
    }
}
