package com.example.simplebudget

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplebudget.databinding.ActivityMainBinding
import com.example.simplebudget.databinding.DialogEditEntryBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var store: EntryStore
    private lateinit var adapter: EntryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        store = EntryStore(this)
        adapter = EntryAdapter(store.load(), ::showItemOptions)
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = adapter

        binding.btnIncome.setOnClickListener { addEntry(isExpense = false) }
        binding.btnExpense.setOnClickListener { addEntry(isExpense = true) }

        updateHeader()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clear -> {
                confirmClearAll()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addEntry(isExpense: Boolean) {
        val description = binding.etDescription.text.toString().trim()
        val amount = binding.etAmount.text.toString().trim().toDoubleOrNull()

        if (amount == null || amount <= 0) {
            binding.tilAmount.error = getString(R.string.error_amount)
            return
        }
        binding.tilAmount.error = null

        val entry = Entry(
            id = System.currentTimeMillis(),
            description = description.ifEmpty { getString(R.string.default_description) },
            amount = amount,
            isExpense = isExpense,
            createdAt = System.currentTimeMillis()
        )
        adapter.entries.add(entry)
        adapter.notifyItemInserted(adapter.entries.size - 1)
        binding.recycler.scrollToPosition(adapter.entries.size - 1)
        store.save(adapter.entries)

        binding.etDescription.setText("")
        binding.etAmount.setText("")
        binding.etDescription.requestFocus()
        updateHeader()
        hideKeyboard()
    }

    private fun updateHeader() {
        val entries = adapter.entries
        val income = entries.filter { !it.isExpense }.sumOf { it.amount }
        val expense = entries.filter { it.isExpense }.sumOf { it.amount }
        val balance = income - expense
        binding.tvBalance.text = formatMoney(balance)
        binding.tvBalance.setTextColor(
            getColor(if (balance < 0) R.color.expense_red else R.color.balance_black)
        )
        binding.tvTotals.text = getString(
            R.string.totals_line, formatMoney(income), formatMoney(expense)
        )
        binding.tvEmpty.visibility =
            if (entries.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showItemOptions(entry: Entry) {
        val options = arrayOf(getString(R.string.action_edit), getString(R.string.action_delete))
        MaterialAlertDialogBuilder(this)
            .setTitle(entry.description)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditDialog(entry)
                    1 -> deleteEntry(entry)
                }
            }
            .show()
    }

    private fun showEditDialog(entry: Entry) {
        val dialogBinding = DialogEditEntryBinding.inflate(layoutInflater)
        dialogBinding.etDescription.setText(entry.description)
        dialogBinding.etAmount.setText(
            String.format(Locale.US, "%.2f", entry.amount)
        )

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.action_edit)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.action_save) { _, _ ->
                val newDescription = dialogBinding.etDescription.text.toString().trim()
                val newAmount = dialogBinding.etAmount.text.toString()
                    .trim().toDoubleOrNull()
                if (newAmount != null && newAmount > 0) {
                    val index = adapter.entries.indexOfFirst { it.id == entry.id }
                    if (index >= 0) {
                        val existing = adapter.entries[index]
                        adapter.entries[index] = entry.copy(
                            description = newDescription
                                .ifEmpty { getString(R.string.default_description) },
                            amount = newAmount
                        )
                        adapter.notifyItemChanged(index)
                        store.save(adapter.entries)
                        updateHeader()
                        return@setPositiveButton
                    }
                }
                updateHeader()
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    private fun deleteEntry(entry: Entry) {
        val index = adapter.entries.indexOfFirst { it.id == entry.id }
        if (index >= 0) {
            adapter.entries.removeAt(index)
            adapter.notifyItemRemoved(index)
            store.save(adapter.entries)
            updateHeader()
        }
    }

    private fun confirmClearAll() {
        if (adapter.entries.isEmpty()) return
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.action_clear)
            .setMessage(R.string.clear_confirm)
            .setPositiveButton(R.string.action_delete) { _, _ ->
                adapter.replaceAll(emptyList())
                store.save(adapter.entries)
                updateHeader()
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let { imm.hideSoftInputFromWindow(it.windowToken, 0) }
    }

    private fun formatMoney(value: Double): String =
        String.format(Locale.US, "%.2f", value)
}
