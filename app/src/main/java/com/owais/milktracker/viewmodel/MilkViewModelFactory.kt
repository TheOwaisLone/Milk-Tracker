package com.owais.milktracker.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.owais.milktracker.data.database.MilkDatabase
import com.owais.milktracker.data.repository.MilkRepository

class MilkViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val repo = MilkRepository(MilkDatabase.getDatabase(context).milkEntryDao())

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MilkViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MilkViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
