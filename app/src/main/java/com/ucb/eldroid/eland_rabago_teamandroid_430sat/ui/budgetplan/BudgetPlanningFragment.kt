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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.R
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.data.locals.AppDatabaseHelper
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.viewmodel.budgetplan.BudgetPlanningViewModel

class BudgetPlanningFragment : Fragment() {

    private val viewModel: BudgetPlanningViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_budget_planning, container, false)

    @SuppressLint("DefaultLocale", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = FirebaseAuth.getInstance().currentUser?.email
        viewModel.initializeUser(email)

        val spinner = view.findViewById<Spinner>(R.id.categorySpinner)
        val budgetAmountTextView = view.findViewById<TextView>(R.id.budgetLimitAmount)
        val expenseTextView = view.findViewById<TextView>(R.id.expenseTotalAmount)
        val totalBudgetTextView = view.findViewById<TextView>(R.id.userbudgettotaldisplay)
        val warningMessage = view.findViewById<TextView>(R.id.removableerrormessage)

        viewModel.totalBudget.observe(viewLifecycleOwner) {
            totalBudgetTextView.text = "₱${String.format("%,d", it)}"
        }

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            val categoryNames = categories.map { it["itemName"] ?: "Unnamed" }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categoryNames
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            if (categories.isNotEmpty()) {
                val firstCategoryId = categories.first()["itemID"]?.toIntOrNull()
                firstCategoryId?.let { viewModel.onCategorySelected(it) }
            }
        }

        viewModel.budgetLimit.observe(viewLifecycleOwner) {
            budgetAmountTextView.text = "₱${String.format("%,d", it)}"
        }

        viewModel.totalExpense.observe(viewLifecycleOwner) {
            expenseTextView.text = "₱${String.format("%,d", it)}"
        }

        viewModel.warningMessageVisibility.observe(viewLifecycleOwner) { isVisible ->
            warningMessage.visibility = if (isVisible) View.VISIBLE else View.GONE
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                val selectedId = viewModel.categories.value?.get(position)?.get("itemID")?.toIntOrNull()
                selectedId?.let { viewModel.onCategorySelected(it) }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        view.findViewById<LinearLayout>(R.id.manageBudgetPlanButton).setOnClickListener {
            showAddPlanDialog()
        }
    }

    private fun showAddPlanDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_budget_plan, null)
        val spinner = dialogView.findViewById<Spinner>(R.id.spinnerCategory)
        val etAmount = dialogView.findViewById<EditText>(R.id.etAmount)
        val btnAdd = dialogView.findViewById<ImageView>(R.id.btnAddPlan)

        val categories = viewModel.categories.value ?: return
        val categoryNames = categories.map { it["itemName"] ?: "Unnamed" }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val categoryId = categories[position]["itemID"]?.toIntOrNull()
                categoryId?.let { viewModel.onCategorySelected(it) }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnAdd.setOnClickListener {
            val budgetText = etAmount.text.toString().replace(",", "").replace("₱", "").trim()
            val newLimit = budgetText.toIntOrNull()

            if (newLimit == null || viewModel.selectedCategoryID.value == null) {
                Toast.makeText(context, getString(R.string.invalid_budget_input), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.updateBudget(newLimit)
            Toast.makeText(context, getString(R.string.budget_saved_success), Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}