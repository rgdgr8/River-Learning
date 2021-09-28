package com.rgdgr8.riverlearning;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
    private String params = "";

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LoginActivity.dataFetcher.getTasks(params).enqueue(new Callback<List<OpenTask>>() {
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

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_my_tasks, container, false);

        TextView greeting = root.findViewById(R.id.greeting);
        String fname = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(MainActivity.SP_USER_FNAME, "");
        if (fname.equals(""))
            greeting.setText("Hi");
        else
            greeting.setText("Hi, " + fname);

        ImageButton filter = root.findViewById(R.id.filter);
        filter.setOnClickListener(v -> {
            SearchFragment<OpenTask> searchFragment = new SearchFragment<>(R.layout.filter_status_and_target_end, TAG);
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

                    LoginActivity.dataFetcher.getTasks(params)
                            .enqueue(new Callback<List<OpenTask>>() {
                                @Override
                                public void onResponse(Call<List<OpenTask>> call, Response<List<OpenTask>> response) {
                                    Log.d(TAG, "onResponseFilterMyTasks: " + response.code());
                                    if (!response.isSuccessful()) {
                                        Toast.makeText(getContext(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    List<OpenTask> t = response.body();
                                    tasks.clear();
                                    if (t != null && !t.isEmpty()) {
                                        tasks.addAll(t);
                                    } else {
                                        Toast.makeText(getContext(), "Empty Body", Toast.LENGTH_SHORT).show();
                                    }
                                    setAdapter();
                                }

                                @Override
                                public void onFailure(Call<List<OpenTask>> call, Throwable t) {
                                    Toast.makeText(getContext(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "onFilterTaskFetchFailure: ", t.getCause());
                                }
                            });
                }
            });
            searchFragment.show(getParentFragmentManager(), "FilterMyTasks");
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
            adapter = new OpenTasksAdapter(this, tasks, R.layout.open_task_item, true);
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