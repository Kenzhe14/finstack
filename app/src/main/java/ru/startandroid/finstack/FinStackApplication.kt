package ru.startandroid.finstack

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.startandroid.finstack.data.db.AppDatabase
import ru.startandroid.finstack.data.model.Category
import ru.startandroid.finstack.data.model.Transaction as FinTransaction
import ru.startandroid.finstack.data.repository.CategoryRepository
import ru.startandroid.finstack.data.repository.CurrencyRepository
import ru.startandroid.finstack.data.repository.TransactionRepository
import ru.startandroid.finstack.util.PreferenceManager

/**
 * Application class for FinStack.
 */
class FinStackApplication : Application() {

    // Database instance
    private lateinit var database: AppDatabase
    
    // Repositories
    lateinit var transactionRepository: TransactionRepository
        private set
    
    lateinit var categoryRepository: CategoryRepository
        private set
    
    lateinit var currencyRepository: CurrencyRepository
        private set
    
    // Preferences
    lateinit var preferenceManager: PreferenceManager
        private set
    
    // Application scope
    private val applicationScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        
        // Initialize database
        database = AppDatabase.getDatabase(this)
        
        // Initialize repositories
        transactionRepository = TransactionRepository(database.transactionDao())
        categoryRepository = CategoryRepository(database.categoryDao())
        currencyRepository = CurrencyRepository(this)
        
        // Initialize preferences
        preferenceManager = PreferenceManager(this)
        
        // Set theme mode from preferences
        AppCompatDelegate.setDefaultNightMode(preferenceManager.getThemeMode())
        
