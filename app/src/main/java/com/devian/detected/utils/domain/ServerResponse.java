package com.devian.detected.utils.domain;

import com.devian.detected.utils.security.AES256;

public class ServerResponse {

    /*

    0 : default

    ------------ OK ------------

    10 : auth success

    ----------- ERROR ----------
    
    

     */
    
    private int type;
    private String data;
    
    public ServerResponse() {
        this.type = 0;
        this.data = "";
    }

    public int getType() {
        return this.type;
    }
    
    public String getData() {
        return AES256.decrypt(this.data);
    }
}
