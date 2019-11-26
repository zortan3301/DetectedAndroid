package com.devian.detected.utils.domain;

public class SignUp {
    private String login;
    private String email;
    private String passHash;
    
    public SignUp(String login, String email, String passHash) {
        this.login = login;
        this.email = email;
        this.passHash = passHash;
    }
    
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassHash() {
        return passHash;
    }

    public void setPassHash(String passHash) {
        this.passHash = passHash;
    }
}
