package com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.exp_and_cat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.R
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.data.locals.AppDatabaseHelper
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.data.model.CategoryItem
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.adapter.CategoryAdapter
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.viewmodel.exp_and_cat.CategoryViewModel

class CategoryFragment : Fragment() {

    private lateinit var viewModel: CategoryViewModel
    private lateinit var adapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_category, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dbHelper = AppDatabaseHelper(requireContext())

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CategoryViewModel(dbHelper) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[CategoryViewModel::class.java]

        val recyclerView = view.findViewById<RecyclerView>(R.id.CategoryRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = CategoryAdapter(emptyList())
        recyclerView.adapter = adapter

        val nameEditText = view.findViewById<EditText>(R.id.CategoryNameEditText)
        val descEditText = view.findViewById<EditText>(R.id.CategoryDescEditText)
        val submitBtn = view.findViewById<Button>(R.id.CategorySubmitBtn)

        val email = FirebaseAuth.getInstance().currentUser?.email
        if (email != null) {
            viewModel.loadUser(email)
        }

        viewModel.categories.observe(viewLifecycleOwner) {
            adapter.updateList(it)
        }

        viewModel.insertSuccess.observe(viewLifecycleOwner) {
            Toast.makeText(
                requireContext(),
                if (it) getString(R.string.category_saved) else getString(R.string.category_save_failed),
                Toast.LENGTH_SHORT
            ).show()

            if (it) {
                nameEditText.text.clear()
                descEditText.text.clear()
            }
        }

        submitBtn.setOnClickListener {
            val name = nameEditText.text.toString()
            val desc = descEditText.text.toString()
            viewModel.addCategory(name, desc)
        }
    }
}