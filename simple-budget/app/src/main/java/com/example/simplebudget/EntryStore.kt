package com.example.simplebudget

import android.content.Context
import org.json.JSONArray

class EntryStore(context: Context) {

    private val prefs = context.getSharedPreferences("simple_budget", Context.MODE_PRIVATE)

    fun load(): MutableList<Entry> {
        val json = prefs.getString(KEY, null) ?: return mutableListOf()
        val result = mutableListOf<Entry>()
        runCatching {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                result.add(
                    Entry(
                        id = o.getLong("id"),
                        description = o.getString("description"),
                        amount = o.getDouble("amount"),
                        isExpense = o.getBoolean("isExpense"),
                        createdAt = o.getLong("createdAt")
                    )
                )
            }
        }
        return result
    }

    fun save(entries: List<Entry>) {
        val array = JSONArray()
        for (e in entries) {
            array.put(
                org.json.JSONObject()
                    .put("id", e.id)
                    .put("description", e.description)
                    .put("amount", e.amount)
                    .put("isExpense", e.isExpense)
                    .put("createdAt", e.createdAt)
            )
        }
        prefs.edit().putString(KEY, array.toString()).apply()
    }

    companion object {
        private const val KEY = "entries"
    }
}
