package com.devian.detected.main;

import android.os.Bundle;
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
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskInfoFragment extends Fragment {
    
    private static final String TAG = "TaskInfoFragment";
    
    private Gson gson = new Gson();
    
    private Task task;
    
    @BindView(R.id.taskinfo_image)
    PhotoView imageView;
    @BindView(R.id.taskinfo_tvTitle)
    TextView tvTitle;
    @BindView(R.id.taskinfo_tvReward)
    TextView tvReward;
    @BindView(R.id.taskinfo_tvDescription)
    TextView tvDescription;
    
    public TaskInfoFragment(Task task) {
        this.task = task;
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_taskinfo, container, false);
        ButterKnife.bind(this, v);
        
        tvTitle.setText(task.getTitle());
        tvReward.setText(String.valueOf(task.getReward()));
        tvDescription.setText(task.getDescription());
        
        imageView.setZoomable(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.with(getContext())
                .load(task.getImgUrl())
                .into(imageView);
        
        return v;
    }
    
}
