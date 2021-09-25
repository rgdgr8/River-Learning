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

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserKpisFragment extends Fragment {
    public static final String TAG = "UserKpisFrag";
    private KpiAdapter adapter;
    private final List<Kpi> kpiList = new ArrayList<>();

    static class Kpi implements Serializable {
        private final String kpi;
        private String uom;
        private final String direction;

        public Kpi(String kpi, String uom, String direction) {
            this.kpi = kpi;
            this.uom = uom;
            this.direction = direction;
        }

        public String getUom() {
            return uom;
        }

        public String getKpi() {
            return kpi;
        }

        public String getDirection() {
            return direction;
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

        LoginActivity.dataFetcher.getUserKpis().enqueue(new Callback<List<Kpi>>() {
            @Override
            public void onResponse(@NotNull Call<List<Kpi>> call, @NotNull Response<List<Kpi>> response) {
                Log.d(TAG, "onResponse: " + response.code());
                if (response.isSuccessful()) {
                    List<Kpi> t = response.body();
                    if (t == null || t.isEmpty()) {
                        Toast.makeText(getContext(), "Empty Body", Toast.LENGTH_SHORT).show();
                    } else {
                        kpiList.clear();
                        kpiList.addAll(t);
                        setAdapter();
                    }
                } else {
                    Toast.makeText(getContext(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Kpi>> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: ", t.getCause());
            }
        });
    }

    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_user_kpis, container, false);

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
            adapter = new KpiAdapter();
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private class KpiHolder extends RecyclerView.ViewHolder {
        private final TextView sr;
        private final TextView kpi;
        private final TextView uom;
        private final TextView dir;

        public KpiHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            sr = itemView.findViewById(R.id.sr);
            kpi = itemView.findViewById(R.id.kpi);
            kpi.setOnClickListener(v -> Toast.makeText(getActivity(), kpi.getText().toString(), Toast.LENGTH_SHORT).show());
            uom = itemView.findViewById(R.id.uom);
            dir = itemView.findViewById(R.id.dir);
        }

        @SuppressLint("SetTextI18n")
        public void bind(int pos) {
            Kpi k = kpiList.get(pos);
            sr.setText((pos + 1) + "");
            kpi.setText(k.getKpi());
            uom.setText(k.getUom());
            dir.setText(k.getDirection());
        }
    }

    private class KpiAdapter extends RecyclerView.Adapter<KpiHolder> {
        @NonNull
        @NotNull
        @Override
        public KpiHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View v = requireActivity().getLayoutInflater().inflate(R.layout.user_kpi_item, parent, false);
            return new KpiHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull KpiHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return kpiList.size();
        }
    }
}