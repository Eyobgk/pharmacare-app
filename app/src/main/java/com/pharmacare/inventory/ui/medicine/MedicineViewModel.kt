package com.pharmacare.inventory.ui.medicine

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pharmacare.inventory.data.local.PharmacareDatabase
import com.pharmacare.inventory.data.local.entity.Medicine
import com.pharmacare.inventory.data.repository.MedicineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the Medicine screen.
 *
 * Extends [AndroidViewModel] to safely access [Application] context for
 * database initialization without leaking an Activity reference.
 *
 * All UI state is exposed via [StateFlow] so the Composable re-composes
 * only when data actually changes (hot observable, unlike cold Flow).
 *
 * @param application The application instance used to get the Room DB singleton.
 */
class MedicineViewModel(application: Application) : AndroidViewModel(application) {

    // --- Dependencies ---

    private val repository: MedicineRepository = MedicineRepository(
        PharmacareDatabase.getDatabase(application).medicineDao()
    )

    // --- UI State ---

    /**
     * Live list of all medicines from Room, converted to [StateFlow].
     * [SharingStarted.WhileSubscribed] stops the upstream flow when there are
     * no active collectors (e.g., when the screen is in the background).
     */
    val medicines: StateFlow<List<Medicine>> = repository.allMedicines
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    /**
     * Controls the visibility of the Add/Edit dialog.
     * `null` means the dialog is closed; a non-null value means it is open
     * and holds the [Medicine] being edited (or a blank template for "add").
     */
    private val _dialogState = MutableStateFlow<MedicineDialogState?>(null)
    val dialogState: StateFlow<MedicineDialogState?> = _dialogState.asStateFlow()

    /**
     * Snackbar message to show after operations (success/error feedback).
     * Consumed by the UI after display.
     */
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // --- Dialog Management ---

    /** Opens the dialog pre-filled for editing an existing [medicine]. */
    fun onEditMedicine(medicine: Medicine) {
        _dialogState.value = MedicineDialogState(medicine = medicine, isEditMode = true)
    }

    /** Opens a blank dialog for adding a new medicine. */
    fun onAddMedicine() {
        _dialogState.value = MedicineDialogState(medicine = null, isEditMode = false)
    }

    /** Dismisses the dialog without saving changes. */
    fun onDismissDialog() {
        _dialogState.value = null
    }

    // --- CRUD Operations ---

    /**
     * Validates input then either inserts or updates a medicine record.
     *
     * Called when the user confirms the Add/Edit dialog.
     * Runs the DB write on [viewModelScope] (IO-dispatched by Room internally).
     *
     * @param medicine The [Medicine] object built from dialog form fields.
     * @param isEditMode `true` → update; `false` → insert.
     */
    fun saveMedicine(medicine: Medicine, isEditMode: Boolean) {
        viewModelScope.launch {
            try {
                if (isEditMode) {
                    repository.update(medicine)
                    _snackbarMessage.value = "Medicine updated successfully"
                } else {
                    // Reset ID to 0 so Room auto-generates a new primary key
                    repository.insert(medicine.copy(medicineId = 0))
                    _snackbarMessage.value = "Medicine added successfully"
                }
                _dialogState.value = null   // Close dialog on success
            } catch (e: Exception) {
                _snackbarMessage.value = "Error: ${e.localizedMessage}"
            }
        }
    }

    /**
     * Deletes the given [medicine] from the database.
     * Shows a snackbar on success or error.
     *
     * @param medicine The [Medicine] to delete.
     */
    fun deleteMedicine(medicine: Medicine) {
        viewModelScope.launch {
            try {
                repository.delete(medicine)
                _snackbarMessage.value = "${medicine.medicineName} deleted"
            } catch (e: Exception) {
                _snackbarMessage.value = "Delete failed: ${e.localizedMessage}"
            }
        }
    }

    /** Clears the snackbar message after it has been shown. */
    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }
}

/**
 * Sealed-like data class representing the state of the Add/Edit dialog.
 *
 * @param medicine   The medicine to edit, or `null` when adding a new one.
 * @param isEditMode `true` if editing an existing record, `false` for a new one.
 */
data class MedicineDialogState(
    val medicine: Medicine?,
    val isEditMode: Boolean
)
