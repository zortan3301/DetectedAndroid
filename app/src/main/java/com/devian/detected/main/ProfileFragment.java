package com.devian.detected.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.devian.detected.R;
import com.devian.detected.utils.Network.NetworkService;
import com.devian.detected.utils.domain.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    
    private TextView tvLogin;
    private TextView tvEmail;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        
        tvLogin = v.findViewById(R.id.profile_tvLogin);
        tvEmail = v.findViewById(R.id.profile_tvEmail);
        
        init();
        
        return v;
    }
    
    private void init() {
        NetworkService.getInstance()
                .getJSONApi()
                .getProfile()
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        User user = response.body();
                        tvLogin.setText(user.getLogin());
                        tvEmail.setText(user.getEmail());
                        
                    }
                
                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        tvLogin.setText(t.getMessage());
                        tvEmail.setText("");
                    }
                });
    }
}
