package com.rgdgr8.riverlearning;

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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTrainingsFragment extends Fragment {
    public static final String TAG = "MyTrainingsFrag";
    public static final String ENROLL = "enroll";
    private TrainingAdapter adapter;
    private List<Trainings.Training> trainingList;
    private View root;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        trainingList = new ArrayList<>();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LoginActivity.dataFetcher.getTrainings().enqueue(new Callback<Trainings>() {
            @Override
            public void onResponse(Call<Trainings> call, Response<Trainings> response) {
                Log.d(TAG, "onResponse: " + response.code());
                try {
                    if (response.isSuccessful()) {
                        Trainings training = response.body();
                        if (training == null || ((training.getEnrolled() == null || training.getEnrolled().isEmpty()) && (training.getTrainings() == null || training.getTrainings().isEmpty()))) {
                            Toast.makeText(MainActivity.ctx.get(), "Empty Body", Toast.LENGTH_SHORT).show();
                        } else {
                            trainingList.clear();
                            for (Trainings.Training t : training.getTrainings()) {
                                t.setEnrolled(false);
                                trainingList.add(t);
                            }

                            for (Trainings.Training t : training.getEnrolled()) {
                                t.setEnrolled(true);
                                trainingList.add(t);
                            }

                            //Log.d(TAG, "onResponse: " + " " + trainingList.toString());
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
            public void onFailure(Call<Trainings> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getCause());
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
        root = inflater.inflate(R.layout.fragment_my_trainings, container, false);

        ((MainActivity) requireActivity()).setDrawerEnabled(false);

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
            adapter = new TrainingAdapter();
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        ((MainActivity) requireActivity()).setDrawerEnabled(true);
    }

    static class Trainings {
        private final List<Training> trainings;
        private final List<Training> enrolled;
        public Trainings(List<Training> trainings, List<Training> enrolled) {
            this.trainings = trainings;
            this.enrolled = enrolled;
        }

        public List<Training> getTrainings() {
            return trainings;
        }

        public List<Training> getEnrolled() {
            return enrolled;
        }

        static class Training implements Serializable {
            private final Integer id;
            private final String topic_name;
            private final String trainer_name;
            private final String start_time;
            private final String end_time;
            private final String date;
            private final String venue;
            private boolean enrolled = true;

            public Training(Integer id, String topic_name, String trainer_name, String start_time, String end_time, String date, String venue) {
                this.id = id;
                this.topic_name = topic_name;
                this.trainer_name = trainer_name;
                this.start_time = start_time;
                this.end_time = end_time;
                this.date = date;
                this.venue = venue;
            }

            public boolean isEnrolled() {
                return enrolled;
            }

            public void setEnrolled(boolean enrolled) {
                this.enrolled = enrolled;
            }

            public Integer getId() {
                return id;
            }

            public String getTopic_name() {
                return topic_name;
            }

            public String getTrainer_name() {
                return trainer_name;
            }

            public String getStart_time() {
                return start_time;
            }

            public String getEnd_time() {
                return end_time;
            }

            public String getDate() {
                return date;
            }

            public String getVenue() {
                return venue;
            }

            @Override
            public String toString() {
                return "Training{" +
                        "id=" + id +
                        ", topic_name='" + topic_name + '\'' +
                        ", trainer_name='" + trainer_name + '\'' +
                        ", start_time='" + start_time + '\'' +
                        ", end_time='" + end_time + '\'' +
                        ", date='" + date + '\'' +
                        ", venue='" + venue + '\'' +
                        ", enrolled=" + enrolled +
                        '}';
            }
        }
    }

    private class TrainingHolder extends RecyclerView.ViewHolder {
        private final TextView topic;
        private final TextView trainer;
        private final TextView date;
        private final TextView startTime;
        private final TextView endTime;
        private final TextView venue;

        public TrainingHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            topic = itemView.findViewById(R.id.topic);
            topic.setOnClickListener(v -> Toast.makeText(getActivity(), topic.getText().toString(), Toast.LENGTH_SHORT).show());
            trainer = itemView.findViewById(R.id.trainer);
            date = itemView.findViewById(R.id.date);
            startTime = itemView.findViewById(R.id.start_time);
            endTime = itemView.findViewById(R.id.end_time);
            venue = itemView.findViewById(R.id.venue);
            ImageButton enroll = itemView.findViewById(R.id.enroll);
            enroll.setOnClickListener(v -> {
                if (daysExceeded(1)) {
                    Toast.makeText(getActivity(), "Disabled", Toast.LENGTH_SHORT).show();
                    return;
                }

                Trainings.Training training = trainingList.get(getAdapterPosition());
                if (training.isEnrolled()) {
                    Toast.makeText(MainActivity.ctx.get(), "Already enrolled", Toast.LENGTH_SHORT).show();
                    return;
                }

                Bundle b = new Bundle();
                b.putSerializable(ENROLL, training);
                Navigation.findNavController(root).navigate(R.id.action_myTrainingsFragment_to_trainingEnrollFragment, b);
            });
            ImageButton feedback = itemView.findViewById(R.id.feedback);
            feedback.setOnClickListener(v -> {
                if (daysExceeded(5)) {
                    Toast.makeText(getActivity(), "Disabled", Toast.LENGTH_SHORT).show();
                    return;
                }

                Trainings.Training training = trainingList.get(getAdapterPosition());
                if (!training.isEnrolled()) {
                    Toast.makeText(getActivity(), "Not enrolled", Toast.LENGTH_SHORT).show();
                    return;
                }

                Bundle b = new Bundle();
                b.putInt(TAG, training.getId());
                Navigation.findNavController(root).navigate(R.id.action_myTrainingsFragment_to_trainingFeedbackFragment, b);
            });
        }

        public void bind(int pos) {
            Trainings.Training training = trainingList.get(pos);
            topic.setText(training.getTopic_name());
            trainer.setText(training.getTrainer_name());
            date.setText(training.getDate());
            startTime.setText(training.getStart_time());
            endTime.setText(training.getEnd_time());
            venue.setText(training.getVenue());
        }

        private boolean daysExceeded(int d) {
            Date trainingDate;
            try {
                trainingDate = new SimpleDateFormat("yyyy-MM-dd").parse(date.getText().toString());
                Date currentDate = Calendar.getInstance().getTime();
                int days = Days.daysBetween(new LocalDate(trainingDate), new LocalDate(currentDate)).getDays();
                Log.d(TAG, "days passed: " + days);
                if (days >= d) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }

            return false;
        }
    }

    private class TrainingAdapter extends RecyclerView.Adapter<TrainingHolder> {

        @NonNull
        @NotNull
        @Override
        public TrainingHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View view = requireActivity().getLayoutInflater().inflate(R.layout.training_item, parent, false);
            return new TrainingHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull MyTrainingsFragment.TrainingHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return trainingList.size();
        }
    }
}