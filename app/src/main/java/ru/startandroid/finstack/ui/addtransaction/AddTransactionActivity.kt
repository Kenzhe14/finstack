package ru.startandroid.finstack.ui.addtransaction

import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.radiobutton.MaterialRadioButton
import ru.startandroid.finstack.FinStackApplication
import ru.startandroid.finstack.R
import ru.startandroid.finstack.data.model.Category
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.res.ColorStateList

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var viewModel: AddTransactionViewModel
    private lateinit var toolbar: Toolbar
    private lateinit var amountLayout: TextInputLayout
    private lateinit var amountEdit: TextInputEditText
    private lateinit var descriptionLayout: TextInputLayout
    private lateinit var descriptionEdit: TextInputEditText
    private lateinit var dateLayout: TextInputLayout
    private lateinit var dateEdit: TextInputEditText
    private lateinit var categoryLayout: TextInputLayout
    private lateinit var categoryDropdown: AutoCompleteTextView
    private lateinit var transactionTypeGroup: android.widget.RadioGroup
    private lateinit var radioExpense: MaterialRadioButton
    private lateinit var radioIncome: MaterialRadioButton
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var categories = listOf<Category>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)
        
        // Initialize views
        toolbar = findViewById(R.id.toolbar)
        amountLayout = findViewById(R.id.amount_layout)
        amountEdit = findViewById(R.id.amount_edit)
        descriptionLayout = findViewById(R.id.description_layout)
        descriptionEdit = findViewById(R.id.description_edit)
        dateLayout = findViewById(R.id.date_layout)
        dateEdit = findViewById(R.id.date_edit)
        categoryLayout = findViewById(R.id.category_layout)
        categoryDropdown = findViewById(R.id.category_dropdown)
        transactionTypeGroup = findViewById(R.id.transaction_type_group)
        radioExpense = findViewById(R.id.radio_expense)
        radioIncome = findViewById(R.id.radio_income)
        saveButton = findViewById(R.id.save_button)
        cancelButton = findViewById(R.id.cancel_button)
        
        // Set up toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.add_transaction)
        
        // Initialize ViewModel
        val application = application as FinStackApplication
        val factory = AddTransactionViewModel.Factory(
            application.transactionRepository,
            application.categoryRepository
        )
        viewModel = ViewModelProvider(this, factory)[AddTransactionViewModel::class.java]
        
        // Setup date picker
        setupDatePicker()
        
        // Setup category spinner
        setupCategoryDropdown()
        
        // Setup buttons
        setupButtons()
        
        // Set default values
        setupDefaults()
        
        // Observe error messages
        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                // Show error message (e.g., using a Snackbar)
                viewModel.clearError()
            }
        }
        
        // Observe when transaction is saved
        viewModel.transactionSaved.observe(this) { saved ->
            if (saved) {
                finish()
            }
        }
    }
    
    private fun setupDatePicker() {
        // Set current date as default
        dateEdit.setText(dateFormat.format(Date()))
        
        dateEdit.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.date))
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
                
            datePicker.addOnPositiveButtonClickListener { selection ->
                val date = Date(selection)
                dateEdit.setText(dateFormat.format(date))
                viewModel.setDate(date)
            }
            
            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }
    }
    
    private fun setupCategoryDropdown() {
        // Observe expense categories when expense is selected
        viewModel.expenseCategories.observe(this) { expenseCategories ->
            if (radioExpense.isChecked) {
                updateCategoryDropdown(expenseCategories)
                // Обновляем заголовок и подсказки для расходов
                updateFormForExpense()
            }
        }
        
        // Observe income categories when income is selected
        viewModel.incomeCategories.observe(this) { incomeCategories ->
            if (radioIncome.isChecked) {
                updateCategoryDropdown(incomeCategories)
                // Обновляем заголовок и подсказки для доходов
                updateFormForIncome()
            }
        }
        
        // Update categories when transaction type changes
        transactionTypeGroup.setOnCheckedChangeListener { _, checkedId ->
            val isIncome = checkedId == R.id.radio_income
            viewModel.setExpenseType(!isIncome)
            
            // Обновляем интерфейс в зависимости от типа транзакции
            if (isIncome) {
                updateFormForIncome()
            } else {
                updateFormForExpense()
            }
        }
        
        // Handle category selection
        categoryDropdown.setOnItemClickListener { _, _, position, _ ->
            if (position < categories.size) {
                viewModel.setCategory(categories[position])
            }
        }
    }
    
    private fun updateCategoryDropdown(categoryList: List<Category>) {
        categories = categoryList
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            categoryList.map { it.name }
        )
        categoryDropdown.setAdapter(adapter)
    }
    
    private fun updateFormForExpense() {
        // Обновляем заголовок и подсказки для расходов
        categoryLayout.hint = getString(R.string.expense_category)
        supportActionBar?.title = getString(R.string.add_expense)
        amountLayout.hint = getString(R.string.expense_amount)
        descriptionLayout.hint = getString(R.string.expense_description)
        
        // Меняем стиль кнопки и полей ввода
        saveButton.backgroundTintList = ColorStateList.valueOf(getColor(R.color.expense))
        amountLayout.setBoxStrokeColorStateList(getColorStateList(R.color.expense))
        categoryLayout.setBoxStrokeColorStateList(getColorStateList(R.color.expense))
        descriptionLayout.setBoxStrokeColorStateList(getColorStateList(R.color.expense))
        dateLayout.setBoxStrokeColorStateList(getColorStateList(R.color.expense))
        
        // Обновляем подсказки для категорий расходов
        categoryDropdown.hint = getString(R.string.choose_expense_category)
    }
    
    private fun updateFormForIncome() {
        // Обновляем заголовок и подсказки для доходов
        categoryLayout.hint = getString(R.string.income_category)
        supportActionBar?.title = getString(R.string.add_income)
        amountLayout.hint = getString(R.string.income_amount)
        descriptionLayout.hint = getString(R.string.income_description)
        
        // Меняем стиль кнопки и полей ввода
        saveButton.backgroundTintList = ColorStateList.valueOf(getColor(R.color.income))
        amountLayout.setBoxStrokeColorStateList(getColorStateList(R.color.income))
        categoryLayout.setBoxStrokeColorStateList(getColorStateList(R.color.income))
        descriptionLayout.setBoxStrokeColorStateList(getColorStateList(R.color.income))
        dateLayout.setBoxStrokeColorStateList(getColorStateList(R.color.income))
        
        // Обновляем подсказки для категорий доходов
        categoryDropdown.hint = getString(R.string.choose_income_category)
    }
    
    private fun setupButtons() {
        saveButton.setOnClickListener {
            if (validateInputs()) {
                saveTransaction()
            }
        }
        
        cancelButton.setOnClickListener {
            finish()
        }
    }
    
    private fun setupDefaults() {
        // Set expense as default transaction type
        radioExpense.isChecked = true
        viewModel.setExpenseType(true)
    }
    
    private fun validateInputs(): Boolean {
        var isValid = true
        
        // Validate amount
        val amountStr = amountEdit.text.toString()
        if (amountStr.isEmpty()) {
            amountLayout.error = getString(R.string.amount) + " " + getString(R.string.error)
            isValid = false
        } else {
            amountLayout.error = null
            viewModel.setAmount(amountStr.toDoubleOrNull() ?: 0.0)
        }
        
        // Validate description
        val description = descriptionEdit.text.toString()
        if (description.isEmpty()) {
            descriptionLayout.error = getString(R.string.description) + " " + getString(R.string.error)
            isValid = false
        } else {
            descriptionLayout.error = null
            viewModel.setDescription(description)
        }
        
        return isValid
    }
    
    private fun saveTransaction() {
        viewModel.saveTransaction()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 