package com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.R
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.data.model.CategoryItem

class CategoryAdapter(private val categories: List<CategoryItem>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemNameTextView: TextView = view.findViewById(R.id.itemNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.itemNameTextView.text = categories[position].itemName
    }

    override fun getItemCount(): Int = categories.size
}