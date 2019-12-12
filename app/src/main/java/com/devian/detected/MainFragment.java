package com.devian.detected;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import com.devian.detected.main.ScanActivity;
import com.devian.detected.utils.ui.CustomViewPager;
import com.devian.detected.utils.ui.PagerAdapter;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.zxing.Result;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainFragment extends AppCompatActivity
        implements
            View.OnClickListener{
    
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
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
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
                Toast.makeText(this, "Для считывания QR, необходим доступ к камере", Toast.LENGTH_LONG).show();
            }
        }
    }
}

