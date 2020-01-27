package com.devian.detected.main.profile;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.devian.detected.utils.domain.DataWrapper;
import com.devian.detected.utils.domain.RankRow;
import com.devian.detected.utils.domain.User;
import com.devian.detected.utils.domain.UserStats;
import com.devian.detected.utils.network.GsonSerializer;
import com.devian.detected.utils.network.NetworkManager;
import com.devian.detected.utils.network.ServerResponse;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("unused")
class ProfileRepository {
    private static final String TAG = "ProfileRepository";

    private Gson gson = GsonSerializer.getInstance().getGson();

    private MutableLiveData<DataWrapper<User>> mldUser = new MutableLiveData<>();
    private MutableLiveData<DataWrapper<UserStats>> mldUserStats = new MutableLiveData<>();
    private MutableLiveData<DataWrapper<RankRow>> mldSelfRank = new MutableLiveData<>();
    private MutableLiveData<DataWrapper<List<RankRow>>> mldTop10 = new MutableLiveData<>();
    private MutableLiveData<DataWrapper<String>> mldEvent = new MutableLiveData<>();

    MutableLiveData<DataWrapper<User>> getMldUser(String uid) {
        Log.d(TAG, "getMldUser: ");
        Map<String, String> headers = NetworkManager.getInstance().proceedHeader(uid);
        Call<ServerResponse> call = NetworkManager.getInstance().getApi().getUserInfo(headers);
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
                if (serverResponse.getType() == ServerResponse.TYPE_AUTH_SUCCESS) {
                    User user = gson.fromJson(
                            NetworkManager.getInstance().proceedResponse(serverResponse),
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
            }
        });
        return mldUser;
    }

    MutableLiveData<DataWrapper<UserStats>> getMldUserStats(String uid) {
        Log.d(TAG, "getMldUserStats: ");
        Map<String, String> headers = NetworkManager.getInstance().proceedHeader(uid);
        Call<ServerResponse> call = NetworkManager.getInstance().getApi().getUserStats(headers);
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
                if (serverResponse.getType() == ServerResponse.TYPE_STATS_EXISTS) {
                    UserStats userStats = gson.fromJson(
                            NetworkManager.getInstance().proceedResponse(serverResponse),
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
            }
        });
        return mldUserStats;
    }

    MutableLiveData<DataWrapper<RankRow>> getMldSelfRank(String uid) {
        Log.d(TAG, "getMldSelfRank: ");
        Map<String, String> headers = NetworkManager.getInstance().proceedHeader(uid);
        Call<ServerResponse> call = NetworkManager.getInstance().getApi().getSelfRank(headers);
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
                if (serverResponse.getType() == ServerResponse.TYPE_RANK_SUCCESS) {
                    RankRow selfRank = gson.fromJson(
                            NetworkManager.getInstance().proceedResponse(serverResponse),
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
            }
        });
        return mldSelfRank;
    }

    MutableLiveData<DataWrapper<List<RankRow>>> getMldTop10() {
        Log.d(TAG, "getMldTop10: ");
        Call<ServerResponse> call = NetworkManager.getInstance().getApi().getRankTop10();
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
                if (serverResponse.getType() == ServerResponse.TYPE_RANK_SUCCESS) {
                    List<RankRow> top10 = Arrays.asList(gson.fromJson(
                            NetworkManager.getInstance().proceedResponse(serverResponse),
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
            }
        });
        return mldTop10;
    }

    MutableLiveData<DataWrapper<String>> getMldEvent() {
        Log.d(TAG, "getMldEvent: ");
        Call<ServerResponse> call = NetworkManager.getInstance().getApi().getEvent();
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
                    String event = NetworkManager.getInstance().proceedResponse(serverResponse);
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
            }
        });
        return mldEvent;
    }

    MutableLiveData<DataWrapper<User>> changeDisplayName(User user) {
        Log.d(TAG, "changeDisplayName: ");
        Map<String, String> headers = NetworkManager.getInstance().proceedHeader(gson.toJson(user));
        Call<ServerResponse> call = NetworkManager.getInstance().getApi().changeNickname(headers);
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
                if (serverResponse.getType() == ServerResponse.TYPE_CHANGE_NICKNAME_SUCCESS) {
                    User user = gson.fromJson(
                            NetworkManager.getInstance().proceedResponse(serverResponse.getData()),
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
            }
        });
        return mldUser;
    }
}
