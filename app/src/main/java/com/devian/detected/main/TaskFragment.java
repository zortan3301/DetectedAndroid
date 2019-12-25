package com.devian.detected.main;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.devian.detected.R;
import com.devian.detected.utils.Network.NetworkService;
import com.devian.detected.utils.Network.ServerResponse;
import com.devian.detected.utils.domain.Task;
import com.devian.detected.utils.ui.OnItemClickListener;
import com.devian.detected.utils.ui.RecyclerAdapter;
import com.devian.detected.utils.ui.RecyclerItemDecorator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener,
        OnItemClickListener {
    
    private static final String TAG = "TaskFragment";
    
    private OnTaskItemSelectedListener callback;
    
    private Gson gson = new Gson();
    
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.task_refreshLayout)
    SwipeRefreshLayout refreshLayout;
    
    private RecyclerAdapter mAdapter;
    
    private ArrayList<Task> tasks;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_task, container, false);
        ButterKnife.bind(this, v);
        
        refreshLayout.setOnRefreshListener(this);
    
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
    
        mAdapter = new RecyclerAdapter(null, this);
        recyclerView.setAdapter(mAdapter);
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 74,
                getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new RecyclerItemDecorator(space));
    
        checkSavedBundle(savedInstanceState);
        
        return v;
    }
    
    private void checkSavedBundle(Bundle inState) {
        if (inState != null) {
            tasks = (ArrayList<Task>) inState.getSerializable("tasks");
            mAdapter.setTaskList(tasks);
        } else {
            updateTasks();
        }
    }
    
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("tasks", tasks);
    }
    
    private void updateTasks() {
        NetworkService.getInstance().getApi().getTextTasks().enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call,
                                   @NonNull Response<ServerResponse> response) {
                if (response.body() == null) {
                    Log.e(TAG, "updateTasks onResponse: response body is null");
                    return;
                }
                try {
                    if (response.body().getType() == ServerResponse.TYPE_TASK_SUCCESS) {
                        Type listType = new TypeToken<ArrayList<Task>>() {
                        }.getType();
                        tasks = gson.fromJson(response.body().getData(), listType);
                        mAdapter.setTaskList(tasks);
                        refreshLayout.setRefreshing(false);
                    } else {
                        Log.e(TAG, "onResponse: user stats does not exist on the server");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    
            @Override
            public void onFailure(@NonNull Call<ServerResponse> call,
                                  @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }
    
    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(true);
        updateTasks();
    }
    
    @Override
    public void OnItemClicked(Task task) {
        Log.d(TAG, "OnItemClicked: " + task.getTitle());
        callback.onTaskItemSelected(task);
    }
    
    public void setOnTaskItemSelectedListener(OnTaskItemSelectedListener callback) {
        this.callback = callback;
    }
    
    public interface OnTaskItemSelectedListener {
        void onTaskItemSelected(Task task);
    }
}
