package com.devian.detected.main;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.devian.detected.R;
import com.devian.detected.utils.domain.Task;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskInfoFragment extends Fragment {
    
    private static final String TAG = "TaskInfoFragment";
    
    private FirebaseAuth mAuth;
    
    private Task task;
    
    @BindView(R.id.taskinfo_image)
    PhotoView imageView;
    @BindView(R.id.taskinfo_tvTitle)
    TextView tvTitle;
    @BindView(R.id.taskinfo_tvReward)
    TextView tvReward;
    @BindView(R.id.taskinfo_tvDescription)
    TextView tvDescription;
    
    public static TaskInfoFragment newInstance(Task task) {
        Log.d(TAG, "newInstance");
        Bundle args = new Bundle();
        args.putParcelable("task", task);
        TaskInfoFragment fragment = new TaskInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        if (savedInstanceState != null) {
            task = savedInstanceState.getParcelable("task");
        }
        if (getArguments() != null) {
            task = getArguments().getParcelable("task");
        }
        View v = inflater.inflate(R.layout.fragment_taskinfo, container, false);
        ButterKnife.bind(this, v);
        mAuth = FirebaseAuth.getInstance();
        
        tvTitle.setText(task.getTitle());
        tvReward.setText(String.valueOf(task.getReward()));
        tvDescription.setText(getLinuxLikeCommand());
        
        imageView.setZoomable(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.get()
                .load(task.getImgUrl())
                .into(imageView);
        
        return v;
    }
    
    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }
    
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putParcelable("task", task);
    }
    
    private SpannableStringBuilder getLinuxLikeCommand() {
        Log.d(TAG, "getLinuxLikeCommand");
        SpannableStringBuilder defaultString = new SpannableStringBuilder(task.getDescription());
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null)
            return defaultString;
        try {
            String email = currentUser.getEmail();
            if (email == null)
                return defaultString;
            String[] split = email.split("@");
            if (split[0] == null)
                return defaultString;
            String nickname = split[0];
            String command = nickname + "@detected:~$> cat task_" + task.getId() + ".txt\n\n" + task.getDescription();
            int prefix = nickname.length() + 9;
            SpannableStringBuilder spannable = new SpannableStringBuilder(command);
            spannable.setSpan(
                    new ForegroundColorSpan(Color.YELLOW),
                    0,
                    prefix,
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            );
            spannable.setSpan(
                    new ForegroundColorSpan(Color.BLUE),
                    prefix + 1,
                    prefix + 2,
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            );
            return spannable;
        } catch (Exception e) {
            e.printStackTrace();
            return defaultString;
        }
    }
    
}
