package com.devian.detected.model.repo;

import com.devian.detected.modules.network.GsonSerializer;
import com.google.gson.Gson;

@SuppressWarnings("unused")
public class MainRepository {

    private static final String TAG = "MainRepository";

    private Gson gson = GsonSerializer.getInstance().getGson();
}
