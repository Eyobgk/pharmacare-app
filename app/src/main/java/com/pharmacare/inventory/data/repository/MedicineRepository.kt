package com.pharmacare.inventory.data.repository

import com.pharmacare.inventory.data.local.dao.MedicineDao
import com.pharmacare.inventory.data.local.entity.Medicine
import kotlinx.coroutines.flow.Flow

/**
 * Repository that acts as the single source of truth for medicine data.
 *
 * The Repository pattern abstracts the data source (Room in this case) from the
 * ViewModel. This makes it easy to swap the source (e.g., add a remote API)
 * without changing ViewModel or UI code.
 *
 * In a larger project this class would be injected via Hilt/Dagger. Here it is
 * manually instantiated in the ViewModel to keep the project dependency-free.
 *
 * @param medicineDao The DAO to delegate all database operations to.
 */
class MedicineRepository(private val medicineDao: MedicineDao) {

    /**
     * Exposes the live stream of all medicines directly from the DAO.
     * Backed by a Room [Flow], so the UI reacts automatically to DB changes.
     */
    val allMedicines: Flow<List<Medicine>> = medicineDao.getAllMedicines()

    /**
     * Inserts a new medicine. Must be called from a coroutine scope.
     *
     * @param medicine The [Medicine] to persist.
     */
    suspend fun insert(medicine: Medicine) {
        medicineDao.insertMedicine(medicine)
    }

    /**
     * Updates an existing medicine record. Must be called from a coroutine scope.
     *
     * @param medicine The [Medicine] with updated field values.
     *                 Its [Medicine.medicineId] must match an existing record.
     */
    suspend fun update(medicine: Medicine) {
        medicineDao.updateMedicine(medicine)
    }

    /**
     * Deletes a medicine record. Must be called from a coroutine scope.
     *
     * @param medicine The [Medicine] to remove from the database.
     */
    suspend fun delete(medicine: Medicine) {
        medicineDao.deleteMedicine(medicine)
    }
}
