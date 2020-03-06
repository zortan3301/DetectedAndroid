package com.devian.detected.view.extra.admin;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.devian.detected.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {
    
    private static final String TAG = "AdminActivity";
    
    private FirebaseUser firebaseUser;
    
    @BindView(R.id.admin_btnNewTag)
    Button btnNewTag;
    @BindView(R.id.admin_btnMap)
    Button btnMap;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity);
        ButterKnife.bind(this);
        
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        
        btnNewTag.setOnClickListener(this);
        btnMap.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: " + view.getId());
        switch (view.getId()) {
            case R.id.admin_btnNewTag:
                Intent newTagIntent = new Intent(this, NewTagActivity.class);
                newTagIntent.putExtra("admin", firebaseUser.getUid());
                startActivity(newTagIntent);
                break;
            case R.id.admin_btnMap:
        }
        finish();
    }
}
