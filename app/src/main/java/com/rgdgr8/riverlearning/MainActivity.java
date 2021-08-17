package com.rgdgr8.riverlearning;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private NavigationView navView;
    private DrawerLayout drawerLayout;

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        navView = findViewById(R.id.nav_view);
        BottomNavigationView botNavView = findViewById(R.id.bot_nav_view);

        AppBarConfiguration barConfiguration = new AppBarConfiguration
                .Builder(R.id.myTasksFragment,
                R.id.tasksAllocatedFragment,
                R.id.closedTasksFragment,
                R.id.assessTasksFragment,

                R.id.myEvaluationFragment,
                R.id.reportKpiFragment,
                R.id.quickFeedbackFragment,

                R.id.myTrainingsFragment,

                R.id.missionFragment)
                .setOpenableLayout(drawerLayout)
                .build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.my_nav_host_container);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                navView.getMenu().clear();
                switch (destination.getId()) {
                    case R.id.myTasksFragment:
                        Log.d(TAG, "onOptionsItemSelected: Tasks");
                        navView.inflateMenu(R.menu.tasks_drawer_menu);
                        break;
                    case R.id.myEvaluationFragment:
                        Log.d(TAG, "onOptionsItemSelected: Assessments");
                        navView.inflateMenu(R.menu.assessments_drawer_menu);
                        break;
                    case R.id.myTrainingsFragment: break;
                }
            }
        });
        NavigationUI.setupWithNavController(toolbar, navController, barConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        NavigationUI.setupWithNavController(botNavView, navController);
    }
}