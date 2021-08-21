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
import retrofit2.http.GET;

public interface TaskFetcher {
    public static final String TAG = "TaskFetcher";
    public static String BASE_URL = "https://gamma.riverlearning.in/api/";

    @GET("tasks")
    Call<List<OpenTask>> getTasks();
}
