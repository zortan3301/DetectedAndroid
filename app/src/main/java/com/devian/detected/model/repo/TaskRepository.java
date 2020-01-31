package com.devian.detected.model.repo;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.devian.detected.model.domain.DataWrapper;
import com.devian.detected.model.domain.tasks.GeoTask;
import com.devian.detected.model.domain.tasks.GeoTextTask;
import com.devian.detected.model.domain.tasks.Task;
import com.devian.detected.modules.network.GsonSerializer;
import com.devian.detected.modules.network.NetworkModule;
import com.devian.detected.model.domain.network.ServerResponse;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    @Getter
    private MutableLiveData<DataWrapper<Task>> mldCompletedTask = new MutableLiveData<>();
    
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
                updateMldGeoTaskList();
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
                updateMldGeoTextTaskList();
            }
        });
    }

    public void scanTag(String data, String executor) {
        Log.d(TAG, "scanTag: ");
        Uri uri = Uri.parse(data);
        String tagType_param = uri.getQueryParameter("tag_type");
        String tagId_param = uri.getQueryParameter("tag_id");

        if (tagId_param == null || tagType_param == null) {
            Log.d(TAG, "scanTag: ======== tagId_param == null || tagType_param == null");
            mldCompletedTask.setValue(new DataWrapper<>(ServerResponse.TYPE_TASK_FAILURE));
            return;
        }

        int tagType = Integer.parseInt(tagType_param);

        if (tagType != Task.GEO_TAG && tagType != Task.GEO_TEXT_TAG) {
            Log.d(TAG, "scanTag: ======== tagType != Task.GEO_TAG && tagType != Task.GEO_TEXT_TAG");
            mldCompletedTask.setValue(new DataWrapper<>(ServerResponse.TYPE_TASK_FAILURE));
            return;
        }

        Task task;
        if (tagType == Task.GEO_TAG) {
            task = new GeoTask(tagId_param, executor);
        } else {
            task = new GeoTextTask(tagId_param, executor);
        }
        task.setExecutor(executor);

        Map<String, String> headers = NetworkModule.getInstance().proceedHeader(gson.toJson(task));
        Callback<ServerResponse> callback = new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call,
                                   @NonNull Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                Log.d(TAG, "onResponse: ======== Response");
                if (serverResponse == null) {
                    Log.e(TAG, "serverResponse == null");
                    return;
                }
                if (serverResponse.getType() == ServerResponse.TYPE_TASK_COMPLETED) {
                    Log.d(TAG, "onResponse: ======== ServerResponse.TYPE_TASK_COMPLETED");
                    Task completedTask = gson.fromJson(serverResponse.getData(), Task.class);
                    mldCompletedTask.setValue(new DataWrapper<>(completedTask));
                }
                if (serverResponse.getType() == ServerResponse.TYPE_TASK_ALREADY_COMPLETED) {
                    Log.d(TAG, "onResponse: ======== ServerResponse.TYPE_TASK_ALREADY_COMPLETED");
                    mldCompletedTask.setValue(new DataWrapper<>(serverResponse.getType()));
                }
                if (serverResponse.getType() == ServerResponse.TYPE_TASK_FAILURE) {
                    Log.d(TAG, "onResponse: ======== ServerResponse.TYPE_TASK_FAILURE");
                    mldCompletedTask.setValue(new DataWrapper<>(serverResponse.getType()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                mldCompletedTask.setValue(new DataWrapper<>(ServerResponse.TYPE_TASK_FAILURE));
            }
        };

        if (tagType == Task.GEO_TAG) {
            Call<ServerResponse> call = NetworkModule.getInstance().getApi().scanGeoTag(headers);
            call.enqueue(callback);
        } else {
            Call<ServerResponse> call = NetworkModule.getInstance().getApi().scanGeoTextTag(headers);
            call.enqueue(callback);
        }
    }
}
