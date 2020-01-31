package com.devian.detected.model.repo;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.devian.detected.model.domain.DataWrapper;
import com.devian.detected.model.domain.RankRow;
import com.devian.detected.model.domain.User;
import com.devian.detected.model.domain.UserStats;
import com.devian.detected.modules.network.GsonSerializer;
import com.devian.detected.modules.network.NetworkModule;
import com.devian.detected.model.domain.network.ServerResponse;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("unused")
public class ProfileRepository {
    private static final String TAG = "ProfileRepository";

    private Gson gson = GsonSerializer.getInstance().getGson();
    
    @Getter
    private MutableLiveData<UUID> mldNetworkError = new MutableLiveData<>();
    @Getter
    private MutableLiveData<UUID> mldNetworkSuccess = new MutableLiveData<>();

    @Getter
    private MutableLiveData<DataWrapper<User>> mldUser = new MutableLiveData<>();
    @Getter
    private MutableLiveData<DataWrapper<UserStats>> mldUserStats = new MutableLiveData<>();
    @Getter
    private MutableLiveData<DataWrapper<RankRow>> mldSelfRank = new MutableLiveData<>();
    @Getter
    private MutableLiveData<DataWrapper<List<RankRow>>> mldTop10 = new MutableLiveData<>();
    @Getter
    private MutableLiveData<DataWrapper<String>> mldEvent = new MutableLiveData<>();
    
