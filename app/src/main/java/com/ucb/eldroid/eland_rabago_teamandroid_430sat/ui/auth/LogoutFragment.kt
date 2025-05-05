package com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.auth

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.R
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.viewmodel.auth.LogoutViewModel

class LogoutFragment : Fragment() {

    private val viewModel: LogoutViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        observeViewModel()
        showLogoutConfirmationDialog()
        return inflater.inflate(R.layout.fragment_logout, container, false)
    }

    private fun observeViewModel() {
        viewModel.logoutEvent.observe(viewLifecycleOwner, Observer { shouldLogout ->
            if (shouldLogout) {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        })
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.logout_title))
            .setMessage(getString(R.string.logout_message))
            .setPositiveButton(getString(R.string.logout_positive_button)) { _, _ ->
                viewModel.confirmLogout()
            }
            .setNegativeButton(getString(R.string.logout_negative_button)) { _, _ ->
                requireActivity().supportFragmentManager.popBackStack()
            }
            .setCancelable(false)
            .show()
    }
}