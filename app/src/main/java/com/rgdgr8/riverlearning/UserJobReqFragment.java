package com.rgdgr8.riverlearning;

import android.annotation.SuppressLint;
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

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserJobReqFragment extends Fragment {
    public static final String TAG = "UserJobReqFrag";
    private JobReqAdapter adapter;
    private final List<JobReq> jobReqList = new ArrayList<>();

    static class JobReq implements Serializable {
        @SerializedName("requirement")
        private final String job_req;
        private final Integer weightage;
        private final String skill_area;

        public JobReq(String job_req, Integer weightage, String skill_area) {
            this.job_req = job_req;
            this.weightage = weightage;
            this.skill_area = skill_area;
        }

        public String getJob_req() {
            return job_req;
        }

        public Integer getWeightage() {
            return weightage;
        }

        public String getSkill_area() {
            return skill_area;
        }
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LoginActivity.dataFetcher.getUserJobRequirements().enqueue(new Callback<List<JobReq>>() {
            @Override
            public void onResponse(@NotNull Call<List<JobReq>> call, @NotNull Response<List<JobReq>> response) {
                Log.d(TAG, "onResponse: " + response.code());
                try {
                    if (response.isSuccessful()) {
                        List<JobReq> t = response.body();
                        if (t == null || t.isEmpty()) {
                            Toast.makeText(MainActivity.ctx.get(), "Empty Body", Toast.LENGTH_SHORT).show();
                        } else {
                            jobReqList.clear();
                            jobReqList.addAll(t);
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
            public void onFailure(@NotNull Call<List<JobReq>> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t.getCause());
                try {
                    Toast.makeText(MainActivity.ctx.get(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_user_job_req, container, false);

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
            adapter = new JobReqAdapter();
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private class JobReqHolder extends RecyclerView.ViewHolder {
        private final TextView sr;
        private final TextView jobRequirement;
        private final TextView weightage;
        private final TextView skillArea;

        public JobReqHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            sr = itemView.findViewById(R.id.sr);
            jobRequirement = itemView.findViewById(R.id.job_req);
            jobRequirement.setOnClickListener(v -> Toast.makeText(getActivity(), jobRequirement.getText().toString(), Toast.LENGTH_SHORT).show());
            weightage = itemView.findViewById(R.id.weight);
            skillArea = itemView.findViewById(R.id.skill);
        }

        @SuppressLint("SetTextI18n")
        public void bind(int pos) {
            JobReq jr = jobReqList.get(pos);
            sr.setText((pos + 1) + "");
            jobRequirement.setText(jr.getJob_req());
            Integer score = jr.getWeightage();
            if (score != null) {
                this.weightage.setText(String.valueOf(score));
            } else {
                this.weightage.setText(requireContext().getResources().getString(R.string.blank_spinner));
            }
            skillArea.setText(jr.getSkill_area());
        }
    }

    private class JobReqAdapter extends RecyclerView.Adapter<JobReqHolder> {
        @NonNull
        @NotNull
        @Override
        public JobReqHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View v = requireActivity().getLayoutInflater().inflate(R.layout.user_job_req_item, parent, false);
            return new JobReqHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull JobReqHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return jobReqList.size();
        }
    }
}