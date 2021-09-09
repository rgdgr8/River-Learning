package com.rgdgr8.riverlearning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class AssessTasksFragment extends Fragment {
    private static final String TAG = "AssessTasksFrag";
    private AssessTaskAdapter adapter;
    private List<AssessTask> assessTaskList;

    static class AssessTask {
        private String task;
        private String status;
        private String performance;
        private String comments;

        public AssessTask(String task, String status, String performance, String comments) {
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

        public String getPerformance() {
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

        assessTaskList = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            String x = String.valueOf(i + 1);
            assessTaskList.add(new AssessTask(x, x, x, x));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_assess_tasks, container, false);

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
            status = itemView.findViewById(R.id.status);
            perf = itemView.findViewById(R.id.performance);
            comm = itemView.findViewById(R.id.comments);
        }

        public void bind(int pos) {
            AssessTask ct = assessTaskList.get(pos);
            sr.setText((pos + 1) + "");
            task.setText(ct.getTask());
            status.setText(ct.getStatus());
            perf.setText(ct.getPerformance());
            comm.setText(ct.getComments());
        }
    }

    private class AssessTaskAdapter extends RecyclerView.Adapter<AssessTaskHolder> {
        @NonNull
        @NotNull
        @Override
        public AssessTaskHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.assess_task_item, parent, false);
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
}