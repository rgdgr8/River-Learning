package com.rgdgr8.riverlearning;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.logout:
                    LoginActivity.dataFetcher.destroyToken().enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Log.i(TAG, "onLogoutResponse: " + response.message());
                            if (!response.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            } else {
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                        .edit().putString(LoginActivity.TAG, null).apply();

                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e(TAG, "onFailure: ", t.getCause());
                        }
                    });
                    return true;
                default:
                    return false;
            }
        });
        Button feedback = findViewById(R.id.feedback);
        feedback.setOnClickListener(v -> Toast.makeText(MainActivity.this, "feedback", Toast.LENGTH_SHORT).show());

        navView = findViewById(R.id.nav_view);
        navView.setItemIconTintList(null);
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
            if (!feedback.isEnabled()) {//condition is subject to modification
                feedback.setVisibility(View.VISIBLE);
                feedback.setEnabled(true);
            }
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                switch (destination.getId()) {
                    case R.id.myTasksFragment:
                        navView.getMenu().clear();
                        navView.inflateMenu(R.menu.tasks_drawer_menu);
                        break;
                    case R.id.myEvaluationFragment:
                        navView.getMenu().clear();
                        navView.inflateMenu(R.menu.assessments_drawer_menu);
                        break;
                    case R.id.myTrainingsFragment:
                        break;
                    case R.id.editAllocatedTaskFragment:
                    case R.id.commentTaskFragment:
                    case R.id.editMyTaskFragment:
                    default:
                        feedback.setVisibility(View.INVISIBLE);
                        feedback.setEnabled(false);
                }
            }
        });
        NavigationUI.setupWithNavController(toolbar, navController, barConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        NavigationUI.setupWithNavController(botNavView, navController);
    }
}