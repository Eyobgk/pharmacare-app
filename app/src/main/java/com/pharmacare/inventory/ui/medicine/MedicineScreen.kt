package com.pharmacare.inventory.ui.medicine

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pharmacare.inventory.ui.components.MedicineCard
import com.pharmacare.inventory.ui.components.MedicineFormDialog
import com.pharmacare.inventory.ui.theme.*

/**
 * The Medicine inventory screen.
 *
 * Displays all medicine records in a [LazyColumn] of [MedicineCard]s.
 * A [FloatingActionButton] opens the [MedicineFormDialog] for adding new items.
 * Editing is triggered from each card's Edit button.
 *
 * This composable is stateless — it reads from [viewModel] and forwards
 * all user actions back to it, keeping all business logic in the ViewModel.
 *
 * @param viewModel The [MedicineViewModel] providing state and handling events.
 * @param snackbarHostState Shared [SnackbarHostState] from the parent scaffold.
 */
@Composable
fun MedicineScreen(
    viewModel: MedicineViewModel,
    snackbarHostState: SnackbarHostState
) {
    // Collect StateFlow as Compose State — lifecycle-aware (pauses when in background)
    val medicines by viewModel.medicines.collectAsStateWithLifecycle()
    val dialogState by viewModel.dialogState.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    // Show snackbar whenever there is a pending message
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message  = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearSnackbarMessage()
        }
    }

    // Show dialog when dialog state is non-null
    dialogState?.let { state ->
        MedicineFormDialog(
            medicine   = state.medicine,
            isEditMode = state.isEditMode,
            onDismiss  = viewModel::onDismissDialog,
            onSave     = viewModel::saveMedicine
        )
    }

    Scaffold(
        containerColor = BackgroundDark,
        floatingActionButton = {
            FloatingActionButton(
                onClick          = viewModel::onAddMedicine,
                containerColor   = PrimaryGreen,
                contentColor     = OnPrimaryWhite,
                elevation        = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(
                    imageVector     = Icons.Default.Add,
                    contentDescription = "Add new medicine"
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (medicines.isEmpty()) {
                // Empty state illustration
                EmptyMedicineState(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding        = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement   = Arrangement.spacedBy(12.dp),
                    modifier              = Modifier.fillMaxSize()
                ) {
                    // Summary header
                    item {
                        Text(
                            text  = "${medicines.size} medicine${if (medicines.size != 1) "s" else ""} in stock",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceSecondary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    // Medicine cards
                    items(
                        items = medicines,
                        key   = { it.medicineId }   // Stable keys enable smooth animations
                    ) { medicine ->
                        AnimatedVisibility(
                            visible = true,
                            enter   = fadeIn() + slideInVertically()
                        ) {
                            MedicineCard(
                                medicine = medicine,
                                onEdit   = viewModel::onEditMedicine,
                                onDelete = viewModel::deleteMedicine
                            )
                        }
                    }

                    // Bottom padding so the FAB doesn't cover the last card
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

/**
 * Shown when the medicines list is empty, guiding the user to add their first item.
 */
@Composable
private fun EmptyMedicineState(modifier: Modifier = Modifier) {
    Column(
        modifier            = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector     = Icons.Outlined.Medication,
            contentDescription = null,
            tint            = OnSurfaceSecondary.copy(alpha = 0.4f),
            modifier        = Modifier.size(80.dp)
        )
        Text(
            text      = "No medicines yet",
            style     = MaterialTheme.typography.titleMedium,
            color     = OnSurfaceSecondary,
            fontWeight= FontWeight.SemiBold
        )
        Text(
            text      = "Tap the + button below to add your first medicine to the inventory.",
            style     = MaterialTheme.typography.bodyMedium,
            color     = OnSurfaceSecondary.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}
