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

        Button done = root.findViewById(R.id.submit);
        done.setOnClickListener(v -> {
            navController.navigateUp();
        });

        /*Button cancel = root.findViewById(R.id.cancel);
        cancel.setOnClickListener(v -> {
            //if (isFromMyTasks)
                //navController.navigate(R.id.action_commentTaskFragment_to_myTasksFragment);
            //else
                //navController.navigate(R.id.action_commentTaskFragment_to_tasksAllocatedFragment);
            navController.navigateUp();
        });*/

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        navController = Navigation.findNavController(root);
        //TODO: Be aware that navcontroller is not set to the root before onCreateView
    }
}