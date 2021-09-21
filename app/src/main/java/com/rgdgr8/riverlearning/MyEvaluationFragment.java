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

public class MyEvaluationFragment extends Fragment {
    public static final String TAG = "MyEvaluationTasksFrag";
    private MyEvaluationAdapter adapter;
    private final List<MyEvaluation> myEvaluationList = new ArrayList<>();

    static class MyEvaluation implements Serializable {
        private final int id;
        private final String job_req;
        private final Integer emp_score;
        @SerializedName("emp_cmnt")
        private final String comments;

        public int getId() {
            return id;
        }

        public MyEvaluation(int id, String job_req, Integer emp_score, String comments) {
            this.id = id;
            this.job_req = job_req;
            this.emp_score = emp_score;
            this.comments = comments;
        }

        public String getJob_req() {
            return job_req;
        }

        public Integer getEmp_score() {
            return emp_score;
        }

        public String getComments() {
            return comments;
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

        LoginActivity.dataFetcher.getMyEvaluations().enqueue(new Callback<List<MyEvaluation>>() {
            @Override
            public void onResponse(@NotNull Call<List<MyEvaluation>> call, @NotNull Response<List<MyEvaluation>> response) {
                Log.d(TAG, "onResponse: " + response.code());
                if (response.isSuccessful()) {
                    List<MyEvaluation> t = response.body();
                    if (t == null) {
                        Toast.makeText(getContext(), "Empty Body", Toast.LENGTH_SHORT).show();
                    } else {
                        myEvaluationList.clear();
                        myEvaluationList.addAll(t);
                        setAdapter();
                    }
                } else {
                    Toast.makeText(getContext(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<MyEvaluation>> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: ", t.getCause());
            }
        });
    }

    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_my_evaluation, container, false);

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
            adapter = new MyEvaluationAdapter();
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private class MyEvaluationHolder extends RecyclerView.ViewHolder {
        private final TextView sr;
        private final TextView jobRequirement;
        private final TextView score;
        private final TextView comm;

        public MyEvaluationHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            sr = itemView.findViewById(R.id.sr);
            jobRequirement = itemView.findViewById(R.id.job_req);
            jobRequirement.setOnClickListener(v -> {
                Bundle b = new Bundle();
                b.putSerializable(TAG, myEvaluationList.get(getAdapterPosition()));

                Navigation.findNavController(root).navigate(R.id.action_myEvaluationFragment_to_evaluateFragment, b);
            });
            score = itemView.findViewById(R.id.performance);
            comm = itemView.findViewById(R.id.comments);
        }

        @SuppressLint("SetTextI18n")
        public void bind(int pos) {
            MyEvaluation me = myEvaluationList.get(pos);
            sr.setText((pos + 1) + "");
            jobRequirement.setText(me.getJob_req());
            Integer score = me.getEmp_score();
            if (score == null) score = 0;
            this.score.setText(String.valueOf(score));
            comm.setText(me.getComments());
        }
    }

    private class MyEvaluationAdapter extends RecyclerView.Adapter<MyEvaluationHolder> {
        @NonNull
        @NotNull
        @Override
        public MyEvaluationFragment.MyEvaluationHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View v = requireActivity().getLayoutInflater().inflate(R.layout.my_evaluation_item, parent, false);
            return new MyEvaluationHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull MyEvaluationFragment.MyEvaluationHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return myEvaluationList.size();
        }
    }
}