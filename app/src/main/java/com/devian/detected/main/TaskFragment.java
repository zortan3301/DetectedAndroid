package com.devian.detected.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devian.detected.R;
import com.devian.detected.utils.Network.NetworkService;
import com.devian.detected.utils.domain.ServerResponse;
import com.devian.detected.utils.domain.Task;
import com.devian.detected.utils.domain.UserStats;
import com.devian.detected.utils.ui.RecyclerAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskFragment extends Fragment {
    
    private static final String TAG = "TaskFragment";
    
    private Gson gson = new Gson();
    
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    
    private RecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_task, container, false);
        ButterKnife.bind(this, v);
    
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        
        mAdapter = new RecyclerAdapter(null);
        recyclerView.setAdapter(mAdapter);
        
        updateTasks();
        
        return v;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        updateTasks();
    }
    
    public void updateTasks() {
        NetworkService.getInstance().getJSONApi().getTextTasks().enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                Log.d(TAG, "onResponse: " + gson.toJson(response.body()));
                if (response.body().getType() == ServerResponse.TYPE_TASK_SUCCESS) {
                    Type listType = new TypeToken<ArrayList<Task>>(){}.getType();
                    List<Task> listTask = gson.fromJson(response.body().getData(), listType);
                    mAdapter.setTaskList(listTask);
                } else {
                    Log.e(TAG, "onResponse: user stats does not exist on the server");
                }
            }
        
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
