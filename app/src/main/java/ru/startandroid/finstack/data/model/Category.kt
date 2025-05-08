package ru.startandroid.finstack.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * Category entity representing a transaction category in the app.
 */
@Parcelize
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val iconResId: Int,
    val colorResId: Int,
    val isExpense: Boolean
) : Parcelable 