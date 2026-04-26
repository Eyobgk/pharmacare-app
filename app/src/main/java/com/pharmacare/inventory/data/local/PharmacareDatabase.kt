package com.pharmacare.inventory.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pharmacare.inventory.data.local.dao.EquipmentDao
import com.pharmacare.inventory.data.local.dao.MedicineDao
import com.pharmacare.inventory.data.local.entity.Equipment
import com.pharmacare.inventory.data.local.entity.Medicine

/**
 * The single Room Database for the PharmaCare application.
 *
 * Declares both [Medicine] and [Equipment] as entities. The [version] must be
 * incremented whenever the schema changes; a [Migration] should be provided so
 * existing user data is not lost.
 *
 * Implemented as a thread-safe singleton using the double-checked locking pattern
 * so only one instance is ever created per process lifetime.
 *
 * Usage (from a ViewModel or Repository):
 * ```kotlin
 * val db = PharmacareDatabase.getDatabase(context)
 * val medicineDao = db.medicineDao()
 * ```
 */
@Database(
    entities = [Medicine::class, Equipment::class],
    version = 1,
    exportSchema = false   // Set to true and configure schemaLocation for production apps
)
abstract class PharmacareDatabase : RoomDatabase() {

    /** Returns the [MedicineDao] for all medicine-related queries. */
    abstract fun medicineDao(): MedicineDao

    /** Returns the [EquipmentDao] for all equipment-related queries. */
    abstract fun equipmentDao(): EquipmentDao

    companion object {
        /**
         * Volatile ensures that the value of [INSTANCE] is always read from and
         * written to main memory, preventing caching issues across threads.
         */
        @Volatile
        private var INSTANCE: PharmacareDatabase? = null

        /**
         * Returns the singleton database instance, creating it on first call.
         *
         * @param context Application context used to build the database file path.
         * @return The singleton [PharmacareDatabase] instance.
         */
        fun getDatabase(context: Context): PharmacareDatabase {
            // Return existing instance if already created (fast path, no sync)
            return INSTANCE ?: synchronized(this) {
                // Re-check inside the synchronized block (double-checked locking)
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PharmacareDatabase::class.java,
                    "pharmacare_database"         // Physical file name on disk
                )
                    .fallbackToDestructiveMigration() // For development; replace with Migrations in production
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
