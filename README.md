# 💊 PharmaCare — Pharmacy Inventory Management App

A fully featured Android application built with **Kotlin**, **Jetpack Compose**, **Room Database**, and **MVVM architecture**.

## Features
- Manage **Medicines** and **Equipment** inventories separately
- Full **CRUD** (Create, Read, Update, Delete) for both entities
- Tabbed navigation between Medicines and Equipment
- Floating Action Button to add new items
- Inline edit/delete per list item
- StateFlow-driven reactive UI
- Input validation with error messages

## Project Structure
```
app/src/main/java/com/pharmacare/inventory/
├── data/
│   ├── local/
│   │   ├── dao/
│   │   │   ├── MedicineDao.kt
│   │   │   └── EquipmentDao.kt
│   │   ├── entity/
│   │   │   ├── Medicine.kt
│   │   │   └── Equipment.kt
│   │   └── PharmacareDatabase.kt
│   └── repository/
│       ├── MedicineRepository.kt
│       └── EquipmentRepository.kt
├── ui/
│   ├── medicine/
│   │   ├── MedicineViewModel.kt
│   │   └── MedicineScreen.kt
│   ├── equipment/
│   │   ├── EquipmentViewModel.kt
│   │   └── EquipmentScreen.kt
│   ├── components/
│   │   ├── MedicineFormDialog.kt
│   │   ├── EquipmentFormDialog.kt
│   │   └── ItemCard.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
└── MainActivity.kt
```

## Setup
1. Open in Android Studio (Hedgehog or newer)
2. Sync Gradle
3. Run on emulator or device (API 26+)
