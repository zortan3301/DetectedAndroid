package com.devian.detected;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.devian.detected.login.LoginFragment;
import com.devian.detected.utils.LocalSharedPreferences;
import com.devian.detected.utils.Network.NetworkService;
import com.devian.detected.utils.domain.ServerResponse;
import com.devian.detected.utils.domain.User;
import com.devian.detected.utils.security.CipherUtility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    
    private static final String TAG = "MainActivity";
    
    public static MainActivity mInstance;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInstance = this;
        
        CipherUtility.getInstance();
        LocalSharedPreferences.getInstance(this);
        
        init();
        
        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_main, new LoginFragment())
                .commit();

//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.activity_main, new MainFragment())
//                .commit();
    }
    
    private void init() {
        NetworkService.getInstance()
                .getJSONApi()
                .getPUKey()
                .enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        LocalSharedPreferences.getInstance(mInstance).updateServerKey(response.body().getInfo());
                    }
    
                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                        Toast.makeText(mInstance, "Нет соединения с сервером", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    
}
