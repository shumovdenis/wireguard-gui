package com.shumovdenis.wireguardgui.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class User {
    private int id;
    private String username;
    private String userEmail;
    private String lastHandShake;
    private String privateKey;
    private String publicKey;
    private String allowedIPs;


    public User(int id, String username, String userEmail, String lastHandShake, String allowedIPs) {
        this.id = id;
        this.username = username;
        this.userEmail = userEmail;
        this.lastHandShake = lastHandShake;
        this.allowedIPs = allowedIPs;
    }

    public User(String username, String allowedIPs, String privateKey, String publicKey) {
        this.username = username;
        this.allowedIPs = allowedIPs;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.lastHandShake = "None";
    }

    //ttemp
    public User(String username, String userEmail) {
        this.username = username;
        this.userEmail = userEmail;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", lastHandShake='" + lastHandShake + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", publicKey='" + publicKey + '\'' +
                ", allowedIPs='" + allowedIPs + '\'' +
                '}';
    }
}
