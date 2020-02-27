package com.devian.detected.view.extra.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.devian.detected.R;
import com.devian.detected.model.domain.network.ServerResponse;
import com.devian.detected.model.domain.tasks.Tag;
import com.devian.detected.view.MainViewModel;
import com.github.paolorotolo.appintro.ISlidePolicy;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OptionResult extends Fragment implements ISlidePolicy, View.OnClickListener {
    
    private static final String TAG = "OptionResult";
    
    private MainViewModel mainViewModel;
    public Tag tag;
    private String admin;
    private boolean isPolicyRespected;
    
    @BindView(R.id.adminResult_tvId)
    TextView tvId;
    @BindView(R.id.adminResult_tvType)
    TextView tvType;
    @BindView(R.id.adminResult_tvLatitude)
    TextView tvLat;
    @BindView(R.id.adminResult_tvLongitude)
    TextView tvLong;
    @BindView(R.id.adminResult_btnUpload)
    Button btnUpload;
    @BindView(R.id.adminResult_tvWarning)
    TextView tvWarning;
    
    OptionResult(String admin) {
        this.admin = admin;
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.admin_option_result, container, false);
        ButterKnife.bind(this, v);
        
        tag = new Tag();
        btnUpload.setOnClickListener(this);
        
        isPolicyRespected = false;
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.bindAddedTag().observe(this, tagDataWrapper -> {
            isPolicyRespected = true;
            if (tagDataWrapper.isError()) {
                tvWarning.setTextColor(Color.RED);
                if (tagDataWrapper.getCode() == ServerResponse.TYPE_ADD_TAG_ADMIN_FAILURE) {
                    tvWarning.setText(getResources().getString(R.string.not_enough_permissions));
                    btnUpload.setVisibility(View.GONE);
                }
                else {
                    tvWarning.setText(getResources().getString(R.string.error_uploading));
                }
            } else {
                tvWarning.setTextColor(Color.GREEN);
                tvWarning.setText(getResources().getString(R.string.upload_success));
                btnUpload.setVisibility(View.GONE);
            }
        });
        
        return v;
    }
    
    private void updateUI() {
        Log.d(TAG, "updateUI: ");
        tvId.setText(tag.getTagId());
        tvType.setText(String.valueOf(tag.getType()));
        tvLat.setText(String.valueOf(tag.getLatitude()));
        tvLong.setText(String.valueOf(tag.getLongitude()));
    }
    
    public void setTag(Tag tag) {
        this.tag = new Tag();
        this.tag.setTagId(tag.getTagId());
        this.tag.setType(tag.getType());
        this.tag.setLatitude(tag.getLatitude());
        this.tag.setLongitude(tag.getLongitude());
        updateUI();
    }
    
    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: " + view.getId());
        if (view.getId() == R.id.adminResult_btnUpload) {
            mainViewModel.addTag(tag, admin);
            tvWarning.setVisibility(View.VISIBLE);
            tvWarning.setText("Подождите, идет загрузка");
        }
    }
    
    @Override
    public boolean isPolicyRespected() {
        return isPolicyRespected;
    }
    
    @Override
    public void onUserIllegallyRequestedNextPage() {
    
    }
}
