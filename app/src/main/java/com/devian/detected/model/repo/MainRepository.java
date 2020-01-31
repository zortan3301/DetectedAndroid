package com.devian.detected.model.repo;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.devian.detected.model.domain.DataWrapper;
import com.devian.detected.model.domain.User;
import com.devian.detected.model.domain.network.ServerResponse;
import com.devian.detected.modules.network.GsonSerializer;
import com.devian.detected.modules.network.NetworkModule;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("unused")
public class MainRepository {

    private static final String TAG = "MainRepository";
    
    private Gson gson = GsonSerializer.getInstance().getGson();
    
    @Getter
    private MutableLiveData<UUID> mldNetworkError = new MutableLiveData<>();
    @Getter
    private MutableLiveData<UUID> mldNetworkSuccess = new MutableLiveData<>();
    
    @Getter
    private MutableLiveData<DataWrapper<User>> mldSignedUser = new MutableLiveData<>();
    
    public void authUser(FirebaseUser user) {
        Log.d(TAG, "authUser: ");
        User mUser = new User(
                user.getUid(),
                user.getDisplayName(),
                user.getEmail()
        );
        Map<String, String> headers = NetworkModule.getInstance().proceedHeader(gson.toJson(mUser));
        Call<ServerResponse> call = NetworkModule.getInstance().getApi().auth(headers);
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
                    mldSignedUser.setValue(userDataWrapper);
                }
                DataWrapper<User> userDataWrapper = new DataWrapper<>(serverResponse.getType());
                mldSignedUser.setValue(userDataWrapper);
            }
    
            @Override
            public void onFailure(@NonNull Call<ServerResponse> call,
                                  @NonNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                mldNetworkError.setValue(UUID.randomUUID());
                DataWrapper<User> userDataWrapper = new DataWrapper<>(ServerResponse.TYPE_AUTH_FAILURE);
                mldSignedUser.setValue(userDataWrapper);
                authUser(user);
            }
        });
    }
}
