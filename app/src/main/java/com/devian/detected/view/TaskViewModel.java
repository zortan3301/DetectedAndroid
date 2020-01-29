package com.devian.detected.view;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;

import com.devian.detected.model.domain.DataWrapper;
import com.devian.detected.model.domain.tasks.GeoTask;
import com.devian.detected.model.domain.tasks.GeoTextTask;
import com.devian.detected.model.domain.tasks.Task;
import com.devian.detected.model.repo.TaskRepository;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private static final String TAG = "TaskViewModel";

    private TaskRepository repository;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "TaskViewModel: ");
        repository = new TaskRepository();
    }

    public LiveData<DataWrapper<List<GeoTextTask>>> bindTaskList() {
        Log.d(TAG, "bindTaskList: ");
        return repository.getMldGeoTextTasks();
    }

    public void updateTaskList() {
        Log.d(TAG, "updateTaskList: ");
        repository.updateMldGeoTextTaskList();
    }

    public LiveData<DataWrapper<List<GeoTask>>> bindMarkers() {
        Log.d(TAG, "bindMarkers: ");
        return repository.getMldGeoTaskList();
    }

    public void updateMarkers() {
        Log.d(TAG, "updateMarkers: ");
        repository.updateMldGeoTaskList();
    }

    public LiveData<DataWrapper<Task>> bindCompletedTask() {
        Log.d(TAG, "bindCompletedTask: ");
        return repository.getMldCompletedTask();
    }

    public void proceedTag(String data, String executor) {
        Log.d(TAG, "proceedTask: ");
        repository.scanTag(data, executor);
    }

    public void updateTasks() {
        Log.d(TAG, "updateTasks: ");
        repository.updateMldGeoTextTaskList();
        repository.updateMldGeoTaskList();
    }
}
