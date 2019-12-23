package com.devian.detected.utils.Network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {
    
    private static NetworkService mInstance;
    
    private static final String BASE_URL = "http://10.8.78.251:8080";
    //private static final String BASE_URL = "http://192.168.1.53:8080"; // for OnePlus
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
    
}
