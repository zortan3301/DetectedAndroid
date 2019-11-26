package com.devian.detected.utils.Network;

import com.devian.detected.utils.domain.ServerResponse;
import com.devian.detected.utils.domain.User;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface JSONPlaceHolderApi {
    @GET("/getProfile")
    public Call<User> getProfile();
    
    @GET("/security/getPUKey")
    public Call<ServerResponse> getPUKey();
    
    @POST("/signup")
    public Call<String> signUp(@HeaderMap Map<String, String> headers);
}
