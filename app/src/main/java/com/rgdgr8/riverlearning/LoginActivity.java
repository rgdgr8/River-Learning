package com.rgdgr8.riverlearning;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
    public static User user;
    public static String token;
    public static DataFetcher dataFetcher;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setStatusBarColor(getResources().getColor(R.color.theme_blue));

        EditText email = findViewById(R.id.login_email);
        EditText pass = findViewById(R.id.login_pass);
        Button login = findViewById(R.id.login);
        login.setOnClickListener(v -> {
            user = new User(email.getText().toString(), pass.getText().toString());
            fetchToken();
        });

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request originalRequest = chain.request();
                    if(originalRequest.url().toString().equals(DataFetcher.BASE_URL+"token/login")){
                        return chain.proceed(originalRequest);
                    }

                    String t = token;
                    if (t == null) t = "";

                    Request newRequest = ((Request) originalRequest).newBuilder()
                            .header("Authorization", "Token " + t)
                            .build();
                    return chain.proceed(newRequest);
                })
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DataFetcher.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        dataFetcher = retrofit.create(DataFetcher.class);

        intent = new Intent(this, MainActivity.class);

        token = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString(TAG, null);

        if (token != null) {
            startActivity(intent);
        }
    }

    public void fetchToken() {
        dataFetcher.getToken(user).enqueue(new Callback<LoginToken>() {
            @Override
            public void onResponse(Call<LoginToken> call, Response<LoginToken> response) {
                Log.i(TAG, "onResponseTokenFetcher: " + response.message());
                if (response.isSuccessful()) {
                    LoginToken t = response.body();//keep static value in login activity
                    if (t != null) {
                        token = t.getAuth_token();
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                .edit().putString(TAG, token).apply();
                        startActivity(intent);
                    } else
                        Toast.makeText(LoginActivity.this, "Invalid Token", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Could not fetch token", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginToken> call, Throwable t) {
                Log.e(TAG, "onTokenFetchFailure: ", t.getCause());
            }
        });
    }
}