package com.devian.detected;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.devian.detected.main.ScanActivity;
import com.devian.detected.main.task_list.TaskFragment;
import com.devian.detected.main.TaskInfoFragment;
import com.devian.detected.model.domain.tasks.GeoTask;
import com.devian.detected.model.domain.tasks.GeoTextTask;
import com.devian.detected.model.domain.tasks.Task;
import com.devian.detected.modules.network.GsonSerializer;
import com.devian.detected.modules.network.NetworkModule;
import com.devian.detected.modules.network.domain.ServerResponse;
import com.devian.detected.modules.security.SecurityModule;
import com.devian.detected.utils.ui.CustomViewPager;
import com.devian.detected.utils.ui.PagerAdapter;
import com.devian.detected.utils.ui.DefaultPopup;
import com.devian.detected.utils.ui.ResultPopup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParentFragment extends AppCompatActivity
        implements
        View.OnClickListener,
        TaskFragment.OnTaskItemSelectedListener {
    
    private static final String TAG = "ParentFragment";
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_SCAN_CODE = 200;
    
    private Gson gson = GsonSerializer.getInstance().getGson();
    
    private FirebaseUser firebaseUser;
    
    @BindView(R.id.fab_qr) FloatingActionButton fab_qr;
    @BindView(R.id.tab_layout) TabLayout tabLayout;
    @BindView(R.id.pager) CustomViewPager viewPager;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        
        ButterKnife.bind(this);
    
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        
        tabLayout.setupWithViewPager(viewPager);
    
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        
        fab_qr.setOnClickListener(this);
        
    }
    
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_qr) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                DefaultPopup popupPermissions =
                        new DefaultPopup(
                                getResources().getString(R.string.camera_permission),
                                this);
                popupPermissions.getPositiveOption().setOnClickListener(v -> {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                    popupPermissions.dismiss();
                });
                popupPermissions.getNegativeOption().setOnClickListener(v -> {
                    showToast(
                            getResources().getString(R.string.camera_permission_denied),
                            Toast.LENGTH_LONG);
                    popupPermissions.dismiss();
                });
                popupPermissions.show();
            } else {
                Intent i = new Intent(this, ScanActivity.class);
                startActivityForResult(i, CAMERA_SCAN_CODE);
            }
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_SCAN_CODE) {
            if(resultCode == Activity.RESULT_OK){
                if (data != null) {
                    String result = data.getStringExtra("result");
                    proceedTask(result);
                }
            }
        }
    }
    
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent(this, ScanActivity.class);
                startActivityForResult(i, 1);
            } else {
                showToast(
                        getResources().getString(R.string.camera_permission_denied),
                        Toast.LENGTH_LONG);
            }
        }
    }
    
    public void proceedTask(String result) {
        Uri uri = Uri.parse(result);
        String tagType_param = uri.getQueryParameter("tag_type");
        String tagId_param = uri.getQueryParameter("tag_id");
        
        if (tagId_param == null || tagType_param == null) {
            showPopup(ResultPopup.RESULT_FAILURE, 0);
            return;
        }
        
        int tagType = Integer.parseInt(tagId_param);
        
        if (tagType != Task.GEO_TAG && tagType != Task.GEO_TEXT_TAG) {
            showPopup(ResultPopup.RESULT_FAILURE, 0);
        }
    
        String executor = firebaseUser.getUid();
        Task task;
        if (tagType == Task.GEO_TAG) {
            task = new GeoTask(tagId_param, executor);
        } else {
            task = new GeoTextTask(tagId_param, executor);
        }

        String user_data = gson.toJson(task);
    
        Map<String, String> headers = new HashMap<>();
        headers.put("data", SecurityModule.encrypt(user_data));

        NetworkModule.getInstance().getApi().scanTag(headers).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call,
                                   @NonNull Response<ServerResponse> response) {
                if (response.body() == null) {
                    showPopup(ResultPopup.RESULT_FAILURE, 0);
                    return;
                }
                switch (response.body().getType()) {
                    case ServerResponse.TYPE_TASK_ALREADY_COMPLETED:
                        showPopup(ResultPopup.RESULT_ACTIVATED, 0);
                        break;
                    case ServerResponse.TYPE_TASK_FAILURE:
                        showPopup(ResultPopup.RESULT_FAILURE, 0);
                        break;
                    case ServerResponse.TYPE_TASK_COMPLETED:
                        Task completedTask = gson.fromJson(response.body().getData(), Task.class);
                        showPopup(ResultPopup.RESULT_SUCCESS, completedTask.getReward());
                }
            }
    
            @Override
            public void onFailure(@NonNull Call<ServerResponse> call,
                                  @NonNull Throwable t) {
                t.printStackTrace();
                showPopup(ResultPopup.RESULT_FAILURE, 0);
            }
        });
    }
    
    public void showToast(String text, int duration) {
        Toast.makeText(this, text, duration).show();
    }
    
    public void showPopup(int resultCode, int reward) {
        ResultPopup resultPopup = new ResultPopup(
                resultCode, reward, this);
        resultPopup.show();
    }
    
    @Override
    public void onBackPressed() {
    
        int count = getSupportFragmentManager().getBackStackEntryCount();
    
        if (count != 0) {
            getSupportFragmentManager().popBackStack();
        }
    }
    
    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        if (fragment instanceof TaskFragment) {
            TaskFragment taskFragment = (TaskFragment) fragment;
            taskFragment.setOnTaskItemSelectedListener(this);
        }
    }
    
    @Override
    public void onTaskItemSelected(GeoTextTask task) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_main, TaskInfoFragment.newInstance(task), "taskinfo")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right)
                .addToBackStack(null)
                .commit();
    }
}