    public void updateMldUser(String uid) {
        Log.d(TAG, "updateMldUser: ");
        Map<String, String> headers = NetworkModule.getInstance().proceedHeader(uid);
        Call<ServerResponse> call = NetworkModule.getInstance().getApi().getUserInfo(headers);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call,
                                   @NonNull Response<ServerResponse> response) {
                Log.d(TAG, "onResponse: ");
                mldNetworkSuccess.setValue(UUID.randomUUID());
                ServerResponse serverResponse = response.body();
                if (serverResponse == null) {
                    Log.e(TAG, "serverResponse == null");
                    return;
                }
                if (serverResponse.getType() == ServerResponse.TYPE_AUTH_SUCCESS) {
                    User user = gson.fromJson(
                            NetworkModule.getInstance().proceedResponse(serverResponse),
                            User.class);
                    DataWrapper<User> userDataWrapper = new DataWrapper<>(user);
                    mldUser.setValue(userDataWrapper);
                } else {
                    DataWrapper<User> userDataWrapper = new DataWrapper<>(serverResponse.getType());
                    mldUser.setValue(userDataWrapper);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                mldNetworkError.setValue(UUID.randomUUID());
                updateMldUser(uid);
            }
        });
    }
    
    public void updateMldUserStats(String uid) {
        Log.d(TAG, "updateMldUserStats: ");
        Map<String, String> headers = NetworkModule.getInstance().proceedHeader(uid);
        Call<ServerResponse> call = NetworkModule.getInstance().getApi().getUserStats(headers);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call,
                                   @NonNull Response<ServerResponse> response) {
                Log.d(TAG, "onResponse: ");
                mldNetworkSuccess.setValue(UUID.randomUUID());
                ServerResponse serverResponse = response.body();
                if (serverResponse == null) {
                    Log.e(TAG, "serverResponse == null");
                    return;
                }
                if (serverResponse.getType() == ServerResponse.TYPE_STATS_EXISTS) {
                    UserStats userStats = gson.fromJson(
                            NetworkModule.getInstance().proceedResponse(serverResponse),
                            UserStats.class);
                    DataWrapper<UserStats> userStatsDataWrapper = new DataWrapper<>(userStats);
                    mldUserStats.setValue(userStatsDataWrapper);
                } else {
                    DataWrapper<UserStats> userStatsDataWrapper = new DataWrapper<>(serverResponse.getType());
                    mldUserStats.setValue(userStatsDataWrapper);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                mldNetworkError.setValue(UUID.randomUUID());
                updateMldUserStats(uid);
            }
        });
    }
    
    public void updateMldSelfRank(String uid) {
        Log.d(TAG, "updateMldSelfRank: ");
        Map<String, String> headers = NetworkModule.getInstance().proceedHeader(uid);
        Call<ServerResponse> call = NetworkModule.getInstance().getApi().getSelfRank(headers);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call,
                                   @NonNull Response<ServerResponse> response) {
                Log.d(TAG, "onResponse: ");
                mldNetworkSuccess.setValue(UUID.randomUUID());
                ServerResponse serverResponse = response.body();
                if (serverResponse == null) {
                    Log.e(TAG, "serverResponse == null");
                    return;
                }
                if (serverResponse.getType() == ServerResponse.TYPE_RANK_SUCCESS) {
                    RankRow selfRank = gson.fromJson(
                            NetworkModule.getInstance().proceedResponse(serverResponse),
                            RankRow.class);
                    DataWrapper<RankRow> rankRowDataWrapper = new DataWrapper<>(selfRank);
                    mldSelfRank.setValue(rankRowDataWrapper);
                } else {
                    DataWrapper<RankRow> rankRowDataWrapper = new DataWrapper<>(serverResponse.getType());
                    mldSelfRank.setValue(rankRowDataWrapper);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                mldNetworkError.setValue(UUID.randomUUID());
                updateMldSelfRank(uid);
            }
        });
    }
    
    public void updateMldTop10() {
        Log.d(TAG, "updateMldTop10: ");
        Call<ServerResponse> call = NetworkModule.getInstance().getApi().getRankTop10();
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call,
                                   @NonNull Response<ServerResponse> response) {
                Log.d(TAG, "onResponse: ");
                mldNetworkSuccess.setValue(UUID.randomUUID());
                ServerResponse serverResponse = response.body();
                if (serverResponse == null) {
                    Log.e(TAG, "serverResponse == null");
                    return;
                }
                if (serverResponse.getType() == ServerResponse.TYPE_RANK_SUCCESS) {
                    List<RankRow> top10 = Arrays.asList(gson.fromJson(
                            NetworkModule.getInstance().proceedResponse(serverResponse),
                            RankRow[].class));
                    DataWrapper<List<RankRow>> top10DataWrapper = new DataWrapper<>(top10);
                    mldTop10.setValue(top10DataWrapper);
                } else {
                    DataWrapper<List<RankRow>> top10DataWrapper = new DataWrapper<>(serverResponse.getType());
                    mldTop10.setValue(top10DataWrapper);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                mldNetworkError.setValue(UUID.randomUUID());
                updateMldTop10();
            }
        });
    }
    
    public void updateMldEvent() {
        Log.d(TAG, "updateMldEvent: ");
        Call<ServerResponse> call = NetworkModule.getInstance().getApi().getEvent();
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call,
                                   @NonNull Response<ServerResponse> response) {
                Log.d(TAG, "onResponse: ");
                mldNetworkSuccess.setValue(UUID.randomUUID());
                ServerResponse serverResponse = response.body();
                if (serverResponse == null) {
                    Log.e(TAG, "serverResponse == null");
                    return;
                }
                if (serverResponse.getType() == ServerResponse.TYPE_TASK_SUCCESS) {
                    String event = NetworkModule.getInstance().proceedResponse(serverResponse);
                    DataWrapper<String> eventDataWrapper = new DataWrapper<>(event);
                    mldEvent.setValue(eventDataWrapper);
                } else {
                    DataWrapper<String> eventDataWrapper = new DataWrapper<>(serverResponse.getType());
                    mldEvent.setValue(eventDataWrapper);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                mldNetworkError.setValue(UUID.randomUUID());
                updateMldEvent();
            }
        });
    }
    
    public void changeDisplayName(User user) {
        Log.d(TAG, "changeDisplayName: ");
        Map<String, String> headers = NetworkModule.getInstance().proceedHeader(gson.toJson(user));
        Call<ServerResponse> call = NetworkModule.getInstance().getApi().changeNickname(headers);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call,
                                   @NonNull Response<ServerResponse> response) {
                Log.d(TAG, "onResponse: ");
                mldNetworkSuccess.setValue(UUID.randomUUID());
                ServerResponse serverResponse = response.body();
                if (serverResponse == null) {
                    Log.e(TAG, "serverResponse == null");
                    return;
                }
                if (serverResponse.getType() == ServerResponse.TYPE_CHANGE_NICKNAME_SUCCESS) {
                    User user = gson.fromJson(
                            NetworkModule.getInstance().proceedResponse(serverResponse.getData()),
                            User.class);
                    DataWrapper<User> userDataWrapper = new DataWrapper<>(user);
                    mldUser.setValue(userDataWrapper);
                }
                if (serverResponse.getType() == ServerResponse.TYPE_CHANGE_NICKNAME_EXISTS) {
                    DataWrapper<User> userDataWrapper = new DataWrapper<>(ServerResponse.TYPE_CHANGE_NICKNAME_EXISTS);
                    mldUser.setValue(userDataWrapper);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                mldNetworkError.setValue(UUID.randomUUID());
            }
        });
    }
}
