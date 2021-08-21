package com.rgdgr8.riverlearning;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyTasksFragment extends Fragment {
    private static final String TAG = "MyTasksFragment";
    private OpenTasksAdapter adapter;
    private final List<OpenTask> tasks = new ArrayList<>();

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        /*for (int i = 0; i < 30; i++) {
            String x = String.valueOf(i + 1);
            String status = i % 2 == 0 ? OpenTask.OPEN : OpenTask.CLOSED;
            tasks.add(new OpenTask(i + 1, x, x, x, x, status));
        }*/

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TaskFetcher.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TaskFetcher taskFetcher = retrofit.create(TaskFetcher.class);
        taskFetcher.getTasks().enqueue(new Callback<List<OpenTask>>() {
            @Override
            public void onResponse(Call<List<OpenTask>> call, Response<List<OpenTask>> response) {
                if (!response.isSuccessful()) {
                    Log.i(TAG, "onResponse: " + response.code() + " " + response.message());
                }

                List<OpenTask> t = response.body();
                if (t != null) {
                    tasks.clear();
                    tasks.addAll(t);
                } else {
                    Log.i(TAG, "onResponse: empty body");
                }

                setAdapter();
            }

            @Override
            public void onFailure(Call<List<OpenTask>> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
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