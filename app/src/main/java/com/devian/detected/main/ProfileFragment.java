package com.devian.detected.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.devian.detected.MainActivity;
import com.devian.detected.MainFragment;
import com.devian.detected.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileFragment extends Fragment {
    
    private static final String TAG = "ProfileFragment";
    
    private FirebaseAuth mAuth;
    
    @BindView(R.id.profile_tvName) TextView tvName;
    @BindView(R.id.profile_tvEmail) TextView tvEmail;
    @BindView(R.id.profile_btnLogout) Button btnLogout;
    
    @BindView(R.id.profile_tvLevel) TextView tvLevel;
    @BindView(R.id.profile_tvScannedTags) TextView tvScannedTags;
    @BindView(R.id.profile_tvRating) TextView tvRating;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, v);
        mAuth = FirebaseAuth.getInstance();
        init();
        
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        
        return v;
    }
    
    private void init() {
        tvName.setText(mAuth.getCurrentUser().getDisplayName());
        tvEmail.setText(mAuth.getCurrentUser().getEmail());
    }
    
    void logout() {
        // Firebase sign out
        mAuth.signOut();
    
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(),
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    }
                });
    }
    
    void updateStatisctics() {
    
    }
}
