package com.rgdgr8.riverlearning;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private static final String TAG = "Profile";

    static class Profile implements Serializable {
        private final String user_name;
        private final String user_email;
        private final String phone;
        @SerializedName("dept_name")
        private final String dept;
        @SerializedName("role_name")
        private final String role;
        @SerializedName("grade_name")
        private final String grade;
        private final String salary;
        private final String mgr_name;

        public String getUser_name() {
            return user_name;
        }

        public String getUser_email() {
            return user_email;
        }

        public String getPhone() {
            return phone;
        }

        public String getDept() {
            return dept;
        }

        public String getRole() {
            return role;
        }

        public String getGrade() {
            return grade;
        }

        public String getSalary() {
            return salary;
        }

        public String getMgr_name() {
            return mgr_name;
        }

        public Profile(String user_name, String user_email, String phone, String dept, String role, String grade, String salary, String mgr_name) {
            this.user_name = user_name;
            this.user_email = user_email;
            this.phone = phone;
            this.dept = dept;
            this.role = role;
            this.grade = grade;
            this.salary = salary;
            this.mgr_name = mgr_name;
        }

        @Override
        public @NotNull String toString() {
            return "Profile{" +
                    "user_name='" + user_name + '\'' +
                    ", user_email='" + user_email + '\'' +
                    ", phone='" + phone + '\'' +
                    ", dept='" + dept + '\'' +
                    ", role='" + role + '\'' +
                    ", grade='" + grade + '\'' +
                    ", salary='" + salary + '\'' +
                    ", mgr_name='" + mgr_name + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Profile)) return false;
            Profile profile = (Profile) o;
            return user_name.equals(profile.user_name) &&
                    user_email.equals(profile.user_email) &&
                    Objects.equals(phone, profile.phone) &&
                    Objects.equals(dept, profile.dept) &&
                    Objects.equals(role, profile.role) &&
                    Objects.equals(grade, profile.grade) &&
                    Objects.equals(salary, profile.salary) &&
                    Objects.equals(mgr_name, profile.mgr_name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(user_name, user_email, phone, dept, role, grade, salary, mgr_name);
        }
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    private void setFields(Profile p) /*throws NullPointerException if getAcitvity is null*/ {
        String blank = MainActivity.ctx.get().getResources().getString(R.string.blank_spinner);
        name.setText(p.getUser_name());
        email.setText(p.getUser_email());
        if (p.getPhone() != null && !p.getPhone().equals(""))
            phone.setText(p.getPhone());
        else
            phone.setText(blank);
        if (p.getDept() != null && !p.getDept().equals(""))
            dept.setText(p.getDept());
        else
            dept.setText(blank);
        TextView role = root.findViewById(R.id.role);
        if (p.getRole() != null && !p.getRole().equals(""))
            role.setText(p.getRole());
        else
            role.setText(blank);
        if (p.getGrade() != null && !p.getGrade().equals(""))
            grade.setText(p.getGrade());
        else
            grade.setText(blank);
        if (p.getSalary() != null && !p.getSalary().equals(""))
            salary.setText(p.getSalary());
        else
            salary.setText(blank);
        if (p.getMgr_name() != null && !p.getMgr_name().equals(""))
            mang.setText(p.getMgr_name());
        else
            mang.setText(blank);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LoginActivity.dataFetcher.getUserProfile().enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(@NotNull Call<Profile> call, @NotNull Response<Profile> response) {
                Log.d(TAG, "onResponse: " + response.code());
                try {
                    if (response.isSuccessful()) {
                        Profile p = response.body();
                        if (p != null) {
                            Log.d(TAG, "onNotNull: " + p.toString());
                            if (savedProfile == null || !savedProfile.equals(p)) {
                                setFields(p);
                                savedProfile = p;
                                File f = new File(MainActivity.ctx.get().getFilesDir(), TAG);
                                try {
                                    FileOutputStream fileOutputStream = new FileOutputStream(f);
                                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                                    objectOutputStream.writeObject(p);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Toast.makeText(MainActivity.ctx.get(), "Empty Body", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.ctx.get(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NotNull Call<Profile> call, @NotNull Throwable t) {
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
    private TextView name;
    private TextView email;
    private TextView dept;
    private TextView phone;
    private TextView grade;
    private TextView salary;
    private TextView mang;
    private Profile savedProfile = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_profile, container, false);

        ((MainActivity) requireActivity()).setDrawerEnabled(false);

        name = root.findViewById(R.id.name);
        email = root.findViewById(R.id.email);
        dept = root.findViewById(R.id.dept);
        phone = root.findViewById(R.id.phone);
        grade = root.findViewById(R.id.grade);
        salary = root.findViewById(R.id.salary);
        mang = root.findViewById(R.id.mang);

        try {
            File f = new File(MainActivity.ctx.get().getFilesDir(), TAG);
            if (f.exists()) {
                FileInputStream fileInputStream = new FileInputStream(f);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

                savedProfile = (Profile) objectInputStream.readObject();
                setFields(savedProfile);
            } else {
                f.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Button jobReqs = root.findViewById(R.id.job_req);
        jobReqs.setOnClickListener(v -> {
            Navigation.findNavController(root).navigate(R.id.action_profileFragment_to_userJobReqFragment);
        });

        Button kpis = root.findViewById(R.id.kpis);
        kpis.setOnClickListener(v -> {
            Navigation.findNavController(root).navigate(R.id.action_profileFragment_to_userKpisFragment);
        });

        return root;
    }

    @Override
    public void onStop() {
        super.onStop();

        ((MainActivity) requireActivity()).setDrawerEnabled(true);
    }
}