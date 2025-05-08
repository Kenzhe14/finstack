package ru.startandroid.finstack.data.repository

import androidx.lifecycle.LiveData
import ru.startandroid.finstack.data.db.TransactionDao
import ru.startandroid.finstack.data.model.Transaction as FinTransaction
import ru.startandroid.finstack.data.model.TransactionWithCategory
import java.util.Date

/**
 * Repository for handling Transaction data operations.
 */
class TransactionRepository(private val transactionDao: TransactionDao) {

    val allTransactions: LiveData<List<FinTransaction>> = transactionDao.getAllTransactions()
    val allTransactionsWithCategory: LiveData<List<TransactionWithCategory>> = transactionDao.getTransactionsWithCategory()

    suspend fun insert(transaction: FinTransaction): Long {
        return transactionDao.insertTransaction(transaction)
    }

    suspend fun update(transaction: FinTransaction) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun delete(transaction: FinTransaction) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun deleteById(id: Long) {
        transactionDao.deleteTransactionById(id)
    }

    suspend fun deleteAll() {
        transactionDao.deleteAllTransactions()
    }

    fun getTransactionById(id: Long): LiveData<FinTransaction> {
        return transactionDao.getTransactionById(id)
    }

    fun getTransactionWithCategoryById(id: Long): LiveData<TransactionWithCategory> {
        return transactionDao.getTransactionWithCategoryById(id)
    }

    fun getTransactionsByType(isExpense: Boolean): LiveData<List<FinTransaction>> {
        return transactionDao.getTransactionsByType(isExpense)
    }

    fun getTransactionsWithCategoryByType(isExpense: Boolean): LiveData<List<TransactionWithCategory>> {
        return transactionDao.getTransactionsWithCategoryByType(isExpense)
    }

    fun getTransactionsByDateRange(startDate: Date, endDate: Date): LiveData<List<FinTransaction>> {
        return transactionDao.getTransactionsByDateRange(startDate, endDate)
    }

    fun getTransactionsWithCategoryByDateRange(startDate: Date, endDate: Date): LiveData<List<TransactionWithCategory>> {
        return transactionDao.getTransactionsWithCategoryByDateRange(startDate, endDate)
    }

    fun getTransactionsByCategory(categoryId: Long): LiveData<List<FinTransaction>> {
        return transactionDao.getTransactionsByCategory(categoryId)
    }

    fun getTransactionsWithCategoryByCategory(categoryId: Long): LiveData<List<TransactionWithCategory>> {
        return transactionDao.getTransactionsWithCategoryByCategory(categoryId)
    }

    fun getTotalByType(isExpense: Boolean): LiveData<Double> {
        return transactionDao.getTotalByType(isExpense)
    }

    fun getTotalByTypeInDateRange(isExpense: Boolean, startDate: Date, endDate: Date): LiveData<Double> {
        return transactionDao.getTotalByTypeInDateRange(isExpense, startDate, endDate)
    }

    fun searchTransactionsWithCategory(query: String): LiveData<List<TransactionWithCategory>> {
        return transactionDao.searchTransactionsWithCategory(query)
    }
} 