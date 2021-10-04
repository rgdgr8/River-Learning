package com.rgdgr8.riverlearning;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentTaskFragment extends Fragment {
    private static final String TAG = "CommentFrag";
    private NavController navController;
    private View root;

    static class Comment {
        private String data;

        public String getData() {
            return data;
        }

        public Comment(String data) {
            if (data == null)
                data = "";
            this.data = data;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_comment_task, container, false);

        assert getArguments() != null;
        int id = getArguments().getInt(OpenTaskHolder.COMM_OPEN_TASK);

        EditText comment = root.findViewById(R.id.comment);

        Button done = root.findViewById(R.id.submit);
        done.setOnClickListener(v -> {
            if(comment.getText().toString().equals("")){
                Toast.makeText(getActivity(), "Comment cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            Comment comm = new Comment(comment.getText().toString());
            LoginActivity.dataFetcher.submitComment(id, comm).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.d(TAG, "onResponse: " + response.code());
                    try {
                        if (!response.isSuccessful()) {
                            Toast.makeText(MainActivity.ctx.get(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    navController.navigateUp();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "onFailure: ", t.getCause());
                    try {
                        Toast.makeText(MainActivity.ctx.get(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    navController.navigateUp();
                }
            });
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        navController = Navigation.findNavController(root);
        //TODO: Be aware that navcontroller is not set to the root before onCreateView
    }
}