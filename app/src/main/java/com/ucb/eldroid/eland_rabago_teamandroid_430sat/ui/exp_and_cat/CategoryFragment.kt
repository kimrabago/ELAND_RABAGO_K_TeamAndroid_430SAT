package com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.exp_and_cat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.R
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.data.locals.AppDatabaseHelper
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.data.model.CategoryItem
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.adapter.CategoryAdapter

class CategoryFragment : Fragment() {

    private lateinit var dbHelper: AppDatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_category, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dbHelper = AppDatabaseHelper(requireContext())

        val recyclerView = view.findViewById<RecyclerView>(R.id.CategoryRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val nameEditText = view.findViewById<EditText>(R.id.CategoryNameEditText)
        val descEditText = view.findViewById<EditText>(R.id.CategoryDescEditText)
        val submitBtn = view.findViewById<Button>(R.id.CategorySubmitBtn)

        loadCategories()

        submitBtn.setOnClickListener {
            val itemName = nameEditText.text.toString().trim()
            val itemDesc = descEditText.text.toString().trim()
            val email = FirebaseAuth.getInstance().currentUser?.email
            val localUser = dbHelper.getUserByEmail(email ?: "")
            val userId = localUser?.userID

            if (itemName.isEmpty() || itemDesc.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.fill_both_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userId == null) {
                Toast.makeText(requireContext(), getString(R.string.user_not_logged_in), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val success = dbHelper.insertCategory(itemName, itemDesc, userId)
            if (success) {
                Toast.makeText(requireContext(), getString(R.string.category_saved), Toast.LENGTH_SHORT).show()
                nameEditText.text.clear()
                descEditText.text.clear()
                loadCategories()
            } else {
                Toast.makeText(requireContext(), getString(R.string.category_save_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadCategories() {
        val email = FirebaseAuth.getInstance().currentUser?.email ?: return
        val localUser = dbHelper.getUserByEmail(email) ?: return
        val userId = localUser.userID
        val categoryList = dbHelper.getCategoriesByUserId(userId)
            .map {
                CategoryItem(
                    itemID = it["itemID"]?.toIntOrNull() ?: 0,
                    itemName = it["itemName"] ?: "",
                    itemDescription = it["itemDescription"] ?: ""
                )
            }

        val adapter = CategoryAdapter(categoryList)
        view?.findViewById<RecyclerView>(R.id.CategoryRecyclerView)?.adapter = adapter
    }
}