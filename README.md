# 📊 FinStack: Financial Management Application

> **Authors:** Kenzhebai Nariman, Murat Manat  
> **Supervisor:** Amreeva A.M.

---

## 📱 About the Project

**FinStack** is a modern Android application designed to help users efficiently manage their personal finances. With a clean UI and powerful features, it offers robust tools for tracking income and expenses, analyzing financial habits, and customizing the app to user preferences — all with support for multiple currencies including the Kazakhstani tenge (₸).

---

## 🎯 Key Features

- Intuitive transaction management for income and expenses
- Detailed categorization of financial activities
- Visual data analysis via charts and reports
- Multi-currency support (focus on ₸)
- Localized, user-friendly interface
- Filters for time periods: Today, Week, Month, All Time

---

## 🧱 Architecture Overview

FinStack follows the **MVVM (Model-View-ViewModel)** architecture pattern for clean separation of concerns and maintainability.

### 🔹 Data Layer
- **Models:** `Transaction`, `Category`
- **Room Database:** SQLite abstraction with `TypeConverters`
- **DAOs:** CRUD operations for transactions and categories
- **Repositories:** `TransactionRepository`, `CategoryRepository`, `CurrencyRepository`

### 🔹 Domain Layer
- Business logic implemented in `ViewModel` and use cases

### 🔹 Presentation Layer
- `Activities` and `Fragments` for UI
- `ViewModels` for UI state management
- `RecyclerView` Adapters for dynamic lists

---

## ⚙️ Technologies Used

| Component | Technology |
|----------|------------|
| Language | Kotlin |
| IDE | Android Studio |
| Architecture | MVVM |
| Database | Room (with SQLite) |
| UI | Material Design Components |
| Charting Library | [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) |
| Async Tasks | Kotlin Coroutines |
| Dependency Injection | Manual (via Application class) |

---

## 📋 Screens Overview

### 🏠 **Home Screen**
- Total balance, income, and expense overview
- Recent transactions
- Period filters (Today, Week, Month, All Time)
- Quick add via Floating Action Button

### 📄 **Transactions Screen**
- Full transaction history
- Search and filter by category or type
- Swipe-to-delete functionality

### ➕ **Add Transaction Screen**
- Add income or expense
- Input fields: amount, date, category
- Color-coded UI: green (income), red (expense)
- Input validation

### 📈 **Statistics Screen**
- Visual charts: pie, bar, and line graphs
- Category distribution
- Time-based trend analysis

### ⚙️ **Settings Screen**
- Currency preferences
- Language settings
- Theme customization

---

## 🧩 UI Modularity & Lifecycle

### Fragments
- `BalanceFragment`: Overview of financial summary
- `TransactionListFragment`: Filterable list of transactions

### Fragment Lifecycle:
- `onCreateView()`: Inflate layout
- `onViewCreated()`: Setup observers and UI logic
- `onStart()`: Refresh data

### Activities Lifecycle:
- `onCreate()`: Initialize ViewModels and layout
- `onResume()`: Reload data if needed
- `onSaveInstanceState()`: Save UI state during rotation

---

## 🧮 Database Schema

### Transactions Table

| Field        | Type   |
|--------------|--------|
| id           | Int    |
| amount       | Float  |
| description  | String |
| date         | Date   |
| categoryId   | Int    |
| isExpense    | Boolean |

### Categories Table

| Field        | Type   |
|--------------|--------|
| id           | Int    |
| name         | String |
| iconResId    | Int    |
| colorResId   | Int    |
| isExpense    | Boolean |

---

## 🔗 References & Documentation

- 📚 [Android Developer Docs](https://developer.android.com/docs)
- 🏛️ [Room Persistence Library](https://developer.android.com/training/data-storage/room)
- 🎨 [Material Design Guidelines](https://material.io/design)
- 📈 [MPAndroidChart GitHub](https://github.com/PhilJay/MPAndroidChart)
- ⚙️ [Kotlin Coroutines Overview](https://kotlinlang.org/docs/coroutines-overview.html)
- 🧠 [MVVM Architecture](https://developer.android.com/topic/libraries/architecture/viewmodel)
- 🧩 [Android Architecture Components](https://developer.android.com/topic/libraries/architecture)

---

## 👨‍💻 Contacts

Have questions or feedback? Reach out to the authors:

- **Kenzhebai Nariman** narikenzhebai@gmail.com 
- **Murat Manat**  manatmurat968@gmail.com

---

_This README is crafted according to the best practices for open-source GitHub projects and is fully ready for deployment._

