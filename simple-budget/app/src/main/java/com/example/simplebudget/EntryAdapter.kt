package com.example.simplebudget

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.simplebudget.databinding.ItemEntryBinding
import java.text.NumberFormat
import java.util.Locale

class EntryAdapter(
    val entries: MutableList<Entry>,
    private val onItemLongPress: (Entry) -> Unit
) : RecyclerView.Adapter<EntryAdapter.ViewHolder>() {

    private val moneyFormat: NumberFormat = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    class ViewHolder(val binding: ItemEntryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEntryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        val color = ContextCompat.getColor(
            holder.binding.root.context,
            if (entry.isExpense) R.color.expense_red else R.color.income_green
        )
        holder.binding.tvDescription.text = entry.description
        val sign = if (entry.isExpense) "-" else "+"
        holder.binding.tvAmount.text = sign + moneyFormat.format(entry.amount)
        holder.binding.tvAmount.setTextColor(color)
        holder.itemView.setOnLongClickListener {
            onItemLongPress(entry)
            true
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun replaceAll(newEntries: List<Entry>) {
        entries.clear()
        entries.addAll(newEntries)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = entries.size
}
