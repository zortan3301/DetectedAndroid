package com.devian.detected.view.profile_tab;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.devian.detected.model.domain.DataWrapper;
import com.devian.detected.model.domain.RankRow;
import com.devian.detected.model.domain.User;
import com.devian.detected.model.domain.UserStats;
import com.devian.detected.model.repo.ProfileRepository;

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

    LiveData<DataWrapper<User>> bindUserInfo() {
        Log.d(TAG, "bindUserInfo: ");
        return repository.getMldUser();
    }

    LiveData<DataWrapper<UserStats>> bindUserStats() {
        Log.d(TAG, "bindUserStats: ");
        return repository.getMldUserStats();
    }

    LiveData<DataWrapper<RankRow>> bindSelfRank() {
        Log.d(TAG, "bindSelfRank: ");
        return repository.getMldSelfRank();
    }

    LiveData<DataWrapper<List<RankRow>>> bindTop10() {
        Log.d(TAG, "bindTop10: ");
        return repository.getMldTop10();
    }

    LiveData<DataWrapper<String>> bindEvent() {
        Log.d(TAG, "bindEvent: ");
        return repository.getMldEvent();
    }

    void updateUserInfo(String uid) {
        Log.d(TAG, "updateUserInfo: ");
        repository.updateMldUser(uid);
    }

    void updateUserStats(String uid) {
        Log.d(TAG, "updateUserStats: ");
        repository.updateMldUserStats(uid);
    }

    void updateSelfRank(String uid) {
        Log.d(TAG, "updateSelfRank: ");
        repository.updateMldSelfRank(uid);
    }

    void updateTop10() {
        Log.d(TAG, "updateTop10: ");
        repository.updateMldTop10();
    }

    void updateEvent() {
        Log.d(TAG, "updateEvent: ");
        repository.updateMldEvent();
    }

    void changeNickname(User user) {
        Log.d(TAG, "changeNickname: ");
        repository.changeDisplayName(user);
    }

    public void updateInformation(String uid) {
        Log.d(TAG, "updateInformation: ");
        updateUserInfo(uid);
        updateUserStats(uid);
        updateSelfRank(uid);
        updateTop10();
        updateEvent();
    }
}
