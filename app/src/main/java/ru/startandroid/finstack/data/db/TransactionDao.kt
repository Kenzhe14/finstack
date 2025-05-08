package ru.startandroid.finstack.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction as RoomTransaction
import androidx.room.Update
import ru.startandroid.finstack.data.model.Transaction as FinTransaction
import ru.startandroid.finstack.data.model.TransactionWithCategory
import java.util.Date

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: FinTransaction): Long

    @Update
    suspend fun updateTransaction(transaction: FinTransaction)

    @Delete
    suspend fun deleteTransaction(transaction: FinTransaction)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): LiveData<List<FinTransaction>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getTransactionById(id: Long): LiveData<FinTransaction>

    @Query("SELECT * FROM transactions WHERE isExpense = :isExpense ORDER BY date DESC")
    fun getTransactionsByType(isExpense: Boolean): LiveData<List<FinTransaction>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsByDateRange(startDate: Date, endDate: Date): LiveData<List<FinTransaction>>

    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId ORDER BY date DESC")
    fun getTransactionsByCategory(categoryId: Long): LiveData<List<FinTransaction>>

    @Query("SELECT SUM(amount) FROM transactions WHERE isExpense = :isExpense")
    fun getTotalByType(isExpense: Boolean): LiveData<Double>

    @Query("SELECT SUM(amount) FROM transactions WHERE isExpense = :isExpense AND date BETWEEN :startDate AND :endDate")
    fun getTotalByTypeInDateRange(isExpense: Boolean, startDate: Date, endDate: Date): LiveData<Double>

    @RoomTransaction
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getTransactionsWithCategory(): LiveData<List<TransactionWithCategory>>

    @RoomTransaction
    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getTransactionWithCategoryById(id: Long): LiveData<TransactionWithCategory>

    @RoomTransaction
    @Query("SELECT * FROM transactions WHERE isExpense = :isExpense ORDER BY date DESC")
    fun getTransactionsWithCategoryByType(isExpense: Boolean): LiveData<List<TransactionWithCategory>>

    @RoomTransaction
    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsWithCategoryByDateRange(startDate: Date, endDate: Date): LiveData<List<TransactionWithCategory>>

    @RoomTransaction
    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId ORDER BY date DESC")
    fun getTransactionsWithCategoryByCategory(categoryId: Long): LiveData<List<TransactionWithCategory>>

    @RoomTransaction
    @Query("SELECT * FROM transactions WHERE description LIKE '%' || :query || '%' ORDER BY date DESC")
    fun searchTransactionsWithCategory(query: String): LiveData<List<TransactionWithCategory>>
} 