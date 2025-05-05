package com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.bottom_nav

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.R
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.data.locals.AppDatabaseHelper
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.data.model.CategoryItem
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.adapter.CategoryExpenseAdapter
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.adapter.ExpenseAdapter
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.budgetplan.BudgetPlanningFragment

class HomeFragment : Fragment() {

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

        optBudget.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.layout_content, BudgetPlanningFragment())
                .addToBackStack(null)
                .commit()
        }

        optExpense.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.layout_content, ExpenseFragment())
                .addToBackStack(null)
                .commit()
        }

        optCategory.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.layout_content, CategoryFragment())
                .addToBackStack(null)
                .commit()
        }

        val dbHelper = AppDatabaseHelper(requireContext())
        val user = FirebaseAuth.getInstance().currentUser
        val userData = user?.email?.let { dbHelper.getUserByEmail(it) }

        if (userData != null) {
            val categories = dbHelper.getCategoriesByUserId(userData.userID).map {
                CategoryItem(
                    itemID = it["itemID"]?.toIntOrNull() ?: 0,
                    itemName = it["itemName"] ?: "",
                    itemDescription = it["itemDescription"] ?: "",
                )
            }
            val expenseMap = dbHelper.getTotalExpensesPerItem(userData.userID)

            // Calculate the total expense summary
            val totalExpenses = expenseMap.values.sum()
            val expenseTotalTextView = view.findViewById<TextView>(R.id.expense_total)
            expenseTotalTextView.text = "₱%,d".format(totalExpenses)

            val adapter = CategoryExpenseAdapter(categories, expenseMap) { category ->
                val expenses = dbHelper.getExpensesByItemId(userData.userID, category.itemID)
                showExpenseDialog(category.itemName, expenses)
            }
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun showExpenseDialog(categoryName: String, expenseList: List<Map<String, String>>) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_expense, null)
        val titleTextView = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.rvExpenses)

        val dbHelper = AppDatabaseHelper(requireContext())
        val userId = FirebaseAuth.getInstance().currentUser?.let {
            dbHelper.getUserByEmail(it.email ?: "")?.userID
        } ?: return

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
                val itemId = dbHelper.getCategoryIdByName(userId, categoryName)

                val success = dbHelper.deleteExpense(expenseName, expenseAmount, userId, itemId)
                if (success) {
                    expenses.removeAt(position)
                    adapter.notifyItemRemoved(position)  // ✅ Now works
                }
            }
        )

        recyclerView.adapter = adapter

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .show()
    }
}