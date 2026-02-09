package com.owais.milktracker.utils

import android.content.Context
import android.net.Uri
import com.owais.milktracker.data.model.MilkEntry
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Calendar

/**
 * Utility functions for importing and exporting milk tracker data in JSON format
 */
@Suppress("NewApi")
object DataImportExportUtils {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    /**
     * Convert LocalDate to a date string
     */
    private fun localDateToString(date: LocalDate): String {
        // Use reflection to avoid direct API 26 calls on LocalDate
        val calendar = Calendar.getInstance()
        try {
            val year = date.javaClass.getMethod("getYear").invoke(date) as Int
            val month = date.javaClass.getMethod("getMonthValue").invoke(date) as Int
            val dayOfMonth = date.javaClass.getMethod("getDayOfMonth").invoke(date) as Int
            calendar.set(year, month - 1, dayOfMonth)
        } catch (@Suppress("UNUSED_VARIABLE") e: Exception) {
            // Fallback: try direct access (with desugaring this should work)
            calendar.set(date.year, date.monthValue - 1, date.dayOfMonth)
        }
        return dateFormatter.format(calendar.time)
    }

    /**
     * Convert a date string to LocalDate
     */
    private fun stringToLocalDate(dateString: String): LocalDate {
        val date = dateFormatter.parse(dateString)
        if (date == null) {
            return LocalDate.of(2000, 1, 1)
        }
        val calendar = Calendar.getInstance().apply { time = date }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        return try {
            // Use reflection to avoid direct API 26 calls
            val localDateClass = Class.forName("java.time.LocalDate")
            val ofMethod = localDateClass.getMethod("of", Int::class.java, Int::class.java, Int::class.java)
            ofMethod.invoke(null, year, month, dayOfMonth) as LocalDate
        } catch (@Suppress("UNUSED_VARIABLE") e: Exception) {
            // Fallback: direct call (with desugaring this should work)
            LocalDate.of(year, month, dayOfMonth)
        }
    }

    /**
     * Get current date as string
     */
    private fun getCurrentDateString(): String {
        return dateFormatter.format(System.currentTimeMillis())
    }

    /**
     * Export milk entries to a JSON file
     * @param context Android context
     * @param entries List of MilkEntry objects to export
     * @param uri The file URI where to save the JSON file
     * @return Pair<Boolean, String> - Success status and message
     */
    fun exportDataToJson(
        context: Context,
        entries: List<MilkEntry>,
        uri: Uri
    ): Pair<Boolean, String> {
        return try {
            val jsonArray = JSONArray()

            entries.forEach { entry ->
                val jsonObject = JSONObject().apply {
                    put("id", entry.id)
                    put("date", localDateToString(entry.date))
                    put("quantity", entry.quantity)
                    put("isBorrowed", entry.isBorrowed)
                }
                jsonArray.put(jsonObject)
            }

            val jsonData = JSONObject().apply {
                put("version", 1)
                put("exportDate", getCurrentDateString())
                put("dataCount", entries.size)
                put("entries", jsonArray)
            }

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(jsonData.toString(4).toByteArray())
                outputStream.flush()
            }

            Pair(true, "Data exported successfully! (${entries.size} entries)")
        } catch (e: Exception) {
            Pair(false, "Error exporting data: ${e.message}")
        }
    }

    /**
     * Import milk entries from a JSON file
     * @param context Android context
     * @param uri The file URI to import from
     * @return Pair<Boolean, Pair<List<MilkEntry>, String>> - Success status, list of entries, and message
     */
    fun importDataFromJson(
        context: Context,
        uri: Uri
    ): Pair<Boolean, Pair<List<MilkEntry>, String>> {
        return try {
            val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.bufferedReader().readText()
            } ?: return Pair(false, Pair(emptyList(), "Unable to read file"))

            val jsonObject = JSONObject(jsonString)
            val version = jsonObject.optInt("version", 1)

            if (version != 1) {
                return Pair(false, Pair(emptyList(), "Unsupported JSON version: $version"))
            }

            val entriesArray = jsonObject.getJSONArray("entries")
            val entries = mutableListOf<MilkEntry>()

            for (i in 0 until entriesArray.length()) {
                val entryJson = entriesArray.getJSONObject(i)
                try {
                    val entry = MilkEntry(
                        id = entryJson.optInt("id", 0),
                        date = stringToLocalDate(entryJson.getString("date")),
                        quantity = entryJson.getDouble("quantity"),
                        isBorrowed = entryJson.getBoolean("isBorrowed")
                    )
                    entries.add(entry)
                } catch (e: Exception) {
                    return Pair(
                        false,
                        Pair(emptyList(), "Error parsing entry at index $i: ${e.message}")
                    )
                }
            }

            Pair(true, Pair(entries, "Data imported successfully! (${entries.size} entries)"))
        } catch (e: Exception) {
            Pair(false, Pair(emptyList(), "Error importing data: ${e.message}"))
        }
    }

    /**
     * Generate a formatted JSON string for preview or logging
     * @param entries List of MilkEntry objects
     * @return Formatted JSON string
     */
    fun generateJsonPreview(entries: List<MilkEntry>): String {
        return try {
            val jsonArray = JSONArray()

            entries.forEach { entry ->
                val jsonObject = JSONObject().apply {
                    put("id", entry.id)
                    put("date", localDateToString(entry.date))
                    put("quantity", entry.quantity)
                    put("isBorrowed", entry.isBorrowed)
                }
                jsonArray.put(jsonObject)
            }

            val jsonData = JSONObject().apply {
                put("version", 1)
                put("exportDate", getCurrentDateString())
                put("dataCount", entries.size)
                put("entries", jsonArray)
            }

            jsonData.toString(4)
        } catch (e: Exception) {
            "Error generating preview: ${e.message}"
        }
    }
}

