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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener,
        OnItemClickListener {
    
    private static final String TAG = "TaskFragment";
    
    OnTaskItemSelectedListener callback;
    
    private Gson gson = new Gson();
    
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.task_refreshLayout)
    SwipeRefreshLayout refreshLayout;
    
    private RecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_task, container, false);
        ButterKnife.bind(this, v);
        
        refreshLayout.setOnRefreshListener(this);
    
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
    
        mAdapter = new RecyclerAdapter(null, this);
        recyclerView.setAdapter(mAdapter);
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 74,
                getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new RecyclerItemDecorator(space));
        
        updateTasks();
        
        return v;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        updateTasks();
    }
    
    private void updateTasks() {
        NetworkService.getInstance().getApi().getTextTasks().enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.body() == null) {
                    Log.e(TAG, "updateTasks onResponse: response body is null");
                    return;
                }
                try {
                    if (response.body().getType() == ServerResponse.TYPE_TASK_SUCCESS) {
                        Type listType = new TypeToken<ArrayList<Task>>() {
                        }.getType();
                        List<Task> listTask = gson.fromJson(response.body().getData(), listType);
                        mAdapter.setTaskList(listTask);
                        refreshLayout.setRefreshing(false);
                    } else {
                        Log.e(TAG, "onResponse: user stats does not exist on the server");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
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
