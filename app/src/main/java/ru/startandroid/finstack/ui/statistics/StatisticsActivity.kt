package ru.startandroid.finstack.ui.statistics

import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.chip.ChipGroup
import ru.startandroid.finstack.FinStackApplication
import ru.startandroid.finstack.R
import ru.startandroid.finstack.data.model.Category
import ru.startandroid.finstack.ui.statistics.StatisticsViewModel.TransactionType
import ru.startandroid.finstack.util.FormatUtils
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StatisticsActivity : AppCompatActivity() {

    private lateinit var viewModel: StatisticsViewModel
    private lateinit var toolbar: Toolbar
    private lateinit var transactionTypeGroup: RadioGroup
    private lateinit var periodChipGroup: ChipGroup
    private lateinit var chartTypeGroup: RadioGroup
    private lateinit var pieChart: PieChart
    private lateinit var barChart: BarChart
    private lateinit var lineChart: LineChart
    private lateinit var chartTitle: TextView
    private lateinit var totalAmountText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        // Initialize views
        toolbar = findViewById(R.id.toolbar)
        transactionTypeGroup = findViewById(R.id.transaction_type_group)
        periodChipGroup = findViewById(R.id.period_chip_group)
        chartTypeGroup = findViewById(R.id.chart_type_group)
        pieChart = findViewById(R.id.pie_chart)
        barChart = findViewById(R.id.bar_chart)
        lineChart = findViewById(R.id.line_chart)
        chartTitle = findViewById(R.id.chart_title)
        totalAmountText = findViewById(R.id.total_amount)

        // Set up toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.statistics)

        // Initialize view model
        val application = application as FinStackApplication
        val factory = StatisticsViewModel.Factory(
            application.transactionRepository,
            application.categoryRepository
        )
        viewModel = ViewModelProvider(this, factory)[StatisticsViewModel::class.java]

        // Set up transaction type radio buttons
        setupTransactionTypeRadios()

        // Set up period chips
        setupPeriodChips()
        
        // Set up chart type radio buttons
        setupChartTypeRadios()

        // Observe data changes
        observeViewModel()
    }
    
    private fun setupTransactionTypeRadios() {
        transactionTypeGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_expense -> {
                    viewModel.setTransactionType(TransactionType.EXPENSE)
                    chartTitle.text = getString(R.string.expenses_by_category)
                }
                R.id.radio_income -> {
                    viewModel.setTransactionType(TransactionType.INCOME)
                    chartTitle.text = getString(R.string.income_by_category)
                }
            }
        }
    }

    private fun setupPeriodChips() {
        // Period chips listeners
        periodChipGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.chip_today -> {
                    viewModel.setPeriod(StatisticsViewModel.Period.TODAY)
                }
                R.id.chip_week -> {
                    viewModel.setPeriod(StatisticsViewModel.Period.THIS_WEEK)
                }
                R.id.chip_month -> {
                    viewModel.setPeriod(StatisticsViewModel.Period.THIS_MONTH)
                }
                R.id.chip_all -> {
                    viewModel.setPeriod(StatisticsViewModel.Period.ALL_TIME)
                }
            }
        }
    }
    
    private fun setupChartTypeRadios() {
        chartTypeGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_pie_chart -> {
                    viewModel.setChartType(StatisticsViewModel.ChartType.PIE_CHART)
                }
                R.id.radio_bar_chart -> {
                    viewModel.setChartType(StatisticsViewModel.ChartType.BAR_CHART)
                }
                R.id.radio_line_chart -> {
                    viewModel.setChartType(StatisticsViewModel.ChartType.LINE_CHART)
                }
            }
        }
    }

    private fun observeViewModel() {
        // Observe category data for pie chart
        viewModel.categorySummary.observe(this) { categorySummary ->
            if (categorySummary.isNotEmpty()) {
                updatePieChart(categorySummary)
            }
        }
        
        // Observe daily summary for line chart
        viewModel.dailySummary.observe(this) { dailySummary ->
            if (dailySummary.isNotEmpty()) {
                updateLineChart(dailySummary)
            }
        }
        
        // Observe monthly summary for bar chart
        viewModel.monthlySummary.observe(this) { monthlySummary ->
            if (monthlySummary.isNotEmpty()) {
                updateBarChart(monthlySummary)
            }
        }
        
        // Observe selected chart type to show/hide the appropriate chart
        viewModel.selectedChartType.observe(this) { chartType ->
            when (chartType) {
                StatisticsViewModel.ChartType.PIE_CHART -> {
                    pieChart.visibility = View.VISIBLE
                    barChart.visibility = View.GONE
                    lineChart.visibility = View.GONE
                }
                StatisticsViewModel.ChartType.BAR_CHART -> {
                    pieChart.visibility = View.GONE
                    barChart.visibility = View.VISIBLE
                    lineChart.visibility = View.GONE
                }
                StatisticsViewModel.ChartType.LINE_CHART -> {
                    pieChart.visibility = View.GONE
                    barChart.visibility = View.GONE
                    lineChart.visibility = View.VISIBLE
                }
            }
        }
        
        // Observe total amount
        viewModel.totalAmount.observe(this) { total ->
            // Format using KZT
            totalAmountText.text = "Total: ${FormatUtils.formatCurrency(total)}"
        }
    }
    
    private fun updatePieChart(categorySummary: Map<Category, Double>) {
        // Create pie chart entries
        val entries = ArrayList<PieEntry>()
        val colors = ArrayList<Int>()
        
        for ((category, amount) in categorySummary) {
            // Add entries for each category
            entries.add(PieEntry(amount.toFloat(), category.name))
            
            // Add colors
            colors.add(ColorTemplate.MATERIAL_COLORS[entries.size % ColorTemplate.MATERIAL_COLORS.size])
        }
        
        // Create dataset
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors
        
        // Create and set data
        val data = PieData(dataSet)
        pieChart.data = data
        
        // Configure chart appearance
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.setDrawEntryLabels(false)
        pieChart.legend.isEnabled = true
        
        // Refresh
        pieChart.invalidate()
    }

    private fun updateLineChart(dailySummary: Map<Date, Double>) {
        val entries = ArrayList<Entry>()
        
        // Sort entries by date
        val sortedDates = dailySummary.keys.sortedBy { it.time }
        
        // Create entries
        sortedDates.forEachIndexed { index, date ->
            val amount = dailySummary[date] ?: 0.0
            entries.add(Entry(index.toFloat(), amount.toFloat()))
        }
        
        // Create dataset
        val dataSet = LineDataSet(entries, "Daily Amounts")
        dataSet.color = ColorTemplate.MATERIAL_COLORS[0]
        dataSet.lineWidth = 2f
        dataSet.setCircleColor(ColorTemplate.MATERIAL_COLORS[0])
        dataSet.circleRadius = 4f
        dataSet.setDrawCircleHole(false)
        
        // Create data object
        val lineData = LineData(dataSet)
        
        // Get line chart and set data
        lineChart.data = lineData
        
        // Format X axis to show dates
        val dateFormatter = SimpleDateFormat("dd MMM", Locale.getDefault())
        lineChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index >= 0 && index < sortedDates.size) {
                    dateFormatter.format(sortedDates[index])
                } else {
                    ""
                }
            }
        }
        
        // Configure chart appearance
        lineChart.description.isEnabled = false
        lineChart.legend.isEnabled = true
        
        // Refresh
        lineChart.invalidate()
    }
    
    private fun updateBarChart(monthlySummary: Map<String, Double>) {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()
        
        // Sort entries by month
        val sortedMonths = monthlySummary.keys.sorted()
        
        // Create entries
        sortedMonths.forEachIndexed { index, month ->
            val amount = monthlySummary[month] ?: 0.0
            entries.add(BarEntry(index.toFloat(), amount.toFloat()))
            
            // Format month for display
            val yearMonth = month.split("-")
            val monthName = when (yearMonth[1].toInt()) {
                1 -> "Jan"
                2 -> "Feb"
                3 -> "Mar"
                4 -> "Apr"
                5 -> "May"
                6 -> "Jun"
                7 -> "Jul"
                8 -> "Aug"
                9 -> "Sep"
                10 -> "Oct"
                11 -> "Nov"
                12 -> "Dec"
                else -> "???"
            }
            labels.add("$monthName ${yearMonth[0]}")
        }
        
        // Create dataset
        val dataSet = BarDataSet(entries, "Monthly Amounts")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        
        // Create data object
        val barData = BarData(dataSet)
        
        // Get bar chart and set data
        barChart.data = barData
        
        // Format X axis to show months
        barChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index >= 0 && index < labels.size) {
                    labels[index]
                } else {
                    ""
                }
            }
        }
        
        // Configure chart appearance
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = true
        
        // Refresh
        barChart.invalidate()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 