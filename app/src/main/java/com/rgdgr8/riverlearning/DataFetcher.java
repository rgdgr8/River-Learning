package com.rgdgr8.riverlearning;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DataFetcher {
    String TAG = "DataFetcher";
    String SEARCH = "search";

    @GET("employee-list")
    Call<List<MainActivity.Employee>> getEmployeeList();

    @GET("tasks")
    Call<List<OpenTask>> getTasks(@Query(SEARCH) String params);

    @GET("tasks-allocated")
    Call<List<OpenTask>> getAllocatedTasks(@Query(SEARCH) String params);

    @POST("add-comment/{id}")
    Call<Void> submitComment(@Path("id") int id, @Body CommentTaskFragment.Comment comment);

    @POST("token/login")
    Call<LoginToken> getToken(@Body User user);

    @POST("token/logout")
    Call<Void> destroyToken();

    @POST("tasks-create/")
    Call<Void> createNewTask(@Body NewTask task);

    @GET("quick-feedback")
    Call<List<iFeelFragment.iFeel>> getiFeels(@Query(SEARCH) String params);

    @GET("tasks-closed")
    Call<List<ClosedTasksFragment.ClosedTask>> getClosedTasks();

    @DELETE("task-delete/{id}")
    Call<Void> deleteTask(@Path("id") int id);

    @PATCH("task-update-view/{id}")
    Call<Void> updateAllocatedTask(@Path("id") int id, @Body NewTask newTask);

    @PATCH("task-update-view-mytasks/{id}")
    Call<Void> updateMyTask(@Path("id") int id, @Body EditMyTaskFragment.UpdatedTask body);

    @GET("assess-closed-task-view")
    Call<List<AssessTasksFragment.AssessTask>> getAssessTasks(@Query(SEARCH) String query);

    @PUT("assess-task/{id}")
    Call<Void> submitTaskAssessment(@Path("id") int id, @Body AssessmentOfTaskFragment.AssessedTask assessedTask);

    @GET("task-detail/{id}")
    Call<TaskDetailsFragment.TaskDetails> getTaskDetails(@Path("id") int id);

    @GET("training-list")
    Call<MyTrainingsFragment.Trainings> getTrainings();

    @POST("training-feedback/{id}")
    Call<Void> submitTrainingFeedback(@Path("id") int id, @Body TrainingFeedbackFragment.TrainingFeedback trainingFeedback);

    @POST("enroll-training/{id}")
    Call<Void> enrollInTraining(@Path("id") int id, @Body TrainingEnrollFragment.Enrollment userId);

    @POST("quick-feedback-create/")
    Call<Void> iFeel(@Body QuickFeedBackActivity.Feedback feedback);

    @GET("my-evaluation")
    Call<List<MyEvaluationFragment.MyEvaluation>> getMyEvaluations();

    @PUT("my-evaluation/{id}")
    Call<Void> submitMyEvaluation(@Path("id") int id, @Body EvaluateFragment.Evaluation evaluation);

    @GET("user-profile-view")
    Call<ProfileFragment.Profile> getUserProfile();

    @GET("my-kpis")
    Call<List<ReportKpiFragment.Kpi>> getReportKpis();

    @PUT("my-kpis/{id}")
    Call<Void> submitKpiReport(@Path("id") int id, @Body UpdateReportKpiFragment.UpdatedKpi updatedKpi);

    @GET("user-job-requirements")
    Call<List<UserJobReqFragment.JobReq>> getUserJobRequirements();

    @GET("user-kpis")
    Call<List<UserKpisFragment.Kpi>> getUserKpis();
}
