package com.devian.detected.main.map;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.devian.detected.model.domain.DataWrapper;
import com.devian.detected.model.domain.tasks.GeoTask;
import com.devian.detected.model.repo.TaskRepository;

import java.util.List;

public class MapViewModel extends AndroidViewModel {

    private static final String TAG = "MapViewModel";

    private TaskRepository repository;

    public MapViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository();
    }

    LiveData<DataWrapper<List<GeoTask>>> bindMarkers() {
        Log.d(TAG, "bindMarkers: ");
        return repository.getMldGeoTaskList();
    }

    void updateMarkers() {
        Log.d(TAG, "updateMarkers: ");
        repository.updateMldGeoTaskList();
    }
}
