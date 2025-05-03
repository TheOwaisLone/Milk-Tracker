package com.owais.milktracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "milk_entries")
data class MilkEntry(
    @PrimaryKey val date: LocalDate,
    val quantity: Double,
    val isBorrowed: Boolean
)
