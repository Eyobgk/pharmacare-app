package com.pharmacare.inventory.data.local.dao

import androidx.room.*
import com.pharmacare.inventory.data.local.entity.Equipment
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the [Equipment] entity.
 *
 * All write operations are suspend functions (safe to call from a coroutine),
 * while the read query returns a [Flow] for reactive UI updates.
 */
@Dao
interface EquipmentDao {

    /**
     * Inserts a new equipment record.
     * Replaces on conflict (handles upsert scenarios gracefully).
     *
     * @param equipment The [Equipment] object to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipment(equipment: Equipment)

    /**
     * Updates an existing equipment record.
     *
     * @param equipment The updated [Equipment] object (must have a valid, existing ID).
     */
    @Update
    suspend fun updateEquipment(equipment: Equipment)

    /**
     * Deletes the given equipment record from the database.
     *
     * @param equipment The [Equipment] object to delete.
     */
    @Delete
    suspend fun deleteEquipment(equipment: Equipment)

    /**
     * Retrieves all equipment records ordered alphabetically by name.
     *
     * @return A [Flow] emitting a fresh list on every table change.
     */
    @Query("SELECT * FROM equipment ORDER BY equipmentName ASC")
    fun getAllEquipment(): Flow<List<Equipment>>

    /**
     * Retrieves a single equipment item by its primary key.
     *
     * @param id The [Equipment.equipmentId] to look up.
     * @return The matching [Equipment], or null if not found.
     */
    @Query("SELECT * FROM equipment WHERE equipmentId = :id")
    suspend fun getEquipmentById(id: Int): Equipment?
}
