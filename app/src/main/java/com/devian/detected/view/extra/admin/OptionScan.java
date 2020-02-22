package com.devian.detected.view.extra.admin;

import android.os.Bundle;
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
import com.devian.detected.model.domain.tasks.GeoTask;
import com.devian.detected.model.domain.tasks.Tag;
import com.devian.detected.model.domain.tasks.Task;
import com.devian.detected.model.repo.TaskRepository;
import com.github.paolorotolo.appintro.ISlidePolicy;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class OptionScan extends Fragment implements ISlidePolicy, ZXingScannerView.ResultHandler,
        View.OnClickListener {
    
    private static final String TAG = "OptionScan";
    
    public Tag tag;
    private String admin;
    
    @BindView(R.id.admin_scanner)
    ZXingScannerView scannerView;
    @BindView(R.id.admin_tvTagId)
    TextView tvTagId;
    @BindView(R.id.admin_tvTagType)
    TextView tvTagType;
    @BindView(R.id.admin_btnReset)
    ImageView btnReset;
    @BindView(R.id.admin_tvWarning)
    TextView tvWarning;
    @BindView(R.id.admin_tvContinue)
    TextView tvContinue;
    @BindView(R.id.admin_btnFlash)
    ImageView btnFlash;
    
    OptionScan(String admin) {
        this.admin = admin;
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.admin_option_scan, container, false);
        ButterKnife.bind(this, v);
    
        btnReset.setOnClickListener(this);
        btnFlash.setOnClickListener(this);
        
        List<BarcodeFormat> formats = new ArrayList<>();
        formats.add(BarcodeFormat.QR_CODE);
        scannerView.setFormats(formats);
        
        return v;
    }
    
    @Override
    public void handleResult(Result result) {
        Log.d(TAG, "handleResult: ");
        btnReset.setVisibility(View.VISIBLE);
        Task task = TaskRepository.proceedTask(result.getText(), admin);
        if (task == null) {
            tvWarning.setVisibility(View.VISIBLE);
            return;
        }
        tag = new Tag();
        tag.setTagId(task.getTagId());
        if (task instanceof GeoTask)
            tag.setType(1);
        else
            tag.setType(2);
        tvTagId.setText(tag.getTagId());
        tvTagType.setText(String.valueOf(tag.getType()));
        tvContinue.setVisibility(View.VISIBLE);
    }
    
    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: " + view.getId());
        if (view.getId() == R.id.admin_btnReset) {
            btnReset.setVisibility(View.INVISIBLE);
            scannerView.resumeCameraPreview(this);
            tvWarning.setVisibility(View.GONE);
            tvContinue.setVisibility(View.INVISIBLE);
            tvTagId.setText("");
            tvTagType.setText("");
            tag = null;
        }
        if (view.getId() == R.id.admin_btnFlash) {
            scannerView.setFlash(!scannerView.getFlash());
        }
    }
    
    @Override
    public boolean isPolicyRespected() {
        Log.d(TAG, "isPolicyRespected: ");
        return tag != null;
    }
    
    @Override
    public void onUserIllegallyRequestedNextPage() {
        Log.d(TAG, "onUserIllegallyRequestedNextPage: ");
    }
    
    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }
}
