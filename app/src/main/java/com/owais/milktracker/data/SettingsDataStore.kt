package com.owais.milktracker.data

import android.content.Context
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.owais.milktracker.utils.dataStore



object SettingsDataStore {
    private val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
    private val REMINDER_TIME = stringPreferencesKey("reminder_time")
    private val MILK_PRICE = stringPreferencesKey("milk_price")

    fun getReminderEnabled(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[REMINDER_ENABLED] ?: true }

    fun getReminderTime(context: Context): Flow<String> =
        context.dataStore.data.map { it[REMINDER_TIME] ?: "08:00 PM" }

    fun getMilkPrice(context: Context): Flow<String> =
        context.dataStore.data.map { it[MILK_PRICE] ?: "0.0" }

    suspend fun setReminderEnabled(context: Context, enabled: Boolean) {
        context.dataStore.edit { it[REMINDER_ENABLED] = enabled }
    }

    suspend fun setReminderTime(context: Context, time: String) {
        context.dataStore.edit { it[REMINDER_TIME] = time }
    }

    suspend fun setMilkPrice(context: Context, price: String) {
        context.dataStore.edit { it[MILK_PRICE] = price }
    }
}
