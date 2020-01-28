package com.devian.detected.utils.network;

import com.devian.detected.utils.security.AES256;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkManager {

    private static final boolean encryptionEnabled = false;

    private static NetworkManager mInstance;

    //private static final String BASE_URL = "http://10.7.0.209:8080";
    //private static final String BASE_URL = "http://192.168.1.51:8080"; // for OnePlus // 51-pc, 53-laptop
    private static final String BASE_URL = "http://10.0.2.2:8080"; // for emulator

    private Retrofit mRetrofit;

    private NetworkManager() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = builder.build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    public static NetworkManager getInstance() {
        if (mInstance == null) {
            mInstance = new NetworkManager();
        }
        return mInstance;
    }

    public NetworkInterface getApi() {
        return mRetrofit.create(NetworkInterface.class);
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

    public String proceedResponse(String data) {
        if (encryptionEnabled) {
            return AES256.decrypt(data);
        } else {
            return data;
        }
    }
}
