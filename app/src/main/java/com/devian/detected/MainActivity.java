package com.devian.detected;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.devian.detected.login.AuthFragment;
import com.devian.detected.utils.LocalStorage;
import com.devian.detected.utils.Network.NetworkService;
import com.devian.detected.utils.domain.ServerResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    
    private static final String TAG = "MainActivity";
    
    public static MainActivity mInstance;
    
    @BindView(R.id.main_btnRefresh) Button btnRefresh;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInstance = this;
        
        NetworkService.getInstance();
        LocalStorage.getInstance(this);
    
        ButterKnife.bind(this);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testConnection();
            }
        });
        
        testConnection();
    
        // TODO: 05.12.2019 loading wheel

    }
    
    private void testConnection() {
    
        btnRefresh.setVisibility(View.INVISIBLE);
        final TextView textView = findViewById(R.id.main_text);
        textView.setText("Загрузка ...");
        
        NetworkService.getInstance().getJSONApi().testConnection().enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.body().getType() == 0) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.activity_main, new AuthFragment(), "auth")
                            .commit();
                } else {
                    textView.setText("Нет соединения с сервером, попробуйте зайти позже");
                    btnRefresh.setVisibility(View.VISIBLE);
                }
            }
    
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                t.printStackTrace();
                textView.setText("Нет соединения с сервером, попробуйте зайти позже");
                btnRefresh.setVisibility(View.VISIBLE);
            }
        });
    }
    
    @Override
    public void onBackPressed() {
    }
}
