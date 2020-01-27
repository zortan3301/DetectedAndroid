package com.devian.detected.main.map;

import android.util.Log;

import com.devian.detected.utils.domain.Task;
import com.devian.detected.utils.network.GsonSerializer;
import com.devian.detected.utils.network.NetworkManager;
import com.devian.detected.utils.network.ServerResponse;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MapPresenter implements MapContract.Model {

    private static final String TAG = "MapPresenter";

    private MapContract.View mapView;
    private Gson gson = GsonSerializer.getInstance().getGson();

    MapPresenter(MapContract.View mapView) {
        this.mapView = mapView;
    }

    @Override
    public void getMarkers() {
        Log.d(TAG, "getMarkers");
        getObservable().subscribeWith(getObserver());
    }

    private Observable<ServerResponse> getObservable() {
        Log.d(TAG, "getObservable");
        return NetworkManager.getInstance().getApi()
                .getMapTasks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<ServerResponse> getObserver() {
        Log.d(TAG, "getObserver");
        return new DisposableObserver<ServerResponse>() {
            @Override
            public void onNext(ServerResponse serverResponse) {
                Log.d(TAG, "onNext");
                List<Task> markers = Arrays.asList(gson.fromJson(
                        NetworkManager.getInstance().proceedResponse(serverResponse.getData()),
                        Task[].class));
                mapView.displayMarkers(new ArrayList<>(markers));
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ", e);
                mapView.displayError(e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };
    }


}
