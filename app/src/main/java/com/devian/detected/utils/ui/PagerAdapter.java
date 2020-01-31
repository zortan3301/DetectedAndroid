package com.devian.detected.utils.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.devian.detected.view.interfaces.OnLogoutListener;
import com.devian.detected.view.interfaces.OnTaskItemSelectedListener;
import com.devian.detected.view.map_tab.MapFragment;
import com.devian.detected.view.profile_tab.ProfileFragment;
import com.devian.detected.view.tasks_tab.TaskFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    
    private OnLogoutListener logoutListener;
    private OnTaskItemSelectedListener taskItemSelectedListener;
    
    public PagerAdapter(FragmentActivity context) {
        super(context.getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        logoutListener = (OnLogoutListener) context;
        taskItemSelectedListener = (OnTaskItemSelectedListener) context;
    }
    
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ProfileFragment profileFragment = new ProfileFragment();
                profileFragment.setOnLogoutListener(logoutListener);
                return profileFragment;
            case 1:
                return new MapFragment();
            case 2:
                TaskFragment taskFragment = new TaskFragment();
                taskFragment.setOnTaskItemSelectedListener(taskItemSelectedListener);
                return taskFragment;
        }
        return new ProfileFragment();
    }
    
    @Override
    public int getCount() {
        return 3;
    }
    
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Профиль";
            case 1:
                return "Карта";
            case 2:
                return "Задачи";
        }
        return "";
    }
}
