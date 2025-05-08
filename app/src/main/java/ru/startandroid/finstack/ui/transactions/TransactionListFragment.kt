package ru.startandroid.finstack.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup
import ru.startandroid.finstack.FinStackApplication
import ru.startandroid.finstack.R
import ru.startandroid.finstack.data.model.TransactionWithCategory
import java.util.*

class TransactionListFragment : Fragment() {

    private lateinit var viewModel: TransactionsViewModel
    private lateinit var adapter: TransactionAdapter
    
    private lateinit var searchView: SearchView
    private lateinit var filterChipGroup: ChipGroup
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transaction_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize views
        searchView = view.findViewById(R.id.search_view)
        filterChipGroup = view.findViewById(R.id.filter_chip_group)
        recyclerView = view.findViewById(R.id.transactions_recycler_view)
        emptyView = view.findViewById(R.id.empty_view)
        
        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = TransactionAdapter { transaction ->
            onTransactionClick(transaction)
        }
        recyclerView.adapter = adapter
        
        // Initialize ViewModel
        val application = requireActivity().application as FinStackApplication
        val factory = TransactionsViewModel.Factory(
            application.transactionRepository,
            application.categoryRepository
        )
        viewModel = ViewModelProvider(this, factory)[TransactionsViewModel::class.java]
        
        // Setup search
        setupSearch()
        
        // Setup filters
        setupFilters()
        
        // Observe data
        observeViewModel()
    }
    
    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setSearchQuery(newText ?: "")
                return true
            }
        })
    }
    
    private fun setupFilters() {
        // Setup chip click listeners
        filterChipGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.chip_all -> {
                    viewModel.clearFilters()
                }
                R.id.chip_expense -> {
                    viewModel.setTypeFilter(TransactionsViewModel.TransactionType.EXPENSE)
                }
                R.id.chip_income -> {
                    viewModel.setTypeFilter(TransactionsViewModel.TransactionType.INCOME)
                }
                R.id.chip_date -> {
                    // Простая реализация для фильтра по текущему месяцу
                    val calendar = Calendar.getInstance()
                    val endDate = calendar.time
                    
                    calendar.set(Calendar.DAY_OF_MONTH, 1)
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    val startDate = calendar.time
                    
                    viewModel.setDateRangeFilter(Pair(startDate, endDate))
                }
                R.id.chip_category -> {
                    // Для демонстрации просто фильтруем по первой доступной категории расходов
                    viewModel.expenseCategories.value?.firstOrNull()?.let { category ->
                        viewModel.setCategoryFilter(category)
                    }
                }
            }
        }
    }
    
    private fun observeViewModel() {
        viewModel.filteredTransactions.observe(viewLifecycleOwner) { transactions ->
            adapter.submitList(transactions)
            
            // Show/hide empty view
            if (transactions.isEmpty()) {
                emptyView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
    }
    
    private fun onTransactionClick(transaction: TransactionWithCategory) {
        // Navigate to transaction details
        // For simplicity, not implementing navigation in this example
    }
} 