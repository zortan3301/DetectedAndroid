package com.devian.detected.view;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.devian.detected.R;
import com.devian.detected.modules.network.NetworkModule;
import com.devian.detected.view.login.AuthFragment;

public class LauncherActivity extends AppCompatActivity {

    private static final String TAG = "LauncherActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        NetworkModule.getInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_main, new AuthFragment(), "auth")
                .commit();
    }
}
