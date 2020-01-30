package com.devian.detected.view;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.devian.detected.model.domain.DataWrapper;
import com.devian.detected.model.domain.tasks.Task;
import com.devian.detected.model.repo.TaskRepository;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = "MainViewModel";

    private TaskRepository taskRepository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "MainViewModel: ");
        taskRepository = new TaskRepository();
    }

    LiveData<DataWrapper<Task>> bindCompletedTask() {
        Log.d(TAG, "bindCompletedTask: ");
        return taskRepository.getMldCompletedTask();
    }

    void proceedTag(String data, String executor) {
        Log.d(TAG, "proceedTask: ");
        taskRepository.scanTag(data, executor);
    }


}
