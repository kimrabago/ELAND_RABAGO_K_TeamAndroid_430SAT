package com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.budgetplan

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.R
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.data.locals.AppDatabaseHelper

class BudgetPlanningFragment : Fragment() {

    private lateinit var dbHelper: AppDatabaseHelper
    private var selectedCategoryID: Int? = null
    private var userID: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_budget_planning, container, false)
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = AppDatabaseHelper(requireContext())

        val email = FirebaseAuth.getInstance().currentUser?.email
        val user = email?.let { dbHelper.getUserByEmail(it) }

        if (user != null) {
            userID = user.userID
        }

        val spinner = view.findViewById<Spinner>(R.id.categorySpinner)
        val budgetAmountTextView = view.findViewById<TextView>(R.id.budgetLimitAmount)
        val expenseTextView = view.findViewById<TextView>(R.id.expenseTotalAmount)
        val totalBudgetTextView = view.findViewById<TextView>(R.id.userbudgettotaldisplay)
        val totalBudget = dbHelper.getTotalBudgetForUser(userID!!)
        totalBudgetTextView.text = "Php ${String.format("%,d", totalBudget)}"

        val categories = dbHelper.getCategoriesByUserId(userID!!)
        val categoryNames = categories.map { it["itemName"] ?: "Unnamed" }
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long,
            ) {
                val categoryId = categories[position]["itemID"]?.toIntOrNull() ?: return
                selectedCategoryID = categoryId

                //Display budget
                val budgetLimit = dbHelper.getTotalBudgetForItem(userID!!, selectedCategoryID!!)
                budgetAmountTextView.text = "Php ${String.format("%,d", budgetLimit)}"

                //Display expense total
                val expenses = dbHelper.getExpensesByItemId(userID!!, categoryId)
                val totalExpense = expenses.sumOf { it["expenseAmount"]?.toIntOrNull() ?: 0 }
                expenseTextView.text = "Php ${String.format("%,d", totalExpense)}"

                val warningMessage =
                    requireView().findViewById<TextView>(R.id.removableerrormessage)
                if (totalExpense > budgetLimit) {
                    warningMessage.visibility = View.VISIBLE
                } else {
                    warningMessage.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val addBudgetBtn = view.findViewById<LinearLayout>(R.id.manageBudgetPlanButton)
        addBudgetBtn.setOnClickListener {
            showAddPlanDialog()
        }
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun showAddPlanDialog() {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_budget_plan, null)
        val spinner = dialogView.findViewById<Spinner>(R.id.spinnerCategory)
        val etAmount = dialogView.findViewById<EditText>(R.id.etAmount)
        val btnAdd = dialogView.findViewById<ImageView>(R.id.btnAddPlan)

        //Load categories for spinner
        val categories = dbHelper.getCategoriesByUserId(userID!!)
        val categoryNames = categories.map { it["itemName"] ?: "Unnamed" }

        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long,
            ) {
                selectedCategoryID = categories[position]["itemID"]?.toIntOrNull()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnAdd.setOnClickListener {
            val budgetText = etAmount.text.toString().replace(",", "").replace("₱", "").trim()
            val newLimit = budgetText.toIntOrNull()

            if (newLimit == null || selectedCategoryID == null) {
                Toast.makeText(
                    context,
                    getString(R.string.invalid_budget_input),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val existingBudgetId = dbHelper.getBudgetId(userID!!, selectedCategoryID!!)

            val success = if (existingBudgetId != null) {
                dbHelper.updateBudgetLimit(existingBudgetId, newLimit)
            } else {
                dbHelper.insertBudget(newLimit, userID!!, selectedCategoryID!!)
            }

            if (success) {
                val updatedLimit = dbHelper.getTotalBudgetForItem(userID!!, selectedCategoryID!!)
                view?.findViewById<TextView>(R.id.budgetLimitAmount)?.text =
                    "₱${String.format("%,d", updatedLimit)}"

                val updatedTotalBudget = dbHelper.getTotalBudgetForUser(userID!!)
                view?.findViewById<TextView>(R.id.userbudgettotaldisplay)?.text =
                    "₱${String.format("%,d", updatedTotalBudget)}"

                Toast.makeText(context,  getString(R.string.budget_saved_success), Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            } else {
                Toast.makeText(context,  getString(R.string.budget_saved_failed), Toast.LENGTH_SHORT).show()
            }
        }

        alertDialog.show()
    }
}