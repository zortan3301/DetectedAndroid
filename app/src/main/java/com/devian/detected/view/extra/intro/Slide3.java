package com.devian.detected.view.extra.intro;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.devian.detected.R;
import com.github.paolorotolo.appintro.ISlidePolicy;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Slide3 extends Fragment implements ISlidePolicy {
    
    private static final String TAG = "Slide3";
    
    @BindView(R.id.intro_cbRules)
    CheckBox checkBox;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.intro_slide_3, container, false);
        ButterKnife.bind(this, v);
        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                checkBox.setTextColor(Color.WHITE);
            }
        });
        return v;
    }
    
    @Override
    public boolean isPolicyRespected() {
        Log.d(TAG, "isPolicyRespected: ");
        return checkBox.isChecked();
    }
    
    @Override
    public void onUserIllegallyRequestedNextPage() {
        checkBox.setTextColor(Color.RED);
    }
}
