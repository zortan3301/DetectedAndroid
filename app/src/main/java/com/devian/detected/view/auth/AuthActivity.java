package com.devian.detected.view.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;

import com.devian.detected.R;
import com.devian.detected.view.MainActivity;
import com.devian.detected.view.MainViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {
    
    private static final String TAG = "AuthActivity";
    private static final int RC_SIGN_IN = 9001;
    
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private MainViewModel viewModel;
    
    @BindView(R.id.auth_btnAuth)
    Button btnAuth;
    @BindView(R.id.auth_progress)
    AVLoadingIndicatorView progress;
    @BindView(R.id.auth_layoutError)
    ConstraintLayout layoutError;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);
        
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
    
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        
        bindView();
    }
    
    private void bindView() {
        btnAuth.setOnClickListener(this);
        mAuth.addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null) {
                viewModel.authUserOnServer(firebaseAuth.getCurrentUser());
            }
        });
        viewModel.bindSignedUser().observe(this, userDataWrapper -> {
            hideProgress();
            if (userDataWrapper.isError()) {
                showError();
            } else {
                startMainActivity();
            }
        });
    }
    
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.auth_btnAuth) {
            showProgress();
            signIn();
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null)
                    firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                showError();
            }
        }
    }
    
    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "FirebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        viewModel.authUserOnServer(firebaseUser);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "signInWithCredential:failure", task.getException());
                        showError();
                    }
                });
    }
    
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    
    private void startMainActivity() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
    
    private void showError() {
        Log.d(TAG, "showError: ");
        layoutError.setVisibility(View.VISIBLE);
    }
    
    private void showProgress() {
        Log.d(TAG, "showProgress: ");
        progress.show();
        btnAuth.setVisibility(View.INVISIBLE);
    }
    
    private void hideProgress() {
        Log.d(TAG, "hideProgress: ");
        progress.hide();
        btnAuth.setVisibility(View.VISIBLE);
    }
    
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
    }
}