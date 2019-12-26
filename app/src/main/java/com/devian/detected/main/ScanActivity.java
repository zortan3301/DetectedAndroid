package com.devian.detected.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.devian.detected.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.Result;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler,
        View.OnClickListener {
    
    private static final String TAG = "ScanActivity";
    
    private ZXingScannerView scannerView;
    private Animation fab_clock, fab_anticlock;
    
    @BindView(R.id.fab_flashlight)
    FloatingActionButton fab_flash;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner_view);
        ButterKnife.bind(this);
        scannerView = findViewById(R.id.scanner_view);
    
        fab_clock = AnimationUtils.loadAnimation(this, R.anim.fab_rotate_clock);
        fab_anticlock = AnimationUtils.loadAnimation(this, R.anim.fab_rotate_anticlock);
        fab_flash.setOnClickListener(this);
    }
    
    @Override
    public void handleResult(Result result) {
        Log.d(TAG, "handleResult");
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", result.getText());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
    
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_flashlight) {
            if (scannerView.getFlash()) {
                fab_flash.startAnimation(fab_anticlock);
                scannerView.setFlash(false);
            } else {
                fab_flash.startAnimation(fab_clock);
                scannerView.setFlash(true);
            }
        }
    }
    
    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        scannerView.stopCamera();
    }
    
    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }
}
