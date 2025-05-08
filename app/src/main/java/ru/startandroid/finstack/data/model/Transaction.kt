package ru.startandroid.finstack.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.parcelize.Parcelize
import ru.startandroid.finstack.data.db.DateConverter
import java.util.Date

/**
 * Transaction entity representing a financial transaction in the app.
 */
@Parcelize
@Entity(tableName = "transactions")
@TypeConverters(DateConverter::class)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val description: String,
    val date: Date,
    val categoryId: Long,
    val isExpense: Boolean,
    val isFavorite: Boolean = false
) : Parcelable 