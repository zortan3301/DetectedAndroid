package com.devian.detected.modules.network;

import com.devian.detected.modules.network.domain.ServerResponse;
import com.devian.detected.modules.security.SecurityModule;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkModule {

    private static final boolean encryptionEnabled = false;

    private static NetworkModule mInstance;

    private static final String BASE_URL = "http://172.21.196.220:8080";
    //private static final String BASE_URL = "http://192.168.1.51:8080"; // for OnePlus // 51-pc, 53-laptop
    //private static final String BASE_URL = "http://10.0.2.2:8080"; // for emulator

    private Retrofit mRetrofit;

    private NetworkModule() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = builder.build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    public static NetworkModule getInstance() {
        if (mInstance == null) {
            mInstance = new NetworkModule();
        }
        return mInstance;
    }

    public NetworkInterface getApi() {
        return mRetrofit.create(NetworkInterface.class);
    }

    public Map<String, String> proceedHeader(String data) {
        Map<String, String> headers = new HashMap<>();
        if (encryptionEnabled) {
            headers.put("data", SecurityModule.encrypt(data));
        } else {
            headers.put("data", data);
        }
        return headers;
    }

    public String proceedResponse(ServerResponse response) {
        if (encryptionEnabled) {
            return SecurityModule.decrypt(response.getData());
        } else {
            return response.getData();
        }
    }

    public String proceedResponse(String data) {
        if (encryptionEnabled) {
            return SecurityModule.decrypt(data);
        } else {
            return data;
        }
    }
}
