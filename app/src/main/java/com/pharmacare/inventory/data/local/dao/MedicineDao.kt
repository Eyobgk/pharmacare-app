package com.pharmacare.inventory.data.local.dao

import androidx.room.*
import com.pharmacare.inventory.data.local.entity.Medicine
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the [Medicine] entity.
 *
 * Provides coroutine-friendly suspend functions for write operations and a
 * [Flow]-backed query for the read operation, so the UI automatically reacts
 * to any database change without manual refresh calls.
 */
@Dao
interface MedicineDao {

    /**
     * Inserts a new medicine record.
     * If a record with the same primary key already exists, it is replaced.
     *
     * @param medicine The [Medicine] object to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: Medicine)

    /**
     * Updates an existing medicine record identified by its [Medicine.medicineId].
     *
     * @param medicine The updated [Medicine] object (must have a valid, existing ID).
     */
    @Update
    suspend fun updateMedicine(medicine: Medicine)

    /**
     * Deletes the given medicine record from the database.
     *
     * @param medicine The [Medicine] object to delete.
     */
    @Delete
    suspend fun deleteMedicine(medicine: Medicine)

    /**
     * Retrieves all medicines ordered alphabetically by name.
     *
     * @return A [Flow] that emits a fresh list whenever the table changes.
     *         Collect this in a ViewModel and expose via StateFlow.
     */
    @Query("SELECT * FROM medicines ORDER BY medicineName ASC")
    fun getAllMedicines(): Flow<List<Medicine>>

    /**
     * Retrieves a single medicine by its primary key.
     * Useful for pre-populating the edit form.
     *
     * @param id The [Medicine.medicineId] to look up.
     * @return The matching [Medicine], or null if not found.
     */
    @Query("SELECT * FROM medicines WHERE medicineId = :id")
    suspend fun getMedicineById(id: Int): Medicine?
}
