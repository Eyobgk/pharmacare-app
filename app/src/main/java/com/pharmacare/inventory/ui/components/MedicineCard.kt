package com.pharmacare.inventory.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pharmacare.inventory.data.local.entity.Medicine
import com.pharmacare.inventory.ui.theme.*

/**
 * A premium card composable that displays a single [Medicine] record.
 *
 * Shows name, category, expiry, quantity, and price. Exposes Edit and Delete
 * actions via icon buttons. Uses an animated gradient accent strip for visual polish.
 *
 * @param medicine  The medicine data to display.
 * @param onEdit    Callback invoked when the Edit icon is tapped.
 * @param onDelete  Callback invoked when the Delete icon is tapped.
 */
@Composable
fun MedicineCard(
    medicine: Medicine,
    onEdit: (Medicine) -> Unit,
    onDelete: (Medicine) -> Unit
) {
    // Local state for the confirmation dialog
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Delete confirmation dialog
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Medicine") },
            text  = { Text("Are you sure you want to delete \"${medicine.medicineName}\"? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete(medicine)
                    }
                ) { Text("Delete", color = ErrorRed) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {

            // Left accent strip with gradient
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(IntrinsicSize.Max)
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(PrimaryGreenLight, PrimaryGreen)
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp, top = 14.dp, bottom = 14.dp, end = 4.dp)
            ) {
                // Header row: icon + name + category chip
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Medication,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = medicine.medicineName,
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurfaceWhite,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    CategoryChip(label = medicine.category)
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = SurfaceVariant, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(10.dp))

                // Detail rows
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoItem(label = "Expiry",    value = medicine.expiryDate)
                    InfoItem(label = "Stock",     value = "${medicine.quantityInStock} units")
                    InfoItem(label = "Price",     value = "${"%.2f".format(medicine.price)}")
                }
            }

            // Action buttons column
            Column(
                modifier = Modifier
                    .padding(top = 8.dp, end = 8.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = { onEdit(medicine) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit ${medicine.medicineName}",
                        tint = SecondaryAmber,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = { showDeleteConfirm = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete ${medicine.medicineName}",
                        tint = ErrorRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/** Small pill chip for displaying the category label. */
@Composable
fun CategoryChip(label: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = PrimaryGreen.copy(alpha = 0.18f)
    ) {
        Text(
            text  = label,
            color = PrimaryGreenLight,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

/** Small label+value column used inside cards. */
@Composable
fun InfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text  = label,
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceSecondary
        )
        Text(
            text  = value,
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceWhite,
            fontWeight = FontWeight.SemiBold
        )
    }
}
