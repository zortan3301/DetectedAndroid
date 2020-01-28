package com.devian.detected.main.task_list;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.devian.detected.utils.domain.DataWrapper;
import com.devian.detected.utils.domain.Task;
import com.devian.detected.utils.network.GsonSerializer;
import com.devian.detected.utils.network.NetworkManager;
import com.devian.detected.utils.network.ServerResponse;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class TaskRepository {
    private static final String TAG = "TaskRepository";

    private Gson gson = GsonSerializer.getInstance().getGson();

    private MutableLiveData<DataWrapper<List<Task>>> mldTaskList = new MutableLiveData<>();

    MutableLiveData<DataWrapper<List<Task>>> getMldTaskList() {
        Log.d(TAG, "getMldTaskList: ");
        return mldTaskList;
    }

    void updateTaskList() {
        Log.d(TAG, "updateTaskList: ");
        Call<ServerResponse> call = NetworkManager.getInstance().getApi().getTextTasks();
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
                    List<Task> taskList = Arrays.asList(gson.fromJson(
                            NetworkManager.getInstance().proceedResponse(serverResponse),
                            Task[].class));
                    DataWrapper<List<Task>> taskListDataWrapper = new DataWrapper<>(taskList);
                    mldTaskList.setValue(taskListDataWrapper);
                } else {
                    DataWrapper<List<Task>> taskListDataWrapper = new DataWrapper<>(serverResponse.getType());
                    mldTaskList.setValue(taskListDataWrapper);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: ");
            }
        });
    }
}
