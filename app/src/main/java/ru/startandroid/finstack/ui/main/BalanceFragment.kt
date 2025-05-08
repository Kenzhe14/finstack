package ru.startandroid.finstack.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import ru.startandroid.finstack.FinStackApplication
import ru.startandroid.finstack.R
import ru.startandroid.finstack.ui.transactions.TransactionAdapter
import ru.startandroid.finstack.util.FormatUtils

class BalanceFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var transactionAdapter: TransactionAdapter
    
    // UI components
    private lateinit var balanceAmountTextView: TextView
    private lateinit var incomeAmountTextView: TextView
    private lateinit var expenseAmountTextView: TextView
    private lateinit var periodChipGroup: ChipGroup
    private lateinit var pieChart: PieChart
    private lateinit var recentTransactionsRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_balance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize UI components
        balanceAmountTextView = view.findViewById(R.id.balance_amount)
        incomeAmountTextView = view.findViewById(R.id.income_amount)
        expenseAmountTextView = view.findViewById(R.id.expense_amount)
        periodChipGroup = view.findViewById(R.id.period_chip_group)
        pieChart = view.findViewById(R.id.pie_chart)
        recentTransactionsRecyclerView = view.findViewById(R.id.recent_transactions_recycler)
        
        // Setup recycler view
        recentTransactionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        transactionAdapter = TransactionAdapter()
        recentTransactionsRecyclerView.adapter = transactionAdapter
        
        // Setup view model
        val application = requireActivity().application as FinStackApplication
        val factory = MainViewModel.Factory(
            application.transactionRepository,
            application.categoryRepository,
            application.currencyRepository
        )
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
        
        // Setup period chips
        setupPeriodChips()
        
        // Observe data changes
        observeViewModel()
    }
    
    private fun setupPeriodChips() {
        val chipToday = periodChipGroup.findViewById<Chip>(R.id.chip_today)
        val chipWeek = periodChipGroup.findViewById<Chip>(R.id.chip_week)
        val chipMonth = periodChipGroup.findViewById<Chip>(R.id.chip_month)
        val chipAll = periodChipGroup.findViewById<Chip>(R.id.chip_all)
        
        chipToday.setOnClickListener { viewModel.setPeriod(MainViewModel.Period.TODAY) }
        chipWeek.setOnClickListener { viewModel.setPeriod(MainViewModel.Period.THIS_WEEK) }
        chipMonth.setOnClickListener { viewModel.setPeriod(MainViewModel.Period.THIS_MONTH) }
        chipAll.setOnClickListener { viewModel.setPeriod(MainViewModel.Period.ALL_TIME) }
    }
    
    private fun observeViewModel() {
        // Observe currency
        viewModel.currentCurrency.observe(viewLifecycleOwner) { currency ->
            updateBalanceDisplay()
        }
        
        // Observe balances
        viewModel.totalBalance.observe(viewLifecycleOwner) { updateBalanceDisplay() }
        viewModel.totalIncome.observe(viewLifecycleOwner) { updateBalanceDisplay() }
        viewModel.totalExpense.observe(viewLifecycleOwner) { updateBalanceDisplay() }
        
        // Observe transactions
        viewModel.recentTransactions.observe(viewLifecycleOwner) { transactions ->
            transactionAdapter.submitList(transactions)
        }
        
        // Observe category summary for pie chart
        viewModel.expenseByCategoryMap.observe(viewLifecycleOwner) { categorySummary ->
            // Pie chart would be updated here
            // For simplicity, not implementing pie chart in this example
        }
    }
    
    private fun updateBalanceDisplay() {
        val currency = viewModel.currentCurrency.value ?: "USD"
        
        balanceAmountTextView.text = FormatUtils.formatCurrency(
            viewModel.totalBalance.value ?: 0.0, 
            currency
        )
        
        incomeAmountTextView.text = FormatUtils.formatCurrency(
            viewModel.totalIncome.value ?: 0.0,
            currency
        )
        
        expenseAmountTextView.text = FormatUtils.formatCurrency(
            viewModel.totalExpense.value ?: 0.0,
            currency
        )
    }
} 