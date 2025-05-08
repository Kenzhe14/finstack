package ru.startandroid.finstack.ui.transactions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.startandroid.finstack.R
import ru.startandroid.finstack.data.model.TransactionWithCategory
import ru.startandroid.finstack.util.FormatUtils

class TransactionAdapter(
    private val onItemClick: ((TransactionWithCategory) -> Unit)? = null
) : ListAdapter<TransactionWithCategory, TransactionAdapter.TransactionViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction)
    }

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconImageView: ImageView = itemView.findViewById(R.id.category_icon)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.transaction_description)
        private val categoryTextView: TextView = itemView.findViewById(R.id.category_name)
        private val amountTextView: TextView = itemView.findViewById(R.id.transaction_amount)
        private val dateTextView: TextView = itemView.findViewById(R.id.transaction_date)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(getItem(position))
                }
            }
        }

        fun bind(transaction: TransactionWithCategory) {
            descriptionTextView.text = transaction.transaction.description
            categoryTextView.text = transaction.category.name
            
            // Format and set the amount with appropriate color
            val amount = transaction.transaction.amount
            // Use KZT as the default currency instead of USD
            val formattedAmount = FormatUtils.formatCurrency(amount)
            amountTextView.text = formattedAmount
            
            // Set text color based on transaction type
            val colorResId = if (transaction.transaction.isExpense) {
                R.color.expense
            } else {
                R.color.income
            }
            amountTextView.setTextColor(itemView.context.getColor(colorResId))
            
            // Format and set the date
            dateTextView.text = FormatUtils.formatDate(transaction.transaction.date)
            
            // Set the category icon with background color
            iconImageView.setImageResource(transaction.category.iconResId)
            
            // Set background tint for the icon
            iconImageView.background.setTint(
                itemView.context.getColor(transaction.category.colorResId)
            )
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TransactionWithCategory>() {
            override fun areItemsTheSame(
                oldItem: TransactionWithCategory, 
                newItem: TransactionWithCategory
            ): Boolean {
                return oldItem.transaction.id == newItem.transaction.id
            }

            override fun areContentsTheSame(
                oldItem: TransactionWithCategory, 
                newItem: TransactionWithCategory
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
} 