package com.devian.detected.main.task_list;

import com.devian.detected.utils.domain.Task;

import java.util.ArrayList;

public interface TaskListContract {

    interface Model {

        void getTasks();
    }

    interface View {

        void showProgress();

        void hideProgress();

        void displayTasks(ArrayList<Task> taskList);

        void displayError(String s);
    }

    interface OnTaskItemClickListener {
        void OnItemClicked(Task task);
    }
}
