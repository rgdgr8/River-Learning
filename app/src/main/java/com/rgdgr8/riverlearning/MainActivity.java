package com.rgdgr8.riverlearning;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    public static final String SP_USER_FNAME = "user_fname";
    private static final String TAG = "MainActivity";
    public static List<String> spinnerEmployeeList = new ArrayList<>();
    public static List<Integer> employeeIdList = new ArrayList<>();
    static WeakReference<Context> ctx = null;
    private NavigationView navView;
    private NavController navController;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    static boolean isNetworkAvailableAndConnected() {
        ConnectivityManager manager = (ConnectivityManager) ctx.get().getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = (manager.getActiveNetworkInfo() != null);

        return (isNetworkAvailable && manager.getActiveNetworkInfo().isConnected());
    }

    static void checkNetworkAndShowDialog(Context context) {
        if (!isNetworkAvailableAndConnected()) {
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setTitle("No internet connection")
                    .setPositiveButton("OK", null)
                    .show();

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.black));
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    public void setDrawerEnabled(boolean enabled) {
        if (enabled) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toolbar.setNavigationIcon(null);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctx = new WeakReference<>(getApplicationContext());

        LoginActivity.dataFetcher.getEmployeeList().enqueue(new Callback<List<Employee>>() {
            @Override
            public void onResponse(Call<List<Employee>> call, Response<List<Employee>> response) {
                Log.d(TAG, "onResponseEmpListFetcher: " + response.message());
                try {
                    if (response.isSuccessful()) {
                        List<Employee> list = response.body();
                        if (list != null) {
                            Collections.sort(list);

                            //employeeIdList = new ArrayList<>(list.size());

                            spinnerEmployeeList = new ArrayList<>(list.size() + 1);
                            spinnerEmployeeList.add(getResources().getString(R.string.blank_spinner));

                            String loginEmail = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString(LoginActivity.SP_EMAIL, "");
                            String fname = "";
                            for (Employee e : list) {
                                Log.d(TAG, "onResponse: " + e.toString());
                                if (e.getUser().getEmail().equals(loginEmail)) {
                                    fname = e.getUser().getFname();
                                }
                                spinnerEmployeeList.add(e.getUser().getFname() + " " + e.getUser().getLname());
                                employeeIdList.add(e.getUser().getId());
                            }
                            PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit()
                                    .putString(SP_USER_FNAME, fname).apply();
                        } else {
                            Toast.makeText(MainActivity.this, "Empty employee list", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Couldn't fetch employee list", Toast.LENGTH_SHORT).show();
                    }
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<Employee>> call, Throwable t) {
                MainActivity.checkNetworkAndShowDialog(MainActivity.this);
                Log.e(TAG, "onEmpListFetchFailure: ", t.getCause());
            }
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            switch (item.getItemId()) {
                case R.id.logout:
                    LoginActivity.dataFetcher.destroyToken().enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Log.d(TAG, "onLogoutResponse: " + response.message());

                            try {
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                preferences.edit().putString(LoginActivity.TAG, null).apply();
                                preferences.edit().putString(LoginActivity.SP_TENANT, null).apply();
                                preferences.edit().putString(SP_USER_FNAME, null).apply();

                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e(TAG, "onFailure: ", t.getCause());
                            try {
                                Toast.makeText(MainActivity.this, "Problem Occurred", Toast.LENGTH_SHORT).show();
                                checkNetworkAndShowDialog(MainActivity.this);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    return true;
                default:
                    return false;
            }
        });
        Button feedback = findViewById(R.id.feedback);

        navView = findViewById(R.id.nav_view);
        navView.setItemIconTintList(null);
        BottomNavigationView botNavView = findViewById(R.id.bot_nav_view);
        botNavView.setItemIconTintList(null);

        AppBarConfiguration barConfiguration = new AppBarConfiguration
                .Builder(R.id.myTasksFragment,
                R.id.tasksAllocatedFragment,
                R.id.closedTasksFragment,
                R.id.assessTasksFragment,

                R.id.myEvaluationFragment,
                //R.id.ownerEvaluation
                R.id.reportKpiFragment,
                R.id.iFeelFragment,

                R.id.myTrainingsFragment,

                R.id.profileFragment)
                .setOpenableLayout(drawerLayout)
                .build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.my_nav_host_container);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
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
                        navView.getMenu().clear();
                        navView.inflateMenu(R.menu.training_menu);
                        break;

                    case R.id.tasksAllocatedFragment:
                    case R.id.closedTasksFragment:
                    case R.id.assessTasksFragment:
                    case R.id.reportKpiFragment:
                    case R.id.iFeelFragment:
                        break;

                    //case R.id.profileFragment:
                    //navView.getMenu().clear();
                    default:
                        feedback.setVisibility(View.INVISIBLE);
                        feedback.setEnabled(false);
                }
            }
        });
        NavigationUI.setupWithNavController(toolbar, navController, barConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        NavigationUI.setupWithNavController(botNavView, navController);

        feedback.setOnClickListener(v -> {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .edit().putInt(TAG, navController.getCurrentDestination().getId()).apply();
            startActivityForResult(new Intent(this, QuickFeedBackActivity.class), 1);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            int lastFragment = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(TAG, R.id.myTasksFragment);
            navController.navigate(lastFragment);
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    static class Employee implements Comparable<Employee> {
        private final User user;

        public Employee(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }

        @Override
        public int compareTo(Employee o) {
            return this.user.id - o.user.id;
        }

        @Override
        public @NotNull String toString() {
            return user.toString();
        }

        private static class User {
            private final int id;
            @SerializedName("first_name")
            private final String fname;
            @SerializedName("last_name")
            private final String lname;
            private final String email;

            public User(int id, String fname, String lname, String email) {
                this.id = id;
                this.fname = fname;
                this.lname = lname;
                this.email = email;
            }

            public String getEmail() {
                return email;
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
            public @NotNull String toString() {
                return "User{" +
                        "id=" + id +
                        ", fname='" + fname + '\'' +
                        ", lname='" + lname + '\'' +
                        ", email='" + email + '\'' +
                        '}';
            }
        }
    }
}