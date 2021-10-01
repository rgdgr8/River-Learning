package com.rgdgr8.riverlearning;

import android.content.Context;
import android.os.Bundle;
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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssessTasksFragment extends Fragment {
    public static final String TAG = "AssessTasksFrag";
    private AssessTaskAdapter adapter;
    private final List<AssessTask> assessTaskList = new ArrayList<>();
    private View root;
    private String params = "";

    static class AssessTask implements Serializable {
        @SerializedName("task")
        private int id;
        @SerializedName("task_name")
        private String task;
        private String status;
        @SerializedName("work_quality")
        private Integer performance;
        @SerializedName("comment")
        private String comments;

        public int getId() {
            return id;
        }

        public AssessTask(int id, String task, String status, Integer performance, String comments) {
            this.id = id;
            this.task = task;
            this.status = status;
            this.performance = performance;
            this.comments = comments;
        }

        public String getTask() {
            return task;
        }

        public String getStatus() {
            return status;
        }

        public Integer getPerformance() {
            return performance;
        }

        public String getComments() {
            return comments;
        }
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        score_scale = requireActivity().getResources().getStringArray(R.array.performance_spinner);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LoginActivity.dataFetcher.getAssessTasks(params).enqueue(new Callback<List<AssessTask>>() {
            @Override
            public void onResponse(Call<List<AssessTask>> call, Response<List<AssessTask>> response) {
                Log.d(TAG, "onResponse: " + response.code());
                try {
                    if (response.isSuccessful()) {
                        List<AssessTask> t = response.body();
                        if (t == null || t.isEmpty()) {
                            Toast.makeText(MainActivity.ctx.get(), "Empty Body", Toast.LENGTH_SHORT).show();
                        } else {
                            assessTaskList.clear();
                            assessTaskList.addAll(t);
                            setAdapter();
                        }
                    } else {
                        Toast.makeText(MainActivity.ctx.get(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<AssessTask>> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t.getCause());
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
        root = inflater.inflate(R.layout.fragment_assess_tasks, container, false);

        ImageButton filter = root.findViewById(R.id.filter);
        filter.setOnClickListener(v -> {
            SearchFragment searchFragment = new SearchFragment(R.layout.filter_status, TAG);
            FragmentManager fm = getParentFragmentManager();
            fm.setFragmentResultListener(SearchFragment.FILTER_RESULT, searchFragment, (requestKey, result) -> {
                if (requestKey.equals(SearchFragment.FILTER_RESULT)) {
                    String status = result.getString(TAG + SearchFragment.STATUS);
                    params = "";
                    if (status != null && !status.equals(getActivity().getResources().getString(R.string.blank_spinner)))
                        params = status;

                    LoginActivity.dataFetcher.getAssessTasks(params)
                            .enqueue(new Callback<List<AssessTask>>() {
                                @Override
                                public void onResponse(Call<List<AssessTask>> call, Response<List<AssessTask>> response) {
                                    Log.d(TAG, "onFilterResponse: " + response.code());
                                    try {
                                        if (!response.isSuccessful()) {
                                            Toast.makeText(MainActivity.ctx.get(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        List<AssessTask> t = response.body();
                                        assessTaskList.clear();
                                        if (t != null && !t.isEmpty()) {
                                            assessTaskList.addAll(t);
                                        } else {
                                            Toast.makeText(MainActivity.ctx.get(), "Empty Body", Toast.LENGTH_SHORT).show();
                                        }
                                        setAdapter();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<AssessTask>> call, Throwable t) {
                                    Log.e(TAG, "onFilterFailure: ", t.getCause());
                                    try {
                                        Toast.makeText(MainActivity.ctx.get(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
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
            adapter = new AssessTaskAdapter();
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    public static String[] score_scale;

    private class AssessTaskHolder extends RecyclerView.ViewHolder {
        private TextView sr;
        private TextView task;
        private TextView status;
        private TextView perf;
        private TextView comm;

        public AssessTaskHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            sr = itemView.findViewById(R.id.sr);
            task = itemView.findViewById(R.id.task);
            task.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putSerializable(TAG, assessTaskList.get(getAdapterPosition()));

                    Navigation.findNavController(root).navigate(R.id.action_assessTasksFragment_to_assessmentOfTaskFragment, b);
                }
            });
            status = itemView.findViewById(R.id.status);
            perf = itemView.findViewById(R.id.performance);
            comm = itemView.findViewById(R.id.comments);
        }

        public void bind(int pos) {
            AssessTask at = assessTaskList.get(pos);
            sr.setText((pos + 1) + "");
            task.setText(at.getTask());
            status.setText(at.getStatus());
            Integer score = at.getPerformance();
            if (score == null || score < 1) score = score_scale.length;
            perf.setText(score_scale[score_scale.length - score]);
            if (at.getComments() == null)
                comm.setText(getActivity().getResources().getString(R.string.blank_spinner));
            else
                comm.setText(at.getComments());
        }
    }

    private class AssessTaskAdapter extends RecyclerView.Adapter<AssessTaskHolder> {
        @NonNull
        @NotNull
        @Override
        public AssessTaskHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View v = requireActivity().getLayoutInflater().inflate(R.layout.assess_task_item, parent, false);
            return new AssessTaskHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull AssessTaskHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return assessTaskList.size();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getActivity().getSharedPreferences(SearchFragment.TAG + TAG, Context.MODE_PRIVATE).edit().clear().apply();
    }
}