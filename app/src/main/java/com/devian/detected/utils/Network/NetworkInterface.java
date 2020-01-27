package com.devian.detected.utils.network;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface NetworkInterface {
    @GET("/testConnection")
    Call<ServerResponse> testConnection();

    @GET("/auth")
    Call<ServerResponse> auth(@HeaderMap Map<String, String> headers);

    @GET("/getUserInfo")
    Call<ServerResponse> getUserInfo(@HeaderMap Map<String, String> headers);

    @GET("/getStats")
    Call<ServerResponse> getStats(@HeaderMap Map<String, String> headers);

    @GET("/getMapTasks")
    Observable<ServerResponse> getMapTasks();

    @GET("/getTextTasks")
    Observable<ServerResponse> getTextTasks();

    @POST("/scanTag")
    Call<ServerResponse> scanTag(@HeaderMap Map<String, String> headers);

    @GET("/getRankTop10")
    Call<ServerResponse> getRankTop10();

    @GET("/getPersonalRank")
    Call<ServerResponse> getPersonalRank(@HeaderMap Map<String, String> headers);

    @GET("/getEvent")
    Call<ServerResponse> getEvent();

    @POST("/changeNickname")
    Call<ServerResponse> changeNickname(@HeaderMap Map<String, String> headers);
}
