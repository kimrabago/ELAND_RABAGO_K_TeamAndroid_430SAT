package com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.exp_and_cat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.R
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.data.locals.AppDatabaseHelper
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.viewmodel.exp_and_cat.ExpenseViewModel

class ExpenseFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_expense, container, false)
    }
        private lateinit var viewModel: ExpenseViewModel
        private lateinit var dbHelper: AppDatabaseHelper

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            dbHelper = AppDatabaseHelper(requireContext())

            val factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ExpenseViewModel(dbHelper) as T
                }
            }
            viewModel = ViewModelProvider(this, factory)[ExpenseViewModel::class.java]

            val email = FirebaseAuth.getInstance().currentUser?.email ?: return
            viewModel.loadCategories(email)

            val categorySpinner = view.findViewById<Spinner>(R.id.expensecategoryspinner)
            val addCatText = view.findViewById<TextView>(R.id.AddInitialCat)

            viewModel.categoryList.observe(viewLifecycleOwner) { categories ->
                if (categories.isNotEmpty()) {
                    categorySpinner.visibility = View.VISIBLE
                    addCatText.visibility = View.GONE

                    val categoryNames = categories.map { it["itemName"] ?: "Unnamed" }
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        categoryNames
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    categorySpinner.adapter = adapter

                    categorySpinner.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                viewModel.selectedCategoryId =
                                    categories[position]["itemID"]?.toIntOrNull()
                            }

                            override fun onNothingSelected(parent: AdapterView<*>) {}
                        }
                } else {
                    categorySpinner.visibility = View.GONE
                    addCatText.visibility = View.VISIBLE
                }
            }

            view.findViewById<Button>(R.id.expensesubmitbutton).setOnClickListener {
                val name = view.findViewById<EditText>(R.id.expensenameedittext).text.toString()
                val amount = view.findViewById<EditText>(R.id.expenseamountedittext).text.toString()
                val desc = view.findViewById<EditText>(R.id.expensedescedittext).text.toString()

                viewModel.submitExpense(name, amount, desc)
            }

            viewModel.expenseSaved.observe(viewLifecycleOwner) { saved ->
                Toast.makeText(
                    context,
                    if (saved) getString(R.string.expense_saved)
                    else getString(R.string.error_saving_expense),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
