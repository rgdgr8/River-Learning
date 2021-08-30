package com.rgdgr8.riverlearning;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;

public class TasksAllocatedFragment extends Fragment {
    private static final String TAG = "TasksAllocatedFrag";
    private OpenTasksAdapter adapter;
    private final List<OpenTask> tasks = new ArrayList<>();
    private View root;

    public View getRoot() {
        return root;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        LoginActivity.dataFetcher.getAllocatedTasks().enqueue(new Callback<List<OpenTask>>() {
            @Override
            public void onResponse(Call<List<OpenTask>> call, Response<List<OpenTask>> response) {
                Log.i(TAG, "onResponseAllocTaskFetcher: " + response.code() + " " + response.message());
                if (!response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Request unsuccessful", Toast.LENGTH_SHORT).show();
                    /*for (int i = 0; i < 30; i++) {
                        String x = String.valueOf(i + 1);
                        String status = i % 2 == 0 ? OpenTask.OPEN : OpenTask.CLOSED;
                        tasks.add(new OpenTask(x, x, x, x, status));
                    }
                    setAdapter();*/
                    return;
                }

                List<OpenTask> t = response.body();
                if (t != null) {
                    tasks.clear();
                    tasks.addAll(t);
                    setAdapter();
                } else {
                    Log.i(TAG, "onResponseAllocTasks: empty body");
                }
            }

            @Override
            public void onFailure(Call<List<OpenTask>> call, Throwable t) {
                Log.e(TAG, "onFailureAllocTasks: ", t.getCause());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_tasks_allocated, container, false);

        Button newTask = root.findViewById(R.id.new_task);
        newTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Create New task", Toast.LENGTH_SHORT).show();
            }
        });

        RecyclerView recyclerView = root.findViewById(R.id.rv);
        LinearLayoutManager rvLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(rvLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), rvLayoutManager.getOrientation()));
        setAdapter();
        recyclerView.setAdapter(adapter);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);

        return root;
    }

    public void setAdapter() {
        if (adapter == null) {
            adapter = new OpenTasksAdapter(this, tasks, R.layout.open_task_item, false);
        } else {
            adapter.notifyDataSetChanged();
        }
    }
}