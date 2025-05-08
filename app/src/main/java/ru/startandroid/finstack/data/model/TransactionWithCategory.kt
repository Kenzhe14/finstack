package ru.startandroid.finstack.data.model

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.parcelize.Parcelize

/**
 * Class that holds a transaction with its related category information.
 */
@Parcelize
data class TransactionWithCategory(
    @Embedded
    val transaction: Transaction,
    
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: Category
) : Parcelable 