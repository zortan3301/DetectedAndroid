package com.devian.detected.utils.domain;

import java.io.Serializable;

public class RankRow implements Serializable {
    private String uid;
    private long rank;
    private String nickname;
    private long points;
    
    public RankRow() {
    }
    
    public String getUid() {
        return uid;
    }
    
    public long getRank() {
        return rank;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public long getPoints() {
        return points;
    }
    
    public void setUid(String uid) {
        this.uid = uid;
    }
    
    public void setRank(long rank) {
        this.rank = rank;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public void setPoints(long points) {
        this.points = points;
    }
}
