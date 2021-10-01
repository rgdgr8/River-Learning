package com.rgdgr8.riverlearning;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TasksAllocatedFragment extends Fragment {
    private static final String TAG = "TasksAllocatedFrag";
    private OpenTasksAdapter adapter;
    private final List<OpenTask> tasks = new ArrayList<>();
    private View root;
    private String params = "";

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LoginActivity.dataFetcher.getAllocatedTasks(params).enqueue(new Callback<List<OpenTask>>() {
            @Override
            public void onResponse(Call<List<OpenTask>> call, Response<List<OpenTask>> response) {
                Log.d(TAG, "onResponseAllocTaskFetcher: " + response.code() + " " + response.message());
                try {
                    if (!response.isSuccessful()) {
                        Toast.makeText(MainActivity.ctx.get(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<OpenTask> t = response.body();
                    if (t != null && !t.isEmpty()) {
                        tasks.clear();
                        tasks.addAll(t);
                        setAdapter();
                    } else {
                        Toast.makeText(MainActivity.ctx.get(), "Empty Body", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<OpenTask>> call, Throwable t) {
                Log.e(TAG, "onFailureAllocTasks: ", t.getCause());
                try {
                    Toast.makeText(MainActivity.ctx.get(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_tasks_allocated, container, false);

        Button newTask = root.findViewById(R.id.new_task);
        newTask.setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.action_tasksAllocatedFragment_to_createNewTaskFragment));

        ImageButton filter = root.findViewById(R.id.filter);
        filter.setOnClickListener(v -> {
            SearchFragment searchFragment = new SearchFragment(R.layout.filter_status_and_target_end, TAG);
            FragmentManager fm = getParentFragmentManager();
            fm.setFragmentResultListener(SearchFragment.FILTER_RESULT, searchFragment, (requestKey, result) -> {
                if (requestKey.equals(SearchFragment.FILTER_RESULT)) {
                    String status = result.getString(TAG + SearchFragment.STATUS);
                    params = "";
                    if (status != null && !status.equals(getActivity().getResources().getString(R.string.blank_spinner)))
                        params = status;
                    String date = result.getString(TAG + SearchFragment.DATE);
                    if (date != null && !date.equals(getActivity().getResources().getString(R.string.blank_spinner)))
                        params = params + "," + date;

                    LoginActivity.dataFetcher.getAllocatedTasks(params)
                            .enqueue(new Callback<List<OpenTask>>() {
                                @Override
                                public void onResponse(Call<List<OpenTask>> call, Response<List<OpenTask>> response) {
                                    Log.d(TAG, "onResponseFilterAllocTasks: " + response.code());
                                    try {
                                        if (!response.isSuccessful()) {
                                            Toast.makeText(MainActivity.ctx.get(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        List<OpenTask> t = response.body();
                                        tasks.clear();
                                        if (t != null && !t.isEmpty()) {
                                            tasks.addAll(t);
                                        } else {
                                            Toast.makeText(MainActivity.ctx.get(), "Empty Body", Toast.LENGTH_SHORT).show();
                                        }
                                        setAdapter();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<OpenTask>> call, Throwable t) {
                                    Log.e(TAG, "onFilterAllocTaskFetchFailure: ", t.getCause());
                                    try {
                                        Toast.makeText(MainActivity.ctx.get(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                }
            });
            searchFragment.show(getParentFragmentManager(), "FilterAllocTasks");
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        getActivity().getSharedPreferences(SearchFragment.TAG + TAG, Context.MODE_PRIVATE).edit().clear().apply();
    }
}