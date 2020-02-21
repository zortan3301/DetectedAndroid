package com.devian.detected.modules.network;

import com.devian.detected.model.domain.network.ServerResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface NetworkInterface {
    @GET("auth")
    Call<ServerResponse> auth(@HeaderMap Map<String, String> headers);

    @GET("getUserInfo")
    Call<ServerResponse> getUserInfo(@HeaderMap Map<String, String> headers);

    @GET("getUserStats")
    Call<ServerResponse> getUserStats(@HeaderMap Map<String, String> headers);

    @GET("getMapTasks")
    Call<ServerResponse> getMapTasks();

    @GET("getTextTasks")
    Call<ServerResponse> getTextTasks();
    
    @POST("scanGeoTag")
    Call<ServerResponse> scanGeoTag(@HeaderMap Map<String, String> headers);
    
    @POST("scanGeoTextTag")
    Call<ServerResponse> scanGeoTextTag(@HeaderMap Map<String, String> headers);

    @GET("getRankTop10")
    Call<ServerResponse> getRankTop10();

    @GET("getSelfRank")
    Call<ServerResponse> getSelfRank(@HeaderMap Map<String, String> headers);

    @GET("getEvent")
    Call<ServerResponse> getEvent();

    @POST("changeNickname")
    Call<ServerResponse> changeNickname(@HeaderMap Map<String, String> headers);
}
