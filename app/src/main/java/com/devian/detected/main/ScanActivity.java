package com.devian.detected.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

// TODO: 26.12.2019 add flashlight

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    
    private static final String TAG = "ScanActivity";
    
    private ZXingScannerView scannerView;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
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
