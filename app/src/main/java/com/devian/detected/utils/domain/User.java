package com.devian.detected.utils.domain;

import java.util.Date;

public class User {
    
    private String uid;
    private String displayName;
    private String email;
    private Date lastLogin;

    public User(String uid, String displayName, String email) {
        this.uid = uid;
        this.displayName = displayName;
        this.email = email;
        this.lastLogin = null;
    }
}
