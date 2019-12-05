package com.devian.detected.utils.Network;

import com.devian.detected.utils.domain.ServerResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;

public interface JSONPlaceHolderApi {
    @GET("/testConnection")
    Call<ServerResponse> testConnection();
    
    @GET("/auth")
    Call<ServerResponse> auth(@HeaderMap Map<String, String> headers);
}
