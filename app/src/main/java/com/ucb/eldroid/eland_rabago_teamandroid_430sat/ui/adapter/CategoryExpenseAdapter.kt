package com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.R
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.data.model.CategoryItem

class CategoryExpenseAdapter(
    private val categoryList: List<CategoryItem>,
    private val expenseTotals: Map<Int, Int>,
    private val onItemClick: (CategoryItem) -> Unit
) : RecyclerView.Adapter<CategoryExpenseAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryName: TextView = view.findViewById(R.id.categoryName)
        val categoryTotal: TextView = view.findViewById(R.id.totalAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_summary, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.categoryName.text = category.itemName

        val total = expenseTotals[category.itemID] ?: 0
        holder.categoryTotal.text = "₱%,d".format(total)

        holder.itemView.setOnClickListener {
            onItemClick(category)
        }
    }

    override fun getItemCount(): Int = categoryList.size
}