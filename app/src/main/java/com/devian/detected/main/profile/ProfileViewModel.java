package com.devian.detected.main.profile;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.devian.detected.utils.domain.DataWrapper;
import com.devian.detected.utils.domain.RankRow;
import com.devian.detected.utils.domain.User;
import com.devian.detected.utils.domain.UserStats;

import java.util.List;

@SuppressWarnings("unused")
public class ProfileViewModel extends AndroidViewModel {

    private static final String TAG = "ProfileViewModel";

    private ProfileRepository repository;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "ProfileViewModel: ");
        repository = new ProfileRepository();
    }

    LiveData<DataWrapper<User>> getUserInfo(String uid) {
        Log.d(TAG, "getUserInfo: ");
        return repository.getMldUser(uid);
    }

    LiveData<DataWrapper<UserStats>> getUserStats(String uid) {
        Log.d(TAG, "getUserStats: ");
        return repository.getMldUserStats(uid);
    }

    LiveData<DataWrapper<RankRow>> getSelfRank(String uid) {
        Log.d(TAG, "getSelfRank: ");
        return repository.getMldSelfRank(uid);
    }

    LiveData<DataWrapper<List<RankRow>>> getTop10() {
        Log.d(TAG, "getTop10: ");
        return repository.getMldTop10();
    }

    LiveData<DataWrapper<String>> getEvent() {
        Log.d(TAG, "getEvent: ");
        return repository.getMldEvent();
    }

    LiveData<DataWrapper<User>> changeNickname(User user) {
        Log.d(TAG, "changeNickname: ");
        return repository.changeDisplayName(user);
    }
}
