package com.devian.detected.view.tasks_tab;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.devian.detected.R;
import com.devian.detected.model.domain.tasks.GeoTextTask;
import com.devian.detected.utils.ui.popups.DefaultPopup;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskInfoFragment extends Fragment implements View.OnClickListener {
    
    private static final String TAG = "TaskInfoFragment";
    
    private final static int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 100;
    
    private FirebaseAuth mAuth;
    
    private GeoTextTask task;
    
    @BindView(R.id.taskinfo_image)
    PhotoView imageView;
    @BindView(R.id.taskinfo_tvTitle)
    TextView tvTitle;
    @BindView(R.id.taskinfo_tvReward)
    TextView tvReward;
    @BindView(R.id.taskinfo_tvDescription)
    TextView tvDescription;
    @BindView(R.id.taskinfo_btnDownload)
    ImageView btnDownload;
    @BindView(R.id.taskinfo_btnBack)
    ImageView btnBack;
    
    public static TaskInfoFragment newInstance(GeoTextTask task) {
        Log.d(TAG, "newInstance");
        Bundle args = new Bundle();
        args.putParcelable("task", task);
        TaskInfoFragment fragment = new TaskInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            task = getArguments().getParcelable("task");
        else
            task = new GeoTextTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_taskinfo, container, false);
        ButterKnife.bind(this, v);
        mAuth = FirebaseAuth.getInstance();
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            task = savedInstanceState.getParcelable("task");
        }
        setupView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putParcelable("task", task);
    }

    private void setupView() {
        tvTitle.setText(task.getTitle());
        tvReward.setText(String.valueOf(task.getReward()));
        tvDescription.setText(getLinuxLikeCommand());

        imageView.setZoomable(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.get()
                .load(task.getImgUrl())
                .into(imageView);

        btnDownload.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: ");
        switch (view.getId()) {
            case R.id.taskinfo_btnDownload:
                onSaveButtonClick();
                break;
            case R.id.taskinfo_btnBack:
                onBackButtonClick();
        }
    }
    
    private Date lastRefresh = new Date(1);
    
    private boolean isRefreshAvailable() {
        Date currTime = new Date();
        if (currTime.getTime() - lastRefresh.getTime() >= getResources().getInteger(R.integer.refresh_delay)) {
            lastRefresh = currTime;
            return true;
        } else {
            return false;
        }
    }

    private void onBackButtonClick() {
        Log.d(TAG, "onBackButtonClick: ");
        getActivityNonNull().getSupportFragmentManager().popBackStack();
    }

    private void onSaveButtonClick() {
        Log.d(TAG, "onSaveButtonClick: ");
        if (!isRefreshAvailable()) {
            return;
        }
        if (getActivityNonNull().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            DefaultPopup popupPermissions =
                    new DefaultPopup(
                            getResources().getString(R.string.storage_permission),
                            getActivityNonNull());
            popupPermissions.setButtonsText(getResources().getString(R.string.allow), getString(R.string.restrict));
            popupPermissions.getPositiveOption().setOnClickListener(v -> {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                popupPermissions.dismiss();
            });
            popupPermissions.getNegativeOption().setOnClickListener(v -> {
                Toast.makeText(getActivityNonNull(),
                        getResources().getString(R.string.storage_permission_denied),
                        Toast.LENGTH_LONG).show();
                popupPermissions.dismiss();
            });
            popupPermissions.show();
        } else {
            saveImage();
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImage();
            } else {
                Toast.makeText(getActivityNonNull(),
                        getResources().getString(R.string.storage_permission_denied),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private void saveImage() {
        Log.d(TAG, "saveImage: ");
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        MediaStore.Images.Media.insertImage(
                getActivityNonNull().getContentResolver(),
                bitmap,
                task.getTitle() + "_" + UUID.randomUUID(),
                task.getDescription()
        );
        Toast.makeText(getActivityNonNull(),
                getResources().getString(R.string.image_saved),
                Toast.LENGTH_SHORT).show();
    }

    private SpannableStringBuilder getLinuxLikeCommand() {
        Log.d(TAG, "getLinuxLikeCommand: ");
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
            String command = nickname + "@detected:~$> cat task" + ".txt\n\n" + task.getDescription();
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

    private FragmentActivity getActivityNonNull() {
        if (super.getActivity() != null) {
            return super.getActivity();
        } else {
            throw new RuntimeException("null returned from getActivity()");
        }
    }
}
