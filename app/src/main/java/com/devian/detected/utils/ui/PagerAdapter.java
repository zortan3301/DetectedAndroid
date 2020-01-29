package com.devian.detected.utils.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.devian.detected.view.map_tab.MapFragment;
import com.devian.detected.view.profile_tab.ProfileFragment;
import com.devian.detected.view.tasks_tab.TaskFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    
    public PagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }
    
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ProfileFragment();
            case 1:
                return new MapFragment();
            case 2:
                return new TaskFragment();
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
