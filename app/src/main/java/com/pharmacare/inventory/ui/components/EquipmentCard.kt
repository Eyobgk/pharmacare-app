package com.pharmacare.inventory.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pharmacare.inventory.data.local.entity.Equipment
import com.pharmacare.inventory.ui.theme.*

/**
 * A premium card composable that displays a single [Equipment] record.
 *
 * Structurally similar to [MedicineCard] but with a secondary amber accent
 * strip to visually distinguish Equipment from Medicine items.
 *
 * @param equipment The equipment data to display.
 * @param onEdit    Callback invoked when the Edit icon is tapped.
 * @param onDelete  Callback invoked when the Delete icon is tapped.
 */
@Composable
fun EquipmentCard(
    equipment: Equipment,
    onEdit: (Equipment) -> Unit,
    onDelete: (Equipment) -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Delete confirmation dialog
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Equipment") },
            text  = { Text("Are you sure you want to delete \"${equipment.equipmentName}\"? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete(equipment)
                    }
                ) { Text("Delete", color = ErrorRed) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
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

            // Amber accent strip for Equipment (differentiates from Medicine cards)
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(IntrinsicSize.Max)
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(SecondaryAmberLight, SecondaryAmber)
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
                        imageVector = Icons.Outlined.MedicalServices,
                        contentDescription = null,
                        tint = SecondaryAmber,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = equipment.equipmentName,
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurfaceWhite,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    // Amber-tinted chip for equipment category
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = SecondaryAmber.copy(alpha = 0.18f)
                    ) {
                        Text(
                            text = equipment.category,
                            color = SecondaryAmberLight,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = SurfaceVariant, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(10.dp))

                // Detail row: stock and price (no expiry for equipment)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    InfoItem(label = "Stock", value = "${equipment.quantityInStock} units")
                    InfoItem(label = "Price", value = "${"%.2f".format(equipment.price)}")
                }
            }

            // Action buttons
            Column(
                modifier = Modifier.padding(top = 8.dp, end = 8.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = { onEdit(equipment) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit ${equipment.equipmentName}",
                        tint = SecondaryAmber,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = { showDeleteConfirm = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete ${equipment.equipmentName}",
                        tint = ErrorRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
