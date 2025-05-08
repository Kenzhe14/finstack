package ru.startandroid.finstack.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import ru.startandroid.finstack.R
import ru.startandroid.finstack.ui.addtransaction.AddTransactionActivity
import ru.startandroid.finstack.ui.main.BalanceFragment
import ru.startandroid.finstack.ui.statistics.StatisticsActivity
import ru.startandroid.finstack.ui.transactions.TransactionListFragment

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var fabAddTransaction: ExtendedFloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Set up toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        
        // Init views
        bottomNavigation = findViewById(R.id.bottom_navigation)
        fabAddTransaction = findViewById(R.id.fab_add_transaction)
        
        // Set up navigation
        setupBottomNavigation()
        
        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(BalanceFragment())
        }
        
        // Set up FAB
        fabAddTransaction.setOnClickListener {
            // Launch AddTransactionActivity
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.navigation_overview -> {
                    loadFragment(BalanceFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_transactions -> {
                    loadFragment(TransactionListFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_statistics -> {
                    // Launch StatisticsActivity
                    val intent = Intent(this, StatisticsActivity::class.java)
                    startActivity(intent)
                    return@setOnItemSelectedListener false
                }
                else -> return@setOnItemSelectedListener false
            }
        }
    }
    
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
} 