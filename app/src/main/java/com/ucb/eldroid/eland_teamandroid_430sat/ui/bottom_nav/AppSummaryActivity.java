package com.ucb.eldroid.eland_teamandroid_430sat.ui.bottom_nav;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ucb.eldroid.eland_teamandroid_430sat.R;
import com.ucb.eldroid.eland_teamandroid_430sat.ui.exp_and_cat.CategoryFragment;
import com.ucb.eldroid.eland_teamandroid_430sat.ui.exp_and_cat.ExpenseFragment;
import com.ucb.eldroid.eland_teamandroid_430sat.ui.profile.ProfileFragment;
import com.ucb.eldroid.eland_teamandroid_430sat.ui.profile.SettingsFragment;
import com.ucb.eldroid.eland_teamandroid_430sat.ui.bottom_nav.HomeFragment;

public class AppSummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_summary);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        // Load the default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_content, new HomeFragment())
                    .commit();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.expense) {
                selectedFragment = new ExpenseFragment();
            } else if (itemId == R.id.profile) {
                selectedFragment = new ProfileFragment();
            } else if (itemId == R.id.settings) {
                selectedFragment = new SettingsFragment();
            } else if (itemId == R.id.logout) {
                selectedFragment = new CategoryFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_content, selectedFragment)
                        .commit();
            }
            return true;
        });
    }
}
