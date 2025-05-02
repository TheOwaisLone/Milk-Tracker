package com.owais.milktracker.data.database

import android.content.Context
import androidx.room.*
import com.owais.milktracker.data.model.MilkEntry

@Database(entities = [MilkEntry::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MilkDatabase : RoomDatabase() {
    abstract fun milkEntryDao(): MilkEntryDao

    companion object {
        @Volatile private var INSTANCE: MilkDatabase? = null

        fun getDatabase(context: Context): MilkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MilkDatabase::class.java,
                    "milk_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
