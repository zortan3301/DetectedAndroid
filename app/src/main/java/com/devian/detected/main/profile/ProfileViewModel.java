package com.devian.detected.main.profile;

import android.app.Application;

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

    private ProfileRepository repository;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        repository = new ProfileRepository();
    }

    LiveData<DataWrapper<User>> getUserInfo(String uid) {
        return repository.getMldUser(uid);
    }

    LiveData<DataWrapper<UserStats>> getUserStats(String uid) {
        return repository.getMldUserStats(uid);
    }

    LiveData<DataWrapper<RankRow>> getSelfRank(String uid) {
        return repository.getMldSelfRank(uid);
    }

    LiveData<DataWrapper<List<RankRow>>> getTop10() {
        return repository.getMldTop10();
    }

    LiveData<DataWrapper<String>> getEvent() {
        return repository.getMldEvent();
    }

    LiveData<DataWrapper<User>> changeNickname(User user) {
        return repository.changeDisplayName(user);
    }
}
