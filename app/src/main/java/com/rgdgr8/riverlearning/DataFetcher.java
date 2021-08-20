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

public interface DataFetcher {
    public static final String TAG = "DataFetcher";
    public static String urlString = "https://gamma.riverlearning.in/api/tasks/";


}
