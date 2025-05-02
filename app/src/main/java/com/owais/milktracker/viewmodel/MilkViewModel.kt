package com.owais.milktracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owais.milktracker.data.model.MilkEntry
import com.owais.milktracker.data.repository.MilkRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class MilkViewModel(private val repository: MilkRepository) : ViewModel() {

    private val _entries = MutableStateFlow<Map<LocalDate, MilkEntry>>(emptyMap())
    val entries: StateFlow<Map<LocalDate, MilkEntry>> = _entries

    init {
        viewModelScope.launch {
            repository.getAllEntries().collect { list ->
                _entries.value = list.associateBy { it.date }
            }
        }
    }

    fun upsertEntry(entry: MilkEntry) {
        viewModelScope.launch {
            repository.upsert(entry)
        }
    }

    fun deleteEntry(entry: MilkEntry) {
        viewModelScope.launch {
            repository.delete(entry)
        }
    }

    fun getEntry(date: LocalDate): MilkEntry? {
        return _entries.value[date]
    }
}
