package com.owais.milktracker.data.repository

import com.owais.milktracker.data.database.MilkEntryDao
import com.owais.milktracker.data.model.MilkEntry
import kotlinx.coroutines.flow.Flow

class MilkRepository(private val dao: MilkEntryDao) {
    fun getAllEntries(): Flow<List<MilkEntry>> = dao.getAllEntries()
    suspend fun delete(entry: MilkEntry) = dao.delete(entry)
    suspend fun upsert(entry: MilkEntry) {
        dao.insert(entry) // DAO should have @Insert(onConflict = OnConflictStrategy.REPLACE)
    }

}
