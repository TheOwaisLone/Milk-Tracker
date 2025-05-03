package com.owais.milktracker.data.database

import androidx.room.*
import com.owais.milktracker.data.model.MilkEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface MilkEntryDao {
    @Query("SELECT * FROM milk_entries WHERE date = :date LIMIT 1")
    fun getEntryByDate(date: LocalDate): Flow<MilkEntry?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: MilkEntry)

    @Query("SELECT * FROM milk_entries")
    fun getAllEntries(): Flow<List<MilkEntry>>

    @Delete
    suspend fun delete(entry: MilkEntry)
}
