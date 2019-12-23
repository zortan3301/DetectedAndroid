package com.devian.detected;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.devian.detected.main.ScanActivity;
import com.devian.detected.main.TaskFragment;
import com.devian.detected.main.TaskInfoFragment;
import com.devian.detected.utils.Network.NetworkService;
import com.devian.detected.utils.Network.ServerResponse;
import com.devian.detected.utils.domain.Task;
import com.devian.detected.utils.security.AES256;
import com.devian.detected.utils.ui.CustomViewPager;
import com.devian.detected.utils.ui.PagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainFragment extends AppCompatActivity
        implements
        View.OnClickListener,
        TaskFragment.OnTaskItemSelectedListener {
    
    private static final String TAG = "MainFragment";
    
    private FirebaseAuth mAuth;
    
    private Gson gson = new Gson();
    
    PagerAdapter pagerAdapter;
    CustomViewPager viewPager;
    
    @BindView(R.id.fab_qr)
    FloatingActionButton fab_qr;
    
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        
        ButterKnife.bind(this);
        
        mAuth = FirebaseAuth.getInstance();
    
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        
        fab_qr.setOnClickListener(this);
        
    }
    
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_qr:
                
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
                } else {
                    Intent i = new Intent(this, ScanActivity.class);
                    startActivityForResult(i, 1);
                }
                
                break;
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("result");
                proceedTask(result);
            }
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent(this, ScanActivity.class);
                startActivityForResult(i, 1);
            } else {
                showToast("Для считывания QR, необходим доступ к камере", Toast.LENGTH_LONG);
            }
        }
    }
    
    public void proceedTask(String text) {
        String tagId = AES256.decrypt(text);
        
        if (tagId == null) {
            showSnackbar("Недействительная метка", Toast.LENGTH_SHORT);
            return;
        }
        
        String executor = mAuth.getCurrentUser().getUid();
        Task task = new Task(tagId, executor);
        String user_data = gson.toJson(task);
    
        Map<String, String> headers = new HashMap<>();
        headers.put("data", AES256.encrypt(user_data));
    
        NetworkService.getInstance().getApi().scanTag(headers).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.body() == null) {
                    showSnackbar("Недействительная метка", Toast.LENGTH_LONG);
                    return;
                }
                switch (response.body().getType()) {
                    case ServerResponse.TYPE_TASK_ALREADY_COMPLETED:
                        showSnackbar("Данное задание уже выполнено", Toast.LENGTH_LONG);
                        break;
                    case ServerResponse.TYPE_TASK_FAILURE:
                        showSnackbar("Недействительная метка", Toast.LENGTH_LONG);
                        break;
                    case ServerResponse.TYPE_TASK_COMPLETED:
                        Task completedTask = gson.fromJson(response.body().getData(), Task.class);
                        showSnackbar("Успешно, вы получили " + completedTask.getReward() + " поинтов", Toast.LENGTH_LONG);
                }
            }
    
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
    
    public void showToast(String text, int duration) {
        Toast.makeText(this, text, duration).show();
    }
    
    public void showSnackbar(String text, int duration) {
        Snackbar.make(findViewById(R.id.fragment_main), text, duration).show();
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
    public void onTaskItemSelected(Task task) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_main, TaskInfoFragment.newInstance(task), "taskinfo")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right)
                .addToBackStack(null)
                .commit();
    }
}

