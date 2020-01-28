package com.devian.detected.main.map;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.devian.detected.utils.domain.DataWrapper;
import com.devian.detected.utils.domain.Task;

import java.util.List;

public class MapViewModel extends AndroidViewModel {

    private static final String TAG = "MapViewModel";

    private MapRepository repository;

    public MapViewModel(@NonNull Application application) {
        super(application);
        repository = new MapRepository();
    }

    LiveData<DataWrapper<List<Task>>> bindMarkers() {
        Log.d(TAG, "bindMarkers: ");
        return repository.getMldMarkers();
    }

    void updateMarkers() {
        Log.d(TAG, "updateMarkers: ");
        repository.updateMldMarkers();
    }
}
