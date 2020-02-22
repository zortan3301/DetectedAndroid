package com.devian.detected.view;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.devian.detected.model.domain.DataWrapper;
import com.devian.detected.model.domain.User;
import com.devian.detected.model.domain.tasks.Tag;
import com.devian.detected.model.domain.tasks.Task;
import com.devian.detected.model.repo.MainRepository;
import com.devian.detected.model.repo.TaskRepository;
import com.google.firebase.auth.FirebaseUser;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = "MainViewModel";

    private MainRepository mainRepository;
    private TaskRepository taskRepository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "MainViewModel: ");
        taskRepository = new TaskRepository();
        mainRepository = new MainRepository();
    }

    LiveData<DataWrapper<Task>> bindCompletedTask() {
        Log.d(TAG, "bindCompletedTask: ");
        return taskRepository.getMldCompletedTask();
    }

    void proceedTag(String data, String executor) {
        Log.d(TAG, "proceedTask: ");
        taskRepository.scanTag(data, executor);
    }
    
    public LiveData<DataWrapper<User>> bindSignedUser() {
        Log.d(TAG, "bindSignedUser: ");
        return mainRepository.getMldSignedUser();
    }

    public void authUserOnServer(FirebaseUser user) {
        Log.d(TAG, "authUserOnServer: ");
        mainRepository.authUser(user);
    }
    
    public LiveData<DataWrapper<Tag>> bindAddedTag() {
        Log.d(TAG, "bindAddedTag: ");
        return taskRepository.getMldAddedTag();
    }
    
    public void addTag(Tag tag, String admin) {
        Log.d(TAG, "addTag: ");
        taskRepository.addTag(tag, admin);
    }
}
