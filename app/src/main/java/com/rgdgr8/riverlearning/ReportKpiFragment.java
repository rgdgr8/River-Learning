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

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportKpiFragment extends Fragment {
    public static final String TAG = "ReportKpiFrag";

    static class Kpi implements Serializable {
        private final Integer id;
        private final String kpi_name;
        private final String uom_name;
        private final String direction_name;
        private final String month_name;
        private final Float actual_value;
        private final Float target_value;

        public Integer getId() {
            return id;
        }

        public String getKpi_name() {
            return kpi_name;
        }

        public String getUom_name() {
            return uom_name;
        }

        public String getDirection_name() {
            return direction_name;
        }

        public String getMonth_name() {
            return month_name;
        }

        public Float getActual_value() {
            return actual_value;
        }

        public Float getTarget_value() {
            return target_value;
        }

        public Kpi(Integer id, String kpi_name, String uom_name, String direction_name, String month_name, Float actual_value, Float target_value) {
            this.id = id;
            this.kpi_name = kpi_name;
            this.uom_name = uom_name;
            this.direction_name = direction_name;
            this.month_name = month_name;
            this.actual_value = actual_value;
            this.target_value = target_value;
        }
    }

    private KpiAdapter adapter;
    private List<Kpi> kpis;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        kpis = new ArrayList<>();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LoginActivity.dataFetcher.getReportKpis().enqueue(new Callback<List<Kpi>>() {
            @Override
            public void onResponse(@NotNull Call<List<Kpi>> call, @NotNull Response<List<Kpi>> response) {
                Log.d(TAG, "onResponse: " + response.code() + " " + response.message());
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Kpi> t = response.body();
                if (t != null) {
                    kpis.clear();
                    kpis.addAll(t);
                    setAdapter();
                } else {
                    Toast.makeText(getContext(), "Empty Body", Toast.LENGTH_SHORT).show();
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
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_report_kpi, container, false);

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
        private final TextView month;
        private final TextView target;
        private final TextView actual;

        public KpiHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            sr = itemView.findViewById(R.id.sr);
            kpi = itemView.findViewById(R.id.kpi);
            kpi.setOnClickListener(v -> {
                Bundle b = new Bundle();
                b.putSerializable(TAG, kpis.get(getAdapterPosition()));

                Navigation.findNavController(root).navigate(R.id.action_reportKpiFragment_to_updateReportKpiFragment, b);
            });
            uom = itemView.findViewById(R.id.uom);
            dir = itemView.findViewById(R.id.dir);
            month = itemView.findViewById(R.id.month);
            target = itemView.findViewById(R.id.target);
            actual = itemView.findViewById(R.id.actual);
        }

        @SuppressLint("SetTextI18n")
        public void bind(int pos) {
            Kpi k = kpis.get(pos);
            sr.setText((pos + 1) + "");
            kpi.setText(k.getKpi_name());
            uom.setText(k.getUom_name());
            dir.setText(k.getDirection_name());
            month.setText(k.getMonth_name());
            if (k.getTarget_value() == null)
                target.setText(getActivity().getResources().getString(R.string.blank_spinner));
            else
                target.setText(String.valueOf(k.getTarget_value()));
            if (k.getActual_value() == null)
                actual.setText(getActivity().getResources().getString(R.string.blank_spinner));
            else
                actual.setText(String.valueOf(k.getActual_value()));
        }
    }

    private class KpiAdapter extends RecyclerView.Adapter<KpiHolder> {
        @NonNull
        @NotNull
        @Override
        public ReportKpiFragment.KpiHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View v = requireActivity().getLayoutInflater().inflate(R.layout.report_kpi_item, parent, false);
            return new KpiHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull ReportKpiFragment.KpiHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return kpis.size();
        }
    }
}