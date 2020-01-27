package com.devian.detected.main.task_list;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
        TaskListContract.OnTaskItemClickListener,
        TaskListContract.View {

    private static final String TAG = "TaskFragment";

    private OnTaskItemSelectedListener callback;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.task_refreshLayout)
    SwipeRefreshLayout refreshLayout;

    private TaskListAdapter mAdapter;
    private TaskPresenter taskPresenter;

    private ArrayList<Task> tasks;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_task, container, false);
        ButterKnife.bind(this, v);

        setupMVP();
        setupView();

        return v;
    }

    @Override
    public void displayTasks(ArrayList<Task> taskList) {
        hideProgress();
        tasks = taskList;
        mAdapter.setTaskList(taskList);
    }

    @Override
    public void displayError(String s) {
        hideProgress();
        Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
    }

    private void setupMVP() {
        taskPresenter = new TaskPresenter(this);
    }

    private void setupView() {
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

    private void getTaskList() {
        showProgress();
        taskPresenter.getTasks();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        if (savedInstanceState != null) {
            tasks = savedInstanceState.getParcelableArrayList("tasks");
            displayTasks(tasks);
        } else {
            getTaskList();
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
        getTaskList();
    }

    @Override
    public void showProgress() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void hideProgress() {
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
