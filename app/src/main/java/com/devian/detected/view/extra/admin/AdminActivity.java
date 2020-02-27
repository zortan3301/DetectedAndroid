package com.devian.detected.view.extra.admin;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;

public class AdminActivity extends AppIntro {
    
    private static final String TAG = "AdminActivity";
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    
        Bundle bundle = getIntent().getExtras();
    
        assert bundle != null;
        addSlide(new OptionScan(bundle.getString("admin")));
        addSlide(new OptionMap());
        addSlide(new OptionResult(bundle.getString("admin")));
    
        setVibrate(true);
        setVibrateIntensity(30);
    }
    
    @Override
    public void onSkipPressed(Fragment currentFragment) {
        Log.d(TAG, "onSkipPressed: ");
        super.onSkipPressed(currentFragment);
        finish();
    }
    
    @Override
    public void onDonePressed(Fragment currentFragment) {
        Log.d(TAG, "onDonePressed: ");
        super.onDonePressed(currentFragment);
        finish();
    }
    
    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        Log.d(TAG, "onSlideChanged: ");
        super.onSlideChanged(oldFragment, newFragment);
        if (oldFragment instanceof OptionScan && newFragment instanceof OptionMap) {
            oldFragment.onPause();
            ((OptionMap) newFragment).setTag(((OptionScan) oldFragment).tag);
        }
        if (oldFragment instanceof OptionMap && newFragment instanceof OptionResult) {
            ((OptionResult) newFragment).setTag(((OptionMap) oldFragment).tag);
        }
        if (oldFragment instanceof OptionMap && newFragment instanceof OptionScan) {
            newFragment.onResume();
        }
    }
}
