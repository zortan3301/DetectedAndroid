package com.devian.detected.main.map;

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

class MapRepository {

    private static final String TAG = "MapRepository";

    private Gson gson = GsonSerializer.getInstance().getGson();

    private MutableLiveData<DataWrapper<List<Task>>> mldMarkers = new MutableLiveData<>();

    MutableLiveData<DataWrapper<List<Task>>> getMldMarkers() {
        Log.d(TAG, "getMldMarkers: ");
        return mldMarkers;
    }

    void updateMldMarkers() {
        Log.d(TAG, "updateMldMarkers: ");
        Call<ServerResponse> call = NetworkManager.getInstance().getApi().getMapTasks();
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
                    List<Task> markers = Arrays.asList(gson.fromJson(
                            NetworkManager.getInstance().proceedResponse(serverResponse),
                            Task[].class));
                    DataWrapper<List<Task>> markersDataWrapper = new DataWrapper<>(markers);
                    mldMarkers.setValue(markersDataWrapper);
                } else {
                    DataWrapper<List<Task>> markersDataWrapper = new DataWrapper<>(serverResponse.getType());
                    mldMarkers.setValue(markersDataWrapper);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

}
