package com.devian.detected.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.devian.detected.R;
import com.devian.detected.utils.LocalSharedPreferences;
import com.devian.detected.utils.Network.NetworkService;
import com.devian.detected.utils.domain.ServerResponse;
import com.devian.detected.utils.domain.SignUp;
import com.devian.detected.utils.domain.User;
import com.devian.detected.utils.security.CipherUtility;
import com.devian.detected.utils.security.HashUtility;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupFragment extends Fragment implements View.OnClickListener {
    
    private EditText etLogin, etEmail, etPass1, etPass2;
    private Button btnSignup;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signup, container, false);
        
        etLogin = v.findViewById(R.id.signup_etLogin);
        etEmail = v.findViewById(R.id.signup_etEmail);
        etPass1 = v.findViewById(R.id.signup_etPass1);
        etPass2 = v.findViewById(R.id.signup_etPass2);
        btnSignup = v.findViewById(R.id.signup_btnSignUp);
        
        v.findViewById(R.id.signup_tvLogin).setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        
        return v;
    }
    
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signup_tvLogin:
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.activity_main, new LoginFragment(), "login")
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.signup_btnSignUp:
                signup();
                break;
        }
    }
    
    private void signup() {
        String login = etLogin.getText().toString();
        String email = etEmail.getText().toString();
        String pass1 = etPass1.getText().toString();
        String pass2 = etPass2.getText().toString();
        
        if (!pass1.equals(pass2)) {
            Toast.makeText(getContext(), "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            return;
        }
    
        // TODO: 25.11.2019 check login and email input
    
        SignUp signUp = new SignUp(login, email, HashUtility.hashString(pass1));
        
        String userData = "";
        try {
            CipherUtility.getInstance().encrypt(
                    NetworkService.getInstance().getGson().toJson(signUp),
                    CipherUtility.getInstance().decodePublicKey(
                            LocalSharedPreferences.getInstance(getActivity()).getServerKey()));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Ошибка шифрования", Toast.LENGTH_SHORT).show();
        }
        
        Map<String, String> headers = new HashMap<>();
        headers.put("PUKey", CipherUtility.getInstance().getPUKeyString());
        headers.put("userData", userData);
        
        NetworkService.getInstance()
                .getJSONApi()
                .signUp(headers)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        
                        try {
                            String decodedResponse = CipherUtility
                                    .getInstance()
                                    .decrypt(
                                            response.body(),
                                            CipherUtility.getInstance().getKeyPair().getPrivate());
                            ServerResponse serverResponse = NetworkService
                                    .getInstance()
                                    .getGson().fromJson(decodedResponse, ServerResponse.class);
                            Toast.makeText(getContext(), "uuid = " + serverResponse.getInfo(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                        
                        }
                    }
    
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(getContext(), "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                    }
                });
        
    }
}
