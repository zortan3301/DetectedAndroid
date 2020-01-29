package com.devian.detected.model.domain;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@SuppressWarnings("unused")
public class UserStats implements Serializable {

    private String uid;
    private long points;
    private int tags;
    
    public UserStats(String uid, long points, int tags) {
        this.uid = uid;
        this.points = points;
        this.tags = tags;
    }
}
