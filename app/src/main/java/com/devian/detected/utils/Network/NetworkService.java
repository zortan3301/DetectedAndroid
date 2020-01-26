package com.devian.detected.utils.Network;

import com.devian.detected.utils.security.AES256;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {

    private static final boolean encryptionEnabled = false;
    
    private static NetworkService mInstance;
    
    //private static final String BASE_URL = "http://10.7.0.209:8080";
    private static final String BASE_URL = "http://192.168.1.51:8080"; // for OnePlus // 51-pc, 53-laptop
    //private static final String BASE_URL = "http://10.0.2.2:8080"; // for emulator
    
    private Retrofit mRetrofit;
    
    private NetworkService() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    
    public static NetworkService getInstance() {
        if (mInstance == null) {
            mInstance = new NetworkService();
        }
        return mInstance;
    }
    
    public ApiService getApi() {
        return mRetrofit.create(ApiService.class);
    }

    public Map<String, String> proceedHeader(String data) {
        Map<String, String> headers = new HashMap<>();
        if (encryptionEnabled) {
            headers.put("data", AES256.encrypt(data));
        } else {
            headers.put("data", data);
        }
        return headers;
    }

    public String proceedResponse(ServerResponse response) {
        if (encryptionEnabled) {
            return AES256.decrypt(response.getData());
        } else {
            return response.getData();
        }
    }
}