        // Initialize database with default categories if needed
        initializeDefaultCategories()
    }
    
    private fun initializeDefaultCategories() {
        applicationScope.launch {
            val categoryCount = categoryRepository.getCategoryCount()
            if (categoryCount == 0) {
                val defaultCategories = listOf(
                    // Expense categories - Food & Dining
                    Category(
                        name = getString(R.string.groceries),
                        iconResId = R.drawable.ic_category_food,
                        colorResId = R.color.category_food,
                        isExpense = true
                    ),
                    Category(
                        name = getString(R.string.restaurants),
                        iconResId = R.drawable.ic_category_food,
                        colorResId = R.color.category_food,
                        isExpense = true
                    ),
                    Category(
                        name = getString(R.string.cafe),
                        iconResId = R.drawable.ic_category_food,
                        colorResId = R.color.category_food,
                        isExpense = true
                    ),
                    
                    // Expense categories - Transport
                    Category(
                        name = getString(R.string.public_transport),
                        iconResId = R.drawable.ic_category_transport,
                        colorResId = R.color.category_transport,
                        isExpense = true
                    ),
                    Category(
                        name = getString(R.string.taxi),
                        iconResId = R.drawable.ic_category_transport,
                        colorResId = R.color.category_transport,
                        isExpense = true
                    ),
                    Category(
                        name = getString(R.string.fuel),
                        iconResId = R.drawable.ic_category_transport,
                        colorResId = R.color.category_transport,
                        isExpense = true
                    ),
                    
                    // Expense categories - Housing & Utilities
                    Category(
                        name = getString(R.string.rent),
                        iconResId = R.drawable.ic_category_housing,
                        colorResId = R.color.category_housing,
                        isExpense = true
                    ),
                    Category(
                        name = getString(R.string.mortgage),
                        iconResId = R.drawable.ic_category_housing,
                        colorResId = R.color.category_housing,
                        isExpense = true
                    ),
                    Category(
                        name = getString(R.string.electricity),
                        iconResId = R.drawable.ic_category_utilities,
                        colorResId = R.color.category_utilities,
                        isExpense = true
                    ),
                    Category(
                        name = getString(R.string.water),
                        iconResId = R.drawable.ic_category_utilities,
                        colorResId = R.color.category_utilities,
                        isExpense = true
                    ),
                    Category(
                        name = getString(R.string.internet),
                        iconResId = R.drawable.ic_category_utilities,
                        colorResId = R.color.category_utilities,
                        isExpense = true
                    ),
                    Category(
                        name = getString(R.string.phone),
                        iconResId = R.drawable.ic_category_utilities,
                        colorResId = R.color.category_utilities,
                        isExpense = true
                    ),
                    
                    // Expense categories - Entertainment
                    Category(
                        name = getString(R.string.movies),
                        iconResId = R.drawable.ic_category_entertainment,
                        colorResId = R.color.category_entertainment,
                        isExpense = true
                    ),
                    Category(
                        name = getString(R.string.concerts),
                        iconResId = R.drawable.ic_category_entertainment,
                        colorResId = R.color.category_entertainment,
                        isExpense = true
                    ),
                    Category(
                        name = getString(R.string.subscriptions),
                        iconResId = R.drawable.ic_category_entertainment,
                        colorResId = R.color.category_entertainment,
                        isExpense = true
                    ),
                    
                    // Expense categories - Shopping
                    Category(
                        name = getString(R.string.clothes),
                        iconResId = R.drawable.ic_category_shopping,
                        colorResId = R.color.category_shopping,
                        isExpense = true
                    ),
                    Category(
                        name = getString(R.string.electronics),
                        iconResId = R.drawable.ic_category_shopping,
                        colorResId = R.color.category_shopping,
                        isExpense = true
                    ),
                    Category(
                        name = getString(R.string.home_goods),
                        iconResId = R.drawable.ic_category_shopping,
                        colorResId = R.color.category_shopping,
                        isExpense = true
                    ),
                    
                    // Expense categories - Health
                    Category(
                        name = getString(R.string.medical),
                        iconResId = R.drawable.ic_category_health,
                        colorResId = R.color.category_health,
                        isExpense = true
                    ),
                    Category(
                        name = getString(R.string.pharmacy),
                        iconResId = R.drawable.ic_category_health,
                        colorResId = R.color.category_health,
                        isExpense = true
                    ),
                    Category(
                        name = getString(R.string.fitness),
                        iconResId = R.drawable.ic_category_health,
                        colorResId = R.color.category_health,
                        isExpense = true
                    ),
                    
                    // Expense categories - Education
                    Category(
                        name = getString(R.string.books),
                        iconResId = R.drawable.ic_category_education,
                        colorResId = R.color.category_education,
                        isExpense = true
                    ),
                    Category(
                        name = getString(R.string.courses),
                        iconResId = R.drawable.ic_category_education,
                        colorResId = R.color.category_education,
                        isExpense = true
                    ),
                    
                    // Expense categories - Other
                    Category(
                        name = getString(R.string.personal_care),
                        iconResId = R.drawable.ic_category_other,
                        colorResId = R.color.category_other,
                        isExpense = true
                    ),
                    Category(
                        name = getString(R.string.gifts_expense),
                        iconResId = R.drawable.ic_category_gift,
                        colorResId = R.color.category_gift,
                        isExpense = true
                    ),
                    Category(
                        name = getString(R.string.travel),
                        iconResId = R.drawable.ic_category_entertainment,
                        colorResId = R.color.category_entertainment,
                        isExpense = true
                    ),
                    Category(
                        name = getString(R.string.taxes),
                        iconResId = R.drawable.ic_category_other,
                        colorResId = R.color.category_other,
                        isExpense = true
                    ),
                    Category(
                        name = getString(R.string.other_expense),
                        iconResId = R.drawable.ic_category_other,
                        colorResId = R.color.category_other,
                        isExpense = true
                    ),
                    
                    // Income categories - Work & Job
                    Category(
                        name = getString(R.string.salary),
                        iconResId = R.drawable.ic_category_salary,
                        colorResId = R.color.category_salary,
                        isExpense = false
                    ),
                    Category(
                        name = getString(R.string.bonus),
                        iconResId = R.drawable.ic_category_salary,
                        colorResId = R.color.category_salary,
                        isExpense = false
                    ),
                    Category(
                        name = getString(R.string.overtime),
                        iconResId = R.drawable.ic_category_salary,
                        colorResId = R.color.category_salary,
                        isExpense = false
                    ),
                    Category(
                        name = getString(R.string.part_time),
                        iconResId = R.drawable.ic_category_salary,
                        colorResId = R.color.category_salary,
                        isExpense = false
                    ),
                    
                    // Income categories - Business
                    Category(
                        name = getString(R.string.business_income),
                        iconResId = R.drawable.ic_category_business,
                        colorResId = R.color.category_business,
                        isExpense = false
                    ),
                    Category(
                        name = getString(R.string.freelance),
                        iconResId = R.drawable.ic_category_business,
                        colorResId = R.color.category_business,
                        isExpense = false
                    ),
                    Category(
                        name = getString(R.string.consulting),
                        iconResId = R.drawable.ic_category_business,
                        colorResId = R.color.category_business,
                        isExpense = false
                    ),
                    Category(
                        name = getString(R.string.side_hustle),
                        iconResId = R.drawable.ic_category_business,
                        colorResId = R.color.category_business,
                        isExpense = false
                    ),
                    
                    // Income categories - Investment & Assets
                    Category(
                        name = getString(R.string.dividends),
                        iconResId = R.drawable.ic_category_investment,
                        colorResId = R.color.category_investment,
                        isExpense = false
                    ),
                    Category(
                        name = getString(R.string.stock_profits),
                        iconResId = R.drawable.ic_category_investment,
                        colorResId = R.color.category_investment,
                        isExpense = false
                    ),
                    Category(
                        name = getString(R.string.crypto),
                        iconResId = R.drawable.ic_category_investment,
                        colorResId = R.color.category_investment,
                        isExpense = false
                    ),
                    Category(
                        name = getString(R.string.rent_income),
                        iconResId = R.drawable.ic_category_investment,
                        colorResId = R.color.category_investment,
                        isExpense = false
                    ),
                    Category(
                        name = getString(R.string.interest),
                        iconResId = R.drawable.ic_category_investment,
                        colorResId = R.color.category_investment,
                        isExpense = false
                    ),
                    Category(
                        name = getString(R.string.pension),
                        iconResId = R.drawable.ic_category_investment,
                        colorResId = R.color.category_investment,
                        isExpense = false
                    ),
                    
                    // Income categories - Other
                    Category(
                        name = getString(R.string.gifts_received),
                        iconResId = R.drawable.ic_category_gift,
                        colorResId = R.color.category_gift,
                        isExpense = false
                    ),
                    Category(
                        name = getString(R.string.cashback),
                        iconResId = R.drawable.ic_category_gift,
                        colorResId = R.color.category_gift,
                        isExpense = false
                    ),
                    Category(
                        name = getString(R.string.refund),
                        iconResId = R.drawable.ic_category_other,
                        colorResId = R.color.category_other,
                        isExpense = false
                    ),
                    Category(
                        name = getString(R.string.selling_items),
                        iconResId = R.drawable.ic_category_business,
                        colorResId = R.color.category_business,
                        isExpense = false
                    ),
                    Category(
                        name = getString(R.string.government_benefits),
                        iconResId = R.drawable.ic_category_other,
                        colorResId = R.color.category_other,
                        isExpense = false
                    ),
                    Category(
                        name = getString(R.string.alimony),
                        iconResId = R.drawable.ic_category_other,
                        colorResId = R.color.category_other,
                        isExpense = false
                    ),
                    Category(
                        name = getString(R.string.lottery),
                        iconResId = R.drawable.ic_category_gift,
                        colorResId = R.color.category_gift,
                        isExpense = false
                    ),
                    Category(
                        name = getString(R.string.scholarship),
                        iconResId = R.drawable.ic_category_education,
                        colorResId = R.color.category_education,
                        isExpense = false
                    ),
                    Category(
                        name = getString(R.string.other_income),
                        iconResId = R.drawable.ic_category_other,
                        colorResId = R.color.category_other,
                        isExpense = false
                    )
                )
                
                categoryRepository.insertAll(defaultCategories)
            }
        }
    }

    companion object {
        private const val TAG = "FinStackApplication"
    }
} 