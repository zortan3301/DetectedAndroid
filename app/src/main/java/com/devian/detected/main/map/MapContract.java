package com.devian.detected.main.map;

import com.devian.detected.utils.domain.Task;

import java.util.ArrayList;

public interface MapContract {

    interface Model {

        void getMarkers();
    }

    interface View {

        void showProgress();

        void hideProgress();

        void displayMarkers(ArrayList<Task> markers);

        void displayError(Throwable t);
    }
}
