package com.pharmacare.inventory.ui.equipment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pharmacare.inventory.data.local.PharmacareDatabase
import com.pharmacare.inventory.data.local.entity.Equipment
import com.pharmacare.inventory.data.repository.EquipmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the Equipment screen.
 *
 * Mirrors [MedicineViewModel] in structure, but works exclusively with
 * [Equipment] entities and [EquipmentRepository].
 *
 * @param application The application instance used to get the Room DB singleton.
 */
class EquipmentViewModel(application: Application) : AndroidViewModel(application) {

    // --- Dependencies ---

    private val repository: EquipmentRepository = EquipmentRepository(
        PharmacareDatabase.getDatabase(application).equipmentDao()
    )

    // --- UI State ---

    /**
     * Live list of all equipment from Room, collected into a [StateFlow].
     * Stops the upstream Room Flow 5 seconds after the last collector leaves
     * (e.g., navigation away from the screen).
     */
    val equipmentList: StateFlow<List<Equipment>> = repository.allEquipment
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    /** State of the Add/Edit dialog. `null` = hidden, non-null = visible. */
    private val _dialogState = MutableStateFlow<EquipmentDialogState?>(null)
    val dialogState: StateFlow<EquipmentDialogState?> = _dialogState.asStateFlow()

    /** One-shot snackbar message for user feedback. */
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // --- Dialog Management ---

    /** Opens the dialog pre-filled with [equipment] data for editing. */
    fun onEditEquipment(equipment: Equipment) {
        _dialogState.value = EquipmentDialogState(equipment = equipment, isEditMode = true)
    }

    /** Opens a blank dialog for creating a new equipment entry. */
    fun onAddEquipment() {
        _dialogState.value = EquipmentDialogState(equipment = null, isEditMode = false)
    }

    /** Dismisses the dialog without persisting anything. */
    fun onDismissDialog() {
        _dialogState.value = null
    }

    // --- CRUD Operations ---

    /**
     * Persists the equipment: inserts if [isEditMode] is false, updates otherwise.
     *
     * @param equipment  The [Equipment] object built from the dialog form.
     * @param isEditMode Determines whether to insert or update.
     */
    fun saveEquipment(equipment: Equipment, isEditMode: Boolean) {
        viewModelScope.launch {
            try {
                if (isEditMode) {
                    repository.update(equipment)
                    _snackbarMessage.value = "Equipment updated successfully"
                } else {
                    repository.insert(equipment.copy(equipmentId = 0))
                    _snackbarMessage.value = "Equipment added successfully"
                }
                _dialogState.value = null
            } catch (e: Exception) {
                _snackbarMessage.value = "Error: ${e.localizedMessage}"
            }
        }
    }

    /**
     * Deletes the given [equipment] record.
     *
     * @param equipment The [Equipment] to remove.
     */
    fun deleteEquipment(equipment: Equipment) {
        viewModelScope.launch {
            try {
                repository.delete(equipment)
                _snackbarMessage.value = "${equipment.equipmentName} deleted"
            } catch (e: Exception) {
                _snackbarMessage.value = "Delete failed: ${e.localizedMessage}"
            }
        }
    }

    /** Clears snackbar state after the message has been consumed by the UI. */
    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }
}

/**
 * Represents the state of the Equipment Add/Edit dialog.
 *
 * @param equipment  The equipment being edited, or `null` for a new entry.
 * @param isEditMode `true` if editing an existing record.
 */
data class EquipmentDialogState(
    val equipment: Equipment?,
    val isEditMode: Boolean
)
