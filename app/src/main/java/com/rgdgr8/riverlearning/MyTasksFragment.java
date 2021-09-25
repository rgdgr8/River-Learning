package com.rgdgr8.riverlearning;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTasksFragment extends Fragment {
    private static final String TAG = "MyTasksFragment";
    private OpenTasksAdapter adapter;
    private final List<OpenTask> tasks = new ArrayList<>();

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LoginActivity.dataFetcher.getTasks().enqueue(new Callback<List<OpenTask>>() {
            @Override
            public void onResponse(Call<List<OpenTask>> call, Response<List<OpenTask>> response) {
                Log.d(TAG, "onResponseTaskFetcher: " + response.code() + " " + response.message());
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<OpenTask> t = response.body();
                if (t != null && !t.isEmpty()) {
                    tasks.clear();
                    tasks.addAll(t);
                    setAdapter();
                } else {
                    Toast.makeText(getContext(), "Empty Body", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<OpenTask>> call, Throwable t) {
                Toast.makeText(getContext(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onTaskFetchFailure: ", t.getCause());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_my_tasks, container, false);

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
            adapter = new OpenTasksAdapter(this, tasks, R.layout.open_task_item, true);
        } else {
            adapter.notifyDataSetChanged();
        }
    }
}