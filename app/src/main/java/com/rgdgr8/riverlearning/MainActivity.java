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
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private NavigationView navView;
    private DrawerLayout drawerLayout;
    public static List<String> employeeList = null;

    static class Employee implements Comparable<Employee> {
        private static class User {
            private int id;
            @SerializedName("first_name")
            private String fname;
            @SerializedName("last_name")
            private String lname;

            public User(int id, String fname, String lname) {
                this.id = id;
                this.fname = fname;
                this.lname = lname;
            }

            public int getId() {
                return id;
            }

            public String getFname() {
                return fname;
            }

            public String getLname() {
                return lname;
            }

            @Override
            public String toString() {
                return "User{" +
                        "id=" + id +
                        ", fname='" + fname + '\'' +
                        ", lname='" + lname + '\'' +
                        '}';
            }
        }

        private User user;

        public User getUser() {
            return user;
        }

        public Employee(User user) {
            this.user = user;
        }

        @Override
        public int compareTo(Employee o) {
            return this.user.id - o.user.id;
        }

        @Override
        public @NotNull String toString() {
            return user.toString();
        }
    }

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

        LoginActivity.dataFetcher.getEmployeeList().enqueue(new Callback<List<Employee>>() {
            @Override
            public void onResponse(Call<List<Employee>> call, Response<List<Employee>> response) {
                Log.d(TAG, "onResponseEmpListFetcher: " + response.message());
                if (response.isSuccessful()) {
                    List<Employee> list = response.body();
                    if (list != null) {
                        Collections.sort(list);
                        employeeList = new ArrayList<>(list.size() + 1);
                        employeeList.add(getResources().getString(R.string.blank_spinner));
                        for (Employee e : list) {
                            employeeList.add(e.getUser().getFname()+" "+e.getUser().getLname());
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Empty employee list", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Couldn't fetch employee list", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Employee>> call, Throwable t) {
                Log.e(TAG, "onEmpListFetchFailure: ", t.getCause());
            }
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.logout:
                    LoginActivity.dataFetcher.destroyToken().enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Log.d(TAG, "onLogoutResponse: " + response.message());
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
            //Toast.makeText(this, "dest changed", Toast.LENGTH_SHORT).show();
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

                    case R.id.tasksAllocatedFragment:
                    case R.id.closedTasksFragment:
                    case R.id.assessTasksFragment:
                        break;

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