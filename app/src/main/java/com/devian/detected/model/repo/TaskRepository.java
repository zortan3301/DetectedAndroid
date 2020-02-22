package com.devian.detected.model.repo;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.devian.detected.model.domain.DataWrapper;
import com.devian.detected.model.domain.tasks.GeoTask;
import com.devian.detected.model.domain.tasks.GeoTextTask;
import com.devian.detected.model.domain.tasks.Tag;
import com.devian.detected.model.domain.tasks.Task;
import com.devian.detected.modules.network.GsonSerializer;
import com.devian.detected.modules.network.NetworkModule;
import com.devian.detected.model.domain.network.ServerResponse;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;
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
    @Getter
    private MutableLiveData<DataWrapper<Tag>> mldAddedTag = new MutableLiveData<>();
    
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
        
        Task task = proceedTask(data, executor);
        if (task == null) {
            mldCompletedTask.setValue(new DataWrapper<>(ServerResponse.TYPE_TASK_FAILURE));
            return;
        }
    
        Map<String, String> headers = NetworkModule.getInstance().proceedHeader(gson.toJson(task));
    
        if (task instanceof GeoTask) {
            Call<ServerResponse> call = NetworkModule.getInstance().getApi().scanGeoTag(headers);
            call.enqueue(getTaskCallback());
        } else {
            Call<ServerResponse> call = NetworkModule.getInstance().getApi().scanGeoTextTag(headers);
            call.enqueue(getTaskCallback());
        }
    }
    
    public void addTag(Tag tag, String admin) {
        Log.d(TAG, "addTag: ");
        
        HashMap<String, String> headers = new HashMap<>();
        headers.put("data", gson.toJson(tag));
        headers.put("admin_id", admin);
        Map<String, String> headersMap = NetworkModule.getInstance().proceedHeaders(headers);
    
        Call<ServerResponse> call = NetworkModule.getInstance().getApi().addTag(headersMap);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call,
                                   @NonNull Response<ServerResponse> response) {
                Log.d(TAG, "onResponse: ======== Response");
                ServerResponse serverResponse = response.body();
                if (serverResponse == null) {
                    Log.e(TAG, "serverResponse == null");
                    return;
                }
                if (serverResponse.getType() == ServerResponse.TYPE_ADD_TAG_SUCCESS) {
                    Log.d(TAG, "onResponse: ======== ServerResponse.TYPE_ADD_TAG_SUCCESS");
                    mldAddedTag.setValue(new DataWrapper<>(null));
                }
                if (serverResponse.getType() == ServerResponse.TYPE_ADD_TAG_ADMIN_FAILURE) {
                    Log.d(TAG, "onResponse: ======== ServerResponse.TYPE_ADD_TAG_ADMIN_FAILURE");
                    mldAddedTag.setValue(new DataWrapper<>(ServerResponse.TYPE_ADD_TAG_ADMIN_FAILURE));
                }
            }
    
            @Override
            public void onFailure(@NonNull Call<ServerResponse> call,
                                  @NonNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                addTag(tag, admin);
            }
        });
    }
    
    private Callback<ServerResponse> getTaskCallback() {
        Log.d(TAG, "getTaskCallback: ");
        return new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call,
                                   @NonNull Response<ServerResponse> response) {
                Log.d(TAG, "onResponse: ======== Response");
                ServerResponse serverResponse = response.body();
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
    }
    
    
    public static Task proceedTask(String data, String executor) {
        Log.d(TAG, "proceedTask: ");
        Uri uri = Uri.parse(data);
        String tagType_param = uri.getQueryParameter("tag_type");
        String tagId_param = uri.getQueryParameter("tag_id");
    
        if (tagId_param == null || tagType_param == null) {
            Log.d(TAG, "scanTag: ======== tagId_param == null || tagType_param == null");
            return null;
        }
    
        int tagType = Integer.parseInt(tagType_param);
    
        if (tagType != Task.GEO_TAG && tagType != Task.GEO_TEXT_TAG) {
            Log.d(TAG, "scanTag: ======== tagType != Task.GEO_TAG && tagType != Task.GEO_TEXT_TAG");
            return null;
        }
    
        Task task;
        if (tagType == Task.GEO_TAG) {
            task = new GeoTask(tagId_param, executor);
        } else {
            task = new GeoTextTask(tagId_param, executor);
        }
        task.setExecutor(executor);
        return task;
    }
}
