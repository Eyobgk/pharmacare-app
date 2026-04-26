package com.pharmacare.inventory.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import com.pharmacare.inventory.data.local.entity.Medicine
import com.pharmacare.inventory.ui.theme.*

/**
 * A full-screen dialog (using [AlertDialog]) for adding or editing a [Medicine].
 *
 * All fields are pre-populated when [medicine] is non-null (edit mode).
 * Client-side validation is performed before calling [onSave]; error messages
 * are displayed inline beneath each invalid field.
 *
 * @param medicine     The medicine to edit, or `null` when adding a new one.
 * @param isEditMode   `true` if editing an existing record, `false` for a new entry.
 * @param onDismiss    Invoked when the user cancels or taps outside the dialog.
 * @param onSave       Invoked with the validated [Medicine] object when the user confirms.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineFormDialog(
    medicine: Medicine?,
    isEditMode: Boolean,
    onDismiss: () -> Unit,
    onSave: (Medicine, Boolean) -> Unit
) {
    // ---------- Form field states ----------
    var name      by remember { mutableStateOf(medicine?.medicineName    ?: "") }
    var category  by remember { mutableStateOf(medicine?.category        ?: "") }
    var expiry    by remember { mutableStateOf(medicine?.expiryDate      ?: "") }
    var quantity  by remember { mutableStateOf(medicine?.quantityInStock?.toString() ?: "") }
    var price     by remember { mutableStateOf(medicine?.price?.toString()           ?: "") }

    // ---------- Validation error states ----------
    var nameError     by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }
    var expiryError   by remember { mutableStateOf<String?>(null) }
    var quantityError by remember { mutableStateOf<String?>(null) }
    var priceError    by remember { mutableStateOf<String?>(null) }

    /**
     * Validates all fields and returns a [Medicine] on success, or null if any
     * field is invalid. Sets the corresponding error state for display.
     */
    fun validate(): Medicine? {
        var valid = true

        // Name validation
        if (name.isBlank()) {
            nameError = "Medicine name is required"
            valid = false
        } else { nameError = null }

        // Category validation
        if (category.isBlank()) {
            categoryError = "Category is required"
            valid = false
        } else { categoryError = null }

        // Expiry date validation (basic format check dd/MM/yyyy)
        val expiryRegex = Regex("""\d{2}/\d{2}/\d{4}""")
        if (expiry.isBlank()) {
            expiryError = "Expiry date is required"
            valid = false
        } else if (!expiryRegex.matches(expiry)) {
            expiryError = "Use format DD/MM/YYYY"
            valid = false
        } else { expiryError = null }

        // Quantity validation
        val qty = quantity.toIntOrNull()
        if (qty == null || qty < 0) {
            quantityError = "Enter a valid non-negative quantity"
            valid = false
        } else { quantityError = null }

        // Price validation
        val prc = price.toDoubleOrNull()
        if (prc == null || prc < 0) {
            priceError = "Enter a valid non-negative price"
            valid = false
        } else { priceError = null }

        if (!valid) return null

        return Medicine(
            medicineId      = medicine?.medicineId ?: 0,
            medicineName    = name.trim(),
            category        = category.trim(),
            expiryDate      = expiry.trim(),
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
                    text = if (isEditMode) "Edit Medicine" else "Add New Medicine",
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
            // Scrollable form body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FormField(
                    label         = "Medicine Name *",
                    value         = name,
                    onValueChange = { name = it },
                    placeholder   = "e.g., Amoxicillin 500mg",
                    errorMessage  = nameError
                )
                FormField(
                    label         = "Category *",
                    value         = category,
                    onValueChange = { category = it },
                    placeholder   = "e.g., Antibiotic, Painkiller",
                    errorMessage  = categoryError
                )
                FormField(
                    label         = "Expiry Date *",
                    value         = expiry,
                    onValueChange = { expiry = it },
                    placeholder   = "DD/MM/YYYY",
                    errorMessage  = expiryError,
                    keyboardType  = KeyboardType.Number
                )
                FormField(
                    label         = "Quantity In Stock *",
                    value         = quantity,
                    onValueChange = { quantity = it },
                    placeholder   = "e.g., 100",
                    errorMessage  = quantityError,
                    keyboardType  = KeyboardType.Number
                )
                FormField(
                    label         = "Price *",
                    value         = price,
                    onValueChange = { price = it },
                    placeholder   = "e.g., 4.99",
                    errorMessage  = priceError,
                    keyboardType  = KeyboardType.Decimal
                )

                // Required fields note
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
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape  = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text  = if (isEditMode) "Update" else "Add Medicine",
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

/**
 * Reusable styled [OutlinedTextField] with a label, placeholder, and optional
 * inline error message displayed below the field.
 *
 * @param label         Field label shown above / inside the text field.
 * @param value         Current text value.
 * @param onValueChange Callback for text changes.
 * @param placeholder   Hint text shown when the field is empty.
 * @param errorMessage  Validation error message; if non-null, the field turns red.
 * @param keyboardType  Keyboard type for numeric vs text input.
 */
@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            label         = { Text(label) },
            placeholder   = { Text(placeholder, color = OnSurfaceSecondary) },
            isError       = errorMessage != null,
            singleLine    = true,
            modifier      = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = PrimaryGreen,
                unfocusedBorderColor = SurfaceVariant,
                focusedLabelColor    = PrimaryGreen,
                unfocusedLabelColor  = OnSurfaceSecondary,
                cursorColor          = PrimaryGreen,
                focusedTextColor     = OnSurfaceWhite,
                unfocusedTextColor   = OnSurfaceWhite,
                errorBorderColor     = ErrorRed,
                errorLabelColor      = ErrorRed
            ),
            shape = RoundedCornerShape(10.dp)
        )
        if (errorMessage != null) {
            Text(
                text     = errorMessage,
                color    = ErrorRed,
                style    = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp, top = 2.dp)
            )
        }
    }
}
