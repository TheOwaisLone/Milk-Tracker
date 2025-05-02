package com.owais.milktracker.data.repository

import com.owais.milktracker.data.database.MilkEntryDao
import com.owais.milktracker.data.model.MilkEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class MilkRepository(private val dao: MilkEntryDao) {
    fun getEntryByDate(date: LocalDate): Flow<MilkEntry?> = dao.getEntryByDate(date)
    fun getAllEntries(): Flow<List<MilkEntry>> = dao.getAllEntries()
    suspend fun upsert(entry: MilkEntry) = dao.upsert(entry)
    suspend fun delete(entry: MilkEntry) = dao.delete(entry)
}
