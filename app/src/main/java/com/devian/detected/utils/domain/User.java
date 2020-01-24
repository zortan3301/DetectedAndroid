package com.devian.detected.utils.domain;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    
    private String uid;
    private String displayName;
    private String email;
    private Date lastLogin;
    
    public User() {
    }

    public User(String uid, String displayName, String email) {
        this.uid = uid;
        this.displayName = displayName;
        this.email = email;
        this.lastLogin = null;
    }
    
    public User(String uid) {
        this.uid = uid;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Date getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }
}
