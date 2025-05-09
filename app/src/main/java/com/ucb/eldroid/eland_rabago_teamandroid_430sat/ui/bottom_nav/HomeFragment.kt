package com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.bottom_nav

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.R
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.data.locals.AppDatabaseHelper
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.data.model.CategoryItem
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.adapter.CategoryExpenseAdapter
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.adapter.ExpenseAdapter
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.budgetplan.BudgetPlanningFragment
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.exp_and_cat.CategoryFragment
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.exp_and_cat.ExpenseFragment
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.viewmodel.bottom_nav.HomeViewModel

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val optBudget = view.findViewById<LinearLayout>(R.id.opt_budget)
        val optExpense = view.findViewById<LinearLayout>(R.id.opt_expense)
        val optCategory = view.findViewById<LinearLayout>(R.id.opt_category)
        val recyclerView = view.findViewById<RecyclerView>(R.id.ReportGenRecyclerView)
        val expenseTotalTextView = view.findViewById<TextView>(R.id.expense_total)

        optBudget.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.layout_content, BudgetPlanningFragment())
                .addToBackStack(null).commit()
        }

        optExpense.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.layout_content, ExpenseFragment())
                .addToBackStack(null).commit()
        }

        optCategory.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.layout_content, CategoryFragment())
                .addToBackStack(null).commit()
        }

        viewModel.loadUserData()

        viewModel.categories.observe(viewLifecycleOwner, Observer { categories ->
            viewModel.expenseMap.observe(viewLifecycleOwner, Observer { expenseMap ->
                val adapter = CategoryExpenseAdapter(categories, expenseMap) { category ->
                    val expenses = viewModel.getExpensesByCategory(category.itemID)
                    showExpenseDialog(category.itemName, expenses)
                }
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
            })
        })

        viewModel.totalExpenses.observe(viewLifecycleOwner) { total ->
            expenseTotalTextView.text = "₱%,d".format(total)
        }
    }

    private fun showExpenseDialog(categoryName: String, expenseList: List<Map<String, String>>) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_expense, null)
        val titleTextView = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.rvExpenses)

        val userId = viewModel.getUserId() ?: return
        val itemId = viewModel.getCategoryIdByName(categoryName)

        val expenses = expenseList.toMutableList()
        titleTextView.text = categoryName
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lateinit var adapter: ExpenseAdapter
        adapter = ExpenseAdapter(
            expenses,
            onDeleteClick = { position ->
                val expense = expenses[position]
                val expenseName = expense["expenseName"] ?: ""
                val expenseAmount = expense["expenseAmount"] ?: "0"
                val success = viewModel.deleteExpense(expenseName, expenseAmount, itemId)
                if (success) {
                    expenses.removeAt(position)
                    adapter.notifyItemRemoved(position)
                }
            }
        )

        recyclerView.adapter = adapter

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .show()
    }
}