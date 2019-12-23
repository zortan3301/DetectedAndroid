package com.devian.detected.utils.domain;


import java.io.Serializable;
import java.sql.Date;

public class Task implements Serializable {
    private long id;
    private String tagId;
    private int reward;
    private int type;
    private float Latitude;
    private float Longitude;
    private boolean completed;
    private String executor;
    private Date completedTime;
    
    private String title;
    private String description;
    private String imgUrl;
    
    public Task(String tagId, String executor) {
        this.tagId = tagId;
        this.executor = executor;
    }
    
    public void setTagId(String tagId) {
        this.tagId = tagId;
    }
    
    public void setExecutor(String executor) {
        this.executor = executor;
    }
    
    public long getId() {
        return id;
    }
    
    public String getTagId() {
        return tagId;
    }
    
    public int getReward() {
        return reward;
    }
    
    public int getType() {
        return type;
    }
    
    public float getLatitude() {
        return Latitude;
    }
    
    public float getLongitude() {
        return Longitude;
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    public String getExecutor() {
        return executor;
    }
    
    public Date getCompletedTime() {
        return completedTime;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getImgUrl() {
        return imgUrl;
    }
    
    public static final int TYPE_MAP = 1;
    public static final int TYPE_TEXT = 2;
    
}
