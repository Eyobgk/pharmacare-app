package com.pharmacare.inventory.ui.equipment

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pharmacare.inventory.ui.components.EquipmentCard
import com.pharmacare.inventory.ui.components.EquipmentFormDialog
import com.pharmacare.inventory.ui.theme.*

/**
 * The Equipment inventory screen.
 *
 * Mirrors [MedicineScreen] in structure but for [Equipment] entities.
 * Uses an amber-tinted FAB to visually differentiate from the Medicine tab.
 *
 * @param viewModel         The [EquipmentViewModel] providing state and handling events.
 * @param snackbarHostState Shared [SnackbarHostState] from the parent scaffold.
 */
@Composable
fun EquipmentScreen(
    viewModel: EquipmentViewModel,
    snackbarHostState: SnackbarHostState
) {
    val equipmentList  by viewModel.equipmentList.collectAsStateWithLifecycle()
    val dialogState    by viewModel.dialogState.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(message = it, duration = SnackbarDuration.Short)
            viewModel.clearSnackbarMessage()
        }
    }

    dialogState?.let { state ->
        EquipmentFormDialog(
            equipment  = state.equipment,
            isEditMode = state.isEditMode,
            onDismiss  = viewModel::onDismissDialog,
            onSave     = viewModel::saveEquipment
        )
    }

    Scaffold(
        containerColor = BackgroundDark,
        floatingActionButton = {
            FloatingActionButton(
                onClick        = viewModel::onAddEquipment,
                containerColor = SecondaryAmber,      // Amber FAB for equipment
                contentColor   = OnPrimaryWhite,
                elevation      = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.Add,
                    contentDescription = "Add new equipment"
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (equipmentList.isEmpty()) {
                EmptyEquipmentState(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier            = Modifier.fillMaxSize()
                ) {
                    item {
                        Text(
                            text  = "${equipmentList.size} equipment item${if (equipmentList.size != 1) "s" else ""} in stock",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceSecondary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    items(
                        items = equipmentList,
                        key   = { it.equipmentId }
                    ) { equipment ->
                        AnimatedVisibility(
                            visible = true,
                            enter   = fadeIn() + slideInVertically()
                        ) {
                            EquipmentCard(
                                equipment = equipment,
                                onEdit    = viewModel::onEditEquipment,
                                onDelete  = viewModel::deleteEquipment
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun EmptyEquipmentState(modifier: Modifier = Modifier) {
    Column(
        modifier            = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector        = Icons.Outlined.MedicalServices,
            contentDescription = null,
            tint               = OnSurfaceSecondary.copy(alpha = 0.4f),
            modifier           = Modifier.size(80.dp)
        )
        Text(
            text       = "No equipment yet",
            style      = MaterialTheme.typography.titleMedium,
            color      = OnSurfaceSecondary,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text      = "Tap the + button below to add your first piece of equipment to the inventory.",
            style     = MaterialTheme.typography.bodyMedium,
            color     = OnSurfaceSecondary.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}
