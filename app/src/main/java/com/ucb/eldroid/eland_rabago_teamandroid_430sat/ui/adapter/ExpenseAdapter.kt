package com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.R

class ExpenseAdapter(
    private val expenses: List<Map<String, String>>,
    private val onEditClick: ((position: Int) -> Unit)? = null,
    private val onDeleteClick: ((position: Int) -> Unit)? = null
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(R.id.tvExpenseName)
        val cost = view.findViewById<TextView>(R.id.tvExpenseCost)
        val editBtn = view.findViewById<ImageButton>(R.id.btnEdit)
        val deleteBtn = view.findViewById<ImageButton>(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val item = expenses[position]
        holder.name.text = item["expenseName"]
        holder.cost.text = "₱" + (item["expenseAmount"] ?: "0")

        holder.editBtn.setOnClickListener { onEditClick?.invoke(position) }
        holder.deleteBtn.setOnClickListener { onDeleteClick?.invoke(position) }
    }

    override fun getItemCount() = expenses.size
}