package com.owais.milktracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owais.milktracker.data.model.MilkEntry
import com.owais.milktracker.data.repository.MilkRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class MilkViewModel(private val repository: MilkRepository) : ViewModel() {

    // StateFlow to hold the map of entries (date -> MilkEntry)
    private val _entries = MutableStateFlow<Map<LocalDate, MilkEntry>>(emptyMap())
    val entries: StateFlow<Map<LocalDate, MilkEntry>> = _entries

    init {
        // Collect data from the database and populate _entries
        viewModelScope.launch {
            repository.getAllEntries().collect { list ->
                // Map entries by their date
                _entries.value = list.associateBy { it.date }
            }
        }
    }

    // Function to upsert (save or update) a MilkEntry
    fun upsertEntry(entry: MilkEntry) {
        viewModelScope.launch {
            repository.upsert(entry)  // Save or update the entry
        }
    }

    // Function to delete an entry
    fun deleteEntry(entry: MilkEntry) {
        viewModelScope.launch {
            repository.delete(entry)  // Delete the entry
        }
    }

    // Function to get the entry for a specific date
    fun getEntry(date: LocalDate): MilkEntry? {
        return _entries.value[date]  // Retrieve the entry from _entries map
    }
}
