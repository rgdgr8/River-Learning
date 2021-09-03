package com.rgdgr8.riverlearning;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface DataFetcher {
    String TAG = "DataFetcher";
    String BASE_URL = "https://gamma.riverlearning.in/api/";

    @GET("tasks")
    Call<List<OpenTask>> getTasks();

    @GET("tasks-allocated")
    Call<List<OpenTask>> getAllocatedTasks();

    @POST("token/login")
    Call<LoginToken> getToken(@Body User user);

    @POST("token/logout")
    Call<Void> destroyToken();

    @POST("tasks-create/")
    Call<Void> createNewTask(@Body NewTask task);

    @GET("tasks-closed")
    Call<List<ClosedTasksFragment.ClosedTask>> getClosedTasks();
}
