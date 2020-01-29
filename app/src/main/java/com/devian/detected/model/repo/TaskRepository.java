package com.devian.detected.model.repo;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.devian.detected.model.domain.DataWrapper;
import com.devian.detected.model.domain.tasks.GeoTask;
import com.devian.detected.model.domain.tasks.GeoTextTask;
import com.devian.detected.modules.network.GsonSerializer;
import com.devian.detected.modules.network.NetworkModule;
import com.devian.detected.modules.network.domain.ServerResponse;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("unused")
public class TaskRepository {
    
    private static final String TAG = "TaskRepository";
    
    private Gson gson = GsonSerializer.getInstance().getGson();
    
    @Getter
    private MutableLiveData<DataWrapper<List<GeoTask>>> mldGeoTaskList = new MutableLiveData<>();
    @Getter
    private MutableLiveData<DataWrapper<List<GeoTextTask>>> mldGeoTextTasks = new MutableLiveData<>();
    
    public void updateMldGeoTaskList() {
        Log.d(TAG, "updateMldGeoTaskList: ");
        Call<ServerResponse> call = NetworkModule.getInstance().getApi().getMapTasks();
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call,
                                   @NonNull Response<ServerResponse> response) {
                Log.d(TAG, "onResponse:");
                ServerResponse serverResponse = response.body();
                if (serverResponse == null) {
                    Log.e(TAG, "serverResponse == null");
                    return;
                }
                if (serverResponse.getType() == ServerResponse.TYPE_TASK_SUCCESS) {
                    List<GeoTask> geoTaskList = Arrays.asList(gson.fromJson(
                            NetworkModule.getInstance().proceedResponse(serverResponse),
                            GeoTask[].class));
                    DataWrapper<List<GeoTask>> markersDataWrapper = new DataWrapper<>(geoTaskList);
                    mldGeoTaskList.setValue(markersDataWrapper);
                } else {
                    DataWrapper<List<GeoTask>> markersDataWrapper = new DataWrapper<>(serverResponse.getType());
                    mldGeoTaskList.setValue(markersDataWrapper);
                }
            }
        
            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }
    
    public void updateMldGeoTextTaskList() {
        Log.d(TAG, "updateMldGeoTextTaskList: ");
        Call<ServerResponse> call = NetworkModule.getInstance().getApi().getTextTasks();
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call,
                                   @NonNull Response<ServerResponse> response) {
                Log.d(TAG, "onResponse: ");
                ServerResponse serverResponse = response.body();
                if (serverResponse == null) {
                    Log.e(TAG, "serverResponse == null");
                    return;
                }
                if (serverResponse.getType() == ServerResponse.TYPE_TASK_SUCCESS) {
                    List<GeoTextTask> geoTextTaskList = Arrays.asList(gson.fromJson(
                            NetworkModule.getInstance().proceedResponse(serverResponse),
                            GeoTextTask[].class));
                    DataWrapper<List<GeoTextTask>> taskListDataWrapper = new DataWrapper<>(geoTextTaskList);
                    mldGeoTextTasks.setValue(taskListDataWrapper);
                } else {
                    DataWrapper<List<GeoTextTask>> taskListDataWrapper = new DataWrapper<>(serverResponse.getType());
                    mldGeoTextTasks.setValue(taskListDataWrapper);
                }
            }
        
            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: ");
            }
        });
    }
}
