package com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.bottom_nav

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.R
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.auth.LogoutFragment
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.exp_and_cat.ExpenseFragment
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.profile.ProfileFragment
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.profile.SettingsFragment

class AppSummaryActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_summary)

        bottomNavigationView = findViewById(R.id.bottom_nav)

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.home -> HomeFragment()
                R.id.expense -> ExpenseFragment()
                R.id.profile -> ProfileFragment()
                R.id.settings -> SettingsFragment()
                R.id.logout -> LogoutFragment()
                else -> return@setOnItemSelectedListener false
            }

            replaceFragment(fragment)
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.layout_content, fragment)
            .commit()
    }
}
