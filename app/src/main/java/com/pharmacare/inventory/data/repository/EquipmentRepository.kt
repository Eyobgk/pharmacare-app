package com.pharmacare.inventory.data.repository

import com.pharmacare.inventory.data.local.dao.EquipmentDao
import com.pharmacare.inventory.data.local.entity.Equipment
import kotlinx.coroutines.flow.Flow

/**
 * Repository that acts as the single source of truth for equipment data.
 *
 * Abstracts the Room DAO so the ViewModel never depends directly on a
 * database-specific API. Adding a remote data source in the future only
 * requires changes here.
 *
 * @param equipmentDao The DAO to delegate all database operations to.
 */
class EquipmentRepository(private val equipmentDao: EquipmentDao) {

    /**
     * Exposes the live stream of all equipment directly from the DAO.
     * Room will emit a new list every time the "equipment" table changes.
     */
    val allEquipment: Flow<List<Equipment>> = equipmentDao.getAllEquipment()

    /**
     * Inserts new equipment. Must be called from a coroutine scope.
     *
     * @param equipment The [Equipment] to persist.
     */
    suspend fun insert(equipment: Equipment) {
        equipmentDao.insertEquipment(equipment)
    }

    /**
     * Updates an existing equipment record. Must be called from a coroutine scope.
     *
     * @param equipment The [Equipment] with updated fields.
     */
    suspend fun update(equipment: Equipment) {
        equipmentDao.updateEquipment(equipment)
    }

    /**
     * Deletes an equipment record. Must be called from a coroutine scope.
     *
     * @param equipment The [Equipment] to remove.
     */
    suspend fun delete(equipment: Equipment) {
        equipmentDao.deleteEquipment(equipment)
    }
}
