package com.devian.detected;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.devian.detected.login.AuthFragment;
import com.devian.detected.utils.LocalStorage;
import com.devian.detected.modules.network.NetworkModule;
import com.devian.detected.modules.network.domain.ServerResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    
    private static final String TAG = "MainActivity";
    
    @BindView(R.id.main_btnRefresh) Button btnRefresh;
    
    private Call<ServerResponse> callTestConn;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkModule.getInstance();
        LocalStorage.getInstance(this);
    
        ButterKnife.bind(this);
        btnRefresh.setOnClickListener(view -> testConnection());
        
        testConnection();
    }
    
    private void testConnection() {
        Log.d(TAG, "testConnection");
        
        btnRefresh.setVisibility(View.INVISIBLE);
        final TextView textView = findViewById(R.id.main_text);
        textView.setText("Загрузка ...");

        callTestConn = NetworkModule.getInstance().getApi().testConnection();
        callTestConn.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call,
                                   @NonNull Response<ServerResponse> response) {
                if (response.body() == null) {
                    return;
                }
                if (response.body().getType() == ServerResponse.TYPE_DEFAULT) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.activity_main, new AuthFragment(), "auth")
                            .commit();
                } else {
                    textView.setText("Нет соединения с сервером, попробуйте зайти позже");
                    btnRefresh.setVisibility(View.VISIBLE);
                }
            }
    
            @Override
            public void onFailure(@NonNull Call<ServerResponse> call,
                                  @NonNull Throwable t) {
                if (call.isCanceled())
                    Log.d(TAG, "callTestConn is cancelled");
                else {
                    t.printStackTrace();
                    textView.setText("Нет соединения с сервером, попробуйте зайти позже");
                    btnRefresh.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
    }
    
    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        if (callTestConn != null)
            callTestConn.cancel();
    }
}
