package com.shumovdenis.wireguardgui.entity;

import lombok.Getter;
import lombok.Setter;

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

    public User(String username, String allowedIPs, String privateKey, String publicKey) {
        this.username = username;
        this.allowedIPs = allowedIPs;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.lastHandShake = "None";
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
