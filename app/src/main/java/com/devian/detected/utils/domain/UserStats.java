package com.devian.detected.utils.domain;

import com.devian.detected.utils.LevelManager;

import java.io.Serializable;

public class UserStats implements Serializable {

    private String uid;
    private long points;
    private int level;
    private int tags;

    public UserStats(String uid, long points, int tags) {
        this.uid = uid;
        this.points = points;
        this.level = LevelManager.getLevelByPoints(points);
        this.tags = tags;
    }
    
    public String getUid() {
        return uid;
    }
    
    public void setUid(String uid) {
        this.uid = uid;
    }
    
    public long getPoints() {
        return points;
    }
    
    public void setPoints(long points) {
        this.points = points;
    }
    
    public int getLevel() {
        return level;
    }
    
    public void setLevel(int level) {
        this.level = level;
    }
    
    public int getTags() {
        return tags;
    }
    
    public void setTags(int tags) {
        this.tags = tags;
    }
}
