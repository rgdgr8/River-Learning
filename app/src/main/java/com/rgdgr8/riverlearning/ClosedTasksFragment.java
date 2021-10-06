package com.rgdgr8.riverlearning;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

public class ClosedTasksFragment extends Fragment {
    private static final String TAG = "ClosedTasksFrag";
    private ClosedTaskAdapter adapter;
    private List<ClosedTask> closedTasks;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        closedTasks = new ArrayList<>();

        LoginActivity.dataFetcher.getClosedTasks().enqueue(new Callback<List<ClosedTask>>() {
            @Override
            public void onResponse(Call<List<ClosedTask>> call, Response<List<ClosedTask>> response) {
                Log.d(TAG, "onResponseClosedTasksFetcher: " + response.code() + " " + response.message());
                try {
                    if (!response.isSuccessful()) {
                        Toast.makeText(MainActivity.ctx.get(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<ClosedTask> t = response.body();
                    if (t != null && !t.isEmpty()) {
                        closedTasks.clear();
                        closedTasks.addAll(t);
                        setAdapter();
                    } else {
                        Toast.makeText(MainActivity.ctx.get(), "Empty Body", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<ClosedTask>> call, Throwable t) {
                Log.e(TAG, "onFailureClosedTasks: ", t.getCause());
                try {
                    Toast.makeText(MainActivity.ctx.get(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                    MainActivity.checkNetworkAndShowDialog(getActivity());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_closed_tasks, container, false);

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
            adapter = new ClosedTaskAdapter();
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    static class ClosedTask {
        private final String task;
        private final String allocatedby_name;
        private final String allocation_date;
        private final String end_date;

        public ClosedTask(String task, String allocatedby_name, String allocation_date, String end_date) {
            this.task = task;
            this.allocatedby_name = allocatedby_name;
            this.allocation_date = allocation_date;
            this.end_date = end_date;
        }

        public String getTask() {
            return task;
        }

        public String getAllocatedby_name() {
            return allocatedby_name;
        }

        public String getAllocation_date() {
            return allocation_date;
        }

        public String getEnd_date() {
            return end_date;
        }
    }

    private class ClosedTaskHolder extends RecyclerView.ViewHolder {
        private final TextView sr;
        private final TextView task;
        private final TextView alloc;
        private final TextView alloc_date;
        private final TextView close_date;

        public ClosedTaskHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            sr = itemView.findViewById(R.id.sr);
            task = itemView.findViewById(R.id.task);
            task.setOnClickListener(v -> Toast.makeText(getActivity(), task.getText().toString(), Toast.LENGTH_SHORT).show());
            alloc = itemView.findViewById(R.id.alloc);
            alloc_date = itemView.findViewById(R.id.allocD);
            close_date = itemView.findViewById(R.id.closeD);
        }

        public void bind(int pos) {
            ClosedTask ct = closedTasks.get(pos);
            sr.setText((pos + 1) + "");
            task.setText(ct.getTask());
            alloc.setText(ct.getAllocatedby_name());
            alloc_date.setText(ct.getAllocation_date());
            close_date.setText(ct.getEnd_date());
        }
    }

    private class ClosedTaskAdapter extends RecyclerView.Adapter<ClosedTaskHolder> {
        @NonNull
        @NotNull
        @Override
        public ClosedTaskHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View v = requireActivity().getLayoutInflater().inflate(R.layout.closed_task_item, parent, false);
            return new ClosedTaskHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull ClosedTaskHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return closedTasks.size();
        }
    }
}