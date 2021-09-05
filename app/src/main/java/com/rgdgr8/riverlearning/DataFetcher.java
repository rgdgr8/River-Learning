package com.rgdgr8.riverlearning;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DataFetcher {
    String TAG = "DataFetcher";
    String BASE_URL = "https://gamma.riverlearning.in/api/";

    @GET("employee-list")
    Call<List<MainActivity.Employee>> getEmployeeList();

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

    @GET("task-delete/{id}")
    Call<Void> deleteTask(@Path("id") int id);

    @PATCH("task-update-view/{id}")
    Call<Void> updateAllocatedTask(@Path("id") int id, @Body NewTask newTask);

    @PATCH("task-update-view-mytasks/{id}")
    Call<Void> updateMyTask(@Path("id") int id, @Body EditMyTaskFragment.UpdatedTask body);
}
