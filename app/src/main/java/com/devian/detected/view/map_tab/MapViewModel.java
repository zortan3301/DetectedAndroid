package com.devian.detected.view.map_tab;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.devian.detected.model.domain.DataWrapper;
import com.devian.detected.model.domain.tasks.GeoTask;
import com.devian.detected.model.domain.tasks.Task;
import com.devian.detected.model.repo.TaskRepository;

import java.util.List;

public class MapViewModel extends AndroidViewModel {

    private static final String TAG = "MapViewModel";

    private TaskRepository repository;

    public MapViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "MapViewModel: ");
        repository = new TaskRepository();
    }

    LiveData<DataWrapper<List<GeoTask>>> bindMarkers() {
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
}
