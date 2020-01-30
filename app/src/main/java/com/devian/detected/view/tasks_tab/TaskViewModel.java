package com.devian.detected.view.tasks_tab;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.devian.detected.model.domain.DataWrapper;
import com.devian.detected.model.domain.tasks.GeoTextTask;
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

    LiveData<DataWrapper<List<GeoTextTask>>> bindTaskList() {
        Log.d(TAG, "bindTaskList: ");
        return repository.getMldGeoTextTasks();
    }

    public void updateTaskList() {
        Log.d(TAG, "updateTaskList: ");
        repository.updateMldGeoTextTaskList();
    }

}
