package com.devian.detected.main.task_list;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.devian.detected.utils.domain.DataWrapper;
import com.devian.detected.utils.domain.Task;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private static final String TAG = "TaskViewModel";

    private TaskRepository repository;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "TaskViewModel: ");
        repository = new TaskRepository();
    }

    LiveData<DataWrapper<List<Task>>> bindTaskList() {
        Log.d(TAG, "bindTaskList: ");
        return repository.getMldTaskList();
    }

    void updateTaskList() {
        Log.d(TAG, "updateTaskList: ");
        repository.updateTaskList();
    }
}
