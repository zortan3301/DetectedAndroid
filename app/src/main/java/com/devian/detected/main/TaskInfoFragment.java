package com.devian.detected.main;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
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
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskInfoFragment extends Fragment {
    
    private static final String TAG = "TaskInfoFragment";
    
    private Gson gson = new Gson();
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
        Bundle args = new Bundle();
        args.putSerializable("task", task);
        TaskInfoFragment fragment = new TaskInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            task = (Task) savedInstanceState.getSerializable("task");
        }
        if (getArguments() != null) {
            task = (Task) getArguments().getSerializable("task");
        }
        View v = inflater.inflate(R.layout.fragment_taskinfo, container, false);
        ButterKnife.bind(this, v);
        mAuth = FirebaseAuth.getInstance();
        
        tvTitle.setText(task.getTitle());
        tvReward.setText(String.valueOf(task.getReward()));
        tvDescription.setText(getLinuxLikeCommand());
        
        imageView.setZoomable(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.with(getContext())
                .load(task.getImgUrl())
                .into(imageView);
        
        return v;
    }
    
    @Override
    public void onResume() {
        super.onResume();
    }
    
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("task", task);
    }
    
    private SpannableStringBuilder getLinuxLikeCommand() {
        try {
            String email = mAuth.getCurrentUser().getEmail();
            String nickname = email.split("@")[0];
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
            return new SpannableStringBuilder(task.getDescription());
        }
    }
    
}
