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

public class iFeelFragment extends Fragment {
    private static final String TAG = "iFeelFrag";

    static class iFeel {
        private Integer id;
        private String fto_name;
        private String ffrom_name;
        private String date_created;
        private String details;

        public iFeel(Integer id, String fto_name, String ffrom_name, String date_created, String details) {
            this.id = id;
            this.fto_name = fto_name;
            this.ffrom_name = ffrom_name;
            this.date_created = date_created;
            this.details = details;
        }

        public Integer getId() {
            return id;
        }

        public String getFto_name() {
            return fto_name;
        }

        public String getFfrom_name() {
            return ffrom_name;
        }

        public String  getDate_created() {
            return date_created;
        }

        public String getDetails() {
            return details;
        }
    }

    private iFeelAdapter adapter;
    private List<iFeel> iFeels;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        iFeels = new ArrayList<>();

        LoginActivity.dataFetcher.getiFeels().enqueue(new Callback<List<iFeel>>() {
            @Override
            public void onResponse(Call<List<iFeel>> call, Response<List<iFeel>> response) {
                Log.d(TAG, "onResponse: " + response.code() + " " + response.message());
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<iFeel> t = response.body();
                if (t != null) {
                    iFeels.clear();
                    iFeels.addAll(t);
                    setAdapter();
                } else {
                    Toast.makeText(getContext(), "Empty Body", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<iFeel>> call, Throwable t) {
                Toast.makeText(getContext(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailureClosedTasks: ", t.getCause());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_i_feel, container, false);

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
            adapter = new iFeelAdapter();
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private class iFeelHolder extends RecyclerView.ViewHolder {
        private TextView sr;
        private TextView fto;
        private TextView ffrom;
        private TextView date;
        private TextView comment;

        public iFeelHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            sr = itemView.findViewById(R.id.sr);
            fto = itemView.findViewById(R.id.fto);
            ffrom = itemView.findViewById(R.id.ffrom);
            date = itemView.findViewById(R.id.date);
            comment = itemView.findViewById(R.id.comment);
        }

        public void bind(int pos) {
            iFeel i = iFeels.get(pos);
            sr.setText((pos + 1) + "");
            fto.setText(i.getFto_name());
            ffrom.setText(i.getFfrom_name());
            date.setText(i.getDate_created());
            comment.setText(i.getDetails());
        }
    }

    private class iFeelAdapter extends RecyclerView.Adapter<iFeelHolder> {
        @NonNull
        @NotNull
        @Override
        public iFeelFragment.iFeelHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.ifeel_item, parent, false);
            return new iFeelHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull iFeelFragment.iFeelHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return iFeels.size();
        }
    }
}