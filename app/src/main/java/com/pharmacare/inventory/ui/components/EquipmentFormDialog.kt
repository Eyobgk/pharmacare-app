package com.pharmacare.inventory.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pharmacare.inventory.data.local.entity.Equipment
import com.pharmacare.inventory.ui.theme.*

/**
 * A full-screen dialog for adding or editing an [Equipment] record.
 *
 * Mirrors [MedicineFormDialog] in structure but without the expiry date field,
 * since equipment items don't have an expiry date.
 *
 * Validates all inputs client-side before invoking [onSave].
 *
 * @param equipment  The equipment to edit, or `null` when adding a new entry.
 * @param isEditMode `true` if editing an existing record.
 * @param onDismiss  Invoked on cancel or outside tap.
 * @param onSave     Invoked with the validated [Equipment] object on confirm.
 */
@Composable
fun EquipmentFormDialog(
    equipment: Equipment?,
    isEditMode: Boolean,
    onDismiss: () -> Unit,
    onSave: (Equipment, Boolean) -> Unit
) {
    // ---------- Form field states ----------
    var name     by remember { mutableStateOf(equipment?.equipmentName    ?: "") }
    var category by remember { mutableStateOf(equipment?.category         ?: "") }
    var quantity by remember { mutableStateOf(equipment?.quantityInStock?.toString() ?: "") }
    var price    by remember { mutableStateOf(equipment?.price?.toString()            ?: "") }

    // ---------- Validation error states ----------
    var nameError     by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }
    var quantityError by remember { mutableStateOf<String?>(null) }
    var priceError    by remember { mutableStateOf<String?>(null) }

    /** Validates inputs; returns [Equipment] on success, null on failure. */
    fun validate(): Equipment? {
        var valid = true

        if (name.isBlank()) {
            nameError = "Equipment name is required"; valid = false
        } else { nameError = null }

        if (category.isBlank()) {
            categoryError = "Category is required"; valid = false
        } else { categoryError = null }

        val qty = quantity.toIntOrNull()
        if (qty == null || qty < 0) {
            quantityError = "Enter a valid non-negative quantity"; valid = false
        } else { quantityError = null }

        val prc = price.toDoubleOrNull()
        if (prc == null || prc < 0) {
            priceError = "Enter a valid non-negative price"; valid = false
        } else { priceError = null }

        if (!valid) return null

        return Equipment(
            equipmentId     = equipment?.equipmentId ?: 0,
            equipmentName   = name.trim(),
            category        = category.trim(),
            quantityInStock = qty!!,
            price           = prc!!
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = SurfaceDark,
        shape            = RoundedCornerShape(20.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEditMode) "Edit Equipment" else "Add New Equipment",
                    style = MaterialTheme.typography.titleLarge,
                    color = OnSurfaceWhite,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close dialog",
                        tint = OnSurfaceSecondary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FormField(
                    label         = "Equipment Name *",
                    value         = name,
                    onValueChange = { name = it },
                    placeholder   = "e.g., Blood Pressure Monitor",
                    errorMessage  = nameError
                )
                FormField(
                    label         = "Category *",
                    value         = category,
                    onValueChange = { category = it },
                    placeholder   = "e.g., Diagnostic, Surgical",
                    errorMessage  = categoryError
                )
                FormField(
                    label         = "Quantity In Stock *",
                    value         = quantity,
                    onValueChange = { quantity = it },
                    placeholder   = "e.g., 25",
                    errorMessage  = quantityError,
                    keyboardType  = KeyboardType.Number
                )
                FormField(
                    label         = "Price *",
                    value         = price,
                    onValueChange = { price = it },
                    placeholder   = "e.g., 149.99",
                    errorMessage  = priceError,
                    keyboardType  = KeyboardType.Decimal
                )
                Text(
                    text  = "* Required fields",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceSecondary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val result = validate()
                    if (result != null) onSave(result, isEditMode)
                },
                colors = ButtonDefaults.buttonColors(containerColor = SecondaryAmber),
                shape  = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text  = if (isEditMode) "Update" else "Add Equipment",
                    color = OnPrimaryWhite,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape   = RoundedCornerShape(10.dp)
            ) {
                Text("Cancel", color = OnSurfaceSecondary)
            }
        }
    )
}
