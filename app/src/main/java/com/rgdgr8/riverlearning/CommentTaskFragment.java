package com.rgdgr8.riverlearning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class CommentTaskFragment extends Fragment {
    private NavController navController;
    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_comment_task, container, false);

        boolean isFromMyTasks = getArguments().getBoolean(OpenTaskHolder.COMMENT_FROM);

        Button done = root.findViewById(R.id.done);
        done.setOnClickListener(v -> {
            if (isFromMyTasks)
                Navigation.findNavController(root).navigate(R.id.action_commentTaskFragment_to_myTasksFragment);
            else
                navController.navigate(R.id.action_commentTaskFragment_to_tasksAllocatedFragment);
        });

        Button cancel = root.findViewById(R.id.cancel);
        cancel.setOnClickListener(v -> {
            if (isFromMyTasks)
                navController.navigate(R.id.action_commentTaskFragment_to_myTasksFragment);
            else
                navController.navigate(R.id.action_commentTaskFragment_to_tasksAllocatedFragment);
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