package com.pharmacare.inventory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pharmacare.inventory.ui.equipment.EquipmentScreen
import com.pharmacare.inventory.ui.equipment.EquipmentViewModel
import com.pharmacare.inventory.ui.medicine.MedicineScreen
import com.pharmacare.inventory.ui.medicine.MedicineViewModel
import com.pharmacare.inventory.ui.theme.*

/**
 * Application entry point.
 *
 * Sets up the Compose content root, applies the [PharmacareTheme], and hosts
 * the top-level [PharmacareApp] composable. Edge-to-edge rendering is enabled
 * so the gradient header bleeds into the status bar area.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PharmacareTheme {
                PharmacareApp()
            }
        }
    }
}

// ---------- Tab model ----------

/** Represents one tab in the bottom navigation bar. */
private data class TabItem(
    val title: String,
    val icon: ImageVector,
    val contentDescription: String
)

private val tabs = listOf(
    TabItem("Medicines",  Icons.Outlined.Medication,     "Medicines tab"),
    TabItem("Equipment",  Icons.Outlined.MedicalServices, "Equipment tab")
)

// ---------- Root composable ----------

/**
 * Root composable for the PharmaCare app.
 *
 * Manages the selected tab index and routes to [MedicineScreen] or
 * [EquipmentScreen] accordingly. A single [SnackbarHostState] is shared
 * between both screens so snackbars appear in the same Scaffold.
 *
 * ViewModels are obtained here via [viewModel] (scoped to the Activity)
 * so they survive recomposition and tab switches.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PharmacareApp() {
    // ViewModels — Activity-scoped, survive configuration changes
    val medicineViewModel: MedicineViewModel   = viewModel()
    val equipmentViewModel: EquipmentViewModel = viewModel()

    var selectedTab by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        containerColor  = BackgroundDark,
        snackbarHost    = { SnackbarHost(snackbarHostState) },
        topBar          = { PharmacareTopBar() },
        bottomBar       = {
            PharmacareBottomBar(
                selectedTab    = selectedTab,
                onTabSelected  = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> MedicineScreen(
                    viewModel         = medicineViewModel,
                    snackbarHostState = snackbarHostState
                )
                1 -> EquipmentScreen(
                    viewModel         = equipmentViewModel,
                    snackbarHostState = snackbarHostState
                )
            }
        }
    }
}

// ---------- Top App Bar ----------

/**
 * Gradient top app bar with the app name and a subtitle.
 * Uses [WindowInsets.statusBars] padding so it renders correctly
 * with edge-to-edge enabled.
 */
@Composable
private fun PharmacareTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    listOf(PrimaryGreenDark, PrimaryGreen)
                )
            )
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // App icon badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = OnPrimaryWhite.copy(alpha = 0.15f)
            ) {
                Icon(
                    imageVector        = Icons.Default.Favorite,
                    contentDescription = null,
                    tint               = OnPrimaryWhite,
                    modifier           = Modifier
                        .padding(8.dp)
                        .size(24.dp)
                )
            }

            Column {
                Text(
                    text       = "PharmaCare",
                    color      = OnPrimaryWhite,
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text   = "Inventory Management",
                    color  = OnPrimaryWhite.copy(alpha = 0.75f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

// ---------- Bottom Navigation ----------

/**
 * Custom bottom navigation bar with animated tab indicator.
 *
 * Uses [NavigationBar] from Material3. The selected tab icon and label
 * animate to the primary (or amber) colour, while unselected items remain
 * muted to guide attention to the active section.
 *
 * @param selectedTab   Index of the currently active tab.
 * @param onTabSelected Callback with the newly selected tab index.
 */
@Composable
private fun PharmacareBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = SurfaceDark,
        tonalElevation = 0.dp
    ) {
        tabs.forEachIndexed { index, tab ->
            val isSelected = index == selectedTab

            // Animate the icon/label colour on selection
            val tint by animateColorAsState(
                targetValue = when {
                    isSelected && index == 0 -> PrimaryGreen
                    isSelected && index == 1 -> SecondaryAmber
                    else                     -> OnSurfaceSecondary
                },
                label = "tab_color_$index"
            )

            NavigationBarItem(
                selected = isSelected,
                onClick  = { onTabSelected(index) },
                icon     = {
                    Icon(
                        imageVector        = tab.icon,
                        contentDescription = tab.contentDescription,
                        tint               = tint
                    )
                },
                label    = {
                    Text(
                        text  = tab.title,
                        color = tint,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                colors   = NavigationBarItemDefaults.colors(
                    // Remove the default tinted indicator background
                    indicatorColor = when (index) {
                        0    -> PrimaryGreen.copy(alpha = 0.15f)
                        else -> SecondaryAmber.copy(alpha = 0.15f)
                    }
                )
            )
        }
    }
}
