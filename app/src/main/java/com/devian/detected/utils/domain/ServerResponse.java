package com.devian.detected.utils.domain;

public class ServerResponse {

    /*

    0 : default

    ------------ OK ------------

    1 : sign up success, return uuid

    ----------- ERROR ----------

    -1 : sign up failure, login exist
    -2 : sign up failure, email exist
    -3 : sign up failure

     */


    private int type;
    private String info;

    public ServerResponse(int type, String info) {
        this.type = type;
        this.info = info;
    }

    public ServerResponse(int type) {
        this.type = type;
        switch (type) {
            case 1:
                break;
            case -1:
                info = "sign up failure, login exist";
                break;
            case -2:
                info = "sign up failure, email exist";
                break;
            case -3:
                info = "sign up failure";
                break;
            default:
                info = "OK";
        }
    }

    public ServerResponse() {
        this.type = 0;
        this.info = "";
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    
    public String getInfo() {
        return info;
    }
}
