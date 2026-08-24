# Shree Ganesh POS Application Documentation

## Overview

Shree Ganesh is a Point of Sale (POS) Android application designed to manage a store's inventory, process billing, manage users, and view sales reports. The application is built using modern Android development practices, utilizing Kotlin, Jetpack Compose, Room Database, and Hilt for dependency injection.

## Core Functionalities

### 1. Dashboard
- **Description:** Provides a summary of the store's performance.
- **Key Metrics:** Daily sales, total transactions, total revenue, and low stock warnings.
- **How to use:** Navigate to the Dashboard from the bottom navigation bar or side menu.

### 2. Product Management (Inventory)
- **Description:** Allows the store owner to view, add, edit, and delete products and categories.
- **How to use:** Navigate to the "Products" section.
    - **Add Category:** Click the "Add Category" button, enter the name, and save.
    - **Add Product:** Click the "Add Product" button, fill out the form (name, price, stock, category), and save.
    - **Edit Product:** Click on the edit icon on a product card, update the details in the form, and save.

### 3. Billing (Point of Sale)
- **Description:** The core feature for ringing up sales.
- **How to use:** Navigate to the "Billing" section.
    - **Add to Cart:** Tap on products from the grid to add them to the cart.
    - **Cart Management:** Increase/decrease quantity or remove items in the cart summary section.
    - **Checkout:** Review the total and complete the transaction. Stock quantities are automatically updated.

### 4. Reports
- **Description:** Displays historical data and sales trends.
- **How to use:** Navigate to the "Reports" section to view metrics and charts for daily, weekly, or monthly sales.

### 5. Settings
- **Description:** Configure store details and preferences.
- **How to use:** Navigate to the "Settings" section to update store name, address, tax rate, and currency symbol.

### 6. User Management
- **Description:** Add or edit users and roles (e.g., Admin, Cashier) who can access the POS.
- **How to use:** Navigate to the "Users" section.

## Navigation Flow

The app typically uses a main navigation container with either a Bottom Navigation Bar (for smaller screens/phones) or a Navigation Rail / Drawer (for larger screens/tablets).

*   **Initial Screen:** Splash Screen -> Login Screen.
*   **Main Flow:** After login, the user lands on the Dashboard.
*   **Bottom Navigation Tabs:** Dashboard, Billing, Products, Reports, Settings/Users.

## Key Files and Directories

### UI Layer (`app/src/main/java/com/example/shreeganesh/ui/`)
*   **`screens/`**: Contains the UI logic for each feature.
    *   `products/ProductManagementScreen.kt`: UI for listing, adding, and editing products.
    *   `products/ProductManagementViewModel.kt`: State management for the products screen.
    *   `billing/BillingScreen.kt`: UI for the POS checkout flow.
    *   `dashboard/DashboardScreen.kt`: UI for the main dashboard.
*   **`components/`**: Reusable Compose UI widgets (e.g., `POSCard.kt`, `POSButton.kt`).
*   **`navigation/AppNavigation.kt`**: Defines the navigation routes and flow between screens.

### Data Layer (`app/src/main/java/com/example/shreeganesh/data/`)
*   **`local/entities/`**: Defines the SQLite database tables (e.g., `ProductEntity`, `TransactionEntity`).
*   **`local/dao/`**: Data Access Objects defining SQL queries (e.g., `ProductDao`, `TransactionDao`).
*   **`repository/POSRepository.kt`**: The single source of truth for data access, abstracting the Room database operations from the ViewModels.

### Domain Layer (`app/src/main/java/com/example/shreeganesh/domain/`)
*   **`models/`**: Clean architecture domain models representing the business entities (e.g., `Product`, `Category`, `CartItem`).

### Dependency Injection (`app/src/main/java/com/example/shreeganesh/di/`)
*   **`DatabaseModule.kt`**: Hilt module to provide singleton instances of the database and DAOs.
