package com.devian.detected.main.task_list;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.devian.detected.R;
import com.devian.detected.utils.domain.Task;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener,
        TaskListAdapter.OnTaskItemClickListener {

    private static final String TAG = "TaskFragment";

    private OnTaskItemSelectedListener callback;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.task_refreshLayout)
    SwipeRefreshLayout refreshLayout;

    private TaskListAdapter mAdapter;
    private TaskViewModel viewModel;

    private ArrayList<Task> tasks;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_task, container, false);
        ButterKnife.bind(this, v);

        viewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        setupView();
        bindView();

        return v;
    }

    private void bindView() {
        Log.d(TAG, "bind: ");
        viewModel.bindTaskList().observe(this, taskListWrapper -> {
            hideProgress();
            tasks = new ArrayList<>(taskListWrapper.getObject());
            displayTaskList(tasks);
        });
    }

    private void updateTaskList() {
        Log.d(TAG, "updateTaskList: ");
        showProgress();
        viewModel.updateTaskList();
    }

    private void displayTaskList(ArrayList<Task> taskList) {
        Log.d(TAG, "displayTaskList: ");
        mAdapter.setTaskList(taskList);
    }

    private void setupView() {
        Log.d(TAG, "setupView: ");
        refreshLayout.setOnRefreshListener(this);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new TaskListAdapter(null, this);
        recyclerView.setAdapter(mAdapter);
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 74,
                getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new TaskListAdapter.TaskListItemDecorator(space));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        if (savedInstanceState != null) {
            tasks = savedInstanceState.getParcelableArrayList("tasks");
            displayTaskList(tasks);
        } else {
            updateTaskList();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("tasks", tasks);
    }


    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh");
        updateTaskList();
    }

    private void showProgress() {
        Log.d(TAG, "showProgress: ");
        refreshLayout.setRefreshing(true);
    }

    private void hideProgress() {
        Log.d(TAG, "hideProgress: ");
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void OnItemClicked(Task task) {
        Log.d(TAG, "OnItemClicked: " + task.getTitle());
        callback.onTaskItemSelected(task);
    }

    public void setOnTaskItemSelectedListener(OnTaskItemSelectedListener callback) {
        Log.d(TAG, "setOnTaskItemSelectedListener");
        this.callback = callback;
    }

    public interface OnTaskItemSelectedListener {
        void onTaskItemSelected(Task task);
    }
}
