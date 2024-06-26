package com.rgdgr8.riverlearning;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";
    public static final String SP_TENANT = "login_tenant";
    public static final String SP_EMAIL = "login_email";
    public static DataFetcher dataFetcher;
    public String BASE_URL = "";
    private User user;
    private String token;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setStatusBarColor(getResources().getColor(R.color.theme_blue));

        EditText email = findViewById(R.id.login_email);
        EditText pass = findViewById(R.id.login_pass);
        EditText tenant = findViewById(R.id.tenant);

        Button login = findViewById(R.id.login);
        login.setOnClickListener(v -> {
            login.setEnabled(false);
            user = new User(email.getText().toString(), pass.getText().toString());
            String t = tenant.getText().toString().toLowerCase();
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(SP_TENANT, t).apply();
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(SP_EMAIL, user.getEmail()).apply();
            fetchToken(t);
            login.setEnabled(true);
        });

        intent = new Intent(this, MainActivity.class);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        token = preferences.getString(TAG, null);
        String t = preferences.getString(SP_TENANT, null);
        if (token != null && t != null) {
            initializeDataFetcher(t);
            startActivity(intent);
            finish();
        }
    }

    private void initializeDataFetcher(String tenant) {
        BASE_URL = "https://" + tenant + ".riverlearning.in/api/";

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request originalRequest = chain.request();
                    if (originalRequest.url().toString().equals(BASE_URL + "token/login")) {
                        return chain.proceed(originalRequest);
                    }

                    String t = token;
                    if (t == null) t = "";

                    Request newRequest = originalRequest.newBuilder()
                            .header("Authorization", "Token " + t)
                            .build();
                    return chain.proceed(newRequest);
                })
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().serializeNulls().create()))
                .client(okHttpClient)
                .build();

        dataFetcher = retrofit.create(DataFetcher.class);
    }

    private void fetchToken(String tenant) {
        initializeDataFetcher(tenant);

        dataFetcher.getToken(user).enqueue(new Callback<LoginToken>() {
            @Override
            public void onResponse(Call<LoginToken> call, Response<LoginToken> response) {
                Log.i(TAG, "onResponseTokenFetcher: " + response.message());
                try {
                    if (response.isSuccessful()) {
                        LoginToken t = response.body();//keep static value in login activity
                        if (t != null) {
                            token = t.getAuth_token();
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                    .edit().putString(TAG, token).apply();
                            startActivity(intent);
                            finish();
                        } else
                            Toast.makeText(LoginActivity.this, "Problem Occurred", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LoginToken> call, Throwable t) {
                Log.e(TAG, "onTokenFetchFailure: ", t.getCause());
                try {
                    Toast.makeText(LoginActivity.this, "Problem Occurred", Toast.LENGTH_SHORT).show();
                    if (!MainActivity.isNetworkAvailableAndConnected())
                        Toast.makeText(LoginActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}