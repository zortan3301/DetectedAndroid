package com.devian.detected.utils.domain;

import java.util.Date;
import java.util.UUID;

public class Task {
    private long id;
    private UUID tagId;
    private int reward;
    private int type;
    private float Latitude;
    private float Longitude;
    private boolean completed;
    private String executor;
    private Date completedTime;
    
    public Task(UUID tagId, String executor) {
        this.tagId = tagId;
        this.executor = executor;
    }
    
    public void setTagId(UUID tagId) {
        this.tagId = tagId;
    }
    
    public void setExecutor(String executor) {
        this.executor = executor;
    }
    
    public static final int TYPE_MAP = 1;
    public static final int TYPE_TEXT = 2;
}
