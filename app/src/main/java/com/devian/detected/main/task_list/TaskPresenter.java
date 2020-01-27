package com.devian.detected.main.task_list;

import android.util.Log;

import androidx.annotation.NonNull;

import com.devian.detected.utils.network.GsonSerializer;
import com.devian.detected.utils.network.NetworkManager;
import com.devian.detected.utils.network.ServerResponse;
import com.devian.detected.utils.domain.Task;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class TaskPresenter implements TaskListContract.Model {

    private static final String TAG = "TaskPresenter";

    private TaskListContract.View taskListView;
    private Gson gson = GsonSerializer.getInstance().getGson();

    TaskPresenter(TaskListContract.View taskListView) {
        this.taskListView = taskListView;
    }

    @Override
    public void getTasks() {
        Log.d(TAG, "getTasks");
        getObservable().subscribeWith(getObserver());
    }

    private Observable<ServerResponse> getObservable() {
        Log.d(TAG, "getObservable");
        return NetworkManager.getInstance().getApi()
                .getTextTasks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<ServerResponse> getObserver() {
        Log.d(TAG, "getObserver");
        return new DisposableObserver<ServerResponse>() {
            @Override
            public void onNext(@NonNull ServerResponse response) {
                Log.d(TAG, "onNext:" + response.getData());
                List<Task> tasks = Arrays.asList(gson.fromJson(
                        NetworkManager.getInstance().proceedResponse(response.getData()),
                        Task[].class));
                taskListView.displayTasks(new ArrayList<>(tasks));
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG, "onError: ", e);
                taskListView.displayError(e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };
    }
}
